package org.fusesource.ide.buildtools

import java.net.{PasswordAuthentication, Authenticator}
import java.util.Properties
import java.io.{FileInputStream, File}
import org.sonatype.aether.repository.Authentication
import org.fusesource.insight.maven.aether.Authentications

/**
 * Downloads the latest XSDs and archetypes into the IDE build
 */
object UpdateReleases {

  val fuseArchetypeVersion = System.getProperty("fabric-version", "7.2.0.redhat-010")
  val activemqVersion = System.getProperty("activemq-version", "5.7.0.redhat-010")
  val camelVersion = System.getProperty("camel-version", "2.10.0.redhat-010")

  val fuseVersion = "redhat-" + fuseArchetypeVersion.split("-").last

  val releaseRepo = if (true)
    "nexus/content/groups/ea"
    //"nexus/content/groups/m2-release-proxy"
  else
    "nexus/content/repositories/releases"

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

