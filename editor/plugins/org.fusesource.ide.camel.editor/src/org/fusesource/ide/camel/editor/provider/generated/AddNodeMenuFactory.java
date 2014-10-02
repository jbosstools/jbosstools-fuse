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
package org.fusesource.ide.camel.editor.provider.generated;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.tb.ContextMenuEntry;
import org.eclipse.jface.action.IMenuManager;
import org.fusesource.ide.camel.editor.Messages;
import org.fusesource.ide.camel.editor.provider.AbstractAddNodeMenuFactory;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.generated.*;
import org.fusesource.ide.camel.model.generated.Process;

/**
 * A helper class for creating a popup menu to allow the addition of new nodes in a context menu.
 *
 * NOTE - this file is auto-generated using Velocity.
 *
 * DO NOT EDIT!
 *
 * @author lhein
 */
public class AddNodeMenuFactory extends AbstractAddNodeMenuFactory {

    // Fill Graphiti context menus

    @Override
    protected void fillEndpointsContextMenu(ContextMenuEntry menu, ICustomContext context, IFeatureProvider fp) {
        addMenuItem(menu, Messages.paletteEndpointTitle, Messages.paletteEndpointDescription, Endpoint.class, context, fp);

    }

    @Override
    protected void fillRoutingContextMenu(ContextMenuEntry menu, ICustomContext context, IFeatureProvider fp) {
        addMenuItem(menu, Messages.paletteAggregateTitle, Messages.paletteAggregateDescription, Aggregate.class, context, fp);
        addMenuItem(menu, Messages.paletteChoiceTitle, Messages.paletteChoiceDescription, Choice.class, context, fp);
        addMenuItem(menu, Messages.paletteDynamicRouterTitle, Messages.paletteDynamicRouterDescription, DynamicRouter.class, context, fp);
        addMenuItem(menu, Messages.paletteFilterTitle, Messages.paletteFilterDescription, Filter.class, context, fp);
        addMenuItem(menu, Messages.paletteIdempotentConsumerTitle, Messages.paletteIdempotentConsumerDescription, IdempotentConsumer.class, context, fp);
        addMenuItem(menu, Messages.paletteLoadBalanceTitle, Messages.paletteLoadBalanceDescription, LoadBalance.class, context, fp);
        addMenuItem(menu, Messages.paletteMulticastTitle, Messages.paletteMulticastDescription, Multicast.class, context, fp);
        addMenuItem(menu, Messages.paletteOtherwiseTitle, Messages.paletteOtherwiseDescription, Otherwise.class, context, fp);
        addMenuItem(menu, Messages.palettePipelineTitle, Messages.palettePipelineDescription, Pipeline.class, context, fp);
        addMenuItem(menu, Messages.paletteRecipientListTitle, Messages.paletteRecipientListDescription, RecipientList.class, context, fp);
        addMenuItem(menu, Messages.paletteResequenceTitle, Messages.paletteResequenceDescription, Resequence.class, context, fp);
        addMenuItem(menu, Messages.paletteRoutingSlipTitle, Messages.paletteRoutingSlipDescription, RoutingSlip.class, context, fp);
        addMenuItem(menu, Messages.paletteSortTitle, Messages.paletteSortDescription, Sort.class, context, fp);
        addMenuItem(menu, Messages.paletteSplitTitle, Messages.paletteSplitDescription, Split.class, context, fp);
        addMenuItem(menu, Messages.paletteWhenTitle, Messages.paletteWhenDescription, When.class, context, fp);
        addMenuItem(menu, Messages.paletteWireTapTitle, Messages.paletteWireTapDescription, WireTap.class, context, fp);
    }

    @Override
    protected void fillControlFlowContextMenu(ContextMenuEntry menu, ICustomContext context, IFeatureProvider fp) {
        addMenuItem(menu, Messages.paletteCatchTitle, Messages.paletteCatchDescription, Catch.class, context, fp);
        addMenuItem(menu, Messages.paletteDelayTitle, Messages.paletteDelayDescription, Delay.class, context, fp);
        addMenuItem(menu, Messages.paletteFinallyTitle, Messages.paletteFinallyDescription, Finally.class, context, fp);
        addMenuItem(menu, Messages.paletteInterceptTitle, Messages.paletteInterceptDescription, Intercept.class, context, fp);
        addMenuItem(menu, Messages.paletteInterceptFromTitle, Messages.paletteInterceptFromDescription, InterceptFrom.class, context, fp);
        addMenuItem(menu, Messages.paletteInterceptSendToEndpointTitle, Messages.paletteInterceptSendToEndpointDescription, InterceptSendToEndpoint.class, context, fp);
        addMenuItem(menu, Messages.paletteLoopTitle, Messages.paletteLoopDescription, Loop.class, context, fp);
        addMenuItem(menu, Messages.paletteOnCompletionTitle, Messages.paletteOnCompletionDescription, OnCompletion.class, context, fp);
        addMenuItem(menu, Messages.paletteOnExceptionTitle, Messages.paletteOnExceptionDescription, OnException.class, context, fp);
        addMenuItem(menu, Messages.paletteRollbackTitle, Messages.paletteRollbackDescription, Rollback.class, context, fp);
        addMenuItem(menu, Messages.paletteThrottleTitle, Messages.paletteThrottleDescription, Throttle.class, context, fp);
        addMenuItem(menu, Messages.paletteThrowExceptionTitle, Messages.paletteThrowExceptionDescription, ThrowException.class, context, fp);
        addMenuItem(menu, Messages.paletteTransactedTitle, Messages.paletteTransactedDescription, Transacted.class, context, fp);
        addMenuItem(menu, Messages.paletteTryTitle, Messages.paletteTryDescription, Try.class, context, fp);
    }

    @Override
    protected void fillTransformationContextMenu(ContextMenuEntry menu, ICustomContext context, IFeatureProvider fp) {
        addMenuItem(menu, Messages.paletteConvertBodyTitle, Messages.paletteConvertBodyDescription, ConvertBody.class, context, fp);
        addMenuItem(menu, Messages.paletteEnrichTitle, Messages.paletteEnrichDescription, Enrich.class, context, fp);
        addMenuItem(menu, Messages.paletteInOnlyTitle, Messages.paletteInOnlyDescription, InOnly.class, context, fp);
        addMenuItem(menu, Messages.paletteInOutTitle, Messages.paletteInOutDescription, InOut.class, context, fp);
        addMenuItem(menu, Messages.paletteMarshalTitle, Messages.paletteMarshalDescription, Marshal.class, context, fp);
        addMenuItem(menu, Messages.palettePollEnrichTitle, Messages.palettePollEnrichDescription, PollEnrich.class, context, fp);
        addMenuItem(menu, Messages.paletteRemoveHeaderTitle, Messages.paletteRemoveHeaderDescription, RemoveHeader.class, context, fp);
        addMenuItem(menu, Messages.paletteRemoveHeadersTitle, Messages.paletteRemoveHeadersDescription, RemoveHeaders.class, context, fp);
        addMenuItem(menu, Messages.paletteRemovePropertyTitle, Messages.paletteRemovePropertyDescription, RemoveProperty.class, context, fp);
        addMenuItem(menu, Messages.paletteSetBodyTitle, Messages.paletteSetBodyDescription, SetBody.class, context, fp);
        addMenuItem(menu, Messages.paletteSetExchangePatternTitle, Messages.paletteSetExchangePatternDescription, SetExchangePattern.class, context, fp);
        addMenuItem(menu, Messages.paletteSetFaultBodyTitle, Messages.paletteSetFaultBodyDescription, SetFaultBody.class, context, fp);
        addMenuItem(menu, Messages.paletteSetHeaderTitle, Messages.paletteSetHeaderDescription, SetHeader.class, context, fp);
        addMenuItem(menu, Messages.paletteSetOutHeaderTitle, Messages.paletteSetOutHeaderDescription, SetOutHeader.class, context, fp);
        addMenuItem(menu, Messages.paletteSetPropertyTitle, Messages.paletteSetPropertyDescription, SetProperty.class, context, fp);
        addMenuItem(menu, Messages.paletteTransformTitle, Messages.paletteTransformDescription, Transform.class, context, fp);
        addMenuItem(menu, Messages.paletteUnmarshalTitle, Messages.paletteUnmarshalDescription, Unmarshal.class, context, fp);
    }

    @Override
    protected void fillMiscellaneousContextMenu(ContextMenuEntry menu, ICustomContext context, IFeatureProvider fp) {
        addMenuItem(menu, Messages.paletteAOPTitle, Messages.paletteAOPDescription, AOP.class, context, fp);
        addMenuItem(menu, Messages.palettePolicyTitle, Messages.palettePolicyDescription, Policy.class, context, fp);
        addMenuItem(menu, Messages.paletteSamplingTitle, Messages.paletteSamplingDescription, Sampling.class, context, fp);
        addMenuItem(menu, Messages.paletteStopTitle, Messages.paletteStopDescription, Stop.class, context, fp);
        addMenuItem(menu, Messages.paletteThreadsTitle, Messages.paletteThreadsDescription, Threads.class, context, fp);
        addMenuItem(menu, Messages.paletteValidateTitle, Messages.paletteValidateDescription, Validate.class, context, fp);
    }

    // Fill IMenuManager

    @Override
    protected void fillEndpointsMenu(IMenuManager menu) {
        addMenuItem(menu, Messages.paletteEndpointTitle, Messages.paletteEndpointDescription, Endpoint.class);

    }

    @Override
    protected void fillRoutingMenu(IMenuManager menu) {
        addMenuItem(menu, Messages.paletteAggregateTitle, Messages.paletteAggregateDescription, Aggregate.class);
        addMenuItem(menu, Messages.paletteChoiceTitle, Messages.paletteChoiceDescription, Choice.class);
        addMenuItem(menu, Messages.paletteDynamicRouterTitle, Messages.paletteDynamicRouterDescription, DynamicRouter.class);
        addMenuItem(menu, Messages.paletteFilterTitle, Messages.paletteFilterDescription, Filter.class);
        addMenuItem(menu, Messages.paletteIdempotentConsumerTitle, Messages.paletteIdempotentConsumerDescription, IdempotentConsumer.class);
        addMenuItem(menu, Messages.paletteLoadBalanceTitle, Messages.paletteLoadBalanceDescription, LoadBalance.class);
        addMenuItem(menu, Messages.paletteMulticastTitle, Messages.paletteMulticastDescription, Multicast.class);
        addMenuItem(menu, Messages.paletteOtherwiseTitle, Messages.paletteOtherwiseDescription, Otherwise.class);
        addMenuItem(menu, Messages.palettePipelineTitle, Messages.palettePipelineDescription, Pipeline.class);
        addMenuItem(menu, Messages.paletteRecipientListTitle, Messages.paletteRecipientListDescription, RecipientList.class);
        addMenuItem(menu, Messages.paletteResequenceTitle, Messages.paletteResequenceDescription, Resequence.class);
        addMenuItem(menu, Messages.paletteRoutingSlipTitle, Messages.paletteRoutingSlipDescription, RoutingSlip.class);
        addMenuItem(menu, Messages.paletteSortTitle, Messages.paletteSortDescription, Sort.class);
        addMenuItem(menu, Messages.paletteSplitTitle, Messages.paletteSplitDescription, Split.class);
        addMenuItem(menu, Messages.paletteWhenTitle, Messages.paletteWhenDescription, When.class);
        addMenuItem(menu, Messages.paletteWireTapTitle, Messages.paletteWireTapDescription, WireTap.class);
    }

    @Override
    protected void fillControlFlowMenu(IMenuManager menu) {
        addMenuItem(menu, Messages.paletteCatchTitle, Messages.paletteCatchDescription, Catch.class);
        addMenuItem(menu, Messages.paletteDelayTitle, Messages.paletteDelayDescription, Delay.class);
        addMenuItem(menu, Messages.paletteFinallyTitle, Messages.paletteFinallyDescription, Finally.class);
        addMenuItem(menu, Messages.paletteInterceptTitle, Messages.paletteInterceptDescription, Intercept.class);
        addMenuItem(menu, Messages.paletteInterceptFromTitle, Messages.paletteInterceptFromDescription, InterceptFrom.class);
        addMenuItem(menu, Messages.paletteInterceptSendToEndpointTitle, Messages.paletteInterceptSendToEndpointDescription, InterceptSendToEndpoint.class);
        addMenuItem(menu, Messages.paletteLoopTitle, Messages.paletteLoopDescription, Loop.class);
        addMenuItem(menu, Messages.paletteOnCompletionTitle, Messages.paletteOnCompletionDescription, OnCompletion.class);
        addMenuItem(menu, Messages.paletteOnExceptionTitle, Messages.paletteOnExceptionDescription, OnException.class);
        addMenuItem(menu, Messages.paletteRollbackTitle, Messages.paletteRollbackDescription, Rollback.class);
        addMenuItem(menu, Messages.paletteThrottleTitle, Messages.paletteThrottleDescription, Throttle.class);
        addMenuItem(menu, Messages.paletteThrowExceptionTitle, Messages.paletteThrowExceptionDescription, ThrowException.class);
        addMenuItem(menu, Messages.paletteTransactedTitle, Messages.paletteTransactedDescription, Transacted.class);
        addMenuItem(menu, Messages.paletteTryTitle, Messages.paletteTryDescription, Try.class);
    }

    @Override
    protected void fillTransformationMenu(IMenuManager menu) {
        addMenuItem(menu, Messages.paletteConvertBodyTitle, Messages.paletteConvertBodyDescription, ConvertBody.class);
        addMenuItem(menu, Messages.paletteEnrichTitle, Messages.paletteEnrichDescription, Enrich.class);
        addMenuItem(menu, Messages.paletteInOnlyTitle, Messages.paletteInOnlyDescription, InOnly.class);
        addMenuItem(menu, Messages.paletteInOutTitle, Messages.paletteInOutDescription, InOut.class);
        addMenuItem(menu, Messages.paletteMarshalTitle, Messages.paletteMarshalDescription, Marshal.class);
        addMenuItem(menu, Messages.palettePollEnrichTitle, Messages.palettePollEnrichDescription, PollEnrich.class);
        addMenuItem(menu, Messages.paletteRemoveHeaderTitle, Messages.paletteRemoveHeaderDescription, RemoveHeader.class);
        addMenuItem(menu, Messages.paletteRemoveHeadersTitle, Messages.paletteRemoveHeadersDescription, RemoveHeaders.class);
        addMenuItem(menu, Messages.paletteRemovePropertyTitle, Messages.paletteRemovePropertyDescription, RemoveProperty.class);
        addMenuItem(menu, Messages.paletteSetBodyTitle, Messages.paletteSetBodyDescription, SetBody.class);
        addMenuItem(menu, Messages.paletteSetExchangePatternTitle, Messages.paletteSetExchangePatternDescription, SetExchangePattern.class);
        addMenuItem(menu, Messages.paletteSetFaultBodyTitle, Messages.paletteSetFaultBodyDescription, SetFaultBody.class);
        addMenuItem(menu, Messages.paletteSetHeaderTitle, Messages.paletteSetHeaderDescription, SetHeader.class);
        addMenuItem(menu, Messages.paletteSetOutHeaderTitle, Messages.paletteSetOutHeaderDescription, SetOutHeader.class);
        addMenuItem(menu, Messages.paletteSetPropertyTitle, Messages.paletteSetPropertyDescription, SetProperty.class);
        addMenuItem(menu, Messages.paletteTransformTitle, Messages.paletteTransformDescription, Transform.class);
        addMenuItem(menu, Messages.paletteUnmarshalTitle, Messages.paletteUnmarshalDescription, Unmarshal.class);
    }

    @Override
    protected void fillMiscellaneousMenu(IMenuManager menu) {
        addMenuItem(menu, Messages.paletteAOPTitle, Messages.paletteAOPDescription, AOP.class);
        addMenuItem(menu, Messages.palettePolicyTitle, Messages.palettePolicyDescription, Policy.class);
        addMenuItem(menu, Messages.paletteSamplingTitle, Messages.paletteSamplingDescription, Sampling.class);
        addMenuItem(menu, Messages.paletteStopTitle, Messages.paletteStopDescription, Stop.class);
        addMenuItem(menu, Messages.paletteThreadsTitle, Messages.paletteThreadsDescription, Threads.class);
        addMenuItem(menu, Messages.paletteValidateTitle, Messages.paletteValidateDescription, Validate.class);
    }

}
