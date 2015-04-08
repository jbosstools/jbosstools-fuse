/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.buildtools;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import io.fabric8.insight.maven.aether.Authentications;
import org.sonatype.aether.repository.Authentication;

/**
 * Downloads the latest XSDs and archetypes into the IDE build
 */
public class UpdateReleases {

    public static String fuseArchetypeVersion = System.getProperty("fabric.version", "7.2.0.redhat-060");
    public static String activemqVersion = System.getProperty("activemq.version", "5.9.0.redhat-610379");
    public static String camelVersion = System.getProperty("camel.version", "2.12.0.redhat-610379");

    public static String fuseVersion = "redhat-" + fuseArchetypeVersion.split("-")[fuseArchetypeVersion.split("-").length - 1];

    public static String releaseRepo = "nexus/content/groups/fs-public";
    public static String eaRepo = "nexus/content/groups/ea";

    public static Authenticator fuseAuthenticator = new FuseRepoAuthenticator();

    public static class FuseRepoAuthenticator extends Authenticator {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            Authentication authentication = Authentications.getFuseRepoAuthentication();
            String username = authentication.getUsername();
            String password = authentication.getPassword();
            return new PasswordAuthentication(username, password.toCharArray());
        }
    }

}
