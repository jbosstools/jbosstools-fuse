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

import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.model.OptimisticLockRetryPolicyDefinition;
import org.apache.camel.model.AggregateDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.ExpressionPropertyDescriptor;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.util.Objects;
import org.fusesource.ide.commons.properties.BooleanPropertyDescriptor;
import org.fusesource.ide.commons.properties.ComplexPropertyDescriptor;
import org.fusesource.ide.commons.properties.ComplexUnionPropertyDescriptor;
import org.fusesource.ide.commons.properties.EnumPropertyDescriptor;
import org.fusesource.ide.commons.properties.ListPropertyDescriptor;
import org.fusesource.ide.commons.properties.UnionTypeValue;

/**
 * The Node class from Camel's AggregateDefinition
 *
 * NOTE - this file is auto-generated using Velocity.
 *
 * DO NOT EDIT!
 */
public class Aggregate extends AbstractNode {

    public static final String PROPERTY_CUSTOMID = "Aggregate.CustomId";
    public static final String PROPERTY_INHERITERRORHANDLER = "Aggregate.InheritErrorHandler";
    public static final String PROPERTY_CORRELATIONEXPRESSION = "Aggregate.CorrelationExpression";
    public static final String PROPERTY_COMPLETIONPREDICATE = "Aggregate.CompletionPredicate";
    public static final String PROPERTY_COMPLETIONTIMEOUTEXPRESSION = "Aggregate.CompletionTimeoutExpression";
    public static final String PROPERTY_COMPLETIONSIZEEXPRESSION = "Aggregate.CompletionSizeExpression";
    public static final String PROPERTY_PARALLELPROCESSING = "Aggregate.ParallelProcessing";
    public static final String PROPERTY_OPTIMISTICLOCKING = "Aggregate.OptimisticLocking";
    public static final String PROPERTY_EXECUTORSERVICEREF = "Aggregate.ExecutorServiceRef";
    public static final String PROPERTY_TIMEOUTCHECKEREXECUTORSERVICEREF = "Aggregate.TimeoutCheckerExecutorServiceRef";
    public static final String PROPERTY_AGGREGATIONREPOSITORYREF = "Aggregate.AggregationRepositoryRef";
    public static final String PROPERTY_STRATEGYREF = "Aggregate.StrategyRef";
    public static final String PROPERTY_STRATEGYMETHODNAME = "Aggregate.StrategyMethodName";
    public static final String PROPERTY_STRATEGYMETHODALLOWNULL = "Aggregate.StrategyMethodAllowNull";
    public static final String PROPERTY_COMPLETIONSIZE = "Aggregate.CompletionSize";
    public static final String PROPERTY_COMPLETIONINTERVAL = "Aggregate.CompletionInterval";
    public static final String PROPERTY_COMPLETIONTIMEOUT = "Aggregate.CompletionTimeout";
    public static final String PROPERTY_COMPLETIONFROMBATCHCONSUMER = "Aggregate.CompletionFromBatchConsumer";
    public static final String PROPERTY_GROUPEXCHANGES = "Aggregate.GroupExchanges";
    public static final String PROPERTY_EAGERCHECKCOMPLETION = "Aggregate.EagerCheckCompletion";
    public static final String PROPERTY_IGNOREINVALIDCORRELATIONKEYS = "Aggregate.IgnoreInvalidCorrelationKeys";
    public static final String PROPERTY_CLOSECORRELATIONKEYONCOMPLETION = "Aggregate.CloseCorrelationKeyOnCompletion";
    public static final String PROPERTY_DISCARDONCOMPLETIONTIMEOUT = "Aggregate.DiscardOnCompletionTimeout";
    public static final String PROPERTY_FORCECOMPLETIONONSTOP = "Aggregate.ForceCompletionOnStop";
    public static final String PROPERTY_OPTIMISTICLOCKRETRYPOLICYDEFINITION = "Aggregate.OptimisticLockRetryPolicyDefinition";

    private Boolean customId;
    private Boolean inheritErrorHandler;
    private ExpressionDefinition correlationExpression;
    private ExpressionDefinition completionPredicate;
    private ExpressionDefinition completionTimeoutExpression;
    private ExpressionDefinition completionSizeExpression;
    private Boolean parallelProcessing;
    private Boolean optimisticLocking;
    private String executorServiceRef;
    private String timeoutCheckerExecutorServiceRef;
    private String aggregationRepositoryRef;
    private String strategyRef;
    private String strategyMethodName;
    private Boolean strategyMethodAllowNull;
    private Integer completionSize;
    private Long completionInterval;
    private Long completionTimeout;
    private Boolean completionFromBatchConsumer;
    private Boolean groupExchanges;
    private Boolean eagerCheckCompletion;
    private Boolean ignoreInvalidCorrelationKeys;
    private Integer closeCorrelationKeyOnCompletion;
    private Boolean discardOnCompletionTimeout;
    private Boolean forceCompletionOnStop;
    private OptimisticLockRetryPolicyDefinition optimisticLockRetryPolicyDefinition;

    public Aggregate() {
    }

    public Aggregate(AggregateDefinition definition, RouteContainer parent) {
        super(parent);
        loadPropertiesFromCamelDefinition(definition);
        loadChildrenFromCamelDefinition(definition);
    }

    @Override
    public String getIconName() {
        return "aggregate.png";
    }

    @Override
    public String getDocumentationFileName() {
        return "aggregateEIP";
    }

    @Override
    public String getCategoryName() {
        return "Routing";
    }

    /**
     * @return the customId
     */
    public Boolean getCustomId() {
        return this.customId;
    }

    /**
     * @param customId the customId to set
     */
    public void setCustomId(Boolean customId) {
        Boolean oldValue = this.customId;
        this.customId = customId;
        if (!isSame(oldValue, customId)) {
            firePropertyChange(PROPERTY_CUSTOMID, oldValue, customId);
        }
    }

    /**
     * @return the inheritErrorHandler
     */
    public Boolean getInheritErrorHandler() {
        return this.inheritErrorHandler;
    }

    /**
     * @param inheritErrorHandler the inheritErrorHandler to set
     */
    public void setInheritErrorHandler(Boolean inheritErrorHandler) {
        Boolean oldValue = this.inheritErrorHandler;
        this.inheritErrorHandler = inheritErrorHandler;
        if (!isSame(oldValue, inheritErrorHandler)) {
            firePropertyChange(PROPERTY_INHERITERRORHANDLER, oldValue, inheritErrorHandler);
        }
    }

    /**
     * @return the correlationExpression
     */
    public ExpressionDefinition getCorrelationExpression() {
        return this.correlationExpression;
    }

    /**
     * @param correlationExpression the correlationExpression to set
     */
    public void setCorrelationExpression(ExpressionDefinition correlationExpression) {
        ExpressionDefinition oldValue = this.correlationExpression;
        this.correlationExpression = correlationExpression;
        if (!isSame(oldValue, correlationExpression)) {
            firePropertyChange(PROPERTY_CORRELATIONEXPRESSION, oldValue, correlationExpression);
        }
    }

    /**
     * @return the completionPredicate
     */
    public ExpressionDefinition getCompletionPredicate() {
        return this.completionPredicate;
    }

    /**
     * @param completionPredicate the completionPredicate to set
     */
    public void setCompletionPredicate(ExpressionDefinition completionPredicate) {
        ExpressionDefinition oldValue = this.completionPredicate;
        this.completionPredicate = completionPredicate;
        if (!isSame(oldValue, completionPredicate)) {
            firePropertyChange(PROPERTY_COMPLETIONPREDICATE, oldValue, completionPredicate);
        }
    }

    /**
     * @return the completionTimeoutExpression
     */
    public ExpressionDefinition getCompletionTimeoutExpression() {
        return this.completionTimeoutExpression;
    }

    /**
     * @param completionTimeoutExpression the completionTimeoutExpression to set
     */
    public void setCompletionTimeoutExpression(ExpressionDefinition completionTimeoutExpression) {
        ExpressionDefinition oldValue = this.completionTimeoutExpression;
        this.completionTimeoutExpression = completionTimeoutExpression;
        if (!isSame(oldValue, completionTimeoutExpression)) {
            firePropertyChange(PROPERTY_COMPLETIONTIMEOUTEXPRESSION, oldValue, completionTimeoutExpression);
        }
    }

    /**
     * @return the completionSizeExpression
     */
    public ExpressionDefinition getCompletionSizeExpression() {
        return this.completionSizeExpression;
    }

    /**
     * @param completionSizeExpression the completionSizeExpression to set
     */
    public void setCompletionSizeExpression(ExpressionDefinition completionSizeExpression) {
        ExpressionDefinition oldValue = this.completionSizeExpression;
        this.completionSizeExpression = completionSizeExpression;
        if (!isSame(oldValue, completionSizeExpression)) {
            firePropertyChange(PROPERTY_COMPLETIONSIZEEXPRESSION, oldValue, completionSizeExpression);
        }
    }

    /**
     * @return the parallelProcessing
     */
    public Boolean getParallelProcessing() {
        return this.parallelProcessing;
    }

    /**
     * @param parallelProcessing the parallelProcessing to set
     */
    public void setParallelProcessing(Boolean parallelProcessing) {
        Boolean oldValue = this.parallelProcessing;
        this.parallelProcessing = parallelProcessing;
        if (!isSame(oldValue, parallelProcessing)) {
            firePropertyChange(PROPERTY_PARALLELPROCESSING, oldValue, parallelProcessing);
        }
    }

    /**
     * @return the optimisticLocking
     */
    public Boolean getOptimisticLocking() {
        return this.optimisticLocking;
    }

    /**
     * @param optimisticLocking the optimisticLocking to set
     */
    public void setOptimisticLocking(Boolean optimisticLocking) {
        Boolean oldValue = this.optimisticLocking;
        this.optimisticLocking = optimisticLocking;
        if (!isSame(oldValue, optimisticLocking)) {
            firePropertyChange(PROPERTY_OPTIMISTICLOCKING, oldValue, optimisticLocking);
        }
    }

    /**
     * @return the executorServiceRef
     */
    public String getExecutorServiceRef() {
        return this.executorServiceRef;
    }

    /**
     * @param executorServiceRef the executorServiceRef to set
     */
    public void setExecutorServiceRef(String executorServiceRef) {
        String oldValue = this.executorServiceRef;
        this.executorServiceRef = executorServiceRef;
        if (!isSame(oldValue, executorServiceRef)) {
            firePropertyChange(PROPERTY_EXECUTORSERVICEREF, oldValue, executorServiceRef);
        }
    }

    /**
     * @return the timeoutCheckerExecutorServiceRef
     */
    public String getTimeoutCheckerExecutorServiceRef() {
        return this.timeoutCheckerExecutorServiceRef;
    }

    /**
     * @param timeoutCheckerExecutorServiceRef the timeoutCheckerExecutorServiceRef to set
     */
    public void setTimeoutCheckerExecutorServiceRef(String timeoutCheckerExecutorServiceRef) {
        String oldValue = this.timeoutCheckerExecutorServiceRef;
        this.timeoutCheckerExecutorServiceRef = timeoutCheckerExecutorServiceRef;
        if (!isSame(oldValue, timeoutCheckerExecutorServiceRef)) {
            firePropertyChange(PROPERTY_TIMEOUTCHECKEREXECUTORSERVICEREF, oldValue, timeoutCheckerExecutorServiceRef);
        }
    }

    /**
     * @return the aggregationRepositoryRef
     */
    public String getAggregationRepositoryRef() {
        return this.aggregationRepositoryRef;
    }

    /**
     * @param aggregationRepositoryRef the aggregationRepositoryRef to set
     */
    public void setAggregationRepositoryRef(String aggregationRepositoryRef) {
        String oldValue = this.aggregationRepositoryRef;
        this.aggregationRepositoryRef = aggregationRepositoryRef;
        if (!isSame(oldValue, aggregationRepositoryRef)) {
            firePropertyChange(PROPERTY_AGGREGATIONREPOSITORYREF, oldValue, aggregationRepositoryRef);
        }
    }

    /**
     * @return the strategyRef
     */
    public String getStrategyRef() {
        return this.strategyRef;
    }

    /**
     * @param strategyRef the strategyRef to set
     */
    public void setStrategyRef(String strategyRef) {
        String oldValue = this.strategyRef;
        this.strategyRef = strategyRef;
        if (!isSame(oldValue, strategyRef)) {
            firePropertyChange(PROPERTY_STRATEGYREF, oldValue, strategyRef);
        }
    }

    /**
     * @return the strategyMethodName
     */
    public String getStrategyMethodName() {
        return this.strategyMethodName;
    }

    /**
     * @param strategyMethodName the strategyMethodName to set
     */
    public void setStrategyMethodName(String strategyMethodName) {
        String oldValue = this.strategyMethodName;
        this.strategyMethodName = strategyMethodName;
        if (!isSame(oldValue, strategyMethodName)) {
            firePropertyChange(PROPERTY_STRATEGYMETHODNAME, oldValue, strategyMethodName);
        }
    }

    /**
     * @return the strategyMethodAllowNull
     */
    public Boolean getStrategyMethodAllowNull() {
        return this.strategyMethodAllowNull;
    }

    /**
     * @param strategyMethodAllowNull the strategyMethodAllowNull to set
     */
    public void setStrategyMethodAllowNull(Boolean strategyMethodAllowNull) {
        Boolean oldValue = this.strategyMethodAllowNull;
        this.strategyMethodAllowNull = strategyMethodAllowNull;
        if (!isSame(oldValue, strategyMethodAllowNull)) {
            firePropertyChange(PROPERTY_STRATEGYMETHODALLOWNULL, oldValue, strategyMethodAllowNull);
        }
    }

    /**
     * @return the completionSize
     */
    public Integer getCompletionSize() {
        return this.completionSize;
    }

    /**
     * @param completionSize the completionSize to set
     */
    public void setCompletionSize(Integer completionSize) {
        Integer oldValue = this.completionSize;
        this.completionSize = completionSize;
        if (!isSame(oldValue, completionSize)) {
            firePropertyChange(PROPERTY_COMPLETIONSIZE, oldValue, completionSize);
        }
    }

    /**
     * @return the completionInterval
     */
    public Long getCompletionInterval() {
        return this.completionInterval;
    }

    /**
     * @param completionInterval the completionInterval to set
     */
    public void setCompletionInterval(Long completionInterval) {
        Long oldValue = this.completionInterval;
        this.completionInterval = completionInterval;
        if (!isSame(oldValue, completionInterval)) {
            firePropertyChange(PROPERTY_COMPLETIONINTERVAL, oldValue, completionInterval);
        }
    }

    /**
     * @return the completionTimeout
     */
    public Long getCompletionTimeout() {
        return this.completionTimeout;
    }

    /**
     * @param completionTimeout the completionTimeout to set
     */
    public void setCompletionTimeout(Long completionTimeout) {
        Long oldValue = this.completionTimeout;
        this.completionTimeout = completionTimeout;
        if (!isSame(oldValue, completionTimeout)) {
            firePropertyChange(PROPERTY_COMPLETIONTIMEOUT, oldValue, completionTimeout);
        }
    }

    /**
     * @return the completionFromBatchConsumer
     */
    public Boolean getCompletionFromBatchConsumer() {
        return this.completionFromBatchConsumer;
    }

    /**
     * @param completionFromBatchConsumer the completionFromBatchConsumer to set
     */
    public void setCompletionFromBatchConsumer(Boolean completionFromBatchConsumer) {
        Boolean oldValue = this.completionFromBatchConsumer;
        this.completionFromBatchConsumer = completionFromBatchConsumer;
        if (!isSame(oldValue, completionFromBatchConsumer)) {
            firePropertyChange(PROPERTY_COMPLETIONFROMBATCHCONSUMER, oldValue, completionFromBatchConsumer);
        }
    }

    /**
     * @return the groupExchanges
     */
    public Boolean getGroupExchanges() {
        return this.groupExchanges;
    }

    /**
     * @param groupExchanges the groupExchanges to set
     */
    public void setGroupExchanges(Boolean groupExchanges) {
        Boolean oldValue = this.groupExchanges;
        this.groupExchanges = groupExchanges;
        if (!isSame(oldValue, groupExchanges)) {
            firePropertyChange(PROPERTY_GROUPEXCHANGES, oldValue, groupExchanges);
        }
    }

    /**
     * @return the eagerCheckCompletion
     */
    public Boolean getEagerCheckCompletion() {
        return this.eagerCheckCompletion;
    }

    /**
     * @param eagerCheckCompletion the eagerCheckCompletion to set
     */
    public void setEagerCheckCompletion(Boolean eagerCheckCompletion) {
        Boolean oldValue = this.eagerCheckCompletion;
        this.eagerCheckCompletion = eagerCheckCompletion;
        if (!isSame(oldValue, eagerCheckCompletion)) {
            firePropertyChange(PROPERTY_EAGERCHECKCOMPLETION, oldValue, eagerCheckCompletion);
        }
    }

    /**
     * @return the ignoreInvalidCorrelationKeys
     */
    public Boolean getIgnoreInvalidCorrelationKeys() {
        return this.ignoreInvalidCorrelationKeys;
    }

    /**
     * @param ignoreInvalidCorrelationKeys the ignoreInvalidCorrelationKeys to set
     */
    public void setIgnoreInvalidCorrelationKeys(Boolean ignoreInvalidCorrelationKeys) {
        Boolean oldValue = this.ignoreInvalidCorrelationKeys;
        this.ignoreInvalidCorrelationKeys = ignoreInvalidCorrelationKeys;
        if (!isSame(oldValue, ignoreInvalidCorrelationKeys)) {
            firePropertyChange(PROPERTY_IGNOREINVALIDCORRELATIONKEYS, oldValue, ignoreInvalidCorrelationKeys);
        }
    }

    /**
     * @return the closeCorrelationKeyOnCompletion
     */
    public Integer getCloseCorrelationKeyOnCompletion() {
        return this.closeCorrelationKeyOnCompletion;
    }

    /**
     * @param closeCorrelationKeyOnCompletion the closeCorrelationKeyOnCompletion to set
     */
    public void setCloseCorrelationKeyOnCompletion(Integer closeCorrelationKeyOnCompletion) {
        Integer oldValue = this.closeCorrelationKeyOnCompletion;
        this.closeCorrelationKeyOnCompletion = closeCorrelationKeyOnCompletion;
        if (!isSame(oldValue, closeCorrelationKeyOnCompletion)) {
            firePropertyChange(PROPERTY_CLOSECORRELATIONKEYONCOMPLETION, oldValue, closeCorrelationKeyOnCompletion);
        }
    }

    /**
     * @return the discardOnCompletionTimeout
     */
    public Boolean getDiscardOnCompletionTimeout() {
        return this.discardOnCompletionTimeout;
    }

    /**
     * @param discardOnCompletionTimeout the discardOnCompletionTimeout to set
     */
    public void setDiscardOnCompletionTimeout(Boolean discardOnCompletionTimeout) {
        Boolean oldValue = this.discardOnCompletionTimeout;
        this.discardOnCompletionTimeout = discardOnCompletionTimeout;
        if (!isSame(oldValue, discardOnCompletionTimeout)) {
            firePropertyChange(PROPERTY_DISCARDONCOMPLETIONTIMEOUT, oldValue, discardOnCompletionTimeout);
        }
    }

    /**
     * @return the forceCompletionOnStop
     */
    public Boolean getForceCompletionOnStop() {
        return this.forceCompletionOnStop;
    }

    /**
     * @param forceCompletionOnStop the forceCompletionOnStop to set
     */
    public void setForceCompletionOnStop(Boolean forceCompletionOnStop) {
        Boolean oldValue = this.forceCompletionOnStop;
        this.forceCompletionOnStop = forceCompletionOnStop;
        if (!isSame(oldValue, forceCompletionOnStop)) {
            firePropertyChange(PROPERTY_FORCECOMPLETIONONSTOP, oldValue, forceCompletionOnStop);
        }
    }

    /**
     * @return the optimisticLockRetryPolicyDefinition
     */
    public OptimisticLockRetryPolicyDefinition getOptimisticLockRetryPolicyDefinition() {
        return this.optimisticLockRetryPolicyDefinition;
    }

    /**
     * @param optimisticLockRetryPolicyDefinition the optimisticLockRetryPolicyDefinition to set
     */
    public void setOptimisticLockRetryPolicyDefinition(OptimisticLockRetryPolicyDefinition optimisticLockRetryPolicyDefinition) {
        OptimisticLockRetryPolicyDefinition oldValue = this.optimisticLockRetryPolicyDefinition;
        this.optimisticLockRetryPolicyDefinition = optimisticLockRetryPolicyDefinition;
        if (!isSame(oldValue, optimisticLockRetryPolicyDefinition)) {
            firePropertyChange(PROPERTY_OPTIMISTICLOCKRETRYPOLICYDEFINITION, oldValue, optimisticLockRetryPolicyDefinition);
        }
    }

    @Override
    protected void addCustomProperties(Map<String, PropertyDescriptor> descriptors) {
        super.addCustomProperties(descriptors);

        PropertyDescriptor descCustomId = new BooleanPropertyDescriptor(PROPERTY_CUSTOMID, Messages.propertyLabelAggregateCustomId);
        PropertyDescriptor descInheritErrorHandler = new BooleanPropertyDescriptor(PROPERTY_INHERITERRORHANDLER, Messages.propertyLabelAggregateInheritErrorHandler);
        PropertyDescriptor descCorrelationExpression = new ExpressionPropertyDescriptor(PROPERTY_CORRELATIONEXPRESSION, Messages.propertyLabelAggregateCorrelationExpression);
        PropertyDescriptor descCompletionPredicate = new ExpressionPropertyDescriptor(PROPERTY_COMPLETIONPREDICATE, Messages.propertyLabelAggregateCompletionPredicate);
        PropertyDescriptor descCompletionTimeoutExpression = new ExpressionPropertyDescriptor(PROPERTY_COMPLETIONTIMEOUTEXPRESSION, Messages.propertyLabelAggregateCompletionTimeoutExpression);
        PropertyDescriptor descCompletionSizeExpression = new ExpressionPropertyDescriptor(PROPERTY_COMPLETIONSIZEEXPRESSION, Messages.propertyLabelAggregateCompletionSizeExpression);
        PropertyDescriptor descParallelProcessing = new BooleanPropertyDescriptor(PROPERTY_PARALLELPROCESSING, Messages.propertyLabelAggregateParallelProcessing);
        PropertyDescriptor descOptimisticLocking = new BooleanPropertyDescriptor(PROPERTY_OPTIMISTICLOCKING, Messages.propertyLabelAggregateOptimisticLocking);
        PropertyDescriptor descExecutorServiceRef = new TextPropertyDescriptor(PROPERTY_EXECUTORSERVICEREF, Messages.propertyLabelAggregateExecutorServiceRef);
        PropertyDescriptor descTimeoutCheckerExecutorServiceRef = new TextPropertyDescriptor(PROPERTY_TIMEOUTCHECKEREXECUTORSERVICEREF, Messages.propertyLabelAggregateTimeoutCheckerExecutorServiceRef);
        PropertyDescriptor descAggregationRepositoryRef = new TextPropertyDescriptor(PROPERTY_AGGREGATIONREPOSITORYREF, Messages.propertyLabelAggregateAggregationRepositoryRef);
        PropertyDescriptor descStrategyRef = new TextPropertyDescriptor(PROPERTY_STRATEGYREF, Messages.propertyLabelAggregateStrategyRef);
        PropertyDescriptor descStrategyMethodName = new TextPropertyDescriptor(PROPERTY_STRATEGYMETHODNAME, Messages.propertyLabelAggregateStrategyMethodName);
        PropertyDescriptor descStrategyMethodAllowNull = new BooleanPropertyDescriptor(PROPERTY_STRATEGYMETHODALLOWNULL, Messages.propertyLabelAggregateStrategyMethodAllowNull);
        PropertyDescriptor descCompletionSize = new TextPropertyDescriptor(PROPERTY_COMPLETIONSIZE, Messages.propertyLabelAggregateCompletionSize);
        PropertyDescriptor descCompletionInterval = new TextPropertyDescriptor(PROPERTY_COMPLETIONINTERVAL, Messages.propertyLabelAggregateCompletionInterval);
        PropertyDescriptor descCompletionTimeout = new TextPropertyDescriptor(PROPERTY_COMPLETIONTIMEOUT, Messages.propertyLabelAggregateCompletionTimeout);
        PropertyDescriptor descCompletionFromBatchConsumer = new BooleanPropertyDescriptor(PROPERTY_COMPLETIONFROMBATCHCONSUMER, Messages.propertyLabelAggregateCompletionFromBatchConsumer);
        PropertyDescriptor descGroupExchanges = new BooleanPropertyDescriptor(PROPERTY_GROUPEXCHANGES, Messages.propertyLabelAggregateGroupExchanges);
        PropertyDescriptor descEagerCheckCompletion = new BooleanPropertyDescriptor(PROPERTY_EAGERCHECKCOMPLETION, Messages.propertyLabelAggregateEagerCheckCompletion);
        PropertyDescriptor descIgnoreInvalidCorrelationKeys = new BooleanPropertyDescriptor(PROPERTY_IGNOREINVALIDCORRELATIONKEYS, Messages.propertyLabelAggregateIgnoreInvalidCorrelationKeys);
        PropertyDescriptor descCloseCorrelationKeyOnCompletion = new TextPropertyDescriptor(PROPERTY_CLOSECORRELATIONKEYONCOMPLETION, Messages.propertyLabelAggregateCloseCorrelationKeyOnCompletion);
        PropertyDescriptor descDiscardOnCompletionTimeout = new BooleanPropertyDescriptor(PROPERTY_DISCARDONCOMPLETIONTIMEOUT, Messages.propertyLabelAggregateDiscardOnCompletionTimeout);
        PropertyDescriptor descForceCompletionOnStop = new BooleanPropertyDescriptor(PROPERTY_FORCECOMPLETIONONSTOP, Messages.propertyLabelAggregateForceCompletionOnStop);
        PropertyDescriptor descOptimisticLockRetryPolicyDefinition = new ComplexUnionPropertyDescriptor(PROPERTY_OPTIMISTICLOCKRETRYPOLICYDEFINITION, Messages.propertyLabelAggregateOptimisticLockRetryPolicyDefinition, OptimisticLockRetryPolicyDefinition.class, new UnionTypeValue[] {
        });

        descriptors.put(PROPERTY_CUSTOMID, descCustomId);
        descriptors.put(PROPERTY_INHERITERRORHANDLER, descInheritErrorHandler);
        descriptors.put(PROPERTY_CORRELATIONEXPRESSION, descCorrelationExpression);
        descriptors.put(PROPERTY_COMPLETIONPREDICATE, descCompletionPredicate);
        descriptors.put(PROPERTY_COMPLETIONTIMEOUTEXPRESSION, descCompletionTimeoutExpression);
        descriptors.put(PROPERTY_COMPLETIONSIZEEXPRESSION, descCompletionSizeExpression);
        descriptors.put(PROPERTY_PARALLELPROCESSING, descParallelProcessing);
        descriptors.put(PROPERTY_OPTIMISTICLOCKING, descOptimisticLocking);
        descriptors.put(PROPERTY_EXECUTORSERVICEREF, descExecutorServiceRef);
        descriptors.put(PROPERTY_TIMEOUTCHECKEREXECUTORSERVICEREF, descTimeoutCheckerExecutorServiceRef);
        descriptors.put(PROPERTY_AGGREGATIONREPOSITORYREF, descAggregationRepositoryRef);
        descriptors.put(PROPERTY_STRATEGYREF, descStrategyRef);
        descriptors.put(PROPERTY_STRATEGYMETHODNAME, descStrategyMethodName);
        descriptors.put(PROPERTY_STRATEGYMETHODALLOWNULL, descStrategyMethodAllowNull);
        descriptors.put(PROPERTY_COMPLETIONSIZE, descCompletionSize);
        descriptors.put(PROPERTY_COMPLETIONINTERVAL, descCompletionInterval);
        descriptors.put(PROPERTY_COMPLETIONTIMEOUT, descCompletionTimeout);
        descriptors.put(PROPERTY_COMPLETIONFROMBATCHCONSUMER, descCompletionFromBatchConsumer);
        descriptors.put(PROPERTY_GROUPEXCHANGES, descGroupExchanges);
        descriptors.put(PROPERTY_EAGERCHECKCOMPLETION, descEagerCheckCompletion);
        descriptors.put(PROPERTY_IGNOREINVALIDCORRELATIONKEYS, descIgnoreInvalidCorrelationKeys);
        descriptors.put(PROPERTY_CLOSECORRELATIONKEYONCOMPLETION, descCloseCorrelationKeyOnCompletion);
        descriptors.put(PROPERTY_DISCARDONCOMPLETIONTIMEOUT, descDiscardOnCompletionTimeout);
        descriptors.put(PROPERTY_FORCECOMPLETIONONSTOP, descForceCompletionOnStop);
        descriptors.put(PROPERTY_OPTIMISTICLOCKRETRYPOLICYDEFINITION, descOptimisticLockRetryPolicyDefinition);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource\#setPropertyValue(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setPropertyValue(Object id, Object value) {
        if (PROPERTY_CUSTOMID.equals(id)) {
            setCustomId(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_INHERITERRORHANDLER.equals(id)) {
            setInheritErrorHandler(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_CORRELATIONEXPRESSION.equals(id)) {
            setCorrelationExpression(Objects.convertTo(value, ExpressionDefinition.class));
            return;
        }
        if (PROPERTY_COMPLETIONPREDICATE.equals(id)) {
            setCompletionPredicate(Objects.convertTo(value, ExpressionDefinition.class));
            return;
        }
        if (PROPERTY_COMPLETIONTIMEOUTEXPRESSION.equals(id)) {
            setCompletionTimeoutExpression(Objects.convertTo(value, ExpressionDefinition.class));
            return;
        }
        if (PROPERTY_COMPLETIONSIZEEXPRESSION.equals(id)) {
            setCompletionSizeExpression(Objects.convertTo(value, ExpressionDefinition.class));
            return;
        }
        if (PROPERTY_PARALLELPROCESSING.equals(id)) {
            setParallelProcessing(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_OPTIMISTICLOCKING.equals(id)) {
            setOptimisticLocking(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_EXECUTORSERVICEREF.equals(id)) {
            setExecutorServiceRef(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_TIMEOUTCHECKEREXECUTORSERVICEREF.equals(id)) {
            setTimeoutCheckerExecutorServiceRef(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_AGGREGATIONREPOSITORYREF.equals(id)) {
            setAggregationRepositoryRef(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_STRATEGYREF.equals(id)) {
            setStrategyRef(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_STRATEGYMETHODNAME.equals(id)) {
            setStrategyMethodName(Objects.convertTo(value, String.class));
            return;
        }
        if (PROPERTY_STRATEGYMETHODALLOWNULL.equals(id)) {
            setStrategyMethodAllowNull(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_COMPLETIONSIZE.equals(id)) {
            setCompletionSize(Objects.convertTo(value, Integer.class));
            return;
        }
        if (PROPERTY_COMPLETIONINTERVAL.equals(id)) {
            setCompletionInterval(Objects.convertTo(value, Long.class));
            return;
        }
        if (PROPERTY_COMPLETIONTIMEOUT.equals(id)) {
            setCompletionTimeout(Objects.convertTo(value, Long.class));
            return;
        }
        if (PROPERTY_COMPLETIONFROMBATCHCONSUMER.equals(id)) {
            setCompletionFromBatchConsumer(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_GROUPEXCHANGES.equals(id)) {
            setGroupExchanges(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_EAGERCHECKCOMPLETION.equals(id)) {
            setEagerCheckCompletion(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_IGNOREINVALIDCORRELATIONKEYS.equals(id)) {
            setIgnoreInvalidCorrelationKeys(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_CLOSECORRELATIONKEYONCOMPLETION.equals(id)) {
            setCloseCorrelationKeyOnCompletion(Objects.convertTo(value, Integer.class));
            return;
        }
        if (PROPERTY_DISCARDONCOMPLETIONTIMEOUT.equals(id)) {
            setDiscardOnCompletionTimeout(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_FORCECOMPLETIONONSTOP.equals(id)) {
            setForceCompletionOnStop(Objects.convertTo(value, Boolean.class));
            return;
        }
        if (PROPERTY_OPTIMISTICLOCKRETRYPOLICYDEFINITION.equals(id)) {
            setOptimisticLockRetryPolicyDefinition(Objects.convertTo(value, OptimisticLockRetryPolicyDefinition.class));
            return;
        }
        super.setPropertyValue(id, value);
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.AbstractNode\#getPropertyValue(java.lang.Object)
     */
    @Override
    public Object getPropertyValue(Object id) {
        if (PROPERTY_CUSTOMID.equals(id)) {
            return this.getCustomId();
        }
        if (PROPERTY_INHERITERRORHANDLER.equals(id)) {
            return Objects.<Boolean>getField(this, "inheritErrorHandler");
        }
        if (PROPERTY_CORRELATIONEXPRESSION.equals(id)) {
            return this.getCorrelationExpression();
        }
        if (PROPERTY_COMPLETIONPREDICATE.equals(id)) {
            return this.getCompletionPredicate();
        }
        if (PROPERTY_COMPLETIONTIMEOUTEXPRESSION.equals(id)) {
            return this.getCompletionTimeoutExpression();
        }
        if (PROPERTY_COMPLETIONSIZEEXPRESSION.equals(id)) {
            return this.getCompletionSizeExpression();
        }
        if (PROPERTY_PARALLELPROCESSING.equals(id)) {
            return this.getParallelProcessing();
        }
        if (PROPERTY_OPTIMISTICLOCKING.equals(id)) {
            return this.getOptimisticLocking();
        }
        if (PROPERTY_EXECUTORSERVICEREF.equals(id)) {
            return this.getExecutorServiceRef();
        }
        if (PROPERTY_TIMEOUTCHECKEREXECUTORSERVICEREF.equals(id)) {
            return this.getTimeoutCheckerExecutorServiceRef();
        }
        if (PROPERTY_AGGREGATIONREPOSITORYREF.equals(id)) {
            return this.getAggregationRepositoryRef();
        }
        if (PROPERTY_STRATEGYREF.equals(id)) {
            return this.getStrategyRef();
        }
        if (PROPERTY_STRATEGYMETHODNAME.equals(id)) {
            return this.getStrategyMethodName();
        }
        if (PROPERTY_STRATEGYMETHODALLOWNULL.equals(id)) {
            return this.getStrategyMethodAllowNull();
        }
        if (PROPERTY_COMPLETIONSIZE.equals(id)) {
            return this.getCompletionSize();
        }
        if (PROPERTY_COMPLETIONINTERVAL.equals(id)) {
            return this.getCompletionInterval();
        }
        if (PROPERTY_COMPLETIONTIMEOUT.equals(id)) {
            return this.getCompletionTimeout();
        }
        if (PROPERTY_COMPLETIONFROMBATCHCONSUMER.equals(id)) {
            return this.getCompletionFromBatchConsumer();
        }
        if (PROPERTY_GROUPEXCHANGES.equals(id)) {
            return this.getGroupExchanges();
        }
        if (PROPERTY_EAGERCHECKCOMPLETION.equals(id)) {
            return this.getEagerCheckCompletion();
        }
        if (PROPERTY_IGNOREINVALIDCORRELATIONKEYS.equals(id)) {
            return this.getIgnoreInvalidCorrelationKeys();
        }
        if (PROPERTY_CLOSECORRELATIONKEYONCOMPLETION.equals(id)) {
            return this.getCloseCorrelationKeyOnCompletion();
        }
        if (PROPERTY_DISCARDONCOMPLETIONTIMEOUT.equals(id)) {
            return this.getDiscardOnCompletionTimeout();
        }
        if (PROPERTY_FORCECOMPLETIONONSTOP.equals(id)) {
            return this.getForceCompletionOnStop();
        }
        if (PROPERTY_OPTIMISTICLOCKRETRYPOLICYDEFINITION.equals(id)) {
            return this.getOptimisticLockRetryPolicyDefinition();
        }
        return super.getPropertyValue(id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ProcessorDefinition createCamelDefinition() {
        AggregateDefinition answer = new AggregateDefinition();

        answer.setCustomId(toXmlPropertyValue(PROPERTY_CUSTOMID, this.getCustomId()));
        answer.setInheritErrorHandler(toXmlPropertyValue(PROPERTY_INHERITERRORHANDLER, Objects.<Boolean>getField(this, "inheritErrorHandler")));
        Objects.setField(answer, "correlationExpression", toXmlPropertyValue(PROPERTY_CORRELATIONEXPRESSION, this.getCorrelationExpression()));
        Objects.setField(answer, "completionPredicate", toXmlPropertyValue(PROPERTY_COMPLETIONPREDICATE, this.getCompletionPredicate()));
        Objects.setField(answer, "completionTimeoutExpression", toXmlPropertyValue(PROPERTY_COMPLETIONTIMEOUTEXPRESSION, this.getCompletionTimeoutExpression()));
        Objects.setField(answer, "completionSizeExpression", toXmlPropertyValue(PROPERTY_COMPLETIONSIZEEXPRESSION, this.getCompletionSizeExpression()));
        Objects.setField(answer, "parallelProcessing", toXmlPropertyValue(PROPERTY_PARALLELPROCESSING, this.getParallelProcessing()));
        Objects.setField(answer, "optimisticLocking", toXmlPropertyValue(PROPERTY_OPTIMISTICLOCKING, this.getOptimisticLocking()));
        answer.setExecutorServiceRef(toXmlPropertyValue(PROPERTY_EXECUTORSERVICEREF, this.getExecutorServiceRef()));
        answer.setTimeoutCheckerExecutorServiceRef(toXmlPropertyValue(PROPERTY_TIMEOUTCHECKEREXECUTORSERVICEREF, this.getTimeoutCheckerExecutorServiceRef()));
        answer.setAggregationRepositoryRef(toXmlPropertyValue(PROPERTY_AGGREGATIONREPOSITORYREF, this.getAggregationRepositoryRef()));
        answer.setStrategyRef(toXmlPropertyValue(PROPERTY_STRATEGYREF, this.getStrategyRef()));
        answer.setStrategyMethodName(toXmlPropertyValue(PROPERTY_STRATEGYMETHODNAME, this.getStrategyMethodName()));
        answer.setStrategyMethodAllowNull(toXmlPropertyValue(PROPERTY_STRATEGYMETHODALLOWNULL, this.getStrategyMethodAllowNull()));
        answer.setCompletionSize(toXmlPropertyValue(PROPERTY_COMPLETIONSIZE, this.getCompletionSize()));
        answer.setCompletionInterval(toXmlPropertyValue(PROPERTY_COMPLETIONINTERVAL, this.getCompletionInterval()));
        answer.setCompletionTimeout(toXmlPropertyValue(PROPERTY_COMPLETIONTIMEOUT, this.getCompletionTimeout()));
        answer.setCompletionFromBatchConsumer(toXmlPropertyValue(PROPERTY_COMPLETIONFROMBATCHCONSUMER, this.getCompletionFromBatchConsumer()));
        answer.setGroupExchanges(toXmlPropertyValue(PROPERTY_GROUPEXCHANGES, this.getGroupExchanges()));
        answer.setEagerCheckCompletion(toXmlPropertyValue(PROPERTY_EAGERCHECKCOMPLETION, this.getEagerCheckCompletion()));
        answer.setIgnoreInvalidCorrelationKeys(toXmlPropertyValue(PROPERTY_IGNOREINVALIDCORRELATIONKEYS, this.getIgnoreInvalidCorrelationKeys()));
        answer.setCloseCorrelationKeyOnCompletion(toXmlPropertyValue(PROPERTY_CLOSECORRELATIONKEYONCOMPLETION, this.getCloseCorrelationKeyOnCompletion()));
        answer.setDiscardOnCompletionTimeout(toXmlPropertyValue(PROPERTY_DISCARDONCOMPLETIONTIMEOUT, this.getDiscardOnCompletionTimeout()));
        answer.setForceCompletionOnStop(toXmlPropertyValue(PROPERTY_FORCECOMPLETIONONSTOP, this.getForceCompletionOnStop()));
        answer.setOptimisticLockRetryPolicyDefinition(toXmlPropertyValue(PROPERTY_OPTIMISTICLOCKRETRYPOLICYDEFINITION, this.getOptimisticLockRetryPolicyDefinition()));

        super.savePropertiesToCamelDefinition(answer);
        return answer;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<?> getCamelDefinitionClass() {
        return AggregateDefinition.class;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void loadPropertiesFromCamelDefinition(ProcessorDefinition processor) {
        super.loadPropertiesFromCamelDefinition(processor);

        if (processor instanceof AggregateDefinition) {
            AggregateDefinition node = (AggregateDefinition) processor;

            this.setCustomId(node.getCustomId());
            this.setInheritErrorHandler(Objects.<Boolean>getField(node, "inheritErrorHandler"));
            Objects.setField(this, "correlationExpression", node.getCorrelationExpression());
            Objects.setField(this, "completionPredicate", node.getCompletionPredicate());
            Objects.setField(this, "completionTimeoutExpression", node.getCompletionTimeoutExpression());
            Objects.setField(this, "completionSizeExpression", node.getCompletionSizeExpression());
            Objects.setField(this, "parallelProcessing", node.getParallelProcessing());
            Objects.setField(this, "optimisticLocking", node.getOptimisticLocking());
            this.setExecutorServiceRef(node.getExecutorServiceRef());
            this.setTimeoutCheckerExecutorServiceRef(node.getTimeoutCheckerExecutorServiceRef());
            this.setAggregationRepositoryRef(node.getAggregationRepositoryRef());
            this.setStrategyRef(node.getStrategyRef());
            this.setStrategyMethodName(node.getStrategyMethodName());
            this.setStrategyMethodAllowNull(node.getStrategyMethodAllowNull());
            this.setCompletionSize(node.getCompletionSize());
            this.setCompletionInterval(node.getCompletionInterval());
            this.setCompletionTimeout(node.getCompletionTimeout());
            this.setCompletionFromBatchConsumer(node.getCompletionFromBatchConsumer());
            this.setGroupExchanges(node.getGroupExchanges());
            this.setEagerCheckCompletion(node.getEagerCheckCompletion());
            this.setIgnoreInvalidCorrelationKeys(node.getIgnoreInvalidCorrelationKeys());
            this.setCloseCorrelationKeyOnCompletion(node.getCloseCorrelationKeyOnCompletion());
            this.setDiscardOnCompletionTimeout(node.getDiscardOnCompletionTimeout());
            this.setForceCompletionOnStop(node.getForceCompletionOnStop());
            this.setOptimisticLockRetryPolicyDefinition(node.getOptimisticLockRetryPolicyDefinition());
        } else {
            throw new IllegalArgumentException("ProcessorDefinition not an instanceof AggregateDefinition. Was " + processor.getClass().getName());
        }
    }

}
