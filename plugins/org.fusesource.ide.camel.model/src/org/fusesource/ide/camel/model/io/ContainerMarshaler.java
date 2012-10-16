package org.fusesource.ide.camel.model.io;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.fusesource.ide.camel.model.RouteContainer;


/**
 * @author jstrachan
 */
public interface ContainerMarshaler {

	/**
	 * loads all camel routes from the file and creates a container holding all
	 * routes
	 * 
	 * @param file
	 *            the model file
	 * @return a container with all loaded routes
	 */
	RouteContainer loadRoutes(IFile file);

	RouteContainer loadRoutes(File file);

	/**
	 * saves the given container with routes into the specified file
	 * 
	 * @param file
	 *            the file to save to
	 * @param model
	 *            the container holding the routes to save
	 * @throws CoreException 
	 */
	void save(IFile file, RouteContainer model) throws CoreException;
	void save(IFile ifile, RouteContainer model, IProgressMonitor monitor) throws CoreException;
	
	void save(File file, RouteContainer model);
	
	/**
	 * Takes the given XML text and applies the changes from the model to return
	 * the new XML text. This method is used to update the 
	 * @param xmlText
	 * @param model
	 * @return
	 */
	String updateText(String xmlText, RouteContainer model);

	/**
	 * Load the routes from the editor text
	 * @return
	 */
	RouteContainer loadRoutesFromText(String text);

	/**
	 * returns true if on loading the model there were no routes defined, otherwise false
	 * @return
	 */
	boolean isNoRoutesOnLoad();
}
