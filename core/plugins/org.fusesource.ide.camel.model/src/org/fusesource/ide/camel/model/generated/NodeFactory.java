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

import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.RouteContainer;

import org.apache.camel.model.*;

/**
 * Provides a factory method to create the Eclipse model for a given Camel node
 *
 * NOTE - this file is auto-generated using Velocity.
 *
 * DO NOT EDIT!
 */
public class NodeFactory {

    public static AbstractNode createNode(ProcessorDefinition processor, RouteContainer parent) {
        if (processor instanceof ToDefinition) {
            return Endpoint.newInstance((ToDefinition) processor, parent);
        }
        else if (processor instanceof AggregateDefinition) {
            return new Aggregate((AggregateDefinition)processor, parent);
        }
        else if (processor instanceof AOPDefinition) {
            return new AOP((AOPDefinition)processor, parent);
        }
        else if (processor instanceof BeanDefinition) {
            return new Bean((BeanDefinition)processor, parent);
        }
        else if (processor instanceof CatchDefinition) {
            return new Catch((CatchDefinition)processor, parent);
        }
        else if (processor instanceof ChoiceDefinition) {
            return new Choice((ChoiceDefinition)processor, parent);
        }
        else if (processor instanceof ConvertBodyDefinition) {
            return new ConvertBody((ConvertBodyDefinition)processor, parent);
        }
        else if (processor instanceof DelayDefinition) {
            return new Delay((DelayDefinition)processor, parent);
        }
        else if (processor instanceof DynamicRouterDefinition) {
            return new DynamicRouter((DynamicRouterDefinition)processor, parent);
        }
        else if (processor instanceof EnrichDefinition) {
            return new Enrich((EnrichDefinition)processor, parent);
        }
        else if (processor instanceof FilterDefinition) {
            return new Filter((FilterDefinition)processor, parent);
        }
        else if (processor instanceof FinallyDefinition) {
            return new Finally((FinallyDefinition)processor, parent);
        }
        else if (processor instanceof IdempotentConsumerDefinition) {
            return new IdempotentConsumer((IdempotentConsumerDefinition)processor, parent);
        }
        else if (processor instanceof InOnlyDefinition) {
            return new InOnly((InOnlyDefinition)processor, parent);
        }
        else if (processor instanceof InOutDefinition) {
            return new InOut((InOutDefinition)processor, parent);
        }
        else if (processor instanceof InterceptDefinition) {
            return new Intercept((InterceptDefinition)processor, parent);
        }
        else if (processor instanceof InterceptFromDefinition) {
            return new InterceptFrom((InterceptFromDefinition)processor, parent);
        }
        else if (processor instanceof InterceptSendToEndpointDefinition) {
            return new InterceptSendToEndpoint((InterceptSendToEndpointDefinition)processor, parent);
        }
        else if (processor instanceof LoadBalanceDefinition) {
            return new LoadBalance((LoadBalanceDefinition)processor, parent);
        }
        else if (processor instanceof LogDefinition) {
            return new Log((LogDefinition)processor, parent);
        }
        else if (processor instanceof LoopDefinition) {
            return new Loop((LoopDefinition)processor, parent);
        }
        else if (processor instanceof MarshalDefinition) {
            return new Marshal((MarshalDefinition)processor, parent);
        }
        else if (processor instanceof MulticastDefinition) {
            return new Multicast((MulticastDefinition)processor, parent);
        }
        else if (processor instanceof OnCompletionDefinition) {
            return new OnCompletion((OnCompletionDefinition)processor, parent);
        }
        else if (processor instanceof OnExceptionDefinition) {
            return new OnException((OnExceptionDefinition)processor, parent);
        }
        else if (processor instanceof OtherwiseDefinition) {
            return new Otherwise((OtherwiseDefinition)processor, parent);
        }
        else if (processor instanceof PipelineDefinition) {
            return new Pipeline((PipelineDefinition)processor, parent);
        }
        else if (processor instanceof PolicyDefinition) {
            return new Policy((PolicyDefinition)processor, parent);
        }
        else if (processor instanceof PollEnrichDefinition) {
            return new PollEnrich((PollEnrichDefinition)processor, parent);
        }
        else if (processor instanceof ProcessDefinition) {
            return new Process((ProcessDefinition)processor, parent);
        }
        else if (processor instanceof RecipientListDefinition) {
            return new RecipientList((RecipientListDefinition)processor, parent);
        }
        else if (processor instanceof RemoveHeaderDefinition) {
            return new RemoveHeader((RemoveHeaderDefinition)processor, parent);
        }
        else if (processor instanceof RemoveHeadersDefinition) {
            return new RemoveHeaders((RemoveHeadersDefinition)processor, parent);
        }
        else if (processor instanceof RemovePropertyDefinition) {
            return new RemoveProperty((RemovePropertyDefinition)processor, parent);
        }
        else if (processor instanceof RemovePropertiesDefinition) {
            return new RemoveProperties((RemovePropertiesDefinition)processor, parent);
        }
        else if (processor instanceof ResequenceDefinition) {
            return new Resequence((ResequenceDefinition)processor, parent);
        }
        else if (processor instanceof RollbackDefinition) {
            return new Rollback((RollbackDefinition)processor, parent);
        }
        else if (processor instanceof RouteDefinition) {
            return new Route((RouteDefinition)processor, parent);
        }
        else if (processor instanceof RoutingSlipDefinition) {
            return new RoutingSlip((RoutingSlipDefinition)processor, parent);
        }
        else if (processor instanceof SamplingDefinition) {
            return new Sampling((SamplingDefinition)processor, parent);
        }
        else if (processor instanceof SetBodyDefinition) {
            return new SetBody((SetBodyDefinition)processor, parent);
        }
        else if (processor instanceof SetExchangePatternDefinition) {
            return new SetExchangePattern((SetExchangePatternDefinition)processor, parent);
        }
        else if (processor instanceof SetFaultBodyDefinition) {
            return new SetFaultBody((SetFaultBodyDefinition)processor, parent);
        }
        else if (processor instanceof SetHeaderDefinition) {
            return new SetHeader((SetHeaderDefinition)processor, parent);
        }
        else if (processor instanceof SetOutHeaderDefinition) {
            return new SetOutHeader((SetOutHeaderDefinition)processor, parent);
        }
        else if (processor instanceof SetPropertyDefinition) {
            return new SetProperty((SetPropertyDefinition)processor, parent);
        }
        else if (processor instanceof SortDefinition) {
            return new Sort((SortDefinition)processor, parent);
        }
        else if (processor instanceof SplitDefinition) {
            return new Split((SplitDefinition)processor, parent);
        }
        else if (processor instanceof StopDefinition) {
            return new Stop((StopDefinition)processor, parent);
        }
        else if (processor instanceof ThreadsDefinition) {
            return new Threads((ThreadsDefinition)processor, parent);
        }
        else if (processor instanceof ThrottleDefinition) {
            return new Throttle((ThrottleDefinition)processor, parent);
        }
        else if (processor instanceof ThrowExceptionDefinition) {
            return new ThrowException((ThrowExceptionDefinition)processor, parent);
        }
        else if (processor instanceof TransactedDefinition) {
            return new Transacted((TransactedDefinition)processor, parent);
        }
        else if (processor instanceof TransformDefinition) {
            return new Transform((TransformDefinition)processor, parent);
        }
        else if (processor instanceof TryDefinition) {
            return new Try((TryDefinition)processor, parent);
        }
        else if (processor instanceof UnmarshalDefinition) {
            return new Unmarshal((UnmarshalDefinition)processor, parent);
        }
        else if (processor instanceof ValidateDefinition) {
            return new Validate((ValidateDefinition)processor, parent);
        }
        else if (processor instanceof WhenDefinition) {
            return new When((WhenDefinition)processor, parent);
        }
        else if (processor instanceof WireTapDefinition) {
            return new WireTap((WireTapDefinition)processor, parent);
        }
        else {
            throw new IllegalArgumentException("Camel node not supported: " + processor);
        }
    }

}
