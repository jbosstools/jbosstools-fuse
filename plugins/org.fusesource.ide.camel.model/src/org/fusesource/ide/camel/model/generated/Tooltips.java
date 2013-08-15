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

 /**
 * NOTE - this file is auto-generated using Scalate. 
 * 
 * DO NOT EDIT!
 */
package org.fusesource.ide.camel.model.generated;

import org.eclipse.osgi.util.NLS;
import java.util.*;

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
    public static String tooltipAggregateExecutorServiceRef;
    public static String tooltipAggregateTimeoutCheckerExecutorServiceRef;
    public static String tooltipAggregateAggregationRepositoryRef;
    public static String tooltipAggregateStrategyRef;
    public static String tooltipAggregateOptimisticLockRetryPolicyDefinition;
    public static String tooltipAggregateParallelProcessing;
    public static String tooltipAggregateOptimisticLocking;
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

    public static String tooltipAOP;
    public static String tooltipAOPBeforeUri;
    public static String tooltipAOPAfterUri;
    public static String tooltipAOPAfterFinallyUri;

    public static String tooltipBean;
    public static String tooltipBeanRef;
    public static String tooltipBeanMethod;
    public static String tooltipBeanBeanType;

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
    public static String tooltipLoadBalanceRef;
    public static String tooltipLoadBalanceLoadBalancerType;

    public static String tooltipLog;
    public static String tooltipLogMessage;
    public static String tooltipLogLogName;
    public static String tooltipLogMarker;
    public static String tooltipLogLoggingLevel;

    public static String tooltipLoop;
    public static String tooltipLoopExpression;
    public static String tooltipLoopCopy;

    public static String tooltipMarshal;
    public static String tooltipMarshalRef;
    public static String tooltipMarshalDataFormatType;

    public static String tooltipMulticast;
    public static String tooltipMulticastStrategyRef;
    public static String tooltipMulticastExecutorServiceRef;
    public static String tooltipMulticastOnPrepareRef;
    public static String tooltipMulticastParallelProcessing;
    public static String tooltipMulticastStreaming;
    public static String tooltipMulticastStopOnException;
    public static String tooltipMulticastTimeout;
    public static String tooltipMulticastShareUnitOfWork;

    public static String tooltipOnCompletion;
    public static String tooltipOnCompletionExecutorServiceRef;
    public static String tooltipOnCompletionOnCompleteOnly;
    public static String tooltipOnCompletionOnFailureOnly;
    public static String tooltipOnCompletionUseOriginalMessagePolicy;

    public static String tooltipOnException;
    public static String tooltipOnExceptionExceptions;
    public static String tooltipOnExceptionRetryWhile;
    public static String tooltipOnExceptionRedeliveryPolicyRef;
    public static String tooltipOnExceptionHandled;
    public static String tooltipOnExceptionContinued;
    public static String tooltipOnExceptionOnRedeliveryRef;
    public static String tooltipOnExceptionRedeliveryPolicy;
    public static String tooltipOnExceptionUseOriginalMessagePolicy;

    public static String tooltipOtherwise;

    public static String tooltipPipeline;

    public static String tooltipPolicy;
    public static String tooltipPolicyRef;

    public static String tooltipPollEnrich;
    public static String tooltipPollEnrichResourceUri;
    public static String tooltipPollEnrichAggregationStrategyRef;
    public static String tooltipPollEnrichTimeout;

    public static String tooltipProcess;
    public static String tooltipProcessRef;

    public static String tooltipRecipientList;
    public static String tooltipRecipientListExpression;
    public static String tooltipRecipientListDelimiter;
    public static String tooltipRecipientListStrategyRef;
    public static String tooltipRecipientListExecutorServiceRef;
    public static String tooltipRecipientListOnPrepareRef;
    public static String tooltipRecipientListParallelProcessing;
    public static String tooltipRecipientListStopOnException;
    public static String tooltipRecipientListIgnoreInvalidEndpoints;
    public static String tooltipRecipientListStreaming;
    public static String tooltipRecipientListTimeout;
    public static String tooltipRecipientListShareUnitOfWork;

    public static String tooltipRemoveHeader;
    public static String tooltipRemoveHeaderHeaderName;

    public static String tooltipRemoveHeaders;
    public static String tooltipRemoveHeadersPattern;
    public static String tooltipRemoveHeadersExcludePattern;

    public static String tooltipRemoveProperty;
    public static String tooltipRemovePropertyPropertyName;

    public static String tooltipResequence;
    public static String tooltipResequenceExpression;
    public static String tooltipResequenceResequencerConfig;

    public static String tooltipRollback;
    public static String tooltipRollbackMessage;
    public static String tooltipRollbackMarkRollbackOnly;
    public static String tooltipRollbackMarkRollbackOnlyLast;

    public static String tooltipRoute;
    public static String tooltipRouteAutoStartup;
    public static String tooltipRouteDelayer;
    public static String tooltipRouteErrorHandlerRef;
    public static String tooltipRouteGroup;
    public static String tooltipRouteHandleFault;
    public static String tooltipRouteMessageHistory;
    public static String tooltipRouteRoutePolicyRef;
    public static String tooltipRouteStreamCache;
    public static String tooltipRouteTrace;

    public static String tooltipRoutingSlip;
    public static String tooltipRoutingSlipExpression;
    public static String tooltipRoutingSlipUriDelimiter;
    public static String tooltipRoutingSlipIgnoreInvalidEndpoints;

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
    public static String tooltipSplitStrategyRef;
    public static String tooltipSplitExecutorServiceRef;
    public static String tooltipSplitOnPrepareRef;
    public static String tooltipSplitParallelProcessing;
    public static String tooltipSplitStreaming;
    public static String tooltipSplitStopOnException;
    public static String tooltipSplitTimeout;
    public static String tooltipSplitShareUnitOfWork;

    public static String tooltipStop;

    public static String tooltipThreads;
    public static String tooltipThreadsExecutorServiceRef;
    public static String tooltipThreadsThreadName;
    public static String tooltipThreadsPoolSize;
    public static String tooltipThreadsMaxPoolSize;
    public static String tooltipThreadsKeepAliveTime;
    public static String tooltipThreadsTimeUnit;
    public static String tooltipThreadsMaxQueueSize;
    public static String tooltipThreadsRejectedPolicy;
    public static String tooltipThreadsCallerRunsWhenRejected;

    public static String tooltipThrottle;
    public static String tooltipThrottleExpression;
    public static String tooltipThrottleExecutorServiceRef;
    public static String tooltipThrottleTimePeriodMillis;
    public static String tooltipThrottleAsyncDelayed;
    public static String tooltipThrottleCallerRunsWhenRejected;

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
    public static String tooltipWireTapOnPrepareRef;
    public static String tooltipWireTapCopy;


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
    map.put("tooltipAggregate.CorrelationExpression", tooltipAggregateCorrelationExpression);
    map.put("tooltipAggregate.CompletionPredicate", tooltipAggregateCompletionPredicate);
    map.put("tooltipAggregate.CompletionTimeoutExpression", tooltipAggregateCompletionTimeoutExpression);
    map.put("tooltipAggregate.CompletionSizeExpression", tooltipAggregateCompletionSizeExpression);
    map.put("tooltipAggregate.ExecutorServiceRef", tooltipAggregateExecutorServiceRef);
    map.put("tooltipAggregate.TimeoutCheckerExecutorServiceRef", tooltipAggregateTimeoutCheckerExecutorServiceRef);
    map.put("tooltipAggregate.AggregationRepositoryRef", tooltipAggregateAggregationRepositoryRef);
    map.put("tooltipAggregate.StrategyRef", tooltipAggregateStrategyRef);
    map.put("tooltipAggregate.OptimisticLockRetryPolicyDefinition", tooltipAggregateOptimisticLockRetryPolicyDefinition);
    map.put("tooltipAggregate.ParallelProcessing", tooltipAggregateParallelProcessing);
    map.put("tooltipAggregate.OptimisticLocking", tooltipAggregateOptimisticLocking);
    map.put("tooltipAggregate.CompletionSize", tooltipAggregateCompletionSize);
    map.put("tooltipAggregate.CompletionInterval", tooltipAggregateCompletionInterval);
    map.put("tooltipAggregate.CompletionTimeout", tooltipAggregateCompletionTimeout);
    map.put("tooltipAggregate.CompletionFromBatchConsumer", tooltipAggregateCompletionFromBatchConsumer);
    map.put("tooltipAggregate.GroupExchanges", tooltipAggregateGroupExchanges);
    map.put("tooltipAggregate.EagerCheckCompletion", tooltipAggregateEagerCheckCompletion);
    map.put("tooltipAggregate.IgnoreInvalidCorrelationKeys", tooltipAggregateIgnoreInvalidCorrelationKeys);
    map.put("tooltipAggregate.CloseCorrelationKeyOnCompletion", tooltipAggregateCloseCorrelationKeyOnCompletion);
    map.put("tooltipAggregate.DiscardOnCompletionTimeout", tooltipAggregateDiscardOnCompletionTimeout);
    map.put("tooltipAggregate.ForceCompletionOnStop", tooltipAggregateForceCompletionOnStop);
    map.put("tooltipAOP", tooltipAOP);
    map.put("tooltipAOP.BeforeUri", tooltipAOPBeforeUri);
    map.put("tooltipAOP.AfterUri", tooltipAOPAfterUri);
    map.put("tooltipAOP.AfterFinallyUri", tooltipAOPAfterFinallyUri);
    map.put("tooltipBean", tooltipBean);
    map.put("tooltipBean.Ref", tooltipBeanRef);
    map.put("tooltipBean.Method", tooltipBeanMethod);
    map.put("tooltipBean.BeanType", tooltipBeanBeanType);
    map.put("tooltipCatch", tooltipCatch);
    map.put("tooltipCatch.Exceptions", tooltipCatchExceptions);
    map.put("tooltipCatch.Handled", tooltipCatchHandled);
    map.put("tooltipChoice", tooltipChoice);
    map.put("tooltipConvertBody", tooltipConvertBody);
    map.put("tooltipConvertBody.Type", tooltipConvertBodyType);
    map.put("tooltipConvertBody.Charset", tooltipConvertBodyCharset);
    map.put("tooltipDelay", tooltipDelay);
    map.put("tooltipDelay.Expression", tooltipDelayExpression);
    map.put("tooltipDelay.ExecutorServiceRef", tooltipDelayExecutorServiceRef);
    map.put("tooltipDelay.AsyncDelayed", tooltipDelayAsyncDelayed);
    map.put("tooltipDelay.CallerRunsWhenRejected", tooltipDelayCallerRunsWhenRejected);
    map.put("tooltipDynamicRouter", tooltipDynamicRouter);
    map.put("tooltipDynamicRouter.Expression", tooltipDynamicRouterExpression);
    map.put("tooltipDynamicRouter.UriDelimiter", tooltipDynamicRouterUriDelimiter);
    map.put("tooltipDynamicRouter.IgnoreInvalidEndpoints", tooltipDynamicRouterIgnoreInvalidEndpoints);
    map.put("tooltipEnrich", tooltipEnrich);
    map.put("tooltipEnrich.ResourceUri", tooltipEnrichResourceUri);
    map.put("tooltipEnrich.AggregationStrategyRef", tooltipEnrichAggregationStrategyRef);
    map.put("tooltipFilter", tooltipFilter);
    map.put("tooltipFilter.Expression", tooltipFilterExpression);
    map.put("tooltipFinally", tooltipFinally);
    map.put("tooltipIdempotentConsumer", tooltipIdempotentConsumer);
    map.put("tooltipIdempotentConsumer.Expression", tooltipIdempotentConsumerExpression);
    map.put("tooltipIdempotentConsumer.MessageIdRepositoryRef", tooltipIdempotentConsumerMessageIdRepositoryRef);
    map.put("tooltipIdempotentConsumer.Eager", tooltipIdempotentConsumerEager);
    map.put("tooltipIdempotentConsumer.SkipDuplicate", tooltipIdempotentConsumerSkipDuplicate);
    map.put("tooltipIdempotentConsumer.RemoveOnFailure", tooltipIdempotentConsumerRemoveOnFailure);
    map.put("tooltipInOnly", tooltipInOnly);
    map.put("tooltipInOnly.Uri", tooltipInOnlyUri);
    map.put("tooltipInOut", tooltipInOut);
    map.put("tooltipInOut.Uri", tooltipInOutUri);
    map.put("tooltipIntercept", tooltipIntercept);
    map.put("tooltipInterceptFrom", tooltipInterceptFrom);
    map.put("tooltipInterceptFrom.Uri", tooltipInterceptFromUri);
    map.put("tooltipInterceptSendToEndpoint", tooltipInterceptSendToEndpoint);
    map.put("tooltipInterceptSendToEndpoint.Uri", tooltipInterceptSendToEndpointUri);
    map.put("tooltipInterceptSendToEndpoint.SkipSendToOriginalEndpoint", tooltipInterceptSendToEndpointSkipSendToOriginalEndpoint);
    map.put("tooltipLoadBalance", tooltipLoadBalance);
    map.put("tooltipLoadBalance.Ref", tooltipLoadBalanceRef);
    map.put("tooltipLoadBalance.LoadBalancerType", tooltipLoadBalanceLoadBalancerType);
    map.put("tooltipLog", tooltipLog);
    map.put("tooltipLog.Message", tooltipLogMessage);
    map.put("tooltipLog.LogName", tooltipLogLogName);
    map.put("tooltipLog.Marker", tooltipLogMarker);
    map.put("tooltipLog.LoggingLevel", tooltipLogLoggingLevel);
    map.put("tooltipLoop", tooltipLoop);
    map.put("tooltipLoop.Expression", tooltipLoopExpression);
    map.put("tooltipLoop.Copy", tooltipLoopCopy);
    map.put("tooltipMarshal", tooltipMarshal);
    map.put("tooltipMarshal.Ref", tooltipMarshalRef);
    map.put("tooltipMarshal.DataFormatType", tooltipMarshalDataFormatType);
    map.put("tooltipMulticast", tooltipMulticast);
    map.put("tooltipMulticast.StrategyRef", tooltipMulticastStrategyRef);
    map.put("tooltipMulticast.ExecutorServiceRef", tooltipMulticastExecutorServiceRef);
    map.put("tooltipMulticast.OnPrepareRef", tooltipMulticastOnPrepareRef);
    map.put("tooltipMulticast.ParallelProcessing", tooltipMulticastParallelProcessing);
    map.put("tooltipMulticast.Streaming", tooltipMulticastStreaming);
    map.put("tooltipMulticast.StopOnException", tooltipMulticastStopOnException);
    map.put("tooltipMulticast.Timeout", tooltipMulticastTimeout);
    map.put("tooltipMulticast.ShareUnitOfWork", tooltipMulticastShareUnitOfWork);
    map.put("tooltipOnCompletion", tooltipOnCompletion);
    map.put("tooltipOnCompletion.ExecutorServiceRef", tooltipOnCompletionExecutorServiceRef);
    map.put("tooltipOnCompletion.OnCompleteOnly", tooltipOnCompletionOnCompleteOnly);
    map.put("tooltipOnCompletion.OnFailureOnly", tooltipOnCompletionOnFailureOnly);
    map.put("tooltipOnCompletion.UseOriginalMessagePolicy", tooltipOnCompletionUseOriginalMessagePolicy);
    map.put("tooltipOnException", tooltipOnException);
    map.put("tooltipOnException.Exceptions", tooltipOnExceptionExceptions);
    map.put("tooltipOnException.RetryWhile", tooltipOnExceptionRetryWhile);
    map.put("tooltipOnException.RedeliveryPolicyRef", tooltipOnExceptionRedeliveryPolicyRef);
    map.put("tooltipOnException.Handled", tooltipOnExceptionHandled);
    map.put("tooltipOnException.Continued", tooltipOnExceptionContinued);
    map.put("tooltipOnException.OnRedeliveryRef", tooltipOnExceptionOnRedeliveryRef);
    map.put("tooltipOnException.RedeliveryPolicy", tooltipOnExceptionRedeliveryPolicy);
    map.put("tooltipOnException.UseOriginalMessagePolicy", tooltipOnExceptionUseOriginalMessagePolicy);
    map.put("tooltipOtherwise", tooltipOtherwise);
    map.put("tooltipPipeline", tooltipPipeline);
    map.put("tooltipPolicy", tooltipPolicy);
    map.put("tooltipPolicy.Ref", tooltipPolicyRef);
    map.put("tooltipPollEnrich", tooltipPollEnrich);
    map.put("tooltipPollEnrich.ResourceUri", tooltipPollEnrichResourceUri);
    map.put("tooltipPollEnrich.AggregationStrategyRef", tooltipPollEnrichAggregationStrategyRef);
    map.put("tooltipPollEnrich.Timeout", tooltipPollEnrichTimeout);
    map.put("tooltipProcess", tooltipProcess);
    map.put("tooltipProcess.Ref", tooltipProcessRef);
    map.put("tooltipRecipientList", tooltipRecipientList);
    map.put("tooltipRecipientList.Expression", tooltipRecipientListExpression);
    map.put("tooltipRecipientList.Delimiter", tooltipRecipientListDelimiter);
    map.put("tooltipRecipientList.StrategyRef", tooltipRecipientListStrategyRef);
    map.put("tooltipRecipientList.ExecutorServiceRef", tooltipRecipientListExecutorServiceRef);
    map.put("tooltipRecipientList.OnPrepareRef", tooltipRecipientListOnPrepareRef);
    map.put("tooltipRecipientList.ParallelProcessing", tooltipRecipientListParallelProcessing);
    map.put("tooltipRecipientList.StopOnException", tooltipRecipientListStopOnException);
    map.put("tooltipRecipientList.IgnoreInvalidEndpoints", tooltipRecipientListIgnoreInvalidEndpoints);
    map.put("tooltipRecipientList.Streaming", tooltipRecipientListStreaming);
    map.put("tooltipRecipientList.Timeout", tooltipRecipientListTimeout);
    map.put("tooltipRecipientList.ShareUnitOfWork", tooltipRecipientListShareUnitOfWork);
    map.put("tooltipRemoveHeader", tooltipRemoveHeader);
    map.put("tooltipRemoveHeader.HeaderName", tooltipRemoveHeaderHeaderName);
    map.put("tooltipRemoveHeaders", tooltipRemoveHeaders);
    map.put("tooltipRemoveHeaders.Pattern", tooltipRemoveHeadersPattern);
    map.put("tooltipRemoveHeaders.ExcludePattern", tooltipRemoveHeadersExcludePattern);
    map.put("tooltipRemoveProperty", tooltipRemoveProperty);
    map.put("tooltipRemoveProperty.PropertyName", tooltipRemovePropertyPropertyName);
    map.put("tooltipResequence", tooltipResequence);
    map.put("tooltipResequence.Expression", tooltipResequenceExpression);
    map.put("tooltipResequence.ResequencerConfig", tooltipResequenceResequencerConfig);
    map.put("tooltipRollback", tooltipRollback);
    map.put("tooltipRollback.Message", tooltipRollbackMessage);
    map.put("tooltipRollback.MarkRollbackOnly", tooltipRollbackMarkRollbackOnly);
    map.put("tooltipRollback.MarkRollbackOnlyLast", tooltipRollbackMarkRollbackOnlyLast);
    map.put("tooltipRoute", tooltipRoute);
    map.put("tooltipRoute.AutoStartup", tooltipRouteAutoStartup);
    map.put("tooltipRoute.Delayer", tooltipRouteDelayer);
    map.put("tooltipRoute.ErrorHandlerRef", tooltipRouteErrorHandlerRef);
    map.put("tooltipRoute.Group", tooltipRouteGroup);
    map.put("tooltipRoute.HandleFault", tooltipRouteHandleFault);
    map.put("tooltipRoute.MessageHistory", tooltipRouteMessageHistory);
    map.put("tooltipRoute.RoutePolicyRef", tooltipRouteRoutePolicyRef);
    map.put("tooltipRoute.StreamCache", tooltipRouteStreamCache);
    map.put("tooltipRoute.Trace", tooltipRouteTrace);
    map.put("tooltipRoutingSlip", tooltipRoutingSlip);
    map.put("tooltipRoutingSlip.Expression", tooltipRoutingSlipExpression);
    map.put("tooltipRoutingSlip.UriDelimiter", tooltipRoutingSlipUriDelimiter);
    map.put("tooltipRoutingSlip.IgnoreInvalidEndpoints", tooltipRoutingSlipIgnoreInvalidEndpoints);
    map.put("tooltipSampling", tooltipSampling);
    map.put("tooltipSampling.SamplePeriod", tooltipSamplingSamplePeriod);
    map.put("tooltipSampling.MessageFrequency", tooltipSamplingMessageFrequency);
    map.put("tooltipSampling.Units", tooltipSamplingUnits);
    map.put("tooltipSetBody", tooltipSetBody);
    map.put("tooltipSetBody.Expression", tooltipSetBodyExpression);
    map.put("tooltipSetExchangePattern", tooltipSetExchangePattern);
    map.put("tooltipSetExchangePattern.Pattern", tooltipSetExchangePatternPattern);
    map.put("tooltipSetFaultBody", tooltipSetFaultBody);
    map.put("tooltipSetFaultBody.Expression", tooltipSetFaultBodyExpression);
    map.put("tooltipSetHeader", tooltipSetHeader);
    map.put("tooltipSetHeader.Expression", tooltipSetHeaderExpression);
    map.put("tooltipSetHeader.HeaderName", tooltipSetHeaderHeaderName);
    map.put("tooltipSetOutHeader", tooltipSetOutHeader);
    map.put("tooltipSetOutHeader.Expression", tooltipSetOutHeaderExpression);
    map.put("tooltipSetOutHeader.HeaderName", tooltipSetOutHeaderHeaderName);
    map.put("tooltipSetProperty", tooltipSetProperty);
    map.put("tooltipSetProperty.Expression", tooltipSetPropertyExpression);
    map.put("tooltipSetProperty.PropertyName", tooltipSetPropertyPropertyName);
    map.put("tooltipSort", tooltipSort);
    map.put("tooltipSort.Expression", tooltipSortExpression);
    map.put("tooltipSort.ComparatorRef", tooltipSortComparatorRef);
    map.put("tooltipSplit", tooltipSplit);
    map.put("tooltipSplit.Expression", tooltipSplitExpression);
    map.put("tooltipSplit.StrategyRef", tooltipSplitStrategyRef);
    map.put("tooltipSplit.ExecutorServiceRef", tooltipSplitExecutorServiceRef);
    map.put("tooltipSplit.OnPrepareRef", tooltipSplitOnPrepareRef);
    map.put("tooltipSplit.ParallelProcessing", tooltipSplitParallelProcessing);
    map.put("tooltipSplit.Streaming", tooltipSplitStreaming);
    map.put("tooltipSplit.StopOnException", tooltipSplitStopOnException);
    map.put("tooltipSplit.Timeout", tooltipSplitTimeout);
    map.put("tooltipSplit.ShareUnitOfWork", tooltipSplitShareUnitOfWork);
    map.put("tooltipStop", tooltipStop);
    map.put("tooltipThreads", tooltipThreads);
    map.put("tooltipThreads.ExecutorServiceRef", tooltipThreadsExecutorServiceRef);
    map.put("tooltipThreads.ThreadName", tooltipThreadsThreadName);
    map.put("tooltipThreads.PoolSize", tooltipThreadsPoolSize);
    map.put("tooltipThreads.MaxPoolSize", tooltipThreadsMaxPoolSize);
    map.put("tooltipThreads.KeepAliveTime", tooltipThreadsKeepAliveTime);
    map.put("tooltipThreads.TimeUnit", tooltipThreadsTimeUnit);
    map.put("tooltipThreads.MaxQueueSize", tooltipThreadsMaxQueueSize);
    map.put("tooltipThreads.RejectedPolicy", tooltipThreadsRejectedPolicy);
    map.put("tooltipThreads.CallerRunsWhenRejected", tooltipThreadsCallerRunsWhenRejected);
    map.put("tooltipThrottle", tooltipThrottle);
    map.put("tooltipThrottle.Expression", tooltipThrottleExpression);
    map.put("tooltipThrottle.ExecutorServiceRef", tooltipThrottleExecutorServiceRef);
    map.put("tooltipThrottle.TimePeriodMillis", tooltipThrottleTimePeriodMillis);
    map.put("tooltipThrottle.AsyncDelayed", tooltipThrottleAsyncDelayed);
    map.put("tooltipThrottle.CallerRunsWhenRejected", tooltipThrottleCallerRunsWhenRejected);
    map.put("tooltipThrowException", tooltipThrowException);
    map.put("tooltipThrowException.Ref", tooltipThrowExceptionRef);
    map.put("tooltipTransacted", tooltipTransacted);
    map.put("tooltipTransacted.Ref", tooltipTransactedRef);
    map.put("tooltipTransform", tooltipTransform);
    map.put("tooltipTransform.Expression", tooltipTransformExpression);
    map.put("tooltipTry", tooltipTry);
    map.put("tooltipUnmarshal", tooltipUnmarshal);
    map.put("tooltipUnmarshal.Ref", tooltipUnmarshalRef);
    map.put("tooltipUnmarshal.DataFormatType", tooltipUnmarshalDataFormatType);
    map.put("tooltipValidate", tooltipValidate);
    map.put("tooltipValidate.Expression", tooltipValidateExpression);
    map.put("tooltipWhen", tooltipWhen);
    map.put("tooltipWhen.Expression", tooltipWhenExpression);
    map.put("tooltipWireTap", tooltipWireTap);
    map.put("tooltipWireTap.Uri", tooltipWireTapUri);
    map.put("tooltipWireTap.NewExchangeProcessorRef", tooltipWireTapNewExchangeProcessorRef);
    map.put("tooltipWireTap.NewExchangeExpression", tooltipWireTapNewExchangeExpression);
    map.put("tooltipWireTap.Headers", tooltipWireTapHeaders);
    map.put("tooltipWireTap.ExecutorServiceRef", tooltipWireTapExecutorServiceRef);
    map.put("tooltipWireTap.OnPrepareRef", tooltipWireTapOnPrepareRef);
    map.put("tooltipWireTap.Copy", tooltipWireTapCopy);
    }
}
