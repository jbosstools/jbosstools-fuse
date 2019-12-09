/*******************************************************************************
* Copyright (c) 2015 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at https://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.imports.sap;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "feature")
@XmlAccessorType(XmlAccessType.FIELD)
public class Feature {
	
	@XmlRootElement(name = "description")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Description {
		
		@XmlAttribute(name = "url")
		private String url;
		
		@XmlValue
		private String content;
		
		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

	}

	@XmlRootElement(name = "copyright")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Copyright {
		
		@XmlAttribute(name = "url")
		private String url;
		
		@XmlValue
		private String content;
		
		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

	}

	@XmlRootElement(name = "license")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class License {
		
		@XmlAttribute(name = "url")
		private String url;
		
		@XmlValue
		private String content;
		
		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

	}

	@XmlRootElement(name = "url")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Url {
		
		@XmlRootElement(name = "update")
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class Update {

			@XmlAttribute(name = "url")
			private String url;
		
			@XmlAttribute(name = "label")
			private String label;

			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}

			public String getLabel() {
				return label;
			}

			public void setLabel(String label) {
				this.label = label;
			}

		}

		@XmlRootElement(name = "discovery")
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class Discovery {

			@XmlAttribute(name = "url")
			private String url;
		
			@XmlAttribute(name = "label")
			private String label;

			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}

			public String getLabel() {
				return label;
			}

			public void setLabel(String label) {
				this.label = label;
			}

	}
		
		@XmlElement(name = "update")
		private Update update;

		@XmlElement(name = "discovery")
		private List<Discovery> discoveries;

		public Update getUpdate() {
			return update;
		}

		public void setUpdate(Update update) {
			this.update = update;
		}

		public List<Discovery> getDiscoveries() {
			return discoveries;
		}

		public void setDiscoveries(List<Discovery> discoveries) {
			this.discoveries = discoveries;
		}

	}
	
	@XmlRootElement(name = "includes")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Includes {
		
		@XmlAttribute(name = "id")
		private static String id;
		
		@XmlAttribute(name = "version")
		private static String version;
		
		@XmlAttribute(name = "name")
		private static String name;
		
		@XmlAttribute(name = "optional")
		private static boolean optional;
		
		@XmlAttribute(name = "os")
		private static String os;
		
		@XmlAttribute(name = "arch")
		private static String arch;
		
		@XmlAttribute(name = "ws")
		private static String ws;
		
		@XmlAttribute(name = "nl")
		private static String nl;

		public static String getId() {
			return id;
		}

		public static void setId(String id) {
			Includes.id = id;
		}

		public static String getVersion() {
			return version;
		}

		public static void setVersion(String version) {
			Includes.version = version;
		}

		public static String getName() {
			return name;
		}

		public static void setName(String name) {
			Includes.name = name;
		}

		public static boolean isOptional() {
			return optional;
		}

		public static void setOptional(boolean optional) {
			Includes.optional = optional;
		}

		public static String getOs() {
			return os;
		}

		public static void setOs(String os) {
			Includes.os = os;
		}

		public static String getArch() {
			return arch;
		}

		public static void setArch(String arch) {
			Includes.arch = arch;
		}

		public static String getWs() {
			return ws;
		}

		public static void setWs(String ws) {
			Includes.ws = ws;
		}

		public static String getNl() {
			return nl;
		}

		public static void setNl(String nl) {
			Includes.nl = nl;
		}

	}

	@XmlRootElement(name = "requires")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Requires {
		
		@XmlElement(name = "import")
		private List<Import> imports;

		public List<Import> getImports() {
			return imports;
		}

		public void setImports(List<Import> imports) {
			this.imports = imports;
		}
		
	}
	
	@XmlRootElement(name = "import")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Import {
		
		@XmlEnum(String.class)
		public static enum Match {
			perfect, equivalent, compatible, greaterOrEqual;
		}
		
		@XmlAttribute(name = "plugin")
		private String plugin;
		
		@XmlAttribute(name = "feature")
		private String feature;
		
		@XmlAttribute(name = "version")
		private String version;
		
		@XmlAttribute(name = "match")
		private Match match = Match.compatible;
		
		@XmlAttribute(name = "patch")
		private boolean patch;

		public String getPlugin() {
			return plugin;
		}

		public void setPlugin(String plugin) {
			this.plugin = plugin;
		}

		public String getFeature() {
			return feature;
		}

		public void setFeature(String feature) {
			this.feature = feature;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public Match getMatch() {
			return match;
		}

		public void setMatch(Match match) {
			this.match = match;
		}

		public boolean isPatch() {
			return patch;
		}

		public void setPatch(boolean patch) {
			this.patch = patch;
		}
		
	}
	
	@XmlRootElement(name = "plugin")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Plugin {

		@XmlAttribute(name = "id")
		private String id;
	
		@XmlAttribute(name = "version")
		private String version;

		@XmlAttribute(name = "fragment")
		private boolean fragment;
		
		@XmlAttribute(name = "os")
		private String os;
		
		@XmlAttribute(name = "arch")
		private String arch;
		
		@XmlAttribute(name = "ws")
		private String ws;

		@XmlAttribute(name = "nl")
		private String nl;

		@XmlAttribute(name = "download-size")
		private String downloadSize;

		@XmlAttribute(name = "install-size")
		private String installSize;

		@XmlAttribute(name = "unpack")
		private boolean unpack = true;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public boolean isFragment() {
			return fragment;
		}

		public void setFragment(boolean fragment) {
			this.fragment = fragment;
		}

		public String getOs() {
			return os;
		}

		public void setOs(String os) {
			this.os = os;
		}

		public String getArch() {
			return arch;
		}

		public void setArch(String arch) {
			this.arch = arch;
		}

		public String getWs() {
			return ws;
		}

		public void setWs(String ws) {
			this.ws = ws;
		}

		public String getNl() {
			return nl;
		}

		public void setNl(String nl) {
			this.nl = nl;
		}

		public String getDownloadSize() {
			return downloadSize;
		}

		public void setDownloadSize(String downloadSize) {
			this.downloadSize = downloadSize;
		}

		public String getInstallSize() {
			return installSize;
		}

		public void setInstallSize(String installSize) {
			this.installSize = installSize;
		}

		public boolean isUnpack() {
			return unpack;
		}

		public void setUnpack(boolean unpack) {
			this.unpack = unpack;
		}
		
	}

	@XmlAttribute(name = "id")
	private String id;
	
	@XmlAttribute(name = "label")
	private String label;
	
	@XmlAttribute(name = "version")
	private String version;
	
	@XmlAttribute(name = "provider-name")
	private String providerName;
	
	@XmlAttribute(name = "os")
	private String os;
	
	@XmlAttribute(name = "arch")
	private String arch;
	
	@XmlAttribute(name = "ws")
	private String ws;
	
	@XmlAttribute(name = "nl")
	private String nl;
	
	@XmlElement(name = "description")
	private Description description;
	
	@XmlElement(name = "copyright")
	private Copyright copyright;
	
	@XmlElement(name = "license")
	private License license;
	
	@XmlElement(name = "url")
	private Url url;
	
	@XmlElement(name = "includes")
	private List<Includes> includes;
	
	@XmlElement(name = "requires")
	private Requires requires;
	
	@XmlElement(name = "plugin")
	private List<Plugin> plugins;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getArch() {
		return arch;
	}

	public void setArch(String arch) {
		this.arch = arch;
	}

	public String getWs() {
		return ws;
	}

	public void setWs(String ws) {
		this.ws = ws;
	}

	public String getNl() {
		return nl;
	}

	public void setNl(String nl) {
		this.nl = nl;
	}

	public Description getDescription() {
		return description;
	}

	public void setDescription(Description description) {
		this.description = description;
	}
	
	public Copyright getCopyright() {
		return copyright;
	}

	public void setCopyright(Copyright copyright) {
		this.copyright = copyright;
	}

	public License getLicense() {
		return license;
	}

	public void setLicense(License license) {
		this.license = license;
	}

	public Url getUrl() {
		return url;
	}

	public void setUrl(Url url) {
		this.url = url;
	}

	public List<Includes> getIncludes() {
		return includes;
	}

	public void setIncludes(List<Includes> includes) {
		this.includes = includes;
	}

	public Requires getRequires() {
		return requires;
	}

	public void setRequires(Requires requires) {
		this.requires = requires;
	}

	public List<Plugin> getPlugins() {
		return plugins;
	}

	public void setPlugins(List<Plugin> plugin) {
		this.plugins = plugin;
	}

}
