/******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: JBoss by Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor.internal.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.jboss.tools.fuse.transformation.core.dozer.DozerMapperConfiguration;
import org.jboss.tools.fuse.transformation.editor.internal.l10n.Messages;

public class DozerMigrator {

	public void migrateIfNecessary(IEditorSite site, IEditorPart editor, IFile configFile, IProgressMonitor monitor) throws IOException, CoreException {
		File tmpFile = File.createTempFile("dozer", ".xml");
		tmpFile.deleteOnExit();
		java.nio.file.Path xformPath = Paths.get(configFile.getLocationURI());
		boolean changed = migrate(tmpFile, xformPath);
		if (changed) {
			if (!migrationConfirmed(site.getShell())) {
				Display.getDefault().asyncExec(() -> site.getWorkbenchWindow().getActivePage().closeEditor(editor, false));
				throw new PartInitException(Messages.TransformationEditorUnableToOpenIncompatibleTransformationFile);
			}
            Files.move(tmpFile.toPath(), xformPath, StandardCopyOption.REPLACE_EXISTING);
            configFile.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
	}

	boolean migrate(File migratedFile, java.nio.file.Path initialFile) throws IOException {
		boolean changed = false;
		try (Stream<String> reader = Files.lines(initialFile);
				PrintWriter writer = new PrintWriter(migratedFile)) {
			for (Iterator<String> iter = reader.iterator(); iter.hasNext();) {
				String line = iter.next();
				if (line.contains(DozerMapperConfiguration.PRE_DOZER_6_1_SCHEMA_LOC)) {
					line = line.replace(DozerMapperConfiguration.PRE_DOZER_6_1_SCHEMA_LOC, DozerMapperConfiguration.DOZER_6_1_SCHEMA_LOC);
					changed = true;
				}
				if (line.contains(DozerMapperConfiguration.PRE_DOZER_6_1_XMLNS)) {
					line = line.replace(DozerMapperConfiguration.PRE_DOZER_6_1_XMLNS, DozerMapperConfiguration.DOZER_6_1_XMLNS);
					changed = true;
				}
				writer.println(line);
			}
		}
		return changed;
	}

	boolean migrationConfirmed(Shell shell) {
		return MessageDialog.openConfirm(shell, Messages.TransformationEditor_ConfirmDialogTtile, Messages.TransformationEditorMigrationDialogConfirmation);
	}

}