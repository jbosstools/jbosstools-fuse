/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.sap.ui.edit.command;

import org.eclipse.emf.common.command.BasicCommandStack;

public class TransactionalCommandStack extends BasicCommandStack {
	
	// Top of stack when transaction begins
	protected int txnBeginIdx = -1;
	
	public boolean isInTransaction() {
		return txnBeginIdx != -1;
	}

	public void begin() {
		txnBeginIdx = top;
	}
	
	public void commit() {
		
		if (!isInTransaction()) 
			return;

		txnBeginIdx = -1;
	}
	
	public void rollback() {

		if (!isInTransaction()) 
			return;
		
		while (top > txnBeginIdx) {
			undo();
		}
 		
 		txnBeginIdx = -1;
	}
	
	@Override
	protected void handleError(Exception exception) {
		rollback();
		super.handleError(exception);
	}

}
