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

package org.fusesource.ide.camel.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.camel.ExchangePattern;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ChoiceDefinition;
import org.apache.camel.model.DescriptionDefinition;
import org.apache.camel.model.ExpressionNode;
import org.apache.camel.model.LoadBalanceDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.OtherwiseDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.SetHeaderDefinition;
import org.apache.camel.model.ToDefinition;
import org.apache.camel.model.WhenDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.model.language.XPathExpression;
import org.apache.camel.model.loadbalancer.CustomLoadBalancerDefinition;
import org.apache.camel.model.loadbalancer.FailoverLoadBalancerDefinition;
import org.apache.camel.model.loadbalancer.RandomLoadBalancerDefinition;
import org.apache.camel.model.loadbalancer.RoundRobinLoadBalancerDefinition;
import org.apache.camel.model.loadbalancer.StickyLoadBalancerDefinition;
import org.apache.camel.model.loadbalancer.TopicLoadBalancerDefinition;
import org.apache.camel.model.loadbalancer.WeightedLoadBalancerDefinition;
import org.apache.camel.spi.NodeIdFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.fusesource.ide.camel.model.generated.Messages;
import org.fusesource.ide.camel.model.generated.NodeFactory;
import org.fusesource.ide.camel.model.generated.Tooltips;
import org.fusesource.ide.camel.model.generated.UniversalEIPNode;
import org.fusesource.ide.camel.model.generated.UniversalEIPUtility;
import org.fusesource.ide.camel.model.util.Expressions;
import org.fusesource.ide.commons.camel.tools.CamelModelUtils;
import org.fusesource.ide.foundation.core.util.Filter;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;


/**
 * @author lhein
 */
public abstract class AbstractNode implements IPropertySource, IAdaptable {

	protected static final boolean lazyCreateIds = false;
	protected static final boolean useCamelIds = true;

	public static final String PROPERTY_LAYOUT_NODE = "AbstractNode.Layout";
	public static final String SOURCE_CONNECTIONS = "AbstractNode.SourceConn";
	public static final String TARGET_CONNECTIONS = "AbstractNode.TargetConn";
	public static final String PROPERTY_ID = "AbstractNode.Id";
	public static final String PROPERTY_DESCRIPTION = "AbstractNode.Description";
	public static final String PROPERTY_INHERITERRORHANDLER = "AbstractNode.InheritErrorHandler";

	private static final String ICON = "generic.png";

	public static final Rectangle DEFAULT_LAYOUT = new Rectangle(0, 0, 140, 80);
	protected static final ICellEditorValidator DEFAULT_STRING_VALIDATOR = new ICellEditorValidator() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ICellEditorValidator#isValid(java.lang.
		 * Object)
		 */
		@Override
		public String isValid(Object value) {
			String val = (String) value;
			return val != null && val.trim().length() > 0 ? null : Messages.invalidValidatorValueLabel;
		}
	};

	private static transient NodeIdFactory nodeIdFactory;

	private String name;
	private String id;
	private String description;
	private Rectangle layout;
	private List<Flow> sourceConnections;
	private List<Flow> targetConnections;
	private RouteContainer parent;
	private transient Map<String, PropertyDescriptor> descriptors;
	private transient PropertyChangeSupport listeners;
	private boolean disableListeners;
	private Image image;
	private Image smallImage;
	private Boolean inheritErrorHandler;

	public AbstractNode(RouteContainer parent) {
		this(parent, false);
	}
	public AbstractNode(RouteContainer parent, boolean skipCustomProperties) {
		this(skipCustomProperties);
		this.parent = parent;
		if (parent != null) {
			parent.addChild(this);
		}
	}
	
	/**
	 * default constructor
	 */
	public AbstractNode() {
		this(true);
		addCustomProperties();
	}

	/**
	 * This method is basically only here to allow the delaying of adding custom properties
	 * until after the subclass's constructor has finished
	 * 
	 * @param skipAddCustomProperties
	 */
	protected AbstractNode(boolean skipAddCustomProperties) {
		this.sourceConnections = new ArrayList<Flow>();
		this.targetConnections = new ArrayList<Flow>();
		this.listeners = new PropertyChangeSupport(this);
		this.layout = DEFAULT_LAYOUT;

		this.descriptors = new HashMap<String, PropertyDescriptor>();
		this.descriptors.put(PROPERTY_ID, new TextPropertyDescriptor(PROPERTY_ID, Messages.propertyLabelId));
		this.descriptors.get(PROPERTY_ID).setValidator(new ICellEditorValidator() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.jface.viewers.ICellEditorValidator#isValid(java.lang
			 * .Object)
			 */
			@Override
			public String isValid(Object value) {
				String val = (String) value;
				return val != null && val.trim().length() > 0 && AbstractNode.this.isUniqueId(val) ? null
						: Messages.invalidValidatorUniqueValueLabel;
			}
		});
		this.descriptors.put(PROPERTY_DESCRIPTION, new TextPropertyDescriptor(PROPERTY_DESCRIPTION,
				Messages.propertyLabelDescription));
		this.descriptors.get(PROPERTY_DESCRIPTION).setValidator(DEFAULT_STRING_VALIDATOR);

		// now let the subclasses add stuff
		if( !skipAddCustomProperties)
			addCustomProperties();
	}

	/**
	 * Return the typeid of this node, if applicable. 
	 * This should match the parameter name from the eip.xml model, so for example, 
	 * doTry, resequence, etc
	 * @return
	 */
	public String getNodeTypeId() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return false;
	}

	public String getCamelContextId() {
		RouteContainer rc = null;
		if (getParent() == null) {
			rc = (RouteContainer)this;
		} else {
			rc = getParent();
			if (rc.getParent() != null) {
				rc = rc.getParent();
			}	
		}		
		if (rc != null) {
			return rc.getContextId();
		}
		return null;
	}
	
	/**
	 * Clears any EMF / diagram related resources
	 */
	public void clearResources() {
		//		this.eSetDirectResource(null);
		//		//this.eSetResource(null, null);
		//
		//		Resource resource = this.eResource();
		//		if (resource != null) {
		//			throw new IllegalStateException("Should have cleared the resources!!!");
		//		}


		List<AbstractNode> children = getOutputs();
		for (AbstractNode node : children) {
			node.clearResources();
		}
	}

	/*
	 * (non-Javadoc) Method declared on IAdaptable
	 */
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IWorkbenchAdapter.class)
			return new IWorkbenchAdapter() {

			@Override
			public Object[] getChildren(Object o) {
				if (o instanceof AbstractNode) {
					AbstractNode node = (AbstractNode) o;
					node.getChildren().toArray();
				}
				return null;
			}

			@Override
			public ImageDescriptor getImageDescriptor(Object o) {
				if (o instanceof AbstractNode) {
					// TODO no dependency on Activator...
					// return node.getImageDescriptor();
					// return Activator.getDefault().getImage(iconName);
					return null;

				}
				return null;
			}

			@Override
			public String getLabel(Object o) {
				if (o instanceof AbstractNode) {
					AbstractNode node = (AbstractNode) o;
					return node.getDisplayText();
				}
				return null;
			}

			@Override
			public Object getParent(Object o) {
				if (o instanceof AbstractNode) {
					AbstractNode node = (AbstractNode) o;
					return node.getParent();
				}
				return null;
			}

		};
		if (adapter == IPropertySource.class)
			return this;
		if (adapter == AbstractNode.class)
			return this;
		return null;
	}

	/**
	 * checks whether the id is unique among all other nodes
	 * 
	 * @param id
	 * @return
	 */
	protected boolean isUniqueId(String id) {
		// TODO not sure we need this logic now as we use Camel to create unique
		// ids...
		return true;
		/*
		 * Route route = (this instanceof Route) ? (Route)this :
		 * (Route)getParent(); boolean unique = true; for (AbstractNode child :
		 * route.getChildren()) { if (child.getId().equals(id)) { unique =
		 * false; break; } } return unique;
		 */}

	public List<AbstractNode> getChildren() {
		return Collections.EMPTY_LIST;
	}

	/**
	 * returns the name under which the icon is registered in the image registry
	 * of the bundle
	 * 
	 * @return the name / key of the icon
	 */
	public String getIconName() {
		return ICON;
	}


	public String getCategoryName() {
		return "Routing";
	}


	public String getSmallIconName() {
		return UniversalEIPUtility.getSmallIconName(getNodeTypeId());
	}

	public Image getImage() {
		if (image == null) {
			String name = getIconName();
			image = Activator.getDefault().getImage(name);
		}
		return image;
	}

	public Image getSmallImage() {
		if (smallImage == null) {
			String name = getSmallIconName();
			smallImage = Activator.getDefault().getImage(name);
		}
		return smallImage;
	}

	/**
	 * Invoke when content which defines the image kind is changed
	 */
	protected void clearImages() {
		image = null;
		smallImage = null;
	}

	/**
	 * Returns true if this node can have output added so that a new Flow can be
	 * added from this node to something else
	 */
	public boolean canAcceptOutput() {
		Class<?> aClass = getCamelDefinitionClass();
		ProcessorDefinition def = createCamelDefinition();
		return CamelModelUtils.canAcceptOutput(aClass, def);
	}

	/**
	 * By default we load consective steps in a route as children of the each node so that
	 * we load from(a).to(b).to(c) as from -> to(b) -> to(c) with the parent changing.
	 * 
	 * However sometimes we don't want to do that for certain nodes as they can already accept input
	 */
	public boolean isNextSiblingStepAddedAsNodeChild() {
		Class<?> aClass = getCamelDefinitionClass();
		ProcessorDefinition def = createCamelDefinition();
		return CamelModelUtils.isNextSiblingStepAddedAsNodeChild(aClass, def);
	}

	/**
	 * Returns true if this node can have input added so that a new Flow can be
	 * added to this node from something else
	 */
	public boolean canAcceptInput(AbstractNode newInput) {
		if (newInput.getParent() instanceof RouteSupport &&
				newInput instanceof Endpoint) {
			// only if all inputs are rootlevel inputs we allow them
			return true;
		}
		// nodes by default do not support multiple inputs
		return getInputs().size() <= 0;
	}

	public Class<?> getCamelDefinitionClass() {
		return null;
	}

	/**
	 * allows a subclass to call addCustomProperties once its constructor is complete
	 */
	protected void addCustomProperties() {
		addCustomProperties(descriptors);
	}
	
	
	/**
	 * override this method to register class specific property descriptors
	 * 
	 * @param descriptors
	 *            a map of descriptors
	 */
	protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
		// nothing to do here
	}

	/**
	 * Attach a non-null PropertyChangeListener to this object.
	 * 
	 * @param l
	 *            a non-null PropertyChangeListener instance
	 * @throws IllegalArgumentException
	 *             if the parameter is null
	 */
	public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
		if (l == null) {
			throw new IllegalArgumentException();
		}
		listeners.addPropertyChangeListener(l);
	}

	public void firePropertyChange(String prop, Object old, Object newValue) {
		// hack to ensure empty strings will be set as null instead
		// this ensures that the generated source dont output empty attributes / nodes etc
		// as the value should be null instead of an empty string
		if (newValue instanceof String && "".equals(newValue)) {
			// must disable listeners while setting null value
			disableListeners = true;
			setPropertyValue(prop, null);
			disableListeners = false;
		}
		if (!disableListeners && hasPropertyListener(prop)) {
			listeners.firePropertyChange(prop, old, newValue);
		}
	}

	protected void fireChildAdded(String prop, Object child, Object index) {
		if (hasPropertyListener(prop)) {
			listeners.firePropertyChange(prop, index, child);
		}
	}

	protected void fireChildRemoved(String prop, Object child) {
		if (hasPropertyListener(prop)) {
			listeners.firePropertyChange(prop, child, null);
		}
	}

	protected void fireStructureChange(String prop, Object child) {
		if (hasPropertyListener(prop)) {
			listeners.firePropertyChange(prop, null, child);
		}
	}

	protected boolean hasPropertyListener(String prop) {
		return listeners != null && listeners.hasListeners(prop);
	}

	/**
	 * Remove a PropertyChangeListener from this component.
	 * 
	 * @param l
	 *            a PropertyChangeListener instance
	 */
	public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
		if (l != null && listeners != null) {
			listeners.removePropertyChangeListener(l);
		}
	}

	/**
	 * @return the id
	 */
	public synchronized String getId() {
		if (id == null) {
			if (lazyCreateIds) {
				id = getNewID();
			}
		}
		return this.id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		String oldId = this.id;
		this.id = id;
		if (!Objects.equal(this.id, oldId)) {
			firePropertyChange(PROPERTY_ID, oldId, this.id);
		}
	}

	public Boolean getInheritErrorHandler() {
		return inheritErrorHandler;
	}

	public void setInheritErrorHandler(Boolean inheritErrorHandler) {
		Boolean old = this.inheritErrorHandler;
		this.inheritErrorHandler = inheritErrorHandler;
		if (!Objects.equal(inheritErrorHandler, old)) {
			firePropertyChange(PROPERTY_INHERITERRORHANDLER, old, this.inheritErrorHandler);
		}
	}

	/**
	 * creates a random id
	 * 
	 * @return a random id
	 */
	public String getNewID() {
		String answer = null;
		if (useCamelIds) {
			ProcessorDefinition definition = createCamelDefinition();
			if (definition != null) {
				answer = definition.idOrCreate(getNodeIdFactory());
			}
		}
		if (answer == null) {
			answer = UUID.randomUUID().toString();
		}
		return answer;
	}

	protected synchronized static NodeIdFactory getNodeIdFactory() {
		if (nodeIdFactory == null) {
			nodeIdFactory = new DefaultCamelContext().getNodeIdFactory();
		}
		return nodeIdFactory;
	}

	/**
	 * @return the description
	 */
	public synchronized String getDescription() {
		/*
		if (description == null) {
			this.description = toString();
		}
		 */
		return this.description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		String oldDesc = this.description;
		this.description = description;
		if (!Objects.equal(description, oldDesc)) {
			firePropertyChange(PROPERTY_DESCRIPTION, oldDesc, this.description);
		}
	}

	/**
	 * @return the parent
	 */
	public RouteContainer getParent() {
		return this.parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(RouteContainer parent) {
		if (this.parent != null) {
			this.parent.removeChild(this);
		}
		this.parent = parent;
		if (this.parent != null) {
			this.parent.addChild(this);
		}
	}

	/**
	 * @return the layout
	 */
	public Rectangle getLayout() {
		return this.layout;
	}

	/**
	 * @param layout
	 *            the layout to set
	 */
	public void setLayout(Rectangle layout) {
		Rectangle oldLayout = this.layout;
		this.layout = layout;
		firePropertyChange(PROPERTY_LAYOUT_NODE, oldLayout, this.layout);
	}

	/**
	 * Returns the text to display on the shape such as the URI of an endpoint
	 * or ref/method of a bean. This defaults to the ID of the node if none is
	 * available
	 * 
	 * @return the string to be displayed as decoration on the shape figure
	 */
	public final String getDisplayText() {
		return getDisplayText(true);
	}
    protected static String convertCamelCase(String original) {
    	return Strings.convertCamelCase(original); 
    }

    protected static String capitalizeFirstLetter(String input) {
    	return Strings.capitalize(input);
    }
    
	public final String getDisplayText(boolean useID) {

		// honor the PREFER_ID_AS_LABEL preference
		// we initially set it to the value of the contains method
		boolean preferID = false;
		if (useID) {
			preferID = PreferenceManager.getInstance().containsPreference(
					PreferencesConstants.EDITOR_PREFER_ID_AS_LABEL);
			// as second step if the value is there, we use it for the flag
			if (PreferenceManager.getInstance().containsPreference(PreferencesConstants.EDITOR_PREFER_ID_AS_LABEL)) {
				preferID = PreferenceManager.getInstance().loadPreferenceAsBoolean(
						PreferencesConstants.EDITOR_PREFER_ID_AS_LABEL);
			}
		}

		// we only return the id if we are told so by the preference AND the
		// value of the ID is set != null
		if (preferID && getId() != null) {
			return getId();
		}

		if (this instanceof Endpoint) {
			Endpoint node = (Endpoint) this;
			if (node.getUri() != null && node.getUri().trim().length()>0) {
				// uri specified, use it
				return node.getUri();
			}
		} 
		
		String eipType = getNodeTypeId();
		// For some nodes, we just return their node name
		String[] nodeNameOnly = new String[]{
				"choice", "try", "finally","otherwise", "marshal",  "unmarshal" 
		};
		if( Arrays.asList(nodeNameOnly).contains(eipType))
			return eipType;
		
		// Some nodes just need the value of a param
		HashMap<String, String> singlePropertyDisplay = new HashMap<String, String>();
		singlePropertyDisplay.put("bean",  "ref");
		singlePropertyDisplay.put("convertBodyTo",  "type");
		singlePropertyDisplay.put("enrich",  "uri");
		singlePropertyDisplay.put("inOnly",  "uri");
		singlePropertyDisplay.put("inOut",  "uri");
		singlePropertyDisplay.put("interceptSendToEndpoint",  "uri");
		singlePropertyDisplay.put("log",  "logName");
		singlePropertyDisplay.put("onException",  "exception");
		singlePropertyDisplay.put("pollEnrich",  "uri");
		singlePropertyDisplay.put("removeHeader",  "headerName");
		singlePropertyDisplay.put("removeProperty",  "propertyName");
		singlePropertyDisplay.put("rollback",  "message");
		singlePropertyDisplay.put("sort",  "expression");
		singlePropertyDisplay.put("when",  "expression");
		
		String propertyToCheck = singlePropertyDisplay.get(eipType);
		if( propertyToCheck != null ) {
			Object propVal = getShortPropertyValue(propertyToCheck, Object.class);
			String suffix = null;
			if( propVal instanceof ExpressionDefinition ) {
				suffix = Expressions.getExpressionOrElse(((ExpressionDefinition)propVal));
			} else {
				suffix = Strings.getOrElse(propVal);
			}
			String ret = convertCamelCase(eipType) + " " + suffix;
			return ret;
		}
		
		
		if ("catch".equals(eipType)) {
			List exceptions = getShortPropertyValue("exception", List.class);
			if (exceptions != null && exceptions.size() > 0) {
				return "catch " + exceptions;
			} else {
				return "catch " + Expressions.getExpressionOrElse(getShortPropertyValue("handled", ExpressionDefinition.class));
			}
		} else if ("setExchangePattern".equals(eipType)) {
			ExchangePattern pattern = getShortPropertyValue("handled", ExchangePattern.class);
			if (pattern == null) {
				return "setExchangePattern";
			} else {
				return "set " + pattern;
			}
		} else if ("loadBalance".equals(eipType)) {
			String ref = getShortPropertyValue("ref", String.class);
			if (ref != null) {
				return "custom " + Strings.getOrElse(ref);
			} 
			Object loadType = getShortPropertyValue("loadBalancerType", Object.class);
			if (loadType  != null) {
				if (loadType.getClass().isAssignableFrom(CustomLoadBalancerDefinition.class)) {
					CustomLoadBalancerDefinition custom = (CustomLoadBalancerDefinition) loadType;
					return "custom " + Strings.getOrElse(custom.getRef());
				} else if (loadType.getClass().isAssignableFrom(FailoverLoadBalancerDefinition.class)) {
					return "failover";
				} else if (loadType.getClass().isAssignableFrom(RandomLoadBalancerDefinition.class)) {
					return "random";
				} else if (loadType.getClass().isAssignableFrom(RoundRobinLoadBalancerDefinition.class)) {
					return "round robin";
				} else if (loadType.getClass().isAssignableFrom(StickyLoadBalancerDefinition.class)) {
					return "sticky";
				} else if (loadType.getClass().isAssignableFrom(TopicLoadBalancerDefinition.class)) {
					return "topic";
				} else if (loadType.getClass().isAssignableFrom(WeightedLoadBalancerDefinition.class)) {
					return "weighted";
				}
			} else {
				return "load balance";
			}
		}

		String answer = null;
		@SuppressWarnings("rawtypes")
		ProcessorDefinition camelDef = createCamelDefinition();
		if (camelDef != null) {
			try {
				answer = camelDef.getLabel();
			} catch (Exception e) {
				// ignore errors in Camel
			}
		}
		if (Strings.isBlank(answer)) {
			answer = getId();
		}
		if (Strings.isBlank(answer)) {
			answer = getPatternName();
		}
		return answer;
	}

	/**
	 * Returns the pattern name
	 */
	public String getPatternName() {
		return getClass().getSimpleName();
	}

	/**
	 * Returns the text to display as the tooltip of a shape figure.
	 * 
	 * @return the tooltip to display for the shape figure
	 */
	public String getDisplayToolTip() {
		String eipType = getNodeTypeId();
		if ("when".equals(eipType)) {
			ExpressionDefinition propVal = getShortPropertyValue("expression", ExpressionDefinition.class);
			return "when " + Expressions.getExpressionOrElse(propVal);
		}
		
		
		String answer = Tooltips.tooltip(getPatternName());
		if (answer == null) {
			ProcessorDefinition camelDef = createCamelDefinition();
			if (camelDef != null) {
				if (camelDef instanceof RouteDefinition) {
					RouteDefinition route = (RouteDefinition) camelDef;
					return "Route " + (route.getId() != null ? route.getId() : "");
				} else if (camelDef instanceof ToDefinition && isNotTarget()) {
					// if its the first in the route and its an endpoint, then use From instead of To
					// (notice that createCamelDefinition returns a ToDefinition for all kind of Endpoints)
					return "From " + camelDef.getLabel();
				}
				return camelDef.getShortName() + " " + camelDef.getLabel();
			}
			return getDescription();
		}
		return answer;
	}

	/**
	 * Returns the documentation file name if available or null
	 */
	public String getDocumentationFileName() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s: %s", getClass().getSimpleName(), getDisplayText());
	}

	/**
	 * Returns a list of all the nodes which this node links to
	 */
	public List<AbstractNode> getOutputs() {
		ArrayList<AbstractNode> answer = new ArrayList<AbstractNode>();
		List<Flow> list = getSourceConnections();
		for (Flow flow : list) {
			answer.add(flow.getTarget());
		}
		return answer;
	}

	public List<AbstractNode> getInputs() {
		ArrayList<AbstractNode> answer = new ArrayList<AbstractNode>();
		List<Flow> list = getTargetConnections();
		for (Flow flow : list) {
			answer.add(flow.getSource());
		}
		return answer;
	}

	// TODO is target the right name for, from(this).to(to)
	public void addTargetNode(AbstractNode to) {
		new Flow(this, to);
	}

	/**
	 * Adds the given node as the last step in this tree of steps.
	 * For example for a Choice node then we add this to the last When / Otherwise branches etc.
	 */
	public void addTargetNodeAsLastStep(AbstractNode node) {
		List<AbstractNode> children = getOutputs();
		if (children == null || children.isEmpty()) {
			addTargetNode(node);
		} else {
			for (AbstractNode child : children) {
				child.addTargetNodeAsLastStep(node);
			}
		}
	}


	/**
	 * Add an incoming or outgoing connection to this shape.
	 * 
	 * @param conn
	 *            a non-null connection instance
	 * @throws IllegalArgumentException
	 *             if the connection is null or has not distinct endpoints
	 */
	public void addConnection(Flow conn) {
		AbstractNode target = conn.getTarget();
		if (conn == null || conn.getSource() == target) {
			throw new IllegalArgumentException();
		}

		if (containsFlow(sourceConnections, conn) || containsFlow(targetConnections, conn)) {
			// ignore duplicates
			return;
		}

		if (conn.getSource() == this) {
			Filter<Flow> before = createBeforePredicate(target);
			boolean added = false;
			if (before != null) {
				int idx = 0;
				for (Flow f : sourceConnections) {
					if (before.matches(f)) {
						sourceConnections.add(idx, conn);
						added = true;
						break;
					}
					idx++;
				}
			}
			if (!added) {
				sourceConnections.add(conn);
			}
			firePropertyChange(SOURCE_CONNECTIONS, null, conn);
		} else if (target == this) {
			targetConnections.add(conn);
			firePropertyChange(TARGET_CONNECTIONS, null, conn);
		}
	}

	protected boolean containsFlow(Collection<Flow> coll, Flow flow) {
		for (Flow aFlow : coll) {
			boolean sourceEqual = Objects.equal(aFlow.getSource(), flow.getSource());
			boolean targetEqual = Objects.equal(aFlow.getTarget(), flow.getTarget());
			if (sourceEqual && targetEqual) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates a predicate to decide if a flow for the given target should be added
	 * before a flow
	 */
	protected Filter<Flow> createBeforePredicate(AbstractNode target) {
		String targetType = target.getNodeTypeId();
		String thisType = getNodeTypeId();
		if ("choice".equals(thisType)) {
			// when is first
			if ("when".equals(targetType)) {
				return new Filter<Flow>() {
					@Override
					public boolean matches(Flow f) {
						return "otherwise".equals(f.getTarget().getNodeTypeId()) 
								|| !("when".equals(f.getTarget().getNodeTypeId()));
					}
				};
			} else if ("otherwise".equals(targetType)) {
				return new Filter<Flow>() {
					@Override
					public boolean matches(Flow f) {
						return !("when".equals(f.getTarget().getNodeTypeId()));
					}
				};
			}
		} else if ("try".equals(thisType)) {
			if ("catch".equals(targetType)) {
				// add before finally
				return new Filter<Flow>() {
					@Override
					public boolean matches(Flow f) {
						return "finally".equals(f.getTarget().getNodeTypeId());
					}
				};
			} else if (!("finally".equals(targetType))) {
				// add before catch/finally
				return new Filter<Flow>() {
					@Override
					public boolean matches(Flow f) {
						return "catch".equals(f.getTarget().getNodeTypeId())
								|| "finally".equals(f.getTarget().getNodeTypeId());
					}
				};
			}
		}
		return null;
	}

	public void addConnections(List<Flow> flows) {
		for (Flow flow : flows) {
			addConnection(flow);
		}
	}

	/**
	 * Remove an incoming or outgoing connection from this shape.
	 * 
	 * @param conn
	 *            a non-null connection instance
	 * @throws IllegalArgumentException
	 *             if the parameter is null
	 */
	void removeConnection(Flow conn) {
		if (conn == null) {
			throw new IllegalArgumentException();
		}

		if (conn.getSource() == this) {
			sourceConnections.remove(conn);
			firePropertyChange(SOURCE_CONNECTIONS, null, conn);
		} else if (conn.getTarget() == this) {
			targetConnections.remove(conn);
			firePropertyChange(TARGET_CONNECTIONS, null, conn);
		}
	}

	/**
	 * As this node is being deleted from the diagram we need to remove all the
	 * flows from its neighbours
	 */
	public void delete(List<Flow> deletedFlows) {
		// sourceConnections have source == this
		for (Flow flow : sourceConnections) {
			flow.getTarget().removeConnection(flow);
			deletedFlows.add(flow);
		}
		for (Flow flow : targetConnections) {
			flow.getSource().removeConnection(flow);
			deletedFlows.add(flow);
		}
	}

	/**
	 * Return a List of outgoing Connections.
	 */
	public List<Flow> getSourceConnections() {
		return new ArrayList<Flow>(sourceConnections);
	}

	/**
	 * Return a List of incoming Connections.
	 */
	public List<Flow> getTargetConnections() {
		return new ArrayList<Flow>(targetConnections);
	}

	public List<AbstractNode> getTargetNodes() {
		List<AbstractNode> answer = new ArrayList<AbstractNode>();
		for (Flow connection : targetConnections) {
			answer.add(connection.getTarget());
		}
		return answer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	@Override
	public Object getEditableValue() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return this.descriptors.values().toArray(new IPropertyDescriptor[this.descriptors.size()]);
	}

	public IPropertyDescriptor getPropertyDescriptor(Object id) {
		IPropertyDescriptor[] array = getPropertyDescriptors();
		for (IPropertyDescriptor descriptor : array) {
			if (Objects.equal(id, descriptor.getId())) {
				return descriptor;
			}
		}
		return null;
	}

	/*
	public <T> T getPropertyDefaultValue(Object id) {

	}
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java
	 * .lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object id) {
		if (PROPERTY_ID.equals(id)) {
			return getId();
		} else if (PROPERTY_DESCRIPTION.equals(id)) {
			return getDescription();
		} else if (PROPERTY_INHERITERRORHANDLER.equals(id)) {
			return getInheritErrorHandler();
		}
		return null;
	}

	
	/**
	 * Pass in a short property, such as "uri" as opposed to "Enrich.Uri"
	 * 
	 * The default impl here will return the map value for the id provided. 
	 * Subclasses (Such as {@link UniversalEIPNode} will change "ref" to 
	 * Bean.Ref  instead, since that's the key the values are stored under. 
	 * 
	 * 
	 * @param id
	 * @return
	 */
	public <T> T getShortPropertyValue(String id, Class<T> c) {
		Object ret = getPropertyValue(id);
    	if( ret != null && c.isInstance(ret)) {
    		return c.cast(ret);
    	}
    	return null;
	}
	
	/**
	 * Set the short property (such as uri) by discovering what key it is actually
	 * stored under, and setting that property. 
	 * 
	 * For example, a subclass may choose to change "uri" to "Bean.Uri" in a Bean eip element. 
	 * 
	 * This abstract class's implementation returns the value of 
	 * the exact key with no modifications during lookup. 
	 * 
	 * @param id
	 * @param val
	 */
	public void setShortPropertyValue(String id, Object val) {
		setPropertyValue(id, val);
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang
	 * .Object)
	 */
	@Override
	public boolean isPropertySet(Object id) {
		return descriptors.containsKey(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java
	 * .lang.Object)
	 */
	@Override
	public void resetPropertyValue(Object id) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java
	 * .lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (PROPERTY_ID.equals(id)) {
			setId((String) value);
		} else if (PROPERTY_DESCRIPTION.equals(id)) {
			setDescription((String) value);
		} else if (PROPERTY_INHERITERRORHANDLER.equals(id)) {
			setInheritErrorHandler((Boolean) value);
		}
	}

	/**
	 * Returns true if the given name is a valid property name on this model.
	 */
	public boolean isPropertyName(String name) {
		return name != null && descriptors.containsKey(name);
	}

	/**
	 * Creates a new Camel model definition object
	 */
	@SuppressWarnings("rawtypes")
	public abstract ProcessorDefinition createCamelDefinition();

	protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
		description = processor.getDescriptionText();
		id = processor.getId();
		inheritErrorHandler = null;
		Boolean bool = processor.isInheritErrorHandler();
		if (bool != null && bool.booleanValue()) {
			inheritErrorHandler = true;
		}
	}

	@SuppressWarnings("rawtypes")
	protected void loadChildrenFromCamelDefinition(ProcessorDefinition processor) {
		List<ProcessorDefinition> outputs = getOutputs(processor);
		AbstractNode parent = this;
		for (ProcessorDefinition childProcessor : outputs) {
			AbstractNode node = NodeFactory.createNode(childProcessor, getParent());
			parent.addTargetNode(node);
			if (!parent.isMulticastNode(parent, node) && node.isNextSiblingStepAddedAsNodeChild()) {
				parent = node;
			}
		}
		// Activator.getLogger().debug("Now " + this + " with outputs " + getOutputs() +
		// " from camel outputs " + outputs);
	}

	/**
	 * Returns true if this node is a multicast node - i.e. all immediate children are rendered connecting to this node.
	 */
	protected boolean isMulticastNode(AbstractNode parent, AbstractNode child) {
		String parentType, childType;
		parentType = parent == null ? null : parent.getNodeTypeId();
		childType = child == null ? null : child.getNodeTypeId();
		
		return "multicast".equals(parent) ||
				("choice".equals(parent) && ("when".equals(child) || "otherwise".equals(child))) ||
				("try".equals(parent) && ("catch".equals(child) || "finally".equals(child)));
	}

	/**
	 * Workaround of Camel bug for some reason getOutputs() on a
	 * ChoiceDefinition does not return the children; but it returns the outputs
	 * on the When?
	 */
	protected List<ProcessorDefinition> getOutputs(ProcessorDefinition processor) { //
		if (processor instanceof ChoiceDefinition) {
			ChoiceDefinition choice = (ChoiceDefinition) processor;
			List<ProcessorDefinition> list = new ArrayList<ProcessorDefinition>();
			List<WhenDefinition> whenClauses = choice.getWhenClauses();
			if (whenClauses != null) {
				list.addAll(whenClauses);
			}
			OtherwiseDefinition otherwise = choice.getOtherwise();
			if (otherwise != null) {
				list.add(otherwise);
			}
			return list;
		} else {
			return processor.getOutputs();
		}
	}

	/**
	 * Workaround of Camel bug for some reason addOutput() on a ChoiceDefinition
	 * does not work with When/Otherwise
	 */
	protected void addCamelOutput(ProcessorDefinition processor, ProcessorDefinition toNode) {
		// special for choice
		if (processor instanceof ChoiceDefinition) {
			ChoiceDefinition choice = (ChoiceDefinition) processor;
			if (toNode instanceof WhenDefinition) {
				List<WhenDefinition> defs = choice.getWhenClauses();
				boolean found = false;
				Iterator<WhenDefinition> it = defs.iterator();
				while(it.hasNext() && !found) {
					WhenDefinition t = it.next();
					if( t.getLabel().equals(toNode.getLabel())) {
						// Two nodes have the same 'when'
						found = true;
					}
				}
				if( !found) {
					// avoid duplicates
					choice.getWhenClauses().add((WhenDefinition) toNode);
				}
			} else if (toNode instanceof OtherwiseDefinition) {
				choice.setOtherwise((OtherwiseDefinition) toNode);
			} else {
				// there may be a nested choice so we need to add it on its parent
				ProcessorDefinition grandParent = choice.getParent();
				if (grandParent != null) {
					grandParent.addOutput(toNode);
				} else {
					Activator.getLogger().warning("No parent of " + choice + " so cannot add output " + toNode);
				}
			}
		} else {
			// Work around to avoid NPE in Camel
			if (processor instanceof ExpressionNode) {
				ExpressionNode en = (ExpressionNode) processor;
				if (en.getExpression() == null) {
					en.setExpression(new ExpressionDefinition());
				}
			}
			processor.addOutput(toNode);
		}
	}

	@SuppressWarnings("rawtypes")
	public void savePropertiesToCamelDefinition(ProcessorDefinition processor) {
		if (inheritErrorHandler != null && inheritErrorHandler.booleanValue()) {
			processor.setInheritErrorHandler(inheritErrorHandler);
		} else {
			// zap false values as we do not want them in the generated XML
			// but we should preserve "false" for the load balance EIP, as it may be
			// needed to explicit configured inheritErrorHandler="false" for the failover EIP
			if (processor.getClass().isAssignableFrom(LoadBalanceDefinition.class)) {
				if (inheritErrorHandler != null) {
					processor.setInheritErrorHandler(inheritErrorHandler);
				} else {
					processor.setInheritErrorHandler(null);
				}
			} else {
				processor.setInheritErrorHandler(null);
			}
		}
		if (description != null && description.trim().length() > 0) {
			DescriptionDefinition descriptionDefinition = new DescriptionDefinition();
			descriptionDefinition.setText(description);
			processor.setDescription(descriptionDefinition);
		}
		if (id != null && id.trim().length() > 0) {
			processor.setId(id);
			resetCustomId(processor);
		}
	}

	@SuppressWarnings("rawtypes")
	public void saveChildrenToCamelDefinitions(ProcessorDefinition processor, ArrayList<AbstractNode> processedNodes) {
		// now lets recurse into any children...
		// TODO not sure why we use SourceConnections - shouldn't it be
		// TargetConnections?
		List<Flow> children = getSourceConnections();
		for (Flow flow : children) {
			AbstractNode target = flow.getTarget();
			ProcessorDefinition toNode = target.createCamelDefinition();

			if (!processedNodes.contains(target)) {
				addCamelOutput(processor, toNode);
				processedNodes.add(target);
			}


			ProcessorDefinition outputNode;
			if (target.canAcceptOutput()) {
				outputNode = toNode;
			} else {
				outputNode = processor;
			}

			target.saveChildrenToCamelDefinitions(outputNode, processedNodes);
		}
	}

	/**
	 * Allow values to be transformed before they are turned into JAXB objects, such as to unwrap custom objects like {@link LanguageExpressionBean}
	 * into the correct underlying JAXB model object
	 */
	protected <T> T toXmlPropertyValue(Object id, T value) {
		if (value instanceof LanguageExpressionBean) {
			LanguageExpressionBean lb = (LanguageExpressionBean) value;
			return (T) lb.toXmlExpression();
		}
		if (CamelModelHelper.isPropertyListOFSetHeaders(id) && value instanceof List) {
			List list = (List) value;
			for (Object object : list) {
				Object newValue = object;
				if (object instanceof SetHeaderDefinition) {
					SetHeaderDefinition sh = (SetHeaderDefinition) object;
					ExpressionDefinition expression = sh.getExpression();
					if (expression instanceof LanguageExpressionBean) {
						LanguageExpressionBean lb = (LanguageExpressionBean) expression;
						sh.setExpression(lb.toXmlExpression());
					}
				}
			}
		}
		return value;
	}

	/**
	 * Returns true if this node is not a source of connections (i.e. its a From
	 * node or a node that is not connected to anything
	 */
	public boolean isNotTarget() {
		// TODO really should this be getSourceConnections()??
		return getTargetConnections().isEmpty();
	}

	/**
	 * recalculate layout
	 */
	public void layout() {
		// nothing to do
	}

	/**
	 * Returns all source and target connections
	 */
	public Set<Flow> getAllConnections() {
		Set<Flow> set = new HashSet<Flow>();
		set.addAll(getSourceConnections());
		set.addAll(getTargetConnections());
		return set;
	}

	/**
	 * Returns true if this node can support more output(s).
	 * <p/>
	 * Some nodes can support multiple outputs, where others can only support exactly one output.
	 * 
	 * @return true if this node can support more output(s)
	 */
	public boolean canSupportOutput() {
		// we can only support output if either if node supports outputs
		boolean output = canAcceptOutput();
		if (output) {
			return true;
		}

		// or if node does not support outputs, but there are no existing flows
		for (Flow flow : getAllConnections()) {
			if (flow.getSource() == this) {
				// there is already a flow on this node so we cannot accept more outputs
				return false;
			}
		}

		return true;
	}
	
	/**
	 * checks if the node is the from node and therefore
	 * the first node in my route
	 * 
	 * @return
	 */
	public boolean isFirstNodeInRoute() {
		return getInputs().isEmpty();
	}
	
	/**
	 * checks if the node is an expression node
	 * 
	 * @return
	 */
	public boolean supportsBreakpoint() {
		return !isFirstNodeInRoute() && 				// not working on the From node
				!"when".equals(getNodeTypeId()) && 		// not working for When nodes
				!"otherwise".equals(getNodeTypeId());	// not working for Otherwise nodes
//				Strings.isBlank(getCamelContextId()) == false && // not working if no Camel Context Id is set
//				Strings.isBlank(getId()) == false;		// not working if no ID is set **/
	}

	/**
	 * Returns true if this node can be the source and be connected to the
	 * target in a diagram like <code>this -> target</code>
	 * 
	 * @param target
	 *            the node that this node is trying to be connected to.
	 * @return true if this source can be connected to the given target
	 */
	public boolean canConnectTo(AbstractNode target) {
		if (target == this || target instanceof RouteContainer) {
			return false;
		}
		if (target == null) {
			return true;
		}
		// lets check that we are not already connected to this node
		// either as a source or as a target
		for (Flow flow : getAllConnections()) {
			if (target.equals(flow.getSource()) || target.equals(flow.getTarget())) {
				return false;
			}
		}

		// detect loop between this and target
		if (detectLoops(this, target)) {
			return false;
		}

		// detect loops between this and target inputs
		if (detectInputLoops(this, target)) {
			return false;
		}

		if (!target.canAcceptInput(this)) {
			return false;
		}

		boolean isOtherwise = "otherwise".equals(getNodeTypeId());
		boolean isWhen = "when".equals(getNodeTypeId());
		boolean whenOrOtherwise = isWhen || isOtherwise;
		boolean thisIsChoice = "choice".equals(getNodeTypeId());

		// try / catch / finally is not like choice/when/otherwise as try can
		// connect to anything
		if (thisIsChoice) {
			if (isOtherwise) {
				List<AbstractNode> outputs = getOutputs("otherwise");
				return outputs.size() == 0;
			}
			// you must also be able to connect to anything from choice
			// as it may be a node thats _after_ the content based router
			return true;
		} else if (whenOrOtherwise) {
			if (thisIsChoice) {
				return true;
			} else {
				if (isWhen) {
					// various classes in the model have a when claus, lets find
					// them all
					Class<?> camelClass = getCamelDefinitionClass();
					if (camelClass != null) {
						Field[] fields = camelClass.getDeclaredFields();
						for (Field field : fields) {
							if (WhenDefinition.class.isAssignableFrom(field.getType())) {
								return true;
							}
						}
					}
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Gets the input of the given node (recursive), that matches the criteria(s).
	 * 
	 * @param node  the node.
	 * @param inputMustAcceptOutput whether the input must accept outputs (will recursive until matching this parameter)
	 * @return the input or <tt>null</tt> if no input was found
	 */
	private AbstractNode getMatchedInput(AbstractNode node, boolean inputCanAcceptOutput) {
		if (node == null) {
			return null;
		}

		for (AbstractNode input : node.getInputs()) {
			if (!inputCanAcceptOutput) {
				return input;
			} else if (input.canAcceptOutput()) {
				return input;
			} else {
				// recursive
				AbstractNode child = getMatchedInput(input, inputCanAcceptOutput);
				if (child != null) {
					return child;
				}
			}
		}

		return null;
	}

	/**
	 * crawls all nodes to see if there is some loop
	 * 
	 * @param sourceNode
	 *            the connection source
	 * @param targetNode
	 *            the connection target
	 * @return true if there was a loop detected
	 */
	protected boolean detectLoops(AbstractNode sourceNode, AbstractNode targetNode) {
		List<AbstractNode> nodes = sourceNode.getInputs();
		for (AbstractNode n : nodes) {
			if (n.equals(targetNode)) {
				// the target is already used as source somewhere in route
				return true;
			} else {
				// check if the inputs of that node contain out target
				if (detectLoops(n, targetNode)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Crawl the inputs of the nodes and detect if they will become looped if source and target would become connected.
	 * 
	 * @param sourceNode  the connection source
	 * @param targetNode  the connection target
	 * @return true if a loop detected
	 */
	protected boolean detectInputLoops(AbstractNode sourceNode, AbstractNode targetNode) {
		// we cannot connect if target and this node has same nested parent (that accepts multiple outputs)
		// as it would become a circular connection
		AbstractNode thisParent = getMatchedInput(sourceNode, true);
		AbstractNode targetParent = getMatchedInput(targetNode, true);
		if (thisParent != null && targetParent != null && thisParent.equals(targetParent)) {
			return true;
		}
		// no loop detected
		return false;
	}

	@Deprecated
	public List<AbstractNode> getOutputs(Class<? extends AbstractNode> aClass) {
		List<AbstractNode> list = getOutputs();
		List<AbstractNode> answer = new ArrayList<AbstractNode>();
		for (AbstractNode node : list) {
			if (aClass.isInstance(node)) {
				answer.add(node);
			}
		}
		return answer;
	}

	public List<AbstractNode> getOutputs(String eipType) {
		List<AbstractNode> list = getOutputs();
		List<AbstractNode> answer = new ArrayList<AbstractNode>();
		for (AbstractNode node : list) {
			if( node.getNodeTypeId().equals(eipType)) {
				answer.add(node);
			}
		}
		return answer;
	}

	
	
	/**
	 * Appends all the endpoint URIs used by this node to the given set
	 */
	public void appendEndpointUris(Set<String> uris) {
		List<AbstractNode> outputs = getOutputs();
		for (AbstractNode node : outputs) {
			node.appendEndpointUris(uris);
		}
	}

	protected void appendDescendents(Set<AbstractNode> answer) {
		answer.add(this);
		List<AbstractNode> list = getOutputs();
		for (AbstractNode node : list) {
			node.appendDescendents(answer);
		}
	}

	/**
	 * Removes the connection to the given node
	 */
	public void removeConnection(AbstractNode target) {
		List<Flow> list = getSourceConnections();
		for (Flow flow : list) {
			if (flow.getTarget() == target) {
				flow.disconnect();
				return;
			}
		}
	}

	/**
	 * Detatches this node from the route it contains and any source/target connections
	 */
	public void detach() {
		RouteContainer p = getParent();
		if (p != null) {
			p.removeChild(this);
		}

		disconnect(getSourceConnections());
		disconnect(getTargetConnections());

	}

	private void disconnect(List<Flow> list) {
		for (Flow flow : list) {
			flow.disconnect();
		}
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected boolean isSame(Object a, Object b) {
		// null checks
		if (a == null && b == null) return true;
		if (a == null && b != null) return false;
		if (a != null && b == null) return false;

		if (a instanceof String && b instanceof String) {
			return ((String)a).equals(b);
		}

		if (a instanceof ExpressionDefinition &&
				b instanceof ExpressionDefinition) {
			// compare expressions
			ExpressionDefinition e_a = (ExpressionDefinition)a;
			ExpressionDefinition e_b = (ExpressionDefinition)b;

			if (!isSameExpression(e_a, e_b)) {
				return false;
			}
		} else {
			// do reflection crawling
			if (!isSameByReflectionCompare(a, b)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * compares two expressions
	 * 
	 * @param e_a
	 * @param e_b
	 * @return
	 */
	private boolean isSameExpression(ExpressionDefinition e_a, ExpressionDefinition e_b) {
		if (!e_a.getExpression().equals(e_b.getExpression()) ||
				!e_a.getLanguage().equals(e_b.getLanguage())) {
			return false;
		}

		XPathExpression xp_a = null;
		XPathExpression xp_b = null;

		if (e_a instanceof XPathExpression) {
			xp_a = (XPathExpression)e_a;
			xp_b = null;

			if (e_b instanceof XPathExpression) {
				xp_b = (XPathExpression)e_b;
			} else {
				try {
					Field f_original = e_b.getClass().getDeclaredField("original");
					f_original.setAccessible(true);
					xp_b = (XPathExpression)f_original.get(e_b);
				} catch (Exception ex) {
					Activator.getLogger().error(ex);
				}
			}

			if (xp_a.getResultTypeName() != xp_b.getResultTypeName()) {
				return false;
			}
		} else if (e_b instanceof XPathExpression) {
			xp_a = null;
			xp_b = (XPathExpression)e_b;

			if (e_a instanceof XPathExpression) {
				xp_a = (XPathExpression)e_a;
			} else {
				try {
					Field f_original = e_a.getClass().getDeclaredField("original");
					f_original.setAccessible(true);
					xp_a = (XPathExpression)f_original.get(e_a);
				} catch (Exception ex) {
					Activator.getLogger().error(ex);
				}
			}

			if (xp_a.getResultTypeName() != xp_b.getResultTypeName()) {
				return false;
			}
		}
		return true;
	}

	private boolean isSameByReflectionCompare(Object a, Object b) {
		// now use reflection to compare all common fields of the objects
		boolean isSame = compareObjects(a, b);
		if (isSame) {
			// now opposite order to compare really each field of each object
			isSame = compareObjects(b, a);
		}
		return isSame;
	}

	private boolean compareObjects(Object a, Object b) {
		Class ca = a.getClass();
		Class cb = b.getClass();

		boolean found = false;
		for (Field fa : ca.getDeclaredFields()) {
			fa.setAccessible(true);
			found = false;
			for (Field fb : cb.getDeclaredFields()) {
				fb.setAccessible(true);
				if (fb.getName().equals(fa.getName())) {
					found = true;
					try {
						Object valA = fa.get(a);
						Object valB = fb.get(b);
						if (valB == null && valA != null) return false;
						if (valB != null && valA != null && !valB.equals(valA)) {
							return false;
						}
					} catch (Exception ex) {
						Activator.getLogger().error(ex);
					}
				}
			}
			if (!found) {
				return false;
			}
		}

		Class sca = ca.getSuperclass();
		Class scb = cb.getSuperclass();
		// both must have the same super class or both no super class
		if (sca == null && scb == null) {
			// all fine
		} else if (sca != null && scb != null) {
			// all fine
		} else {
			return false;
		}

		// both super classes must be equal
		if (!sca.getName().equals(scb.getName())) {
			return false;
		}

		return true;
	}
	
	/**
	 * this is a workaround for a bug in Camel 2.12 and will be
	 * most likely removed when the bug has been fixed
	 * (see https://issues.apache.org/jira/browse/CAMEL-7544)
	 * 
	 * @param def definition 
	 */
	@Deprecated
	protected void resetCustomId(OptionalIdentifiedDefinition def) {
		def.setCustomId(null);
	}
}
