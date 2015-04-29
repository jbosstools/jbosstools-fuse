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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.osgi.util.NLS;

/**
 * NOTE - this file is auto-generated using Velocity.
 *
 * DO NOT EDIT!
 */
public class Tooltips extends NLS {

    private static final String BUNDLE_NAME = "org.fusesource.ide.camel.model.l10n.tooltips";
    private static final Map<String,String> map = new HashMap<String,String>();

    public static String tooltipAbstractNode;
    public static String tooltipAbstractNodeId;
    public static String tooltipAbstractNodeDescription;

    public static String tooltipEndpoint;
    public static String tooltipEndpointUrl;

    public static String tooltipAggregate;
    public static String tooltipAggregateCorrelationExpression;
    public static String tooltipAggregateCompletionPredicate;
    public static String tooltipAggregateCompletionTimeoutExpression;
    public static String tooltipAggregateCompletionSizeExpression;
    public static String tooltipAggregateParallelProcessing;
    public static String tooltipAggregateOptimisticLocking;
    public static String tooltipAggregateExecutorServiceRef;
    public static String tooltipAggregateTimeoutCheckerExecutorServiceRef;
    public static String tooltipAggregateAggregationRepositoryRef;
    public static String tooltipAggregateStrategyRef;
    public static String tooltipAggregateStrategyMethodName;
    public static String tooltipAggregateStrategyMethodAllowNull;
    public static String tooltipAggregateCompletionSize;
    public static String tooltipAggregateCompletionInterval;
    public static String tooltipAggregateCompletionTimeout;
    public static String tooltipAggregateCompletionFromBatchConsumer;
    public static String tooltipAggregateGroupExchanges;
    public static String tooltipAggregateEagerCheckCompletion;
    public static String tooltipAggregateIgnoreInvalidCorrelationKeys;
    public static String tooltipAggregateCloseCorrelationKeyOnCompletion;
    public static String tooltipAggregateDiscardOnCompletionTimeout;
    public static String tooltipAggregateForceCompletionOnStop;
    public static String tooltipAggregateOptimisticLockRetryPolicyDefinition;

    public static String tooltipAOP;
    public static String tooltipAOPBeforeUri;
    public static String tooltipAOPAfterUri;
    public static String tooltipAOPAfterFinallyUri;

    public static String tooltipBean;
    public static String tooltipBeanRef;
    public static String tooltipBeanMethod;
    public static String tooltipBeanBeanType;
    public static String tooltipBeanCache;
    public static String tooltipBeanMultiParameterArray;

    public static String tooltipCatch;
    public static String tooltipCatchExceptions;
    public static String tooltipCatchHandled;

    public static String tooltipChoice;

    public static String tooltipConvertBody;
    public static String tooltipConvertBodyType;
    public static String tooltipConvertBodyCharset;

    public static String tooltipDelay;
    public static String tooltipDelayExpression;
    public static String tooltipDelayExecutorServiceRef;
    public static String tooltipDelayAsyncDelayed;
    public static String tooltipDelayCallerRunsWhenRejected;

    public static String tooltipDynamicRouter;
    public static String tooltipDynamicRouterExpression;
    public static String tooltipDynamicRouterUriDelimiter;
    public static String tooltipDynamicRouterIgnoreInvalidEndpoints;

    public static String tooltipEnrich;
    public static String tooltipEnrichResourceUri;
    public static String tooltipEnrichAggregationStrategyRef;
    public static String tooltipEnrichAggregationStrategyMethodName;
    public static String tooltipEnrichAggregationStrategyMethodAllowNull;
    public static String tooltipEnrichAggregateOnException;

    public static String tooltipFilter;
    public static String tooltipFilterExpression;

    public static String tooltipFinally;

    public static String tooltipIdempotentConsumer;
    public static String tooltipIdempotentConsumerExpression;
    public static String tooltipIdempotentConsumerMessageIdRepositoryRef;
    public static String tooltipIdempotentConsumerEager;
    public static String tooltipIdempotentConsumerSkipDuplicate;
    public static String tooltipIdempotentConsumerRemoveOnFailure;

    public static String tooltipInOnly;
    public static String tooltipInOnlyUri;

    public static String tooltipInOut;
    public static String tooltipInOutUri;

    public static String tooltipIntercept;

    public static String tooltipInterceptFrom;
    public static String tooltipInterceptFromUri;

    public static String tooltipInterceptSendToEndpoint;
    public static String tooltipInterceptSendToEndpointUri;
    public static String tooltipInterceptSendToEndpointSkipSendToOriginalEndpoint;

    public static String tooltipLoadBalance;
    public static String tooltipLoadBalanceInheritErrorHandler;
    public static String tooltipLoadBalanceRef;
    public static String tooltipLoadBalanceLoadBalancerType;

    public static String tooltipLog;
    public static String tooltipLogMessage;
    public static String tooltipLogLoggingLevel;
    public static String tooltipLogLogName;
    public static String tooltipLogMarker;
    public static String tooltipLogLoggerRef;

    public static String tooltipLoop;
    public static String tooltipLoopExpression;
    public static String tooltipLoopCopy;

    public static String tooltipMarshal;
    public static String tooltipMarshalRef;
    public static String tooltipMarshalDataFormatType;

    public static String tooltipMulticast;
    public static String tooltipMulticastParallelProcessing;
    public static String tooltipMulticastStrategyRef;
    public static String tooltipMulticastStrategyMethodName;
    public static String tooltipMulticastStrategyMethodAllowNull;
    public static String tooltipMulticastExecutorServiceRef;
    public static String tooltipMulticastStreaming;
    public static String tooltipMulticastStopOnException;
    public static String tooltipMulticastTimeout;
    public static String tooltipMulticastOnPrepareRef;
    public static String tooltipMulticastShareUnitOfWork;
    public static String tooltipMulticastParallelAggregate;

    public static String tooltipOnCompletion;
    public static String tooltipOnCompletionMode;
    public static String tooltipOnCompletionOnCompleteOnly;
    public static String tooltipOnCompletionOnFailureOnly;
    public static String tooltipOnCompletionParallelProcessing;
    public static String tooltipOnCompletionExecutorServiceRef;
    public static String tooltipOnCompletionUseOriginalMessagePolicy;

    public static String tooltipOnException;
    public static String tooltipOnExceptionExceptions;
    public static String tooltipOnExceptionRetryWhile;
    public static String tooltipOnExceptionRedeliveryPolicyRef;
    public static String tooltipOnExceptionHandled;
    public static String tooltipOnExceptionContinued;
    public static String tooltipOnExceptionOnRedeliveryRef;
    public static String tooltipOnExceptionUseOriginalMessagePolicy;
    public static String tooltipOnExceptionRedeliveryPolicyType;

    public static String tooltipOtherwise;

    public static String tooltipPipeline;

    public static String tooltipPolicy;
    public static String tooltipPolicyRef;

    public static String tooltipPollEnrich;
    public static String tooltipPollEnrichResourceUri;
    public static String tooltipPollEnrichTimeout;
    public static String tooltipPollEnrichAggregationStrategyRef;
    public static String tooltipPollEnrichAggregationStrategyMethodName;
    public static String tooltipPollEnrichAggregationStrategyMethodAllowNull;
    public static String tooltipPollEnrichAggregateOnException;

    public static String tooltipProcess;
    public static String tooltipProcessRef;

    public static String tooltipRecipientList;
    public static String tooltipRecipientListExpression;
    public static String tooltipRecipientListDelimiter;
    public static String tooltipRecipientListParallelProcessing;
    public static String tooltipRecipientListStrategyRef;
    public static String tooltipRecipientListStrategyMethodName;
    public static String tooltipRecipientListStrategyMethodAllowNull;
    public static String tooltipRecipientListExecutorServiceRef;
    public static String tooltipRecipientListStopOnException;
    public static String tooltipRecipientListIgnoreInvalidEndpoints;
    public static String tooltipRecipientListStreaming;
    public static String tooltipRecipientListTimeout;
    public static String tooltipRecipientListOnPrepareRef;
    public static String tooltipRecipientListShareUnitOfWork;
    public static String tooltipRecipientListCacheSize;
    public static String tooltipRecipientListParallelAggregate;

    public static String tooltipRemoveHeader;
    public static String tooltipRemoveHeaderHeaderName;

    public static String tooltipRemoveHeaders;
    public static String tooltipRemoveHeadersPattern;
    public static String tooltipRemoveHeadersExcludePattern;

    public static String tooltipRemoveProperty;
    public static String tooltipRemovePropertyPropertyName;

    public static String tooltipRemoveProperties;
    public static String tooltipRemovePropertiesPattern;
    public static String tooltipRemovePropertiesExcludePattern;

    public static String tooltipResequence;
    public static String tooltipResequenceExpression;
    public static String tooltipResequenceResequencerConfig;

    public static String tooltipRollback;
    public static String tooltipRollbackMarkRollbackOnly;
    public static String tooltipRollbackMarkRollbackOnlyLast;
    public static String tooltipRollbackMessage;

    public static String tooltipRoute;
    public static String tooltipRouteAutoStartup;
    public static String tooltipRouteDelayer;
    public static String tooltipRouteErrorHandlerRef;
    public static String tooltipRouteGroup;
    public static String tooltipRouteHandleFault;
    public static String tooltipRouteMessageHistory;
    public static String tooltipRouteRoutePolicyRef;
    public static String tooltipRouteShutdownRoute;
    public static String tooltipRouteShutdownRunningTask;
    public static String tooltipRouteStartupOrder;
    public static String tooltipRouteStreamCache;
    public static String tooltipRouteTrace;

    public static String tooltipRoutingSlip;
    public static String tooltipRoutingSlipExpression;
    public static String tooltipRoutingSlipUriDelimiter;
    public static String tooltipRoutingSlipIgnoreInvalidEndpoints;
    public static String tooltipRoutingSlipCacheSize;

    public static String tooltipSampling;
    public static String tooltipSamplingSamplePeriod;
    public static String tooltipSamplingMessageFrequency;
    public static String tooltipSamplingUnits;

    public static String tooltipSetBody;
    public static String tooltipSetBodyExpression;

    public static String tooltipSetExchangePattern;
    public static String tooltipSetExchangePatternPattern;

    public static String tooltipSetFaultBody;
    public static String tooltipSetFaultBodyExpression;

    public static String tooltipSetHeader;
    public static String tooltipSetHeaderExpression;
    public static String tooltipSetHeaderHeaderName;

    public static String tooltipSetOutHeader;
    public static String tooltipSetOutHeaderExpression;
    public static String tooltipSetOutHeaderHeaderName;

    public static String tooltipSetProperty;
    public static String tooltipSetPropertyExpression;
    public static String tooltipSetPropertyPropertyName;

    public static String tooltipSort;
    public static String tooltipSortExpression;
    public static String tooltipSortComparatorRef;

    public static String tooltipSplit;
    public static String tooltipSplitExpression;
    public static String tooltipSplitParallelProcessing;
    public static String tooltipSplitStrategyRef;
    public static String tooltipSplitStrategyMethodName;
    public static String tooltipSplitStrategyMethodAllowNull;
    public static String tooltipSplitExecutorServiceRef;
    public static String tooltipSplitStreaming;
    public static String tooltipSplitStopOnException;
    public static String tooltipSplitTimeout;
    public static String tooltipSplitOnPrepareRef;
    public static String tooltipSplitShareUnitOfWork;
    public static String tooltipSplitParallelAggregate;

    public static String tooltipStop;

    public static String tooltipThreads;
    public static String tooltipThreadsExecutorServiceRef;
    public static String tooltipThreadsPoolSize;
    public static String tooltipThreadsMaxPoolSize;
    public static String tooltipThreadsKeepAliveTime;
    public static String tooltipThreadsTimeUnit;
    public static String tooltipThreadsMaxQueueSize;
    public static String tooltipThreadsAllowCoreThreadTimeOut;
    public static String tooltipThreadsThreadName;
    public static String tooltipThreadsRejectedPolicy;
    public static String tooltipThreadsCallerRunsWhenRejected;

    public static String tooltipThrottle;
    public static String tooltipThrottleExpression;
    public static String tooltipThrottleExecutorServiceRef;
    public static String tooltipThrottleTimePeriodMillis;
    public static String tooltipThrottleAsyncDelayed;
    public static String tooltipThrottleCallerRunsWhenRejected;
    public static String tooltipThrottleRejectExecution;

    public static String tooltipThrowException;
    public static String tooltipThrowExceptionRef;

    public static String tooltipTransacted;
    public static String tooltipTransactedRef;

    public static String tooltipTransform;
    public static String tooltipTransformExpression;

    public static String tooltipTry;

    public static String tooltipUnmarshal;
    public static String tooltipUnmarshalRef;
    public static String tooltipUnmarshalDataFormatType;

    public static String tooltipValidate;
    public static String tooltipValidateExpression;

    public static String tooltipWhen;
    public static String tooltipWhenExpression;

    public static String tooltipWireTap;
    public static String tooltipWireTapUri;
    public static String tooltipWireTapNewExchangeProcessorRef;
    public static String tooltipWireTapNewExchangeExpression;
    public static String tooltipWireTapHeaders;
    public static String tooltipWireTapExecutorServiceRef;
    public static String tooltipWireTapCopy;
    public static String tooltipWireTapOnPrepareRef;


    public static String tooltip(String key) {
        return map.get("tooltip" + key);
    }

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Tooltips.class);

        map.put("AbstractNode", tooltipAbstractNode);
        map.put("AbstractNode.Id", tooltipAbstractNodeId);
        map.put("AbstractNode.Description", tooltipAbstractNodeDescription);

        map.put("Endpoint", tooltipEndpoint);
        map.put("Endpoint.Url", tooltipEndpointUrl);

        map.put("Route", tooltipRoute);

        map.put("tooltipAggregate", tooltipAggregate);
        map.put("tooltipAggregateCorrelationExpression", tooltipAggregateCorrelationExpression);
        map.put("tooltipAggregateCompletionPredicate", tooltipAggregateCompletionPredicate);
        map.put("tooltipAggregateCompletionTimeoutExpression", tooltipAggregateCompletionTimeoutExpression);
        map.put("tooltipAggregateCompletionSizeExpression", tooltipAggregateCompletionSizeExpression);
        map.put("tooltipAggregateParallelProcessing", tooltipAggregateParallelProcessing);
        map.put("tooltipAggregateOptimisticLocking", tooltipAggregateOptimisticLocking);
        map.put("tooltipAggregateExecutorServiceRef", tooltipAggregateExecutorServiceRef);
        map.put("tooltipAggregateTimeoutCheckerExecutorServiceRef", tooltipAggregateTimeoutCheckerExecutorServiceRef);
        map.put("tooltipAggregateAggregationRepositoryRef", tooltipAggregateAggregationRepositoryRef);
        map.put("tooltipAggregateStrategyRef", tooltipAggregateStrategyRef);
        map.put("tooltipAggregateStrategyMethodName", tooltipAggregateStrategyMethodName);
        map.put("tooltipAggregateStrategyMethodAllowNull", tooltipAggregateStrategyMethodAllowNull);
        map.put("tooltipAggregateCompletionSize", tooltipAggregateCompletionSize);
        map.put("tooltipAggregateCompletionInterval", tooltipAggregateCompletionInterval);
        map.put("tooltipAggregateCompletionTimeout", tooltipAggregateCompletionTimeout);
        map.put("tooltipAggregateCompletionFromBatchConsumer", tooltipAggregateCompletionFromBatchConsumer);
        map.put("tooltipAggregateGroupExchanges", tooltipAggregateGroupExchanges);
        map.put("tooltipAggregateEagerCheckCompletion", tooltipAggregateEagerCheckCompletion);
        map.put("tooltipAggregateIgnoreInvalidCorrelationKeys", tooltipAggregateIgnoreInvalidCorrelationKeys);
        map.put("tooltipAggregateCloseCorrelationKeyOnCompletion", tooltipAggregateCloseCorrelationKeyOnCompletion);
        map.put("tooltipAggregateDiscardOnCompletionTimeout", tooltipAggregateDiscardOnCompletionTimeout);
        map.put("tooltipAggregateForceCompletionOnStop", tooltipAggregateForceCompletionOnStop);
        map.put("tooltipAggregateOptimisticLockRetryPolicyDefinition", tooltipAggregateOptimisticLockRetryPolicyDefinition);

        map.put("tooltipAOP", tooltipAOP);
        map.put("tooltipAOPBeforeUri", tooltipAOPBeforeUri);
        map.put("tooltipAOPAfterUri", tooltipAOPAfterUri);
        map.put("tooltipAOPAfterFinallyUri", tooltipAOPAfterFinallyUri);

        map.put("tooltipBean", tooltipBean);
        map.put("tooltipBeanRef", tooltipBeanRef);
        map.put("tooltipBeanMethod", tooltipBeanMethod);
        map.put("tooltipBeanBeanType", tooltipBeanBeanType);
        map.put("tooltipBeanCache", tooltipBeanCache);
        map.put("tooltipBeanMultiParameterArray", tooltipBeanMultiParameterArray);

        map.put("tooltipCatch", tooltipCatch);
        map.put("tooltipCatchExceptions", tooltipCatchExceptions);
        map.put("tooltipCatchHandled", tooltipCatchHandled);

        map.put("tooltipChoice", tooltipChoice);

        map.put("tooltipConvertBody", tooltipConvertBody);
        map.put("tooltipConvertBodyType", tooltipConvertBodyType);
        map.put("tooltipConvertBodyCharset", tooltipConvertBodyCharset);

        map.put("tooltipDelay", tooltipDelay);
        map.put("tooltipDelayExpression", tooltipDelayExpression);
        map.put("tooltipDelayExecutorServiceRef", tooltipDelayExecutorServiceRef);
        map.put("tooltipDelayAsyncDelayed", tooltipDelayAsyncDelayed);
        map.put("tooltipDelayCallerRunsWhenRejected", tooltipDelayCallerRunsWhenRejected);

        map.put("tooltipDynamicRouter", tooltipDynamicRouter);
        map.put("tooltipDynamicRouterExpression", tooltipDynamicRouterExpression);
        map.put("tooltipDynamicRouterUriDelimiter", tooltipDynamicRouterUriDelimiter);
        map.put("tooltipDynamicRouterIgnoreInvalidEndpoints", tooltipDynamicRouterIgnoreInvalidEndpoints);

        map.put("tooltipEnrich", tooltipEnrich);
        map.put("tooltipEnrichResourceUri", tooltipEnrichResourceUri);
        map.put("tooltipEnrichAggregationStrategyRef", tooltipEnrichAggregationStrategyRef);
        map.put("tooltipEnrichAggregationStrategyMethodName", tooltipEnrichAggregationStrategyMethodName);
        map.put("tooltipEnrichAggregationStrategyMethodAllowNull", tooltipEnrichAggregationStrategyMethodAllowNull);
        map.put("tooltipEnrichAggregateOnException", tooltipEnrichAggregateOnException);

        map.put("tooltipFilter", tooltipFilter);
        map.put("tooltipFilterExpression", tooltipFilterExpression);

        map.put("tooltipFinally", tooltipFinally);

        map.put("tooltipIdempotentConsumer", tooltipIdempotentConsumer);
        map.put("tooltipIdempotentConsumerExpression", tooltipIdempotentConsumerExpression);
        map.put("tooltipIdempotentConsumerMessageIdRepositoryRef", tooltipIdempotentConsumerMessageIdRepositoryRef);
        map.put("tooltipIdempotentConsumerEager", tooltipIdempotentConsumerEager);
        map.put("tooltipIdempotentConsumerSkipDuplicate", tooltipIdempotentConsumerSkipDuplicate);
        map.put("tooltipIdempotentConsumerRemoveOnFailure", tooltipIdempotentConsumerRemoveOnFailure);

        map.put("tooltipInOnly", tooltipInOnly);
        map.put("tooltipInOnlyUri", tooltipInOnlyUri);

        map.put("tooltipInOut", tooltipInOut);
        map.put("tooltipInOutUri", tooltipInOutUri);

        map.put("tooltipIntercept", tooltipIntercept);

        map.put("tooltipInterceptFrom", tooltipInterceptFrom);
        map.put("tooltipInterceptFromUri", tooltipInterceptFromUri);

        map.put("tooltipInterceptSendToEndpoint", tooltipInterceptSendToEndpoint);
        map.put("tooltipInterceptSendToEndpointUri", tooltipInterceptSendToEndpointUri);
        map.put("tooltipInterceptSendToEndpointSkipSendToOriginalEndpoint", tooltipInterceptSendToEndpointSkipSendToOriginalEndpoint);

        map.put("tooltipLoadBalance", tooltipLoadBalance);
        map.put("tooltipLoadBalanceInheritErrorHandler", tooltipLoadBalanceInheritErrorHandler);
        map.put("tooltipLoadBalanceRef", tooltipLoadBalanceRef);
        map.put("tooltipLoadBalanceLoadBalancerType", tooltipLoadBalanceLoadBalancerType);

        map.put("tooltipLog", tooltipLog);
        map.put("tooltipLogMessage", tooltipLogMessage);
        map.put("tooltipLogLoggingLevel", tooltipLogLoggingLevel);
        map.put("tooltipLogLogName", tooltipLogLogName);
        map.put("tooltipLogMarker", tooltipLogMarker);
        map.put("tooltipLogLoggerRef", tooltipLogLoggerRef);

        map.put("tooltipLoop", tooltipLoop);
        map.put("tooltipLoopExpression", tooltipLoopExpression);
        map.put("tooltipLoopCopy", tooltipLoopCopy);

        map.put("tooltipMarshal", tooltipMarshal);
        map.put("tooltipMarshalRef", tooltipMarshalRef);
        map.put("tooltipMarshalDataFormatType", tooltipMarshalDataFormatType);

        map.put("tooltipMulticast", tooltipMulticast);
        map.put("tooltipMulticastParallelProcessing", tooltipMulticastParallelProcessing);
        map.put("tooltipMulticastStrategyRef", tooltipMulticastStrategyRef);
        map.put("tooltipMulticastStrategyMethodName", tooltipMulticastStrategyMethodName);
        map.put("tooltipMulticastStrategyMethodAllowNull", tooltipMulticastStrategyMethodAllowNull);
        map.put("tooltipMulticastExecutorServiceRef", tooltipMulticastExecutorServiceRef);
        map.put("tooltipMulticastStreaming", tooltipMulticastStreaming);
        map.put("tooltipMulticastStopOnException", tooltipMulticastStopOnException);
        map.put("tooltipMulticastTimeout", tooltipMulticastTimeout);
        map.put("tooltipMulticastOnPrepareRef", tooltipMulticastOnPrepareRef);
        map.put("tooltipMulticastShareUnitOfWork", tooltipMulticastShareUnitOfWork);
        map.put("tooltipMulticastParallelAggregate", tooltipMulticastParallelAggregate);

        map.put("tooltipOnCompletion", tooltipOnCompletion);
        map.put("tooltipOnCompletionMode", tooltipOnCompletionMode);
        map.put("tooltipOnCompletionOnCompleteOnly", tooltipOnCompletionOnCompleteOnly);
        map.put("tooltipOnCompletionOnFailureOnly", tooltipOnCompletionOnFailureOnly);
        map.put("tooltipOnCompletionParallelProcessing", tooltipOnCompletionParallelProcessing);
        map.put("tooltipOnCompletionExecutorServiceRef", tooltipOnCompletionExecutorServiceRef);
        map.put("tooltipOnCompletionUseOriginalMessagePolicy", tooltipOnCompletionUseOriginalMessagePolicy);

        map.put("tooltipOnException", tooltipOnException);
        map.put("tooltipOnExceptionExceptions", tooltipOnExceptionExceptions);
        map.put("tooltipOnExceptionRetryWhile", tooltipOnExceptionRetryWhile);
        map.put("tooltipOnExceptionRedeliveryPolicyRef", tooltipOnExceptionRedeliveryPolicyRef);
        map.put("tooltipOnExceptionHandled", tooltipOnExceptionHandled);
        map.put("tooltipOnExceptionContinued", tooltipOnExceptionContinued);
        map.put("tooltipOnExceptionOnRedeliveryRef", tooltipOnExceptionOnRedeliveryRef);
        map.put("tooltipOnExceptionUseOriginalMessagePolicy", tooltipOnExceptionUseOriginalMessagePolicy);
        map.put("tooltipOnExceptionRedeliveryPolicyType", tooltipOnExceptionRedeliveryPolicyType);

        map.put("tooltipOtherwise", tooltipOtherwise);

        map.put("tooltipPipeline", tooltipPipeline);

        map.put("tooltipPolicy", tooltipPolicy);
        map.put("tooltipPolicyRef", tooltipPolicyRef);

        map.put("tooltipPollEnrich", tooltipPollEnrich);
        map.put("tooltipPollEnrichResourceUri", tooltipPollEnrichResourceUri);
        map.put("tooltipPollEnrichTimeout", tooltipPollEnrichTimeout);
        map.put("tooltipPollEnrichAggregationStrategyRef", tooltipPollEnrichAggregationStrategyRef);
        map.put("tooltipPollEnrichAggregationStrategyMethodName", tooltipPollEnrichAggregationStrategyMethodName);
        map.put("tooltipPollEnrichAggregationStrategyMethodAllowNull", tooltipPollEnrichAggregationStrategyMethodAllowNull);
        map.put("tooltipPollEnrichAggregateOnException", tooltipPollEnrichAggregateOnException);

        map.put("tooltipProcess", tooltipProcess);
        map.put("tooltipProcessRef", tooltipProcessRef);

        map.put("tooltipRecipientList", tooltipRecipientList);
        map.put("tooltipRecipientListExpression", tooltipRecipientListExpression);
        map.put("tooltipRecipientListDelimiter", tooltipRecipientListDelimiter);
        map.put("tooltipRecipientListParallelProcessing", tooltipRecipientListParallelProcessing);
        map.put("tooltipRecipientListStrategyRef", tooltipRecipientListStrategyRef);
        map.put("tooltipRecipientListStrategyMethodName", tooltipRecipientListStrategyMethodName);
        map.put("tooltipRecipientListStrategyMethodAllowNull", tooltipRecipientListStrategyMethodAllowNull);
        map.put("tooltipRecipientListExecutorServiceRef", tooltipRecipientListExecutorServiceRef);
        map.put("tooltipRecipientListStopOnException", tooltipRecipientListStopOnException);
        map.put("tooltipRecipientListIgnoreInvalidEndpoints", tooltipRecipientListIgnoreInvalidEndpoints);
        map.put("tooltipRecipientListStreaming", tooltipRecipientListStreaming);
        map.put("tooltipRecipientListTimeout", tooltipRecipientListTimeout);
        map.put("tooltipRecipientListOnPrepareRef", tooltipRecipientListOnPrepareRef);
        map.put("tooltipRecipientListShareUnitOfWork", tooltipRecipientListShareUnitOfWork);
        map.put("tooltipRecipientListCacheSize", tooltipRecipientListCacheSize);
        map.put("tooltipRecipientListParallelAggregate", tooltipRecipientListParallelAggregate);

        map.put("tooltipRemoveHeader", tooltipRemoveHeader);
        map.put("tooltipRemoveHeaderHeaderName", tooltipRemoveHeaderHeaderName);

        map.put("tooltipRemoveHeaders", tooltipRemoveHeaders);
        map.put("tooltipRemoveHeadersPattern", tooltipRemoveHeadersPattern);
        map.put("tooltipRemoveHeadersExcludePattern", tooltipRemoveHeadersExcludePattern);

        map.put("tooltipRemoveProperty", tooltipRemoveProperty);
        map.put("tooltipRemovePropertyPropertyName", tooltipRemovePropertyPropertyName);

        map.put("tooltipRemoveProperties", tooltipRemoveProperties);
        map.put("tooltipRemovePropertiesPattern", tooltipRemovePropertiesPattern);
        map.put("tooltipRemovePropertiesExcludePattern", tooltipRemovePropertiesExcludePattern);

        map.put("tooltipResequence", tooltipResequence);
        map.put("tooltipResequenceExpression", tooltipResequenceExpression);
        map.put("tooltipResequenceResequencerConfig", tooltipResequenceResequencerConfig);

        map.put("tooltipRollback", tooltipRollback);
        map.put("tooltipRollbackMarkRollbackOnly", tooltipRollbackMarkRollbackOnly);
        map.put("tooltipRollbackMarkRollbackOnlyLast", tooltipRollbackMarkRollbackOnlyLast);
        map.put("tooltipRollbackMessage", tooltipRollbackMessage);

        map.put("tooltipRoute", tooltipRoute);
        map.put("tooltipRouteAutoStartup", tooltipRouteAutoStartup);
        map.put("tooltipRouteDelayer", tooltipRouteDelayer);
        map.put("tooltipRouteErrorHandlerRef", tooltipRouteErrorHandlerRef);
        map.put("tooltipRouteGroup", tooltipRouteGroup);
        map.put("tooltipRouteHandleFault", tooltipRouteHandleFault);
        map.put("tooltipRouteMessageHistory", tooltipRouteMessageHistory);
        map.put("tooltipRouteRoutePolicyRef", tooltipRouteRoutePolicyRef);
        map.put("tooltipRouteShutdownRoute", tooltipRouteShutdownRoute);
        map.put("tooltipRouteShutdownRunningTask", tooltipRouteShutdownRunningTask);
        map.put("tooltipRouteStartupOrder", tooltipRouteStartupOrder);
        map.put("tooltipRouteStreamCache", tooltipRouteStreamCache);
        map.put("tooltipRouteTrace", tooltipRouteTrace);

        map.put("tooltipRoutingSlip", tooltipRoutingSlip);
        map.put("tooltipRoutingSlipExpression", tooltipRoutingSlipExpression);
        map.put("tooltipRoutingSlipUriDelimiter", tooltipRoutingSlipUriDelimiter);
        map.put("tooltipRoutingSlipIgnoreInvalidEndpoints", tooltipRoutingSlipIgnoreInvalidEndpoints);
        map.put("tooltipRoutingSlipCacheSize", tooltipRoutingSlipCacheSize);

        map.put("tooltipSampling", tooltipSampling);
        map.put("tooltipSamplingSamplePeriod", tooltipSamplingSamplePeriod);
        map.put("tooltipSamplingMessageFrequency", tooltipSamplingMessageFrequency);
        map.put("tooltipSamplingUnits", tooltipSamplingUnits);

        map.put("tooltipSetBody", tooltipSetBody);
        map.put("tooltipSetBodyExpression", tooltipSetBodyExpression);

        map.put("tooltipSetExchangePattern", tooltipSetExchangePattern);
        map.put("tooltipSetExchangePatternPattern", tooltipSetExchangePatternPattern);

        map.put("tooltipSetFaultBody", tooltipSetFaultBody);
        map.put("tooltipSetFaultBodyExpression", tooltipSetFaultBodyExpression);

        map.put("tooltipSetHeader", tooltipSetHeader);
        map.put("tooltipSetHeaderExpression", tooltipSetHeaderExpression);
        map.put("tooltipSetHeaderHeaderName", tooltipSetHeaderHeaderName);

        map.put("tooltipSetOutHeader", tooltipSetOutHeader);
        map.put("tooltipSetOutHeaderExpression", tooltipSetOutHeaderExpression);
        map.put("tooltipSetOutHeaderHeaderName", tooltipSetOutHeaderHeaderName);

        map.put("tooltipSetProperty", tooltipSetProperty);
        map.put("tooltipSetPropertyExpression", tooltipSetPropertyExpression);
        map.put("tooltipSetPropertyPropertyName", tooltipSetPropertyPropertyName);

        map.put("tooltipSort", tooltipSort);
        map.put("tooltipSortExpression", tooltipSortExpression);
        map.put("tooltipSortComparatorRef", tooltipSortComparatorRef);

        map.put("tooltipSplit", tooltipSplit);
        map.put("tooltipSplitExpression", tooltipSplitExpression);
        map.put("tooltipSplitParallelProcessing", tooltipSplitParallelProcessing);
        map.put("tooltipSplitStrategyRef", tooltipSplitStrategyRef);
        map.put("tooltipSplitStrategyMethodName", tooltipSplitStrategyMethodName);
        map.put("tooltipSplitStrategyMethodAllowNull", tooltipSplitStrategyMethodAllowNull);
        map.put("tooltipSplitExecutorServiceRef", tooltipSplitExecutorServiceRef);
        map.put("tooltipSplitStreaming", tooltipSplitStreaming);
        map.put("tooltipSplitStopOnException", tooltipSplitStopOnException);
        map.put("tooltipSplitTimeout", tooltipSplitTimeout);
        map.put("tooltipSplitOnPrepareRef", tooltipSplitOnPrepareRef);
        map.put("tooltipSplitShareUnitOfWork", tooltipSplitShareUnitOfWork);
        map.put("tooltipSplitParallelAggregate", tooltipSplitParallelAggregate);

        map.put("tooltipStop", tooltipStop);

        map.put("tooltipThreads", tooltipThreads);
        map.put("tooltipThreadsExecutorServiceRef", tooltipThreadsExecutorServiceRef);
        map.put("tooltipThreadsPoolSize", tooltipThreadsPoolSize);
        map.put("tooltipThreadsMaxPoolSize", tooltipThreadsMaxPoolSize);
        map.put("tooltipThreadsKeepAliveTime", tooltipThreadsKeepAliveTime);
        map.put("tooltipThreadsTimeUnit", tooltipThreadsTimeUnit);
        map.put("tooltipThreadsMaxQueueSize", tooltipThreadsMaxQueueSize);
        map.put("tooltipThreadsAllowCoreThreadTimeOut", tooltipThreadsAllowCoreThreadTimeOut);
        map.put("tooltipThreadsThreadName", tooltipThreadsThreadName);
        map.put("tooltipThreadsRejectedPolicy", tooltipThreadsRejectedPolicy);
        map.put("tooltipThreadsCallerRunsWhenRejected", tooltipThreadsCallerRunsWhenRejected);

        map.put("tooltipThrottle", tooltipThrottle);
        map.put("tooltipThrottleExpression", tooltipThrottleExpression);
        map.put("tooltipThrottleExecutorServiceRef", tooltipThrottleExecutorServiceRef);
        map.put("tooltipThrottleTimePeriodMillis", tooltipThrottleTimePeriodMillis);
        map.put("tooltipThrottleAsyncDelayed", tooltipThrottleAsyncDelayed);
        map.put("tooltipThrottleCallerRunsWhenRejected", tooltipThrottleCallerRunsWhenRejected);
        map.put("tooltipThrottleRejectExecution", tooltipThrottleRejectExecution);

        map.put("tooltipThrowException", tooltipThrowException);
        map.put("tooltipThrowExceptionRef", tooltipThrowExceptionRef);

        map.put("tooltipTransacted", tooltipTransacted);
        map.put("tooltipTransactedRef", tooltipTransactedRef);

        map.put("tooltipTransform", tooltipTransform);
        map.put("tooltipTransformExpression", tooltipTransformExpression);

        map.put("tooltipTry", tooltipTry);

        map.put("tooltipUnmarshal", tooltipUnmarshal);
        map.put("tooltipUnmarshalRef", tooltipUnmarshalRef);
        map.put("tooltipUnmarshalDataFormatType", tooltipUnmarshalDataFormatType);

        map.put("tooltipValidate", tooltipValidate);
        map.put("tooltipValidateExpression", tooltipValidateExpression);

        map.put("tooltipWhen", tooltipWhen);
        map.put("tooltipWhenExpression", tooltipWhenExpression);

        map.put("tooltipWireTap", tooltipWireTap);
        map.put("tooltipWireTapUri", tooltipWireTapUri);
        map.put("tooltipWireTapNewExchangeProcessorRef", tooltipWireTapNewExchangeProcessorRef);
        map.put("tooltipWireTapNewExchangeExpression", tooltipWireTapNewExchangeExpression);
        map.put("tooltipWireTapHeaders", tooltipWireTapHeaders);
        map.put("tooltipWireTapExecutorServiceRef", tooltipWireTapExecutorServiceRef);
        map.put("tooltipWireTapCopy", tooltipWireTapCopy);
        map.put("tooltipWireTapOnPrepareRef", tooltipWireTapOnPrepareRef);

    }

}
