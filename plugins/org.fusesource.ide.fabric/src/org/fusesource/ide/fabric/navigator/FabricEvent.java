package org.fusesource.ide.fabric.navigator;

import java.util.EventObject;

public class FabricEvent extends EventObject  {

	private static final long serialVersionUID = -7690212507338650848L;

	public FabricEvent(Fabric fabric) {
		super(fabric);
	}

	public Fabric getFabric() {
		return (Fabric) getSource();
	}

}
