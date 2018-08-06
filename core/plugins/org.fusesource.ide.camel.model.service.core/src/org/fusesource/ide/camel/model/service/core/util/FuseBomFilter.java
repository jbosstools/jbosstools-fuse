/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.util;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.maven.model.Dependency;

public class FuseBomFilter implements Predicate<Dependency> {
	
	private static final String GROUP_ID_BOM_71 = "org.jboss.redhat-fuse";
	
	private static Set<Dependency> possibleBoms = new HashSet<>();
	public static final Dependency BOM_FUSE_6;
	public static final Dependency BOM_FUSE_7_WILDFLY;
	public static final Dependency BOM_FUSE_7;
	public static final Dependency BOM_FUSE_FIS;
	public static final Dependency BOM_FUSE_71_WILDFLY;
	public static final Dependency BOM_FUSE_71_KARAF;
	public static final Dependency BOM_FUSE_71_SPRINGBOOT;
	
	static {
		BOM_FUSE_6 = initPossibleBomWith("org.jboss.fuse.bom", "jboss-fuse-parent");
		BOM_FUSE_7_WILDFLY = initPossibleBomWith("org.wildfly.camel", "wildfly-camel-bom");
		BOM_FUSE_7 = initPossibleBomWith("org.jboss.fuse", "jboss-fuse-parent");
		BOM_FUSE_FIS = initPossibleBomWith("io.fabric8", "fabric8-project-bom-camel-spring-boot");
		BOM_FUSE_71_WILDFLY = initPossibleBomWith(GROUP_ID_BOM_71, "fuse-eap-bom");
		BOM_FUSE_71_KARAF = initPossibleBomWith(GROUP_ID_BOM_71, "fuse-karaf-bom");
		BOM_FUSE_71_SPRINGBOOT = initPossibleBomWith(GROUP_ID_BOM_71, "fuse-springboot-bom");
	}
	
	private static Dependency initPossibleBomWith(String groupId, String artifactId) {
		Dependency bom = new Dependency();
		bom.setGroupId(groupId);
		bom.setArtifactId(artifactId);
		bom.setType("pom");
		possibleBoms.add(bom);
		return bom;
	}
	

	@Override
	public boolean test(Dependency dependency) {
		return possibleBoms.stream().anyMatch(
				possibleBom -> 
				possibleBom.getArtifactId().equals(dependency.getArtifactId())
				&& possibleBom.getGroupId().equals(dependency.getGroupId()));
	}
	
}