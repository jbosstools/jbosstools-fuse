package org.fusesource.ide.sap.ui.editor.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.tb.IImageDecorator;
import org.fusesource.ide.camel.editor.features.create.ext.CreateEndpointFigureFeature;
import org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry;
import org.fusesource.ide.camel.editor.provider.ext.PaletteCategoryItemProvider.CATEGORY_TYPE;
import org.fusesource.ide.camel.model.connectors.ConnectorDependency;
import org.fusesource.ide.camel.model.Endpoint;

public class SapPaletteEntry implements ICustomPaletteEntry {

	public SapPaletteEntry() {
	}

	@Override
	public String getPaletteCategory() {
		return CATEGORY_TYPE.COMPONENTS.name();
	}

	@Override
	public ICreateFeature newCreateFeature(IFeatureProvider fp) {
		return new CreateEndpointFigureFeature(fp, "SAP", "Creates an SAP connector endpoint...", new Endpoint("sap-srfc-destination:fuse-sap:FLCUSTGETLIST"));
	}

	@Override
	public IImageDecorator getImageDecorator(Object object) {
		return null;
	}

	@Override
	public String getTypeName() {
		return "SAP";
	}

	@Override
	public boolean supports(Class type) {
		return false;
	}

	@Override
	public List<ConnectorDependency> getRequiredCapabilities(Object object) {
        List<ConnectorDependency> deps = new ArrayList<ConnectorDependency>();
        ConnectorDependency dep = new ConnectorDependency();
        dep.setGroupId("org.fusesource");
        dep.setArtifactId("camel-sap");
        dep.setVersion("6.2.0");
        deps.add(dep);
        return deps;
	}

}
