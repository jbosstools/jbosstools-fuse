package org.fusesource.ide.camel.model.generated;

import java.util.HashMap;

public class UniversalEIPUtility {
	
	private static final HashMap<String, String> iconNameMap;
	private static final HashMap<String, String> documentationMap;
	private static final HashMap<String, String> categoryMap;
	
	public static final String CATEGORY_CONTROL_FLOW = "Control Flow";
	public static final String CATEGORY_ROUTING = "Routing";
	public static final String CATEGORY_MISC = "Miscellaneous";
	public static final String CATEGORY_COMPONENTS = "Components";
	public static final String CATEGORY_TRANSFORMATION = "Transformation";
	
	
	static {
		iconNameMap = new HashMap<String, String>();
		documentationMap = new HashMap<String, String>();
		categoryMap = new HashMap<String, String>();
		
		iconNameMap.put("delay", "generic.png");
		documentationMap.put("delay", "delayEIP");
		categoryMap.put("delay",CATEGORY_CONTROL_FLOW);
		
		iconNameMap.put("aggregate", "aggregate.png");
		documentationMap.put("aggregate", "aggregateEIP");
		categoryMap.put("aggregate", CATEGORY_ROUTING);
		
		iconNameMap.put("aop", "generic.png");
		documentationMap.put("aop", "AOPEIP");
		categoryMap.put("aop", CATEGORY_MISC);
		
		iconNameMap.put("bean", "bean.png");
		documentationMap.put("bean", "beanComp");
		categoryMap.put("bean", CATEGORY_COMPONENTS);
		
		iconNameMap.put("doCatch", "generic.png");
		documentationMap.put("catch", "catchEIP");
		categoryMap.put("doCatch",CATEGORY_CONTROL_FLOW);
		
		iconNameMap.put("choice", "choice.png");
		documentationMap.put("choice", "choiceEIP");
		categoryMap.put("choice", CATEGORY_ROUTING);
		
		iconNameMap.put("choice", "choice.png");
		documentationMap.put("choice", "choiceEIP");
		categoryMap.put("choice", CATEGORY_ROUTING);
		
		iconNameMap.put("convertBodyTo", "convertBody.png");
		documentationMap.put("convertBodyTo", "convertEIP");
		categoryMap.put("convertBodyTo", CATEGORY_TRANSFORMATION);
		
		iconNameMap.put("enrich", "enrich.png");
		documentationMap.put("enrich", "enrichEIP");
		categoryMap.put("enrich", CATEGORY_TRANSFORMATION);
		
		iconNameMap.put("filter", "filter.png");
		documentationMap.put("filter", "filterEIP");
		categoryMap.put("filter", CATEGORY_ROUTING);
		
		iconNameMap.put("doFinally", "generic.png");
		documentationMap.put("doFinally", "finallyEIP");
		categoryMap.put("doFinally",CATEGORY_CONTROL_FLOW);
		
		iconNameMap.put("idempotentConsumer", "idempotentConsumer.png");
		documentationMap.put("idempotentConsumer", "idempotentConsumer");
		categoryMap.put("idempotentConsumer", CATEGORY_ROUTING);
		
		iconNameMap.put("inOnly", "transform.png");
		documentationMap.put("inOnly", "inOnlyEIP");
		categoryMap.put("inOnly", CATEGORY_TRANSFORMATION);
		
		iconNameMap.put("inOut", "transform.png");
		documentationMap.put("inOut", "inOutEIP");
		categoryMap.put("inOut", CATEGORY_TRANSFORMATION);
		
		iconNameMap.put("intercept", "generic.png");
		documentationMap.put("intercept", "interceptEIP");
		categoryMap.put("intercept",CATEGORY_CONTROL_FLOW);
		
		iconNameMap.put("interceptFrom", "generic.png");
		documentationMap.put("interceptFrom", "interceptFromEIP");
		categoryMap.put("interceptFrom",CATEGORY_CONTROL_FLOW);
		
		iconNameMap.put("interceptSendToEndpoint", "generic.png");
		documentationMap.put("interceptSendToEndpoint", "interceptSendToEndpointEIP");
		categoryMap.put("interceptSendToEndpoint",CATEGORY_CONTROL_FLOW);
		
		iconNameMap.put("loadBalance", "loadBalance.png");
		documentationMap.put("loadBalance", "loadBalanceEIP");
		categoryMap.put("loadBalance", CATEGORY_ROUTING);
		
		iconNameMap.put("loop", "generic.png");
		documentationMap.put("loop", "loopEIP");
		categoryMap.put("loop",CATEGORY_CONTROL_FLOW);
		
		iconNameMap.put("log", "log.png");
		documentationMap.put("log", "logEIP");
		categoryMap.put("log", CATEGORY_COMPONENTS);
		
		iconNameMap.put("marshal", "marshal.png");
		documentationMap.put("marshal", "marshalEIP");
		categoryMap.put("marshal", CATEGORY_TRANSFORMATION);
		
		iconNameMap.put("multicast", "multicast.png");
		documentationMap.put("multicast", "multicastEIP");
		categoryMap.put("multicast", CATEGORY_ROUTING);
		
		iconNameMap.put("onCompletion", "generic.png");
		documentationMap.put("onCompletion", "onCompleteEIP");
		categoryMap.put("onCompletion",CATEGORY_CONTROL_FLOW);

		iconNameMap.put("onException", "generic.png");
		documentationMap.put("onException", "onExceptionEIP");
		categoryMap.put("onException",CATEGORY_CONTROL_FLOW);

		iconNameMap.put("otherwise", "generic.png");
		documentationMap.put("otherwise", "otherwiseEIP");
		categoryMap.put("otherwise", CATEGORY_ROUTING);
		
		iconNameMap.put("pipeline", "pipeline.png");
		documentationMap.put("pipeline", "pipelineEIP");
		categoryMap.put("pipeline", CATEGORY_ROUTING);
		
		iconNameMap.put("policy", "generic.png");
		documentationMap.put("policy", "policyNode");
		categoryMap.put("policy", CATEGORY_MISC);
		
		iconNameMap.put("pollEnrich", "pollEnrich.png");
		documentationMap.put("pollEnrich", "pollEnrichEIP");
		categoryMap.put("pollEnrich", CATEGORY_TRANSFORMATION);
		
		iconNameMap.put("process", "process.png");
		documentationMap.put("process", "processor");
		categoryMap.put("process", CATEGORY_COMPONENTS);
		
		iconNameMap.put("recipientList", "recipientList.png");
		documentationMap.put("recipientList", "recipientListEIP");
		categoryMap.put("recipientList", CATEGORY_ROUTING);
		
		iconNameMap.put("removeHeader", "transform.png");
		documentationMap.put("removeHeader", "removeHeaderNode");
		categoryMap.put("removeHeader", CATEGORY_TRANSFORMATION);
		
		iconNameMap.put("removeHeaders", "transform.png");
		documentationMap.put("removeHeaders", "removeHeadersNode");
		categoryMap.put("removeHeaders", CATEGORY_TRANSFORMATION);
		
		iconNameMap.put("removeProperty", "transform.png");
		documentationMap.put("removeProperty", "removePropertyNode");
		categoryMap.put("removeProperty", CATEGORY_TRANSFORMATION);
		
		iconNameMap.put("removeProperties", "transform.png");
		documentationMap.put("removeProperties", "allEIPs");
		categoryMap.put("removeProperties", CATEGORY_TRANSFORMATION);
		
		iconNameMap.put("resequence", "resequence.png");
		documentationMap.put("resequence", "resequenceEIPs");
		categoryMap.put("resequence", CATEGORY_ROUTING);
		
		iconNameMap.put("rollback", "generic.png");
		documentationMap.put("rollback", "rolbackNode");
		categoryMap.put("rollback",CATEGORY_CONTROL_FLOW);
		
		iconNameMap.put("routingSlip", "routingSlip.png");
		documentationMap.put("routingSlip", "routingSlipEIP");
		categoryMap.put("routingSlip", CATEGORY_ROUTING);
		
		iconNameMap.put("sample", "generic.png");  // Warning, this id has changed from Sampling to Sample
		documentationMap.put("sample", "samplingNode");
		categoryMap.put("sample", CATEGORY_MISC);
		
		iconNameMap.put("setBody", "setBody.png");  
		documentationMap.put("setBody", "setBodyNode");
		categoryMap.put("setBody", CATEGORY_TRANSFORMATION);
		
		iconNameMap.put("setExchangePattern", "transform.png");  
		documentationMap.put("setExchangePattern", "setExchangePatternNode");
		categoryMap.put("setExchangePattern", CATEGORY_TRANSFORMATION);
		
		iconNameMap.put("setFaultBody", "transform.png");  
		documentationMap.put("setFaultBody", "setFaultBodyNode");
		categoryMap.put("setFaultBody", CATEGORY_TRANSFORMATION);
		
		iconNameMap.put("setHeader", "transform.png");  
		documentationMap.put("setHeader", "setHeaderNode");
		categoryMap.put("setHeader", CATEGORY_TRANSFORMATION);
		
		iconNameMap.put("setOutHeader", "transform.png");  
		documentationMap.put("setOutHeader", "setOutHeaderNode");
		categoryMap.put("setOutHeader", CATEGORY_TRANSFORMATION);
		
		iconNameMap.put("setProperty", "transform.png");  
		documentationMap.put("setProperty", "setPropertyNode");
		categoryMap.put("setProperty", CATEGORY_TRANSFORMATION);
		
		iconNameMap.put("sort", "generic.png");  
		documentationMap.put("sort", "sortEIP");
		categoryMap.put("sort", CATEGORY_ROUTING);
		
		iconNameMap.put("split", "split.png");  
		documentationMap.put("split", "splitEIP");
		categoryMap.put("split", CATEGORY_ROUTING);
		
		iconNameMap.put("stop", "generic.png");  
		documentationMap.put("stop", "stopNode");
		categoryMap.put("stop", CATEGORY_MISC);
		
		iconNameMap.put("threads", "generic.png");  
		documentationMap.put("threads", "threadNode");
		categoryMap.put("threads", CATEGORY_MISC);
		
		iconNameMap.put("throttle", "generic.png");  
		documentationMap.put("throttle", "throttleNode");
		categoryMap.put("throttle",CATEGORY_CONTROL_FLOW);
		
		iconNameMap.put("throwException", "generic.png");  
		documentationMap.put("throwException", "throwExceptionNode");
		categoryMap.put("throwException",CATEGORY_CONTROL_FLOW);
		
		iconNameMap.put("transacted", "generic.png");  
		documentationMap.put("transacted", "transactedNode");
		categoryMap.put("transacted",CATEGORY_CONTROL_FLOW);
		
		iconNameMap.put("transform", "transform.png");  
		documentationMap.put("transform", "transformEIP");
		categoryMap.put("transform", CATEGORY_TRANSFORMATION);
		
		iconNameMap.put("doTry", "generic.png");  
		documentationMap.put("doTry", "tryNode");
		categoryMap.put("doTry",CATEGORY_CONTROL_FLOW);
		
		iconNameMap.put("unmarshal", "unmarshal.png");  
		documentationMap.put("unmarshal", "unmarshalNode");
		categoryMap.put("unmarshal", CATEGORY_TRANSFORMATION);

		iconNameMap.put("validate", "generic.png");  
		documentationMap.put("validate", "validateNode");
		categoryMap.put("validate", CATEGORY_MISC);

		iconNameMap.put("when", "generic.png");  
		documentationMap.put("when", "whenNode");
		categoryMap.put("when", CATEGORY_ROUTING);
		
		iconNameMap.put("wireTap", "wireTap.png");  
		documentationMap.put("wireTap", "wireTapEIP");
		categoryMap.put("wireTap", CATEGORY_ROUTING);
	}
	
    public static String getIconName(String eipName) {
    	String ret = iconNameMap.get(eipName);
    	return ret;
    }

	public static String getSmallIconName(String eipName) {
		String iconName = getIconName(eipName);
		if( iconName == null )
			iconName = "generic.png";
		return iconName.replace(".png", "16.png");
	}

    
    public static String getDocumentationFileName(String eipName) {
    	return documentationMap.get(eipName);
    }

    public static String getCategoryName(String eipName) {
    	return categoryMap.get(eipName);
    }

    
}
