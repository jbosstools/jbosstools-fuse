package org.fusesource.ide.camel.model.io;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.commons.util.IFiles;


public abstract class ContainerMarshallerSupport implements ContainerMarshaler  {

	public ContainerMarshallerSupport() {
		super();
	}

	@Override
	public RouteContainer loadRoutes(IFile ifile) {
		return loadRoutes(IFiles.toFile(ifile));
	}

	public void save(IFile ifile, RouteContainer model) throws CoreException {
		save(ifile, model, new NullProgressMonitor());
	}
	
}