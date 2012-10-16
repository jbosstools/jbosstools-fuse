package org.fusesource.ide.fabric;

public class FabricNotConnectedException extends RuntimeException {

	private static final long serialVersionUID = 2064085104231391228L;

	public FabricNotConnectedException(FabricConnector fabricConnector,
			Exception e) {
		super("Could not connect to Fabric " + fabricConnector.getUrl() + " due to: " + e, e);
	}

}
