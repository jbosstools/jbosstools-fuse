/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 *
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.fabric.actions.jclouds;



import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.commons.ui.ICanValidate;
import org.fusesource.ide.commons.ui.form.FormSupport;
import org.fusesource.ide.fabric.actions.Messages;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.providers.ProviderMetadata;

import com.google.common.base.Objects;

/**
 * The form for adding or editing {@link CloudDetails}
 */
public class CloudDetailsForm extends FormSupport {
    private ComboViewer providerNameField;
    private ComboViewer apiNameField;
    private Text endpointField;
    private Text identityField;
    private CloudDetails details = new CloudDetails();
    private Text nameField;

    public CloudDetailsForm(ICanValidate validator) {
        super(validator);
    }

    @Override
    protected boolean isMandatory(Object bean, String propertyName) {
        return !Objects.equal(propertyName, "ownerId");
    }

    public CloudDetails getDetails() {
        return details;
    }

    public void setDetails(CloudDetails details) {
        this.details = details;
    }

    @Override
    public void setFocus() {
        nameField.setFocus();
    }

    @Override
    public void createTextFields(Composite parent) {
        Composite inner = createSectionComposite(Messages.jclouds_cloudDetails, new GridData(GridData.FILL_BOTH));

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        inner.setLayout(layout);

        nameField = createBeanPropertyTextField(inner, details, "name", Messages.jclouds_nameLabel, Messages.jclouds_nameTooltip);
        providerNameField = createBeanPropertyCombo(inner, details, "provider", Messages.jclouds_providerNameLabel, Messages.jclouds_providerNameTooltip, JClouds.getComputeProviders());
        apiNameField = createBeanPropertyCombo(inner, details, "api", Messages.jclouds_apiNameLabel, Messages.jclouds_apiNameTooltip, JClouds.getComputeApis());


        // TODO not sure why we need this :)
        ProviderMetadata provider = details.getProvider();
        if (provider != null) {
            providerNameField.setSelection(new StructuredSelection(provider));
        }
        ApiMetadata api = details.getApi();
        if (api != null) {
            apiNameField.setSelection(new StructuredSelection(api));
        }

        endpointField = createBeanPropertyTextField(inner, details, "endpoint", Messages.jclouds_endpointLabel, Messages.jclouds_endpointTooltip);
        identityField = createBeanPropertyTextField(inner, details, "identity", Messages.jclouds_identityLabel, Messages.jclouds_identityTooltip);
        createBeanPropertyTextField(inner, details, "credential", Messages.jclouds_credentialLabel, Messages.jclouds_credentialTooltip, SWT.PASSWORD);
        createBeanPropertyTextField(inner, details, "ownerId", Messages.jclouds_ownerLabel, Messages.jclouds_ownerTooltip, SWT.PASSWORD);

        apiNameField.addPostSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                endpointField.setEnabled(!event.getSelection().isEmpty());
            }
        });
    }



    protected ComboViewer createBeanPropertyCombo(Composite parent, Object bean, String propertyName, String labelText, String tooltip, Iterable<?> input) {
        ComboViewer answer = createBeanPropertyCombo(parent, bean, propertyName, labelText, tooltip, SWT.READ_ONLY);
        answer.setInput(input);
        answer.setLabelProvider(JCloudsLabelProvider.getInstance());
        return answer;
    }

    @Override
    public void okPressed() {
        // lets cache the data and check if its invalid
        CloudDetailsCachedData.getInstance(details).startLoadingDataJobs();
    }


}