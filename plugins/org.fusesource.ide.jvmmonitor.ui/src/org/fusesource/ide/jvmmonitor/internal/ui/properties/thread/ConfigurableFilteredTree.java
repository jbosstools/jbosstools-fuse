package org.fusesource.ide.jvmmonitor.internal.ui.properties.thread;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.fusesource.ide.commons.ui.IConfigurableColumns;
import org.fusesource.ide.commons.ui.config.TableConfiguration;


public abstract class ConfigurableFilteredTree extends FilteredTree implements IConfigurableColumns {

	private TableConfiguration configuration;

	public ConfigurableFilteredTree(Composite parent, boolean useNewLook) {
		super(parent, useNewLook);
		init();
	}

	public ConfigurableFilteredTree(Composite parent, int treeStyle, PatternFilter filter, boolean useNewLook) {
		super(parent, treeStyle, filter, useNewLook);
		init();
	}

	private void init() {
		configureTree();
		getConfiguration().addListener(this);
	}

	protected abstract void configureTree();

	@Override
	public TableConfiguration getConfiguration() {
		if (configuration == null) {
			configuration = TableConfiguration.loadConfiguration(getColumnConfigurationId());
			configuration.addDefaultColumns(getColumns());
		}
		return configuration;
	}

	@Override
	public void updateColumnConfiguration(TableConfiguration configuration) {
		this.configuration = configuration;
		if (getViewer().getTree().isDisposed()) {
			return;
		}
		configureTree();
		getViewer().refresh();
	}

	@Override
	public void dispose() {
		super.dispose();
		getConfiguration().removeListener(this);
	}

}