package com.github.gclaussn.ssg.server.goal;

import java.util.Arrays;

import javax.websocket.server.ServerEndpointConfig;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gclaussn.ssg.EventDrivenSite;
import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.conf.SiteProperty;
import com.github.gclaussn.ssg.file.SiteFileWatcher;
import com.github.gclaussn.ssg.plugin.SitePluginGoal;
import com.github.gclaussn.ssg.server.ServerApi;
import com.github.gclaussn.ssg.server.ServerAppManager;
import com.github.gclaussn.ssg.server.SiteOutputManager;
import com.github.gclaussn.ssg.server.domain.event.SiteEventEncoder;
import com.github.gclaussn.ssg.server.domain.event.SiteEventEndpoint;
import com.github.gclaussn.ssg.server.domain.event.SiteEventEndpointConfigurator;

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

  @SiteProperty(name = HOST, defaultValue = "localhost")
  protected String host;
  @SiteProperty(name = PORT, defaultValue = "8080")
  protected Integer port;

  private final SiteEventEndpoint eventEndpoint;

  /** JAXRS server instance, providing a REST and a WebSocket API. */
  private UndertowJaxrsServer server;

  public StartGoal(SiteEventEndpoint eventEndpoint) {
    this.eventEndpoint = eventEndpoint;
  }

  protected DeploymentInfo createWebSocketApiDeployment() {
    ServerEndpointConfig endpointConfig = ServerEndpointConfig.Builder.create(SiteEventEndpoint.class, "/events")
        .configurator(new SiteEventEndpointConfigurator(eventEndpoint))
        .encoders(Arrays.asList(SiteEventEncoder.class))
        .build();

    WebSocketDeploymentInfo webSocketDeployment = new WebSocketDeploymentInfo();
    webSocketDeployment.addEndpoint(endpointConfig);
    webSocketDeployment.setBuffers(new DefaultByteBufferPool(false, 1024));

    DeploymentInfo deployment = new DeploymentInfo();
    deployment.addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, webSocketDeployment);
    deployment.setContextPath("/wsa");
    deployment.setDeploymentName("Websocket API");
    deployment.setClassLoader(this.getClass().getClassLoader());

    return deployment;
  }

  @Override
  public int execute(Site site) {
    site.load();
    site.generate();

    // create file watcher
    final SiteFileWatcher siteFileWatcher = SiteFileWatcher.of(site);

    server = new UndertowJaxrsServer();

    server.deploy(new ServerApi(site, siteFileWatcher), "/api");
    server.deploy(createWebSocketApiDeployment());

    server.addResourcePrefixPath("/app", new ResourceHandler(new ServerAppManager()));
    server.addResourcePrefixPath("/", new ResourceHandler(new SiteOutputManager(site)));

    // start server
    server.start(Undertow.builder().addHttpListener(port, host));

    LOGGER.info("Serving site: http://{}:{} and application: http://{}:{}/app", host, port, host, port);

    // start file watcher
    siteFileWatcher.start((EventDrivenSite) site);

    // add shutdown hook that notifies the waiting main thread
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      synchronized (siteFileWatcher) {
        // stop file watcher thread
        siteFileWatcher.stop();

        // notify waiting main thread
        siteFileWatcher.notify();
      }
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

    return SC_SUCCESS;
  }
}
