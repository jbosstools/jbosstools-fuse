/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.generated;

import java.util.Map;

import org.apache.camel.ShutdownRoute;
import org.apache.camel.ShutdownRunningTask;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.camel.model.util.Objects;
import org.fusesource.ide.commons.properties.EnumPropertyDescriptor;

/**
 * The Node class from Camel's RouteDefinition
 *
 * NOTE - this file is auto-generated using Velocity.
 *
 * DO NOT EDIT!
 */
public class Route extends RouteSupport {

    public static final String PROPERTY_AUTOSTARTUP = "Route.AutoStartup";
    public static final String PROPERTY_DELAYER = "Route.Delayer";
    public static final String PROPERTY_ERRORHANDLERREF = "Route.ErrorHandlerRef";
    public static final String PROPERTY_GROUP = "Route.Group";
    public static final String PROPERTY_HANDLEFAULT = "Route.HandleFault";
    public static final String PROPERTY_MESSAGEHISTORY = "Route.MessageHistory";
    public static final String PROPERTY_ROUTEPOLICYREF = "Route.RoutePolicyRef";
    public static final String PROPERTY_SHUTDOWNROUTE = "Route.ShutdownRoute";
    public static final String PROPERTY_SHUTDOWNRUNNINGTASK = "Route.ShutdownRunningTask";
    public static final String PROPERTY_STARTUPORDER = "Route.StartupOrder";
    public static final String PROPERTY_STREAMCACHE = "Route.StreamCache";
    public static final String PROPERTY_TRACE = "Route.Trace";

    private String autoStartup;
    private String delayer;
    private String errorHandlerRef;
    private String group;
    private String handleFault;
    private String messageHistory;
    private String routePolicyRef;
    private ShutdownRoute shutdownRoute;
    private ShutdownRunningTask shutdownRunningTask;
    private Integer startupOrder;
    private String streamCache;
    private String trace;

    public Route() {
    }

    public Route(RouteDefinition definition, RouteContainer parent) {
        super(definition, parent);
    }


    /**
     * @return the autoStartup
     */
    public String getAutoStartup() {
        return this.autoStartup;
    }

    /**
     * @param autoStartup the autoStartup to set
     */
    public void setAutoStartup(String autoStartup) {
        String oldValue = this.autoStartup;
        this.autoStartup = autoStartup;
        if (!isSame(oldValue, autoStartup)) {
            firePropertyChange(PROPERTY_AUTOSTARTUP, oldValue, autoStartup);
        }
    }

    /**
     * @return the delayer
     */
    public String getDelayer() {
        return this.delayer;
    }

    /**
     * @param delayer the delayer to set
     */
    public void setDelayer(String delayer) {
        String oldValue = this.delayer;
        this.delayer = delayer;
        if (!isSame(oldValue, delayer)) {
            firePropertyChange(PROPERTY_DELAYER, oldValue, delayer);
        }
    }

    /**
     * @return the errorHandlerRef
     */
    public String getErrorHandlerRef() {
        return this.errorHandlerRef;
    }

    /**
     * @param errorHandlerRef the errorHandlerRef to set
     */
    public void setErrorHandlerRef(String errorHandlerRef) {
        String oldValue = this.errorHandlerRef;
        this.errorHandlerRef = errorHandlerRef;
        if (!isSame(oldValue, errorHandlerRef)) {
            firePropertyChange(PROPERTY_ERRORHANDLERREF, oldValue, errorHandlerRef);
        }
    }

    /**
     * @return the group
     */
    public String getGroup() {
        return this.group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(String group) {
        String oldValue = this.group;
        this.group = group;
        if (!isSame(oldValue, group)) {
            firePropertyChange(PROPERTY_GROUP, oldValue, group);
        }
    }

    /**
     * @return the handleFault
     */
    public String getHandleFault() {
        return this.handleFault;
    }

    /**
     * @param handleFault the handleFault to set
     */
    public void setHandleFault(String handleFault) {
        String oldValue = this.handleFault;
        this.handleFault = handleFault;
        if (!isSame(oldValue, handleFault)) {
            firePropertyChange(PROPERTY_HANDLEFAULT, oldValue, handleFault);
        }
    }

    /**
     * @return the messageHistory
     */
    public String getMessageHistory() {
        return this.messageHistory;
    }

    /**
     * @param messageHistory the messageHistory to set
     */
    public void setMessageHistory(String messageHistory) {
        String oldValue = this.messageHistory;
        this.messageHistory = messageHistory;
        if (!isSame(oldValue, messageHistory)) {
            firePropertyChange(PROPERTY_MESSAGEHISTORY, oldValue, messageHistory);
        }
    }

    /**
     * @return the routePolicyRef
     */
    public String getRoutePolicyRef() {
        return this.routePolicyRef;
    }

    /**
     * @param routePolicyRef the routePolicyRef to set
     */
    public void setRoutePolicyRef(String routePolicyRef) {
        String oldValue = this.routePolicyRef;
        this.routePolicyRef = routePolicyRef;
        if (!isSame(oldValue, routePolicyRef)) {
            firePropertyChange(PROPERTY_ROUTEPOLICYREF, oldValue, routePolicyRef);
        }
    }

    /**
     * @return the shutdownRoute
     */
    public ShutdownRoute getShutdownRoute() {
        return this.shutdownRoute;
    }

    /**
     * @param shutdownRoute the shutdownRoute to set
     */
    public void setShutdownRoute(ShutdownRoute shutdownRoute) {
        ShutdownRoute oldValue = this.shutdownRoute;
        this.shutdownRoute = shutdownRoute;
        if (!isSame(oldValue, shutdownRoute)) {
            firePropertyChange(PROPERTY_SHUTDOWNROUTE, oldValue, shutdownRoute);
        }
    }

    /**
     * @return the shutdownRunningTask
     */
    public ShutdownRunningTask getShutdownRunningTask() {
        return this.shutdownRunningTask;
    }

    /**
     * @param shutdownRunningTask the shutdownRunningTask to set
     */
    public void setShutdownRunningTask(ShutdownRunningTask shutdownRunningTask) {
        ShutdownRunningTask oldValue = this.shutdownRunningTask;
        this.shutdownRunningTask = shutdownRunningTask;
        if (!isSame(oldValue, shutdownRunningTask)) {
            firePropertyChange(PROPERTY_SHUTDOWNRUNNINGTASK, oldValue, shutdownRunningTask);
        }
    }

    /**
     * @return the startupOrder
     */
    public Integer getStartupOrder() {
        return this.startupOrder;
    }

    /**
     * @param startupOrder the startupOrder to set
     */
    public void setStartupOrder(Integer startupOrder) {
        Integer oldValue = this.startupOrder;
        this.startupOrder = startupOrder;
        if (!isSame(oldValue, startupOrder)) {
            firePropertyChange(PROPERTY_STARTUPORDER, oldValue, startupOrder);
        }
    }

    /**
     * @return the streamCache
     */
    public String getStreamCache() {
        return this.streamCache;
    }

    /**
     * @param streamCache the streamCache to set
     */
    public void setStreamCache(String streamCache) {
        String oldValue = this.streamCache;
        this.streamCache = streamCache;
        if (!isSame(oldValue, streamCache)) {
            firePropertyChange(PROPERTY_STREAMCACHE, oldValue, streamCache);
        }
    }

    /**
     * @return the trace
     */
    public String getTrace() {
        return this.trace;
    }

    /**
     * @param trace the trace to set
     */
    public void setTrace(String trace) {
        String oldValue = this.trace;
        this.trace = trace;
        if (!isSame(oldValue, trace)) {
            firePropertyChange(PROPERTY_TRACE, oldValue, trace);
        }
    }

    @Override
    protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
        super.addCustomProperties(descriptors);

        PropertyDescriptor descAutoStartup = new TextPropertyDescriptor(PROPERTY_AUTOSTARTUP, Messages.propertyLabelRouteAutoStartup);
        PropertyDescriptor descDelayer = new TextPropertyDescriptor(PROPERTY_DELAYER, Messages.propertyLabelRouteDelayer);
        PropertyDescriptor descErrorHandlerRef = new TextPropertyDescriptor(PROPERTY_ERRORHANDLERREF, Messages.propertyLabelRouteErrorHandlerRef);
        PropertyDescriptor descGroup = new TextPropertyDescriptor(PROPERTY_GROUP, Messages.propertyLabelRouteGroup);
        PropertyDescriptor descHandleFault = new TextPropertyDescriptor(PROPERTY_HANDLEFAULT, Messages.propertyLabelRouteHandleFault);
        PropertyDescriptor descMessageHistory = new TextPropertyDescriptor(PROPERTY_MESSAGEHISTORY, Messages.propertyLabelRouteMessageHistory);
        PropertyDescriptor descRoutePolicyRef = new TextPropertyDescriptor(PROPERTY_ROUTEPOLICYREF, Messages.propertyLabelRouteRoutePolicyRef);
        PropertyDescriptor descShutdownRoute = new EnumPropertyDescriptor(PROPERTY_SHUTDOWNROUTE, Messages.propertyLabelRouteShutdownRoute, ShutdownRoute.class);
        PropertyDescriptor descShutdownRunningTask = new EnumPropertyDescriptor(PROPERTY_SHUTDOWNRUNNINGTASK, Messages.propertyLabelRouteShutdownRunningTask, ShutdownRunningTask.class);
        PropertyDescriptor descStartupOrder = new TextPropertyDescriptor(PROPERTY_STARTUPORDER, Messages.propertyLabelRouteStartupOrder);
        PropertyDescriptor descStreamCache = new TextPropertyDescriptor(PROPERTY_STREAMCACHE, Messages.propertyLabelRouteStreamCache);
        PropertyDescriptor descTrace = new TextPropertyDescriptor(PROPERTY_TRACE, Messages.propertyLabelRouteTrace);

        descriptors.put(PROPERTY_AUTOSTARTUP, descAutoStartup);
        descriptors.put(PROPERTY_DELAYER, descDelayer);
        descriptors.put(PROPERTY_ERRORHANDLERREF, descErrorHandlerRef);
        descriptors.put(PROPERTY_GROUP, descGroup);
        descriptors.put(PROPERTY_HANDLEFAULT, descHandleFault);
        descriptors.put(PROPERTY_MESSAGEHISTORY, descMessageHistory);
        descriptors.put(PROPERTY_ROUTEPOLICYREF, descRoutePolicyRef);
        descriptors.put(PROPERTY_SHUTDOWNROUTE, descShutdownRoute);
        descriptors.put(PROPERTY_SHUTDOWNRUNNINGTASK, descShutdownRunningTask);
        descriptors.put(PROPERTY_STARTUPORDER, descStartupOrder);
        descriptors.put(PROPERTY_STREAMCACHE, descStreamCache);
        descriptors.put(PROPERTY_TRACE, descTrace);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource\#setPropertyValue(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setPropertyValue(Object id, Object value) {
        if (PROPERTY_AUTOSTARTUP.equals(id)) {
            setAutoStartup(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_DELAYER.equals(id)) {
            setDelayer(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_ERRORHANDLERREF.equals(id)) {
            setErrorHandlerRef(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_GROUP.equals(id)) {
            setGroup(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_HANDLEFAULT.equals(id)) {
            setHandleFault(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_MESSAGEHISTORY.equals(id)) {
            setMessageHistory(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_ROUTEPOLICYREF.equals(id)) {
            setRoutePolicyRef(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_SHUTDOWNROUTE.equals(id)) {
            setShutdownRoute(Objects.convertTo(value, ShutdownRoute.class));
            return;
        }
        if (PROPERTY_SHUTDOWNRUNNINGTASK.equals(id)) {
            setShutdownRunningTask(Objects.convertTo(value, ShutdownRunningTask.class));
            return;
        }
        if (PROPERTY_STARTUPORDER.equals(id)) {
            setStartupOrder(Objects.convertTo(value, Integer.class));
            return;
        }
        if (PROPERTY_STREAMCACHE.equals(id)) {
            setStreamCache(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_TRACE.equals(id)) {
            setTrace(Objects.convertTo(value, String.class));
            return;
        }
        super.setPropertyValue(id, value);
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode\#getPropertyValue(java.lang.Object)
     */
    @Override
    public Object getPropertyValue(Object id) {
        if (PROPERTY_AUTOSTARTUP.equals(id)) {
            return this.getAutoStartup();
        }
        if (PROPERTY_DELAYER.equals(id)) {
            return this.getDelayer();
        }
        if (PROPERTY_ERRORHANDLERREF.equals(id)) {
            return this.getErrorHandlerRef();
        }
        if (PROPERTY_GROUP.equals(id)) {
            return this.getGroup();
        }
        if (PROPERTY_HANDLEFAULT.equals(id)) {
            return this.getHandleFault();
        }
        if (PROPERTY_MESSAGEHISTORY.equals(id)) {
            return this.getMessageHistory();
        }
        if (PROPERTY_ROUTEPOLICYREF.equals(id)) {
            return this.getRoutePolicyRef();
        }
        if (PROPERTY_SHUTDOWNROUTE.equals(id)) {
            return this.getShutdownRoute();
        }
        if (PROPERTY_SHUTDOWNRUNNINGTASK.equals(id)) {
            return this.getShutdownRunningTask();
        }
        if (PROPERTY_STARTUPORDER.equals(id)) {
            return this.getStartupOrder();
        }
        if (PROPERTY_STREAMCACHE.equals(id)) {
            return this.getStreamCache();
        }
        if (PROPERTY_TRACE.equals(id)) {
            return this.getTrace();
        }
        return super.getPropertyValue(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ProcessorDefinition createCamelDefinition() {
        RouteDefinition answer = new RouteDefinition();

        answer.setAutoStartup(toXmlPropertyValue(PROPERTY_AUTOSTARTUP, this.getAutoStartup()));
        answer.setDelayer(toXmlPropertyValue(PROPERTY_DELAYER, this.getDelayer()));
        answer.setErrorHandlerRef(toXmlPropertyValue(PROPERTY_ERRORHANDLERREF, this.getErrorHandlerRef()));
        answer.setGroup(toXmlPropertyValue(PROPERTY_GROUP, this.getGroup()));
        answer.setHandleFault(toXmlPropertyValue(PROPERTY_HANDLEFAULT, this.getHandleFault()));
        answer.setMessageHistory(toXmlPropertyValue(PROPERTY_MESSAGEHISTORY, this.getMessageHistory()));
        answer.setRoutePolicyRef(toXmlPropertyValue(PROPERTY_ROUTEPOLICYREF, this.getRoutePolicyRef()));
        answer.setShutdownRoute(toXmlPropertyValue(PROPERTY_SHUTDOWNROUTE, this.getShutdownRoute()));
        answer.setShutdownRunningTask(toXmlPropertyValue(PROPERTY_SHUTDOWNRUNNINGTASK, this.getShutdownRunningTask()));
        answer.setStartupOrder(toXmlPropertyValue(PROPERTY_STARTUPORDER, this.getStartupOrder()));
        answer.setStreamCache(toXmlPropertyValue(PROPERTY_STREAMCACHE, this.getStreamCache()));
        answer.setTrace(toXmlPropertyValue(PROPERTY_TRACE, this.getTrace()));

        super.savePropertiesToCamelDefinition(answer);
        return answer;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<?> getCamelDefinitionClass() {
        return RouteDefinition.class;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
        super.loadPropertiesFromCamelDefinition(processor);

        if (processor instanceof RouteDefinition) {
            RouteDefinition node = (RouteDefinition) processor;

            this.setAutoStartup(node.getAutoStartup());
            this.setDelayer(node.getDelayer());
            this.setErrorHandlerRef(node.getErrorHandlerRef());
            this.setGroup(node.getGroup());
            this.setHandleFault(node.getHandleFault());
            this.setMessageHistory(node.getMessageHistory());
            this.setRoutePolicyRef(node.getRoutePolicyRef());
            this.setShutdownRoute(node.getShutdownRoute());
            this.setShutdownRunningTask(node.getShutdownRunningTask());
            this.setStartupOrder(node.getStartupOrder());
            this.setStreamCache(node.getStreamCache());
            this.setTrace(node.getTrace());
        } else {
            throw new IllegalArgumentException("ProcessorDefinition not an instanceof RouteDefinition. Was " + processor.getClass().getName());
        }
    }

}
