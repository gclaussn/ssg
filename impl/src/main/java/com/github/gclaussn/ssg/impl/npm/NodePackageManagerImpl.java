package com.github.gclaussn.ssg.impl.npm;

import java.io.InputStream;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.github.gclaussn.ssg.npm.NodePackage;
import com.github.gclaussn.ssg.npm.NodePackageInfo;
import com.github.gclaussn.ssg.npm.NodePackageManager;
import com.github.gclaussn.ssg.npm.NodePackageRegistry;

public class NodePackageManagerImpl implements NodePackageManager {

  private final ResteasyClient client;

  private final NodePackageRegistry registry;

  public NodePackageManagerImpl(String registryUrl) {
    // use custom deserializer to hide DTOs
    SimpleModule module = new SimpleModule();
    module.addDeserializer(NodePackageInfo.class, new NodePackageInfoDeserializer());

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.registerModule(module);
    objectMapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
    objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

    client = (ResteasyClient) ResteasyClientBuilder.newBuilder()
        .register(new JacksonJsonProvider(objectMapper))
        .build();

    registry = client.target(registryUrl).proxy(NodePackageRegistry.class);
  }

  @Override
  public void close() {
    if (client != null) {
      client.close();
    }
  }

  @Override
  public InputStream download(NodePackage nodePackage) {
    return download(nodePackage.getName(), nodePackage.getVersion());
  }

  @Override
  public InputStream download(String packageName, String version) {
    return registry.download(packageName, version);
  }

  @Override
  public NodePackageInfo getPackage(NodePackage nodePackage) {
    return getPackage(nodePackage.getName(), nodePackage.getVersion());
  }

  @Override
  public NodePackageInfo getPackage(String packageName, String version) {
    return registry.getPackage(packageName, version);
  }
}
