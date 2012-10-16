package org.fusesource.ide.fabric.views.logs;

import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.ui.form.FormSupport;
import org.fusesource.ide.commons.ui.form.Forms;


public class LogDetailForm extends FormSupport implements ISelectionListener {

	private LogEventBean logEvent = new LogEventBean();
	private Text messageField;
	private Text errorField;

	@Override
	public void setFocus() {
		messageField.setFocus();
	}

	@Override
	protected void createTextFields(Composite root) {

		String title = LogMessages.messageLabel;
		title = null;
		Composite messageSection = createSectionComposite(title, new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		messageSection.setLayout(layout);

		messageField = createBeanTextArea(messageSection, this, "logEvent.message", LogMessages.messageLabel, LogMessages.messageTooltip);

		//Composite errorSection = createSectionComposite(LogMessages.exceptionLabel, new GridData(GridData.FILL_BOTH));
		//errorSection.setLayout(layout);
		Composite errorSection  = messageSection;

		errorField = createBeanTextArea(errorSection, this, "logEvent.exceptionText", LogMessages.exceptionLabel, LogMessages.exceptionTooltip);
	}

	protected Text createBeanTextArea(Composite parent, Object bean, String propertyName,
			String labelText, String tooltip) {
		Text text = createTextArea(parent);
		text.setToolTipText(tooltip);
		ISWTObservableValue textValue = Forms.observe(text);
		Forms.bindBeanProperty(getDataBindingContext(), getMessageManager(), bean, propertyName, isMandatory(bean, propertyName), labelText, textValue, text);
		return text;
	}

	@Override
	public void okPressed() {
	}


	public LogEventBean getLogEvent() {
		return logEvent;
	}

	public void setLogEvent(LogEventBean logEvent) {
		Object oldValue = this.logEvent;
		this.logEvent = logEvent;
		firePropertyChange("logEvent", oldValue, logEvent);
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		Object firstSelection = Selections.getFirstSelection(selection);
		if (firstSelection != null) {
			LogEventBean event = LogEventBean.toLogEventBean(firstSelection);
			if (event != null) {
				setLogEvent(event);
			}
		}

	}

}
