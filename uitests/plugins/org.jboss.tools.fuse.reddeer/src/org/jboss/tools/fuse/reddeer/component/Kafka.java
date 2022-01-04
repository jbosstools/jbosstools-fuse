package org.jboss.tools.fuse.reddeer.component;

public class Kafka implements CamelComponent {

	@Override
	public String getPaletteEntry() {
		return "Kafka";
	}

	@Override
	public String getLabel() {
		return "kafka:topic";
	}

	@Override
	public String getTooltip() {
		return "Kafka Component";
	}

}
