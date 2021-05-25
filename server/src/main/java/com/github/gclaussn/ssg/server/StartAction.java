package com.github.gclaussn.ssg.server;

import javax.ws.rs.core.Application;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.conf.SiteProperty;
import com.github.gclaussn.ssg.plugin.SitePluginAction;
import com.github.gclaussn.ssg.server.domain.PageResource;
import com.github.gclaussn.ssg.server.domain.PageSetResource;
import com.github.gclaussn.ssg.server.domain.SiteResource;
import com.github.gclaussn.ssg.server.domain.event.SiteEventEndpoint;
import com.github.gclaussn.ssg.server.domain.event.SiteEventResource;
import com.github.gclaussn.ssg.server.domain.plugin.SitePluginResource;
import com.github.gclaussn.ssg.server.domain.plugin.action.SitePluginActionTaskEndpoint;
import com.github.gclaussn.ssg.server.file.SiteFileWatcher;
import com.github.gclaussn.ssg.server.provider.GenericEndpoint;

import io.undertow.Undertow;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;

/**
 * Action, which starts a development server.<br />
 * The server maps the REST API under {@code /api}, the Websocket API under {@code /wsa}, the server
 * application under {@code /app} and the site itself under its base path (default value {@code /},
 * if no explicit base path was specified).
 */
public class StartAction implements SitePluginAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(StartAction.class);

  public static final String HOST = "ssg.server.host";
  public static final String PORT = "ssg.server.port";

  @SiteProperty(name = HOST, required = false, defaultValue = "localhost")
  protected String host;
  @SiteProperty(name = PORT, required = false, defaultValue = "8080")
  protected Integer port;

  @SiteProperty(name = ServerPlugin.SITE_EVENT_ENDPOINT, required = true, documented = false)
  protected SiteEventEndpoint eventEndpoint;

  /** Websocket endpoint for the execution of plugin actions. */
  private final SitePluginActionTaskEndpoint taskEndpoint;

  /** JAXRS server instance, providing a REST and a WebSocket API. */
  private UndertowJaxrsServer server;

  public StartAction() {
    taskEndpoint = new SitePluginActionTaskEndpoint();
  }

  protected Application createServerApi(Site site, SiteFileWatcher siteFileWatcher) {
    ServerApi serverApi = new ServerApi(site);
    serverApi.add(new ServerResource(siteFileWatcher, this::stop));

    serverApi.add(new PageResource());
    serverApi.add(new PageSetResource());
    serverApi.add(new SiteEventResource());
    serverApi.add(new SitePluginResource(taskEndpoint));
    serverApi.add(new SiteResource());

    return serverApi;
  }

  protected DeploymentInfo createWebSocketApiDeployment() {
    WebSocketDeploymentInfo webSocketDeployment = new WebSocketDeploymentInfo();
    webSocketDeployment.addEndpoint(GenericEndpoint.of(eventEndpoint, "/events"));
    webSocketDeployment.addEndpoint(GenericEndpoint.of(taskEndpoint, "/tasks"));
    webSocketDeployment.setBuffers(new DefaultByteBufferPool(false, 1024));

    DeploymentInfo deployment = new DeploymentInfo();
    deployment.addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, webSocketDeployment);
    deployment.setContextPath("/wsa");
    deployment.setDeploymentName("Websocket API");
    deployment.setClassLoader(this.getClass().getClassLoader());

    return deployment;
  }

  @Override
  public void execute(Site site) {
    // create file watcher
    final SiteFileWatcher siteFileWatcher = SiteFileWatcher.of(site);

    server = new UndertowJaxrsServer();

    server.deploy(createServerApi(site, siteFileWatcher), "/api");
    server.deploy(createWebSocketApiDeployment());

    String basePath = site.getConfiguration().getBasePath();

    server.addResourcePrefixPath("/app", new ResourceHandler(new ServerAppManager()));
    server.addResourcePrefixPath(basePath, new ResourceHandler(new SiteOutputManager(site)));

    // start server
    server.start(Undertow.builder().addHttpListener(port, host));

    String uri = String.format("http://%s:%d", host, port);
    LOGGER.info("Serving site: {}{}", uri, basePath);
    LOGGER.info("Serving app:  {}/app", uri);
    LOGGER.info("Serving api:  {}/api", uri);

    // start file watcher
    siteFileWatcher.start();

    // add shutdown hook that notifies the waiting main thread
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      this.stop(siteFileWatcher);
    }, "hook"));

    // load and generate site once
    site.load();
    site.generate();

    // wait for server stop or shutdown hook
    synchronized (siteFileWatcher) {
      try {
        siteFileWatcher.wait();
      } catch (InterruptedException e) {
        // ignore exception
      }
    }

    // stop server
    server.stop();
  }

  public void stop(SiteFileWatcher siteFileWatcher) {
    synchronized (siteFileWatcher) {
      // close websocket event sessions
      eventEndpoint.close();

      // stop execution of plugin actions
      taskEndpoint.stop();

      // stop file watcher thread
      siteFileWatcher.stop();

      // notify waiting main thread
      siteFileWatcher.notify();
    }
  }
}
