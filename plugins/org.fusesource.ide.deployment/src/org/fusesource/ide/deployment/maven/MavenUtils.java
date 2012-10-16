package org.fusesource.ide.deployment.maven;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.ui.Workbenches;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.commons.util.Strings;
import org.fusesource.ide.deployment.DeployPlugin;
import org.fusesource.ide.launcher.ui.ExecutePomActionSupport;


public class MavenUtils {
	/**
	 * Returns the URI of the form <code>fab:mvn:group/artifact/version</code> for FABs or
	 * for OSGi bundles of the form <code>mvn:group/artifact/version</code>
	 */
	public static String getBundleURI(Model mavenModel) {
		String answer = "mvn:" + mavenModel.getGroupId() + "/" + mavenModel.getArtifactId() + "/" + getVersion(mavenModel);

		// assume its a FAB unless the packaging is a bundle or war etc
		String packaging = mavenModel.getPackaging();
		if (Objects.equal("bundle", packaging)) {
			return answer;
		} else if (Objects.equal("pom", packaging)) {
			return null;
		} else if (Objects.equal("war", packaging)) {
			return "war:" + answer;
		} else {
			return "fab:" + answer;
		}

	}

	public static String getVersion(Model mavenModel) {
		String answer = mavenModel.getVersion();
		if (Strings.isBlank(answer)) {
			Parent parent = mavenModel.getParent();
			if (parent != null) {
				answer = parent.getVersion();
			}
		}
		return answer;
	}

	public static void launch(ExecutePomActionSupport action) {
		ISelection isel = Selections.getSelection(Workbenches.getActiveWorkbenchPartSite());
		Object first = Selections.getFirstSelection(isel);
		// if there is no project / file / folder selected we use the opened editor if available
		if (isel == null || isel.isEmpty() || (first instanceof IJavaElement == false && first instanceof IResource == false)) {
			IEditorPart editor = DeployPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			action.launch(editor, "run");
		} else {
			// use the selected file/folder/project as parameter
			action.launch(isel, "run");
		}

	}

}
