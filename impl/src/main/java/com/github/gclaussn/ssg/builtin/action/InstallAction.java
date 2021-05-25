package com.github.gclaussn.ssg.builtin.action;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.ws.rs.NotFoundException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.github.gclaussn.ssg.Site;
import com.github.gclaussn.ssg.conf.SiteConsole;
import com.github.gclaussn.ssg.conf.SiteProperty;
import com.github.gclaussn.ssg.npm.NodePackage;
import com.github.gclaussn.ssg.npm.NodePackageInfo;
import com.github.gclaussn.ssg.npm.NodePackageManager;
import com.github.gclaussn.ssg.npm.NodePackageSpec;
import com.github.gclaussn.ssg.plugin.SitePluginAction;
import com.github.gclaussn.ssg.plugin.SitePluginException;

/**
 * Installs the specified node packages, using a specific NPM registry.
 */
public class InstallAction implements SitePluginAction {

  protected SiteConsole console;

  /** NPM registry URL to use. */
  @SiteProperty(name = "ssg.install.registryUrl", defaultValue = NodePackageManager.DEFAULT_REGISTRY_URL)
  protected String registryUrl;

  private void createDirectory(Path path) {
    try {
      FileUtils.forceMkdir(path.toFile());
    } catch (IOException e) {
      throw new SitePluginException(String.format("Directory '%s' could not be created", path), e);
    }
  }

  private void deleteDirectory(Path path) {
    if (!Files.exists(path)) {
      return;
    }

    try {
      FileUtils.deleteDirectory(path.toFile());
    } catch (IOException e) {
      throw new SitePluginException(String.format("Directory '%s' could not be deleted", path), e);
    }
  }

  @Override
  public void execute(Site site) {
    try (NodePackageManager nodePackageManager = NodePackageManager.of(registryUrl)) {
      execute(site, nodePackageManager);
    }
  }

  protected void execute(Site site, NodePackageManager nodePackageManager) {
    Optional<NodePackageSpec> spec = site.getNodePackageSpec();
    if (spec.isEmpty()) {
      throw new SitePluginException("No node packages specified");
    }

    List<NodePackage> nodePackages = spec.get().getPackages();

    Path path = site.getPath().resolve(NodePackageManager.NODE_MODULES);
    Path downloadPath = path.resolve(".dl");

    // delete and create node_modules directory
    deleteDirectory(path);
    createDirectory(path);
    createDirectory(downloadPath);

    console.log("Installing...");
    for (NodePackage nodePackage : nodePackages) {
      console.log(nodePackage.toString());

      // check if package exists
      NodePackageInfo nodePackageInfo;
      try {
        nodePackageInfo = nodePackageManager.getPackage(nodePackage);
      } catch (NotFoundException e) {
        throw new SitePluginException(String.format("Node.js package '%s' could not be found", nodePackage), e);
      } catch (RuntimeException e) {
        throw new SitePluginException(String.format("Node.js package '%s' could not be retrieved", nodePackage), e);
      }

      Path tarball = downloadPath.resolve(nodePackageInfo.getFileName());

      // download package
      try (InputStream in = nodePackageManager.download(nodePackage)) {
        Files.copy(in, tarball);
      } catch (IOException e) {
        throw new SitePluginException(String.format("Node.js package '%s' could not be downloaded", nodePackage), e);
      }

      // verify checksum
      verifyChecksum(nodePackageInfo, tarball);

      // create directory for package under node_modules
      Path targetPath = path.resolve(nodePackage.getName());
      createDirectory(targetPath);

      // uncompress package
      uncompressTarball(tarball, targetPath);
    }

    // delete download directory
    deleteDirectory(downloadPath);
  }

  protected void uncompressTarball(Path tarball, Path targetPath) {
    TarArchiveInputStream in = null;
    try {
      in = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(tarball.toFile()))));

      TarArchiveEntry entry;
      while ((entry = in.getNextTarEntry()) != null) {
        int index = entry.getName().indexOf('/');
        if (index == -1) {
          // tarball entry has unexpected name
          // the name should always start with "package/"
          continue;
        }

        // get name without starting "package/"
        String fileName = entry.getName().substring(index + 1);

        Path filePath = targetPath.resolve(fileName);

        // create parent directories
        createDirectory(filePath.getParent());

        // copy entry to file system
        Files.copy(in, filePath);
      }
    } catch (IOException e) {
      throw new SitePluginException(String.format("Tarball '%s' could not be uncompressed", tarball), e);
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  protected void verifyChecksum(NodePackageInfo nodePackageInfo, Path tarball) {
    String a = nodePackageInfo.getChecksum();

    String b;
    try (InputStream in = Files.newInputStream(tarball)) {
      b = DigestUtils.sha1Hex(in);
    } catch (IOException e) {
      throw new SitePluginException(String.format("Tarball '%s': Failed to calculate checksum", tarball), e);
    }

    if (!Objects.equals(a, b)) {
      throw new SitePluginException(String.format("Tarball '%s': Failed to verify checksum (%s != %s)", tarball, a, b));
    }
  }
}
