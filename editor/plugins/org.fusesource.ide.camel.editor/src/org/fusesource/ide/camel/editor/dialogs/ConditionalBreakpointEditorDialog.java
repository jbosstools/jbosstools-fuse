/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.fusesource.ide.camel.model.service.core.CamelServiceManagerUtil;
import org.fusesource.ide.camel.model.service.core.ICamelManagerService;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.util.LanguageUtils;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.preferences.PreferenceManager;
import org.fusesource.ide.preferences.PreferencesConstants;

/**
 * @author lhein
 */
public class ConditionalBreakpointEditorDialog extends TitleAreaDialog {
	
	private static final String PATTERN_SIMPLE = "${%s}";
	private static final String[] SIMPLE_VARS = new String[] {
		"camelId",
		"camelContext.OGNL",
		"exchangeId",
		"id",
		"body",
		"in.body",
		"body.OGNL",
		"in.body.OGNL",
		"bodyAs(type)",
		"mandatoryBodyAs(type)",
		"out.body",
		"header.foo",
		"header[foo]",
		"headers.foo",
		"headers[foo]",
		"in.header.foo",
		"in.header[foo]",
		"in.headers.foo",
		"in.headers[foo]",
		"header.foo[bar]",
		"in.header.foo[bar]",
		"in.headers.foo[bar]",
		"header.foo.OGNL",
		"in.header.foo.OGNL",
		"in.headers.foo.OGNL",
		"out.header.foo",
		"out.header[foo]",
		"out.headers.foo",
		"out.headers[foo]",
		"headerAs(key,type)",
		"headers",
		"in.headers",
		"property.foo",
		"property[foo]",
		"property.foo.OGNL",
		"sys.foo",
		"sysenv.foo",
		"exception",
		"exception.OGNL",
		"exception.message",
		"exception.stacktrace",
		"date:command:pattern",
		"bean:bean expression",
		"properties:locations:key",
		"routeId",
		"threadName",
		"ref:xxx",
		"type:name.field"
	};
	
	private static final String PATTERN_SCRIPTING = "%s";
	private static final String[] SCRIPTING_VARS = new String[] {
		"context",
		"camelContext",
		"exchange",
		"request",
		"response",
		"properties",
		"request.headers.get('foo')",
		"response.headers.get('foo')"
	};
	
	private static final String PATTERN_SPEL = "#{%s}";
	private static final String[] SPEL_VARS = new String[] {
		"this",
		"exchange",
		"exception",
		"exchangeId",
		"fault",
		"body",
		"request",
		"request.headers['foo']",
		"request.body",
		"response",
		"response.headers['foo']",
		"response.body",
		"properties",
		"property(foo)",
		"property(foo, type)"
	};
	
	private String language;
	private String condition;
	private AbstractCamelModelElement node;
	private Group grp_language;
	private Group grp_condition;
	private Combo combo_language;
	private StyledText text_condition;
	private Button btn_variables;
	private Menu textMenu;
	private Menu variablesContextSubMenu;
	private MenuItem mVarItem;
	
	/**
	 * 
	 * @param parentShell
	 * @param node
	 */
	public ConditionalBreakpointEditorDialog(Shell parentShell, AbstractCamelModelElement node) {
	    super(parentShell);
	    this.node = node;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(String.format("Edit Conditional Breakpoint on %s ...", this.node.getDisplayText()));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	@Override
	public void create() {
		super.create();
	    setTitle("Edit the condition and language of your breakpoint...");
	    setMessage("Please choose a language and define a condition for the breakpoint...", IMessageProvider.INFORMATION);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
	    
		Composite container = new Composite(area, SWT.NONE);
	    container.setLayoutData(new GridData(GridData.FILL_BOTH));
	    GridLayout layout = new GridLayout(1, true);
	    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    container.setLayout(layout);

	    // create the language selection combo
	    createLanguageControls(container);
	    
	    // then create the condition text area
	    createConditionControls(container);
	    
	    container.pack();
	    
		prepareContextMenu();
	    
	    return area;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Control c = super.createContents(parent);
		validate();
		return c;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		if (validate()) {
			saveInputs();
			super.okPressed();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#isResizable()
	 */
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TrayDialog#isHelpAvailable()
	 */
	@Override
	public boolean isHelpAvailable() {
		return true;
	}
	
	/**
	 * creates the language combo box and depending controls
	 * 
	 * @param container
	 */
	private void createLanguageControls(Composite container) {
		FillLayout grpLayout = new FillLayout(SWT.VERTICAL);
		grpLayout.marginHeight = 10;
		grpLayout.marginWidth = 10;
		
		grp_language = new Group(container, SWT.NONE);
		grp_language.setText("Language");
		grp_language.setToolTipText("Please select a language from the drop down box...");
	    grp_language.setLayout(grpLayout);
	    grp_language.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));	   
	    
	    combo_language = new Combo(grp_language, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
	    combo_language.setToolTipText("Please select a language from the drop down box...");
	    combo_language.setItems(getSupportedLanguages());
	    combo_language.addSelectionListener(new SelectionAdapter() {
	    	/* (non-Javadoc)
	    	 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	    	 */
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		super.widgetSelected(e);
	    		validate();
	    		prepareContextMenu();
	    	}
		});

	    // if we have a preset language then we will use that if possible
	    if (this.language != null) {
	    	int idx = combo_language.indexOf(this.language);
	    	if (idx != -1) {
	    		combo_language.select(idx);
	    		return;
	    	}
	    }
	    
	    // if we have a saved preference default language we will use that if possible
	    String defaultLanguage = PreferenceManager.getInstance().loadPreferenceAsString(PreferencesConstants.EDITOR_DEFAULT_LANGUAGE);
	    int idx = combo_language.indexOf(defaultLanguage);
	    if (idx != -1) {
	    	combo_language.select(idx);
	    	return;
	    }
	    
	    // if we have no old language value and no default language set in preferences then we take the first in list
	    if (combo_language.getItemCount()>0) combo_language.select(0);
	}
	
	private String[] getSupportedLanguages() {
		return LanguageUtils.languageArray(node.getCamelFile());
	}

	/**
	 * creates the condition text area and depending controls
	 * 
	 * @param container
	 */
	private void createConditionControls(Composite container) {
		GridLayout grpLayout = new GridLayout(1, true);
		grpLayout.verticalSpacing = 10;
		grpLayout.marginHeight = 10;
		grpLayout.marginWidth = 10;
		
		grp_condition = new Group(container, SWT.NONE);
		grp_condition.setText("Condition");
		grp_condition.setToolTipText("Please define your condition...");
		grp_condition.setLayout(grpLayout);
		grp_condition.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));	   

		GridData data_text_condition = new GridData(SWT.FILL, SWT.FILL, true, true);
		data_text_condition.verticalSpan=4;
		
		text_condition = new StyledText(grp_condition, SWT.FULL_SELECTION | SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		text_condition.setEditable(true);
		text_condition.setEnabled(true);
		text_condition.setToolTipText("Please specify your condition...");
		text_condition.setLayoutData(data_text_condition);
		text_condition.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

		GridData data_btn_variables = new GridData(SWT.RIGHT, SWT.TOP, true, false);
		btn_variables = new Button(grp_condition, SWT.FLAT | SWT.PUSH);
		btn_variables.setText("Variables");
		btn_variables.setToolTipText("Select a predefined variable...");
		btn_variables.setLayoutData(data_btn_variables);
		btn_variables.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				onVariablesButtonPressed(e);
			}
		});
				
		// if we have a preset text then we fill it in
		if (this.condition != null) {
			text_condition.setText(this.condition);
		}
	}
	
	/**
	 * the variables button was pressed - here its handled
	 * 
	 * @param e
	 */
	private void onVariablesButtonPressed(SelectionEvent e) {
		Point point = btn_variables.toDisplay(new Point(e.x, e.y));
		Menu varsSubMenu = createVariablesMenu(btn_variables);
		createVariableSubmenu(varsSubMenu);
		varsSubMenu.setLocation(point.x, point.y);
		varsSubMenu.setVisible(true);
	}
	
	/**
	 * adds the context menu to the parent menu
	 */
	private void prepareContextMenu() {
		if (this.textMenu == null) { 
			this.textMenu = createVariablesMenu(this.text_condition);
			
			mVarItem = new MenuItem(this.textMenu, SWT.CASCADE, 0);
			mVarItem.setText("Variables");
			variablesContextSubMenu = new Menu(textMenu);
			createVariableSubmenu(variablesContextSubMenu);
			mVarItem.setMenu(variablesContextSubMenu);
			
			new MenuItem(this.textMenu, SWT.SEPARATOR, 1);
			
			MenuItem cutItem = new MenuItem(this.textMenu, SWT.PUSH, 2);
			cutItem.setText("Cut");
			cutItem.addSelectionListener(new SelectionAdapter() {
				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					text_condition.invokeAction(ST.CUT);
				}
			});
			
			MenuItem copyItem = new MenuItem(this.textMenu, SWT.PUSH, 3);
			copyItem.setText("Copy");
			copyItem.addSelectionListener(new SelectionAdapter() {
				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					text_condition.invokeAction(ST.COPY);
				}
			});
			
			MenuItem pasteItem = new MenuItem(this.textMenu, SWT.PUSH, 4);
			pasteItem.setText("Paste");
			pasteItem.addSelectionListener(new SelectionAdapter() {
				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					text_condition.invokeAction(ST.PASTE);
				}
			});
			
			new MenuItem(this.textMenu, SWT.SEPARATOR, 5);
			
			MenuItem markAllItem = new MenuItem(this.textMenu, SWT.PUSH, 6);
			markAllItem.setText("Select all");
			markAllItem.addSelectionListener(new SelectionAdapter() {
				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					text_condition.invokeAction(ST.SELECT_ALL);
				}
			});
		} else {
			variablesContextSubMenu = new Menu(textMenu);
			createVariableSubmenu(variablesContextSubMenu);
			mVarItem.setMenu(variablesContextSubMenu);
		}
	}
	
	/**
	 * 
	 * @param parent
	 * @return
	 */
	private Menu createVariablesMenu(Control parent) {
		Menu variablesMenu = new Menu(parent);
		parent.setMenu(variablesMenu);
		return variablesMenu;
	}
	
	/**
	 * fills in all predefined variables
	 * 
	 * @param parentItem
	 */
	private void createVariableSubmenu(Menu parentItem) {
		String lang = combo_language.getText().trim();

		if (lang.equalsIgnoreCase("simple")) {
			createVariables(parentItem, SIMPLE_VARS, PATTERN_SIMPLE);
		} else if (lang.equalsIgnoreCase("spel")) {
			createVariables(parentItem, SPEL_VARS, PATTERN_SPEL);
		} else if (lang.equalsIgnoreCase("groovy") ||
				   lang.equalsIgnoreCase("ruby") ||
				   lang.equalsIgnoreCase("php") ||
				   lang.equalsIgnoreCase("el") ||
				   lang.equalsIgnoreCase("sql") ||
				   lang.equalsIgnoreCase("jxpath") ||
				   lang.equalsIgnoreCase("mvel") ||
				   lang.equalsIgnoreCase("ognl") ||
				   lang.equalsIgnoreCase("python") ||
				   lang.equalsIgnoreCase("xpath") ||
				   lang.equalsIgnoreCase("xquery") ||
				   lang.equalsIgnoreCase("javascript")) {
			createVariables(parentItem, SCRIPTING_VARS, PATTERN_SCRIPTING);
		} else {
			// unsupported language
		}
		if (parentItem.getItemCount()==0) {
			// seems we have no predefined vars yet
			// add a notifier
			MenuItem dummyItem = new MenuItem(parentItem, SWT.PUSH);
			dummyItem.setText("<nothing available>");
			dummyItem.setEnabled(false);
		}
	}

	/**
	 * creates variables available in the simple language
	 * 
	 * @param parentItem
	 */
	private void createVariables(Menu parentItem, String[] commands, final String pattern) {
		for (String cmd : commands) {
			final String command = cmd;
			MenuItem item = new MenuItem(parentItem, SWT.PUSH);
			item.setText(cmd);
			item.addSelectionListener(new SelectionAdapter() {
				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					super.widgetSelected(e);
					// user wants to embed a selection in a variable
					if (text_condition.getSelectionText().length()>0 && command.indexOf("foo") != -1) {
						// use the selection for the variable insert
						String selectedText = text_condition.getSelectionText();
						String newCommand = command.replaceFirst("foo", selectedText);
						String insertText = String.format(pattern, newCommand);
						Point p = text_condition.getSelectionRange();
						text_condition.replaceTextRange(p.x, selectedText.length(), insertText);
					} else {
						String insertText = String.format("${%s}", command);
						text_condition.insert(insertText);
					}
				}
			});
		}
	}
	
	/**
	 * saves the made changes from the controls to the variables
	 */
	private void saveInputs() {
		this.language = combo_language.getText();
		this.condition = text_condition.getText().replaceAll("\n", "").replaceAll("\r", "").trim();
	}
	
	/**
	 * validates the language and the condition and sets or clears 
	 * the error messages
	 * 
	 * @return	true if all is fine
	 */
	private boolean validate() {
		if (Strings.isBlank(combo_language.getText())) {
			setErrorMessage("You must select a language from the language combo box.");
			if (getButton(OK) != null) getButton(OK).setEnabled(false);
			return false;
		}
		if (Strings.isBlank(text_condition.getText().replaceAll("\n", "").replaceAll("\r", "").trim())) {
			setErrorMessage("You must enter a valid condition using the selected language.");
			if (getButton(OK) != null) getButton(OK).setEnabled(false);
			return false;
		}
		
		// not initialized yet
		ICamelManagerService svc = CamelServiceManagerUtil.getManagerService();
		if (svc != null) {
			String ex = svc.testExpression(combo_language.getText().trim(), text_condition.getText());
			if (ex != null && !ex.contains("No language could be found for:")) {
				setErrorMessage("Condition Error: " + ex);
				if (getButton(OK) != null) getButton(OK).setEnabled(false);
				return false;
			}
		}

		if (getButton(OK) != null) getButton(OK).setEnabled(true);
		setErrorMessage(null);
		return true;
	}
	
	/**
	 * @return the language
	 */
	public String getLanguage() {
		return this.language;
	}
	
	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	
	/**
	 * @return the condition
	 */
	public String getCondition() {
		return this.condition;
	}
	
	/**
	 * @param condition the condition to set
	 */
	public void setCondition(String condition) {
		this.condition = condition;
	}
}
