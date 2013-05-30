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
package org.fusesource.ide.camel.editor.provider.generated;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.fusesource.ide.camel.editor.Messages;
import org.fusesource.ide.camel.editor.features.create.CreateFigureFeature;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.generated.AOP;
import org.fusesource.ide.camel.model.generated.Aggregate;
import org.fusesource.ide.camel.model.generated.Bean;
import org.fusesource.ide.camel.model.generated.Catch;
import org.fusesource.ide.camel.model.generated.Choice;
import org.fusesource.ide.camel.model.generated.ConvertBody;
import org.fusesource.ide.camel.model.generated.Delay;
import org.fusesource.ide.camel.model.generated.DynamicRouter;
import org.fusesource.ide.camel.model.generated.Enrich;
import org.fusesource.ide.camel.model.generated.Filter;
import org.fusesource.ide.camel.model.generated.Finally;
import org.fusesource.ide.camel.model.generated.IdempotentConsumer;
import org.fusesource.ide.camel.model.generated.InOnly;
import org.fusesource.ide.camel.model.generated.InOut;
import org.fusesource.ide.camel.model.generated.Intercept;
import org.fusesource.ide.camel.model.generated.InterceptFrom;
import org.fusesource.ide.camel.model.generated.InterceptSendToEndpoint;
import org.fusesource.ide.camel.model.generated.LoadBalance;
import org.fusesource.ide.camel.model.generated.Log;
import org.fusesource.ide.camel.model.generated.Loop;
import org.fusesource.ide.camel.model.generated.Marshal;
import org.fusesource.ide.camel.model.generated.Multicast;
import org.fusesource.ide.camel.model.generated.OnCompletion;
import org.fusesource.ide.camel.model.generated.OnException;
import org.fusesource.ide.camel.model.generated.Otherwise;
import org.fusesource.ide.camel.model.generated.Pipeline;
import org.fusesource.ide.camel.model.generated.Policy;
import org.fusesource.ide.camel.model.generated.PollEnrich;
import org.fusesource.ide.camel.model.generated.Process;
import org.fusesource.ide.camel.model.generated.RecipientList;
import org.fusesource.ide.camel.model.generated.RemoveHeader;
import org.fusesource.ide.camel.model.generated.RemoveHeaders;
import org.fusesource.ide.camel.model.generated.RemoveProperty;
import org.fusesource.ide.camel.model.generated.Resequence;
import org.fusesource.ide.camel.model.generated.Rollback;
import org.fusesource.ide.camel.model.generated.Route;
import org.fusesource.ide.camel.model.generated.RoutingSlip;
import org.fusesource.ide.camel.model.generated.Sampling;
import org.fusesource.ide.camel.model.generated.SetBody;
import org.fusesource.ide.camel.model.generated.SetExchangePattern;
import org.fusesource.ide.camel.model.generated.SetFaultBody;
import org.fusesource.ide.camel.model.generated.SetHeader;
import org.fusesource.ide.camel.model.generated.SetOutHeader;
import org.fusesource.ide.camel.model.generated.SetProperty;
import org.fusesource.ide.camel.model.generated.Sort;
import org.fusesource.ide.camel.model.generated.Split;
import org.fusesource.ide.camel.model.generated.Stop;
import org.fusesource.ide.camel.model.generated.Threads;
import org.fusesource.ide.camel.model.generated.Throttle;
import org.fusesource.ide.camel.model.generated.ThrowException;
import org.fusesource.ide.camel.model.generated.Transacted;
import org.fusesource.ide.camel.model.generated.Transform;
import org.fusesource.ide.camel.model.generated.Try;
import org.fusesource.ide.camel.model.generated.Unmarshal;
import org.fusesource.ide.camel.model.generated.Validate;
import org.fusesource.ide.camel.model.generated.When;
import org.fusesource.ide.camel.model.generated.WireTap;


/**
* @author lhein
*/
public final class ProviderHelper{

	private ProviderHelper() {
		// private ddefault constructor
	}

	/**
	 * helper method to return all create features available for the palette
	 * 
	 * @param fp	the feature provider
	 * @return	an array of create features for the palette
	 */
	public static ICreateFeature[] getCreateFeatures(IFeatureProvider fp) {
		return new ICreateFeature[] {
				new CreateFigureFeature<Endpoint>(fp, Messages.paletteEndpointTitle, Messages.paletteEndpointDescription, Endpoint.class)
 
 
 				, new CreateFigureFeature<Aggregate>(fp, Messages.paletteAggregateTitle, Messages.paletteAggregateDescription, Aggregate.class)
   
 
 				, new CreateFigureFeature<AOP>(fp, Messages.paletteAOPTitle, Messages.paletteAOPDescription, AOP.class)
   
 
 				, new CreateFigureFeature<Bean>(fp, Messages.paletteBeanTitle, Messages.paletteBeanDescription, Bean.class)
   
 
 				, new CreateFigureFeature<Catch>(fp, Messages.paletteCatchTitle, Messages.paletteCatchDescription, Catch.class)
   
 
 				, new CreateFigureFeature<Choice>(fp, Messages.paletteChoiceTitle, Messages.paletteChoiceDescription, Choice.class)
   
 
 				, new CreateFigureFeature<ConvertBody>(fp, Messages.paletteConvertBodyTitle, Messages.paletteConvertBodyDescription, ConvertBody.class)
   
 
 				, new CreateFigureFeature<Delay>(fp, Messages.paletteDelayTitle, Messages.paletteDelayDescription, Delay.class)
   
 
 				, new CreateFigureFeature<DynamicRouter>(fp, Messages.paletteDynamicRouterTitle, Messages.paletteDynamicRouterDescription, DynamicRouter.class)
   
 
 				, new CreateFigureFeature<Enrich>(fp, Messages.paletteEnrichTitle, Messages.paletteEnrichDescription, Enrich.class)
   
 
 				, new CreateFigureFeature<Filter>(fp, Messages.paletteFilterTitle, Messages.paletteFilterDescription, Filter.class)
   
 
 				, new CreateFigureFeature<Finally>(fp, Messages.paletteFinallyTitle, Messages.paletteFinallyDescription, Finally.class)
   
 
 				, new CreateFigureFeature<IdempotentConsumer>(fp, Messages.paletteIdempotentConsumerTitle, Messages.paletteIdempotentConsumerDescription, IdempotentConsumer.class)
   
 
 				, new CreateFigureFeature<InOnly>(fp, Messages.paletteInOnlyTitle, Messages.paletteInOnlyDescription, InOnly.class)
   
 
 				, new CreateFigureFeature<InOut>(fp, Messages.paletteInOutTitle, Messages.paletteInOutDescription, InOut.class)
   
 
 				, new CreateFigureFeature<Intercept>(fp, Messages.paletteInterceptTitle, Messages.paletteInterceptDescription, Intercept.class)
   
 
 				, new CreateFigureFeature<InterceptFrom>(fp, Messages.paletteInterceptFromTitle, Messages.paletteInterceptFromDescription, InterceptFrom.class)
   
 
 				, new CreateFigureFeature<InterceptSendToEndpoint>(fp, Messages.paletteInterceptSendToEndpointTitle, Messages.paletteInterceptSendToEndpointDescription, InterceptSendToEndpoint.class)
   
 
 				, new CreateFigureFeature<LoadBalance>(fp, Messages.paletteLoadBalanceTitle, Messages.paletteLoadBalanceDescription, LoadBalance.class)
   
 
 				, new CreateFigureFeature<Log>(fp, Messages.paletteLogTitle, Messages.paletteLogDescription, Log.class)
   
 
 				, new CreateFigureFeature<Loop>(fp, Messages.paletteLoopTitle, Messages.paletteLoopDescription, Loop.class)
   
 
 				, new CreateFigureFeature<Marshal>(fp, Messages.paletteMarshalTitle, Messages.paletteMarshalDescription, Marshal.class)
   
 
 				, new CreateFigureFeature<Multicast>(fp, Messages.paletteMulticastTitle, Messages.paletteMulticastDescription, Multicast.class)
   
 
 				, new CreateFigureFeature<OnCompletion>(fp, Messages.paletteOnCompletionTitle, Messages.paletteOnCompletionDescription, OnCompletion.class)
   
 
 				, new CreateFigureFeature<OnException>(fp, Messages.paletteOnExceptionTitle, Messages.paletteOnExceptionDescription, OnException.class)
   
 
 				, new CreateFigureFeature<Otherwise>(fp, Messages.paletteOtherwiseTitle, Messages.paletteOtherwiseDescription, Otherwise.class)
   
 
 				, new CreateFigureFeature<Pipeline>(fp, Messages.palettePipelineTitle, Messages.palettePipelineDescription, Pipeline.class)
   
 
 				, new CreateFigureFeature<Policy>(fp, Messages.palettePolicyTitle, Messages.palettePolicyDescription, Policy.class)
   
 
 				, new CreateFigureFeature<PollEnrich>(fp, Messages.palettePollEnrichTitle, Messages.palettePollEnrichDescription, PollEnrich.class)
   
 
 				, new CreateFigureFeature<Process>(fp, Messages.paletteProcessTitle, Messages.paletteProcessDescription, Process.class)
   
 
 				, new CreateFigureFeature<RecipientList>(fp, Messages.paletteRecipientListTitle, Messages.paletteRecipientListDescription, RecipientList.class)
   
 
 				, new CreateFigureFeature<RemoveHeader>(fp, Messages.paletteRemoveHeaderTitle, Messages.paletteRemoveHeaderDescription, RemoveHeader.class)
   
 
 				, new CreateFigureFeature<RemoveHeaders>(fp, Messages.paletteRemoveHeadersTitle, Messages.paletteRemoveHeadersDescription, RemoveHeaders.class)
   
 
 				, new CreateFigureFeature<RemoveProperty>(fp, Messages.paletteRemovePropertyTitle, Messages.paletteRemovePropertyDescription, RemoveProperty.class)
   
 
 				, new CreateFigureFeature<Resequence>(fp, Messages.paletteResequenceTitle, Messages.paletteResequenceDescription, Resequence.class)
   
 
 				, new CreateFigureFeature<Rollback>(fp, Messages.paletteRollbackTitle, Messages.paletteRollbackDescription, Rollback.class)
   
  
 
 				, new CreateFigureFeature<RoutingSlip>(fp, Messages.paletteRoutingSlipTitle, Messages.paletteRoutingSlipDescription, RoutingSlip.class)
   
 
 				, new CreateFigureFeature<Sampling>(fp, Messages.paletteSamplingTitle, Messages.paletteSamplingDescription, Sampling.class)
   
 
 				, new CreateFigureFeature<SetBody>(fp, Messages.paletteSetBodyTitle, Messages.paletteSetBodyDescription, SetBody.class)
   
 
 				, new CreateFigureFeature<SetExchangePattern>(fp, Messages.paletteSetExchangePatternTitle, Messages.paletteSetExchangePatternDescription, SetExchangePattern.class)
   
 
 				, new CreateFigureFeature<SetFaultBody>(fp, Messages.paletteSetFaultBodyTitle, Messages.paletteSetFaultBodyDescription, SetFaultBody.class)
   
 
 				, new CreateFigureFeature<SetHeader>(fp, Messages.paletteSetHeaderTitle, Messages.paletteSetHeaderDescription, SetHeader.class)
   
 
 				, new CreateFigureFeature<SetOutHeader>(fp, Messages.paletteSetOutHeaderTitle, Messages.paletteSetOutHeaderDescription, SetOutHeader.class)
   
 
 				, new CreateFigureFeature<SetProperty>(fp, Messages.paletteSetPropertyTitle, Messages.paletteSetPropertyDescription, SetProperty.class)
   
 
 				, new CreateFigureFeature<Sort>(fp, Messages.paletteSortTitle, Messages.paletteSortDescription, Sort.class)
   
 
 				, new CreateFigureFeature<Split>(fp, Messages.paletteSplitTitle, Messages.paletteSplitDescription, Split.class)
   
 
 				, new CreateFigureFeature<Stop>(fp, Messages.paletteStopTitle, Messages.paletteStopDescription, Stop.class)
   
 
 				, new CreateFigureFeature<Threads>(fp, Messages.paletteThreadsTitle, Messages.paletteThreadsDescription, Threads.class)
   
 
 				, new CreateFigureFeature<Throttle>(fp, Messages.paletteThrottleTitle, Messages.paletteThrottleDescription, Throttle.class)
   
 
 				, new CreateFigureFeature<ThrowException>(fp, Messages.paletteThrowExceptionTitle, Messages.paletteThrowExceptionDescription, ThrowException.class)
   
 
 				, new CreateFigureFeature<Transacted>(fp, Messages.paletteTransactedTitle, Messages.paletteTransactedDescription, Transacted.class)
   
 
 				, new CreateFigureFeature<Transform>(fp, Messages.paletteTransformTitle, Messages.paletteTransformDescription, Transform.class)
   
 
 				, new CreateFigureFeature<Try>(fp, Messages.paletteTryTitle, Messages.paletteTryDescription, Try.class)
   
 
 				, new CreateFigureFeature<Unmarshal>(fp, Messages.paletteUnmarshalTitle, Messages.paletteUnmarshalDescription, Unmarshal.class)
   
 
 				, new CreateFigureFeature<Validate>(fp, Messages.paletteValidateTitle, Messages.paletteValidateDescription, Validate.class)
   
 
 				, new CreateFigureFeature<When>(fp, Messages.paletteWhenTitle, Messages.paletteWhenDescription, When.class)
   
 
 				, new CreateFigureFeature<WireTap>(fp, Messages.paletteWireTapTitle, Messages.paletteWireTapDescription, WireTap.class)
  		};
	}

	/**
	 * helper method which provides all images for the figures in the palette
	 * 
	 * @param imageProvider	the image provider to use
	 */
	public static void addFigureIcons(ImageProvider imageProvider) {
		imageProvider.addIconsForClass(new Endpoint());
 
	imageProvider.addIconsForClass(new Aggregate());
 
	imageProvider.addIconsForClass(new AOP());
 
	imageProvider.addIconsForClass(new Bean());
 
	imageProvider.addIconsForClass(new Catch());
 
	imageProvider.addIconsForClass(new Choice());
 
	imageProvider.addIconsForClass(new ConvertBody());
 
	imageProvider.addIconsForClass(new Delay());
 
	imageProvider.addIconsForClass(new DynamicRouter());
 
	imageProvider.addIconsForClass(new Enrich());
 
	imageProvider.addIconsForClass(new Filter());
 
	imageProvider.addIconsForClass(new Finally());
 
	imageProvider.addIconsForClass(new IdempotentConsumer());
 
	imageProvider.addIconsForClass(new InOnly());
 
	imageProvider.addIconsForClass(new InOut());
 
	imageProvider.addIconsForClass(new Intercept());
 
	imageProvider.addIconsForClass(new InterceptFrom());
 
	imageProvider.addIconsForClass(new InterceptSendToEndpoint());
 
	imageProvider.addIconsForClass(new LoadBalance());
 
	imageProvider.addIconsForClass(new Log());
 
	imageProvider.addIconsForClass(new Loop());
 
	imageProvider.addIconsForClass(new Marshal());
 
	imageProvider.addIconsForClass(new Multicast());
 
	imageProvider.addIconsForClass(new OnCompletion());
 
	imageProvider.addIconsForClass(new OnException());
 
	imageProvider.addIconsForClass(new Otherwise());
 
	imageProvider.addIconsForClass(new Pipeline());
 
	imageProvider.addIconsForClass(new Policy());
 
	imageProvider.addIconsForClass(new PollEnrich());
 
	imageProvider.addIconsForClass(new Process());
 
	imageProvider.addIconsForClass(new RecipientList());
 
	imageProvider.addIconsForClass(new RemoveHeader());
 
	imageProvider.addIconsForClass(new RemoveHeaders());
 
	imageProvider.addIconsForClass(new RemoveProperty());
 
	imageProvider.addIconsForClass(new Resequence());
 
	imageProvider.addIconsForClass(new Rollback());
 
	imageProvider.addIconsForClass(new Route());
 
	imageProvider.addIconsForClass(new RoutingSlip());
 
	imageProvider.addIconsForClass(new Sampling());
 
	imageProvider.addIconsForClass(new SetBody());
 
	imageProvider.addIconsForClass(new SetExchangePattern());
 
	imageProvider.addIconsForClass(new SetFaultBody());
 
	imageProvider.addIconsForClass(new SetHeader());
 
	imageProvider.addIconsForClass(new SetOutHeader());
 
	imageProvider.addIconsForClass(new SetProperty());
 
	imageProvider.addIconsForClass(new Sort());
 
	imageProvider.addIconsForClass(new Split());
 
	imageProvider.addIconsForClass(new Stop());
 
	imageProvider.addIconsForClass(new Threads());
 
	imageProvider.addIconsForClass(new Throttle());
 
	imageProvider.addIconsForClass(new ThrowException());
 
	imageProvider.addIconsForClass(new Transacted());
 
	imageProvider.addIconsForClass(new Transform());
 
	imageProvider.addIconsForClass(new Try());
 
	imageProvider.addIconsForClass(new Unmarshal());
 
	imageProvider.addIconsForClass(new Validate());
 
	imageProvider.addIconsForClass(new When());
 
	imageProvider.addIconsForClass(new WireTap());
	}

}
