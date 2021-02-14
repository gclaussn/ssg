package com.github.gclaussn.ssg.server;

import java.util.Arrays;

import javax.websocket.server.ServerEndpointConfig;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.conf.SiteProperty;
import com.github.gclaussn.ssg.file.SiteFileEventListener;
import com.github.gclaussn.ssg.plugin.SitePluginGoal;
import com.github.gclaussn.ssg.server.domain.SiteResource;
import com.github.gclaussn.ssg.server.domain.event.SiteEventEncoder;
import com.github.gclaussn.ssg.server.domain.event.SiteEventEndpoint;
import com.github.gclaussn.ssg.server.domain.event.SiteEventResource;
import com.github.gclaussn.ssg.server.domain.page.PageResource;
import com.github.gclaussn.ssg.server.domain.page.PageSetResource;
import com.github.gclaussn.ssg.server.domain.plugin.SitePluginResource;
import com.github.gclaussn.ssg.server.domain.plugin.goal.SitePluginGoalTaskEndpoint;
import com.github.gclaussn.ssg.server.domain.source.SourceResource;
import com.github.gclaussn.ssg.server.file.SiteFileWatcher;
import com.github.gclaussn.ssg.server.provider.GenericEndpointConfigurator;

import io.undertow.Undertow;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;

/**
 * Goal, which starts a development server.<br />
 * The server maps following urls:
 * <table>
 *   <tr>
 *     <td>REST API</td>
 *     <td>{@code /api}</td>
 *   </tr>
 *   <tr>
 *     <td>Websocket API</td>
 *     <td>{@code /wsa}</td>
 *   </tr>
 *   <tr>
 *     <td>Server application</td>
 *     <td>{@code /app}</td>
 *   </tr>
 *   <tr>
 *     <td>Site</td>
 *     <td>{@code /}</td>
 *   </tr>
 * </table>
 */
public class StartGoal implements SitePluginGoal {

  private static final Logger LOGGER = LoggerFactory.getLogger(StartGoal.class);

  public static final String HOST = "ssg.server.host";
  public static final String PORT = "ssg.server.port";

  public static final String SITE_EVENT_ENDPOINT = "ssg.server.siteEventEndpoint";

  @SiteProperty(name = HOST, required = false, defaultValue = "localhost")
  protected String host;
  @SiteProperty(name = PORT, required = false, defaultValue = "8080")
  protected Integer port;

  @SiteProperty(name = SITE_EVENT_ENDPOINT, required = true, documented = false)
  protected SiteEventEndpoint eventEndpoint;

  /** Websocket endpoint for the execution of plugin goals. */
  private final SitePluginGoalTaskEndpoint taskEndpoint;

  /** JAXRS server instance, providing a REST and a WebSocket API. */
  private UndertowJaxrsServer server;

  public StartGoal() {
    taskEndpoint = new SitePluginGoalTaskEndpoint();
  }

  protected Application createServerApi(Site site, SiteFileWatcher siteFileWatcher) {
    ServerApi serverApi = new ServerApi(site);
    serverApi.add(new ServerResource(siteFileWatcher, this::stop));

    serverApi.add(new PageResource());
    serverApi.add(new PageSetResource());
    serverApi.add(new SiteEventResource());
    serverApi.add(new SitePluginResource(taskEndpoint));
    serverApi.add(new SiteResource());
    serverApi.add(new SourceResource());

    return serverApi;
  }

  protected DeploymentInfo createWebSocketApiDeployment() {
    ServerEndpointConfig eventsConfig = ServerEndpointConfig.Builder.create(SiteEventEndpoint.class, "/events")
        .configurator(new GenericEndpointConfigurator(eventEndpoint))
        .encoders(Arrays.asList(SiteEventEncoder.class))
        .build();

    ServerEndpointConfig tasksConfig = ServerEndpointConfig.Builder.create(SitePluginGoalTaskEndpoint.class, "/tasks")
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
    site.load();
    site.generate();

    // create file watcher
    final SiteFileWatcher siteFileWatcher = SiteFileWatcher.of(site);

    server = new UndertowJaxrsServer();

    server.deploy(createServerApi(site, siteFileWatcher), "/api");
    server.deploy(createWebSocketApiDeployment());

    server.addResourcePrefixPath("/app", new ResourceHandler(new ServerAppManager()));
    server.addResourcePrefixPath("/", new ResourceHandler(new SiteOutputManager(site)));

    // start server
    server.start(Undertow.builder().addHttpListener(port, host));

    LOGGER.info("Serving site: http://{}:{}", host, port);
    LOGGER.info("Serving app:  http://{}:{}/app", host, port);
    LOGGER.info("Serving api:  http://{}:{}/api", host, port);

    // start file watcher
    siteFileWatcher.start((SiteFileEventListener) site);

    // add shutdown hook that notifies the waiting main thread
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      this.stop(siteFileWatcher);
    }, "ssg-shutdown-hook"));

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

      // stop execution of plugin goals
      taskEndpoint.stop();

      // stop file watcher thread
      siteFileWatcher.stop();

      // notify waiting main thread
      siteFileWatcher.notify();
    }
  }
}
