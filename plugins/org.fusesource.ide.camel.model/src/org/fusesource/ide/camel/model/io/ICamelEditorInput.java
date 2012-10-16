package org.fusesource.ide.camel.model.io;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.fusesource.ide.camel.model.RouteContainer;


public interface ICamelEditorInput extends IEditorInput {

	RouteContainer loadModel();

	IEditorInput getFileEditorInput();

	void save(String xml);

	IFile getLastSaveAsFile();

	void setLastSaveAsFile(IFile file);

}
