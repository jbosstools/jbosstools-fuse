/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.fusesource.ide.camel.model.service.core.jmx.camel;

import java.util.List;

/**
 * @author lhein
 *
 */
public interface CamelBacklogTracerMBean {
	void clear(); 
	List<IBacklogTracerEventMessageMBean>	dumpAllTracedMessages(); 
	String dumpAllTracedMessagesAsXml(); 
	List<IBacklogTracerEventMessageMBean> dumpTracedMessages(String nodeOrRouteId); 
	String dumpTracedMessagesAsXml(String nodeOrRouteId); 
	int	getBacklogSize(); 
	int getBodyMaxChars(); 
	String getCamelId(); 
	String getCamelManagementName(); 
	long getTraceCounter(); 
	String getTraceFilter(); 
	String getTracePattern(); 
	boolean	isBodyIncludeFiles(); 
	boolean	isBodyIncludeStreams(); 
	boolean	isEnabled();
	boolean	isRemoveOnDump(); 
	void resetTraceCounter(); 
	void setBacklogSize(int backlogSize); 
	void setBodyIncludeFiles(boolean bodyIncludeFiles); 
	void setBodyIncludeStreams(boolean bodyIncludeStreams); 
	void setBodyMaxChars(int bodyMaxChars); 
	void setEnabled(boolean enabled); 
	void setRemoveOnDump(boolean removeOnDump); 
	void setTraceFilter(String predicate); 
	void setTracePattern(String pattern); 
}
