/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.ui.drop;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

public abstract class DropHandlerSupport implements DropHandler {

	public static boolean isSupported(Transfer transfer, TransferData[] dataTypes) {
		for (TransferData data : dataTypes) {
			if (transfer.isSupportedType(data)) {
				return true;
			}
		}
		return false;
	}

	public static DropHandler createDropHandler(Object target, DropTargetEvent event) {
		if (target instanceof DropHandler) {
			return (DropHandler) target;
		} else if (target instanceof DropHandlerFactory) {
			DropHandlerFactory factory = (DropHandlerFactory) target;
			return factory.createDropHandler(event);
		}
		return null;
	}

	@Override
	public void drop(DropTargetEvent event) {
		Object data = event.data;
		if (isSupported(FileTransfer.getInstance(), event.dataTypes)) {
			// lets do a file transfer
			if (data instanceof Object[]) {
				if (data instanceof Object[]) {
					Object[] array = (Object[]) data;
					for (Object object : array) {
						dropFile(object);
					}
				} else if (data instanceof Collection) {
					Collection coll = (Collection) data;
					for (Object object : coll) {
						dropFile(object);
					}
				} else {
					dropFile(data);
				}
			} else {
				dropFile(data);
			}
		} else {
			dropObject(data);
		}
	}

	protected void dropFile(Object object) {
		File file = null;
		if (object instanceof String) {
			String fileName = (String) object;
			file = new File(fileName);
		} else if (object instanceof File) {
			file = (File) object;
		} else if (object instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) object;
			Iterator iter = selection.iterator();
			while (iter.hasNext()) {
				Object element = iter.next();
				dropFile(element);
			}
		} else if (object instanceof IFile) {
			IFile ifile = (IFile) object;
			dropIFile(ifile);
		} else {
			unknownType("File", object);
		}
		if (file != null) {
			dropFile(file);
		}
	}

	public void dropObject(Object data) {
		if (data instanceof Object[]) {
			Object[] array = (Object[]) data;
			for (Object object : array) {
				dropObject(object);
			}
		} else if (data instanceof Collection) {
			Collection coll = (Collection) data;
			for (Object object : coll) {
				dropObject(object);
			}
		} else if (data instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) data;
			Iterator iter = selection.iterator();
			while (iter.hasNext()) {
				Object element = iter.next();
				dropObject(element);
			}
		} else if (data instanceof IFile) {
			IFile ifile = (IFile) data;
			dropIFile(ifile);
		} else {
			unknownType("Object", data);
		}

	}

	public void unknownType(String kind, Object data) {
	}

	public abstract void dropIFile(IFile ifile);
	public abstract void dropFile(File resource);

}
