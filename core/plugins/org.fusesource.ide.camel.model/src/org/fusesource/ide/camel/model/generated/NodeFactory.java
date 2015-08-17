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

import org.apache.camel.model.LoadBalanceDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.ResequenceDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.ToDefinition;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Activator;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.catalog.CamelModel;
import org.fusesource.ide.camel.model.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.catalog.eips.Eip;

/**
 * Provides a factory method to create the Eclipse model for a given Camel node
 *
 * NOTE - this file is auto-generated using Velocity.
 *
 * DO NOT EDIT!
 */
public class NodeFactory {

    public static AbstractNode createNode(ProcessorDefinition processor, RouteContainer parent) {
    	String version = "2.15.2"; // TODO change this default value?
    	return createNode(processor, parent, version);
    }
    
    public static AbstractNode createNode(ProcessorDefinition processor, RouteContainer parent, String version) {
    	String s = processor.getShortName();
    	CamelModel model = CamelModelFactory.getModelForVersion(version);
    	Eip eip = model.getEipModel().getEIPByClass(s);
    	if( eip == null ) {
    		// TODO error?
    		Activator.getDefault().getLog().log(new Status(IStatus.ERROR, "org.fusesource.ide.camel.model", 
    				"No EIP model found in camel " + version + " for element " + s));
    		return null;
    	}
    	
    	// Two node types I was unable to convert to Universal node format 
        if (processor instanceof ToDefinition) {
            return Endpoint.newInstance((ToDefinition) processor, parent);
        }
        else if (processor instanceof RouteDefinition) {
        	return new Route((RouteDefinition)processor, parent);
        }

        // Classes not yet merged are here:
    	return new UniversalEIPNode(processor, parent, eip);

//        // Merged classes below
//        else if (processor instanceof AggregateDefinition) {
//        	// TODO Testing if we replace with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Aggregate((AggregateDefinition)processor, parent);
//        }        
//        else if (processor instanceof AOPDefinition) {
//        	// TODO Testing if we replace with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new AOP((AOPDefinition)processor, parent);
//        }
//        else if (processor instanceof BeanDefinition) {
//        	// TODO Testing if we replace with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Bean((BeanDefinition)processor, parent);
//        }
//        else if (processor instanceof CatchDefinition) {
//        	// TODO Testing if we replace with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Catch((CatchDefinition)processor, parent);
//        }
//        else if (processor instanceof ChoiceDefinition) {
//        	// TODO Testing if we replace with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Choice((ChoiceDefinition)processor, parent);
//        }
//        else if (processor instanceof ConvertBodyDefinition) {
//        	// TODO Testing if we replace with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new ConvertBody((ConvertBodyDefinition)processor, parent);
//        }
//        else if (processor instanceof DelayDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
//            //return new Delay((DelayDefinition)processor, parent);
//        }
//        else if (processor instanceof DynamicRouterDefinition) {
//            return new DynamicRouter((DynamicRouterDefinition)processor, parent);
//        }
//        else if (processor instanceof EnrichDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Enrich((EnrichDefinition)processor, parent);
//        }
//        else if (processor instanceof FilterDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Filter((FilterDefinition)processor, parent);
//        }
//        else if (processor instanceof FinallyDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Finally((FinallyDefinition)processor, parent);
//        }
//        else if (processor instanceof IdempotentConsumerDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new IdempotentConsumer((IdempotentConsumerDefinition)processor, parent);
//        }
//        else if (processor instanceof InOnlyDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new InOnly((InOnlyDefinition)processor, parent);
//        }
//        else if (processor instanceof InOutDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new InOut((InOutDefinition)processor, parent);
//        }
//        else if (processor instanceof InterceptDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Intercept((InterceptDefinition)processor, parent);
//        }
//        else if (processor instanceof InterceptFromDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new InterceptFrom((InterceptFromDefinition)processor, parent);
//        }
//        else if (processor instanceof InterceptSendToEndpointDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new InterceptSendToEndpoint((InterceptSendToEndpointDefinition)processor, parent);
//        }
//        else if (processor instanceof LoadBalanceDefinition) {
//            return new LoadBalance((LoadBalanceDefinition)processor, parent);
//        }
//        else if (processor instanceof LogDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Log((LogDefinition)processor, parent);
//        }
//        else if (processor instanceof LoopDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Loop((LoopDefinition)processor, parent);
//        }
//        else if (processor instanceof MarshalDefinition) {
//            return new Marshal((MarshalDefinition)processor, parent);
//        }
//        else if (processor instanceof MulticastDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Multicast((MulticastDefinition)processor, parent);
//        }
//        else if (processor instanceof OnExceptionDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new OnException((OnExceptionDefinition)processor, parent);
//        }
//        else if (processor instanceof OnCompletionDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new OnCompletion((OnCompletionDefinition)processor, parent);
//        }
//        else if (processor instanceof OtherwiseDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Otherwise((OtherwiseDefinition)processor, parent);
//        }
//        else if (processor instanceof PipelineDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Pipeline((PipelineDefinition)processor, parent);
//        }
//        else if (processor instanceof PolicyDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Policy((PolicyDefinition)processor, parent);
//        }
//        else if (processor instanceof PollEnrichDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new PollEnrich((PollEnrichDefinition)processor, parent);
//        }
//        else if (processor instanceof ProcessDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Process((ProcessDefinition)processor, parent);
//        }
//        else if (processor instanceof RecipientListDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new RecipientList((RecipientListDefinition)processor, parent);
//        }
//        else if (processor instanceof RemoveHeaderDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new RemoveHeader((RemoveHeaderDefinition)processor, parent);
//        }
//        else if (processor instanceof RemoveHeadersDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new RemoveHeaders((RemoveHeadersDefinition)processor, parent);
//        }
//        else if (processor instanceof RemovePropertyDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new RemoveProperty((RemovePropertyDefinition)processor, parent);
//        }
//        else if (processor instanceof RemovePropertiesDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new RemoveProperties((RemovePropertiesDefinition)processor, parent);
//        }
//        else if (processor instanceof ResequenceDefinition) {
//            return new Resequence((ResequenceDefinition)processor, parent);
//        }
//
//        else if (processor instanceof RollbackDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Rollback((RollbackDefinition)processor, parent);
//        }
//        else if (processor instanceof RouteDefinition) {
//            return new Route((RouteDefinition)processor, parent);
//        }
//        else if (processor instanceof RoutingSlipDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new RoutingSlip((RoutingSlipDefinition)processor, parent);
//        }
//        else if (processor instanceof SamplingDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Sampling((SamplingDefinition)processor, parent);
//        }
//        else if (processor instanceof SetBodyDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new SetBody((SetBodyDefinition)processor, parent);
//        }
//        else if (processor instanceof SetExchangePatternDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new SetExchangePattern((SetExchangePatternDefinition)processor, parent);
//        }
//        else if (processor instanceof SetFaultBodyDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new SetFaultBody((SetFaultBodyDefinition)processor, parent);
//        }
//        else if (processor instanceof SetHeaderDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new SetHeader((SetHeaderDefinition)processor, parent);
//        }
//        else if (processor instanceof SetOutHeaderDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new SetOutHeader((SetOutHeaderDefinition)processor, parent);
//        }
//        else if (processor instanceof SetPropertyDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new SetProperty((SetPropertyDefinition)processor, parent);
//        }
//        else if (processor instanceof SortDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Sort((SortDefinition)processor, parent);
//        }
//        else if (processor instanceof SplitDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Split((SplitDefinition)processor, parent);
//        }
//        else if (processor instanceof StopDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Stop((StopDefinition)processor, parent);
//        }
//        else if (processor instanceof ThreadsDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Threads((ThreadsDefinition)processor, parent);
//        }
//        else if (processor instanceof ThrottleDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Throttle((ThrottleDefinition)processor, parent);
//        }
//        else if (processor instanceof ThrowExceptionDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new ThrowException((ThrowExceptionDefinition)processor, parent);            
//        }
//        else if (processor instanceof TransactedDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Transacted((TransactedDefinition)processor, parent);
//        }
//        else if (processor instanceof TransformDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Transform((TransformDefinition)processor, parent);
//        }
//        else if (processor instanceof TryDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Try((TryDefinition)processor, parent);
//        }
//        else if (processor instanceof UnmarshalDefinition) {
//            return new Unmarshal((UnmarshalDefinition)processor, parent);
//        }
//        else if (processor instanceof ValidateDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new Validate((ValidateDefinition)processor, parent);
//        }
//        else if (processor instanceof WhenDefinition) {
//        	// TODO Testing if we replace delay with our new universal
//        	return new UniversalEIPNode(processor, parent, eip);
////            return new When((WhenDefinition)processor, parent);
//        }
//        else if (processor instanceof WireTapDefinition) {
//            return new WireTap((WireTapDefinition)processor, parent);
//        }
//        else {
//            throw new IllegalArgumentException("Camel node not supported: " + processor);
//        }
    }

}
