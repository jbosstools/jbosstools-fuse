package org.fusesource.ide.buildtools

import java.net.{PasswordAuthentication, Authenticator}
import java.util.Properties
import java.io.{FileInputStream, File}
import org.sonatype.aether.repository.Authentication
import io.fabric8.insight.maven.aether.Authentications

/**
 * Downloads the latest XSDs and archetypes into the IDE build
 */
object UpdateReleases {

  val fuseArchetypeVersion = System.getProperty("fabric-version", "7.2.0.redhat-060")
  val activemqVersion = System.getProperty("activemq-version", "5.9.0.redhat-610379")
  val camelVersion = System.getProperty("camel-version", "2.12.0.redhat-610379")

  val fuseVersion = "redhat-" + fuseArchetypeVersion.split("-").last

  val releaseRepo = if (true)
    "nexus/content/groups/fs-public"
  else
    "nexus/content/groups/ea"

  val fuseAuthenticator = new FuseRepoAuthenticator()
}

class FuseRepoAuthenticator extends Authenticator {
  override def getPasswordAuthentication() : PasswordAuthentication = {
    val authentication = Authentications.getFuseRepoAuthentication()
    val username = authentication.getUsername
    val password = authentication.getPassword()
    return new PasswordAuthentication(username, password.toCharArray());
  }
}

