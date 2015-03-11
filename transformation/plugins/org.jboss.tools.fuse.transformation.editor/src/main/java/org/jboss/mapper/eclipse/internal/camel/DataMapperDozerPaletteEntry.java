/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.mapper.eclipse.internal.camel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.dataformat.DataFormatsDefinition;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.IImageDecorator;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.editor.features.create.ext.CreateEndpointFigureFeature;
import org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry;
import org.fusesource.ide.camel.editor.provider.ext.PaletteCategoryItemProvider.CATEGORY_TYPE;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.camel.model.connectors.ConnectorDependency;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.RecordingCommand;

/**
 * @author bfitzpat
 */
@SuppressWarnings("restriction")
public class DataMapperDozerPaletteEntry implements ICustomPaletteEntry {


    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.provider.ICustomPaletteEntry#newCreateFeature(org.eclipse.graphiti.features.IFeatureProvider)
     */
    @Override
    public ICreateFeature newCreateFeature(IFeatureProvider fp) {
    	// questions
    	// - how much do we need to create here? 
    	// - is this really a component or a defined endpoint?
    	// - fire up wizard, which creates the bits we need
    	// --- do we then have a compound feature that adds each component feature in turn?
//
//        <endpoint uri="transform:xml2json?sourceModel=abcorder.ABCOrder&amp;targetModel=xyzorderschema.XyzOrderSchema&amp;marshalId=transform-json&amp;unmarshalId=abcorder" id="xml2json"/>
//        <dataFormats>
//            <jaxb contextPath="abcorder" id="abcorder"/>
//            <json library="Jackson" id="transform-json"/>
//        </dataFormats>
//	      <route>
//            <to ref="xml2json"/>
//        </route>
    	    	
        return new DataMapperEndpointFigureFeature(fp, 
        		"Data Mapper", 
        		"Creates a Data Mapper endpoint...");
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.provider.ICustomPaletteEntry#getImageDecorator(java.lang.Object)
     */
    @Override
    public IImageDecorator getImageDecorator(Object object) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.provider.ICustomPaletteEntry#getTypeName()
     */
    @Override
    public String getTypeName() {
        return "DataMapper";
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.provider.ICustomPaletteEntry#supports(java.lang.Class)
     */
    @SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class type) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.provider.ICustomPaletteEntry#getRequiredCapabilities(java.lang.Object)
     */
    @Override
    public List<ConnectorDependency> getRequiredCapabilities(Object object) {
        List<ConnectorDependency> deps = new ArrayList<ConnectorDependency>();
        ConnectorDependency dep = new ConnectorDependency();
        dep.setGroupId("org.apache.camel");
        dep.setArtifactId("camel-dozer");
        dep.setVersion("2.15-SNAPSHOT");
        deps.add(dep);
        return deps;
    }
    
    class DataMapperEndpointFigureFeature extends CreateEndpointFigureFeature {

		public DataMapperEndpointFigureFeature(IFeatureProvider fp,
				String name, String description) {
			super(fp, name, description, null);
		}

		@Override
		public Object[] create(ICreateContext context) {
			
			AbstractNode node = null;
            RiderDesignEditor camelEditor = Activator.getDiagramEditor();
			RouteSupport selectedRoute = camelEditor.getSelectedRoute();
			RouteContainer routeContainer = 
			        camelEditor.getMultiPageEditor().getDesignEditorData().model;
            org.apache.camel.spring.CamelContextFactoryBean newContext = 
                    routeContainer.getModel().getContextElement();

//			// fire up the wizard, get the URI
//	    	NewTransformationWizard wizard = new NewTransformationWizard();
//	    	WizardDialog dialog = new WizardDialog(
//	    			Display.getCurrent().getActiveShell(), wizard);
//	    	int status = dialog.open();
//	    	if (status == IStatus.OK) {
//	    		node = createNode(newContext);
//	    	} else {
//	    		return new Object[]{};
//	    	}
            
            // for now just hardwiring this
            node = createNode(newContext);
			
			if (selectedRoute != null) {
				selectedRoute.addChild(node);
			} else {
				Activator.getLogger().warning("Warning! Could not find currently selectedNode, so can't associate this node with the route!: " + node);
			}

			// do the add
			PictogramElement pe = addGraphicalRepresentation(context, node);

			getFeatureProvider().link(pe, node);
			
			// activate direct editing after object creation
			getFeatureProvider().getDirectEditingInfo().setActive(true);
			
			// return newly created business object(s)
			return new Object[] { node };
		}

		private boolean addDataFormat(org.apache.camel.spring.CamelContextFactoryBean context,
		        String defId, String contextPath, String library) {
            DataFormatsDefinition defs = context.getDataFormats();
            boolean foundIt = false;
            if (defs != null && defs.getDataFormats().size() > 0) {
                List<DataFormatDefinition> dataFormatsList = defs.getDataFormats();
                for (Iterator<DataFormatDefinition> iterator = dataFormatsList.iterator(); 
                        iterator.hasNext();) {
                    DataFormatDefinition defn = iterator.next();
                    if (defn.getId().equals(defId)) {
                        foundIt = true;
                        break;
                    }
                }
            }
            if (!foundIt) {
                // add it
                return true;
            }
            return false;
		}
		
		protected AbstractNode createNode( org.apache.camel.spring.CamelContextFactoryBean context) {
		    
		    // Add data formats
		    // <jaxb contextPath="abcorder" id="abcorder"/>
		    // <json library="Jackson" id="transform-json"/>
		    boolean added = addDataFormat(context, "abcorder", "abcorder", null);
		    System.out.println("Added abcorder format: " + added);
            added = addDataFormat(context, "transform-json", null, "Jackson");
            System.out.println("Added transform-json format: " + added);
		    
            final RiderDesignEditor camelEditor = Activator.getDiagramEditor();
            TransactionalEditingDomain domain = camelEditor.getEditingDomain();
            if (domain != null) {
                domain.getCommandStack().execute(new RecordingCommand(domain) {
                    protected void doExecute() {
                        RouteContainer routeContainer = 
                                camelEditor.getMultiPageEditor().getDesignEditorData().model;
                        
                        // Add endpoint we can reference in the route
                        Endpoint extendpoint = 
                                new Endpoint("transform:xml2json?sourceModel=abcorder.ABCOrder&amp;targetModel=xyzorderschema.XyzOrderSchema&amp;marshalId=transform-json&amp;unmarshalId=abcorder");
                        extendpoint.setId("xml2json");

                        routeContainer.addChild(extendpoint);
                        System.out.println("Adding out-of-route endpoint");
                    }
                });
            }
		    
		    // Add endpoint pointing to the external one
		    Endpoint endpoint = new Endpoint("ref:xml2json");
		    System.out.println("Adding actual endpoint");
		    
		    // now return that
			return endpoint;
		}
    	
    }

	@Override
	public String getPaletteCategory() {
        return CATEGORY_TYPE.TRANSFORMATION.name();
	}
}
