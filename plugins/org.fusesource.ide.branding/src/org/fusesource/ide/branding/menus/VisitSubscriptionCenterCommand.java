package org.fusesource.ide.branding.menus;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.fusesource.ide.branding.Activator;


/**
 * @author lhein
 */
public class VisitSubscriptionCenterCommand extends AbstractHandler {

	private static final String FUSESOURCE_SUBSCRIPTION_CENTER_URI = "https://fusesource.com/enterprise-support/fusesource-support/";
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		openURL(FUSESOURCE_SUBSCRIPTION_CENTER_URI);
		return null;
	}
	
	private void openURL(String linkUrl) {
		URL url;
		try {
			url = new URL(linkUrl);
		} catch (MalformedURLException ex) {
			// ignore duff links
			return;
		}
		// we use the Eclipse built in browser for displaying the page
		IWebBrowser browser = null;
		IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
		try {
			browser = support.createBrowser("FuseSource.com");
		} catch (PartInitException e) {
			Activator.getLogger().warning("Could not create browser: " + e, e);
		}
	
		// we can only open up the URL if browser and rider url are
		if (browser != null && url != null) {
			try {
				// now navigate to the created Rider url
				browser.openURL(url);
			} catch (Exception e) {
				Activator.getLogger().warning("Could not open browser at " + url + ". " + e, e);
			}
		}
	}
}
