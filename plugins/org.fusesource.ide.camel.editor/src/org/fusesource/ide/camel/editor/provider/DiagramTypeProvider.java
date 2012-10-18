package org.fusesource.ide.camel.editor.provider;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.notification.INotificationService;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.fusesource.ide.camel.editor.CamelModelNotificationService;

/**
 * @author lhein
 */
public class DiagramTypeProvider extends AbstractDiagramTypeProvider {

	private CamelModelNotificationService camelModelNotificationService;
	
	private IToolBehaviorProvider[] toolBehaviorProviders;

	/**
	 * 
	 */
	public DiagramTypeProvider() {
		super();
		setFeatureProvider(new CamelFeatureProvider(this));
	}

	@Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
		if (toolBehaviorProviders == null) {
			toolBehaviorProviders =
					new IToolBehaviorProvider[] { new ToolBehaviourProvider(this) };
		}
		return toolBehaviorProviders;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.dt.AbstractDiagramTypeProvider#getNotificationService()
	 */
	@Override
	public INotificationService getNotificationService() {
		if (this.camelModelNotificationService == null) {
			this.camelModelNotificationService = new CamelModelNotificationService(this);
		}
		return this.camelModelNotificationService;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.dt.AbstractDiagramTypeProvider#isAutoUpdateAtStartup()
	 */
	@Override
	public boolean isAutoUpdateAtStartup() {
		return true;
	}
}
