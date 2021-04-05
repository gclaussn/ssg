package com.github.gclaussn.ssg.server;

import java.util.Arrays;

import javax.websocket.server.ServerEndpointConfig;
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
import com.github.gclaussn.ssg.server.provider.GenericEncoder;
import com.github.gclaussn.ssg.server.provider.GenericEndpointConfigurator;

import io.undertow.Undertow;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;

/**
 * Action, which starts a development server.<br />
 * The server maps following urls:
 * - REST API: /api
 * - Websocket API: /wsa
 * - Server application: /app
 * - Site: /
 */
public class StartAction implements SitePluginAction {

  private static final Logger LOGGER = LoggerFactory.getLogger(StartAction.class);

  public static final String HOST = "ssg.server.host";
  public static final String PORT = "ssg.server.port";

  public static final String SITE_EVENT_ENDPOINT = "ssg.server.siteEventEndpoint";

  @SiteProperty(name = HOST, required = false, defaultValue = "localhost")
  protected String host;
  @SiteProperty(name = PORT, required = false, defaultValue = "8080")
  protected Integer port;

  @SiteProperty(name = SITE_EVENT_ENDPOINT, required = true, documented = false)
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
    ServerEndpointConfig eventsConfig = ServerEndpointConfig.Builder.create(SiteEventEndpoint.class, "/events")
        .configurator(new GenericEndpointConfigurator(eventEndpoint))
        .encoders(Arrays.asList(GenericEncoder.class))
        .build();

    ServerEndpointConfig tasksConfig = ServerEndpointConfig.Builder.create(SitePluginActionTaskEndpoint.class, "/tasks")
        .configurator(new GenericEndpointConfigurator(taskEndpoint))
        .build();

    WebSocketDeploymentInfo webSocketDeployment = new WebSocketDeploymentInfo();
    webSocketDeployment.addEndpoint(eventsConfig);
    webSocketDeployment.addEndpoint(tasksConfig);
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

    server.addResourcePrefixPath("/app", new ResourceHandler(new ServerAppManager()));
    server.addResourcePrefixPath("/", new ResourceHandler(new SiteOutputManager(site)));

    // start server
    server.start(Undertow.builder().addHttpListener(port, host));

    String uri = String.format("http://%s:%d", host, port);
    LOGGER.info("Serving site: {}", uri);
    LOGGER.info("Serving app:  {}/app", uri);
    LOGGER.info("Serving api:  {}/api", uri);

    // start file watcher
    siteFileWatcher.start(site);

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
