package org.jboss.tools.fuse.reddeer.component;

public class SNMP implements CamelComponent {

	@Override
	public String getPaletteEntry() {
		return "SNMP";
	}

	@Override
	public String getLabel() {
		return "snmp:host:port";
	}

	@Override
	public String getTooltip() {
		return null;
	}

}
