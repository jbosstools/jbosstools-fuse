/**
 *  Copyright 2005-2014 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package org.fusesource.ide.buildtools;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import io.fabric8.insight.maven.aether.Authentications;
import org.sonatype.aether.repository.Authentication;

/**
 * Downloads the latest XSDs and archetypes into the IDE build
 */
public class UpdateReleases {

    public static String fuseArchetypeVersion = System.getProperty("fabric-version", "7.2.0.redhat-060");
    public static String activemqVersion = System.getProperty("activemq-version", "5.9.0.redhat-610379");
    public static String camelVersion = System.getProperty("camel-version", "2.12.0.redhat-610379");

    public static String fuseVersion = "redhat-" + fuseArchetypeVersion.split("-")[fuseArchetypeVersion.split("-").length - 1];

    public static String releaseRepo = "nexus/content/groups/fs-public" /*"nexus/content/groups/ea"*/;

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
