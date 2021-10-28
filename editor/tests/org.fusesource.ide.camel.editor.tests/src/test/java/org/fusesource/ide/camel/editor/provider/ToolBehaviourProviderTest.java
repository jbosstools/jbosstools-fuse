/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.impl.PictogramElementImpl;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.validation.ValidationFactory;
import org.fusesource.ide.camel.validation.ValidationResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class ToolBehaviourProviderTest {

	@Mock
	private ValidationFactory validationFactoryInstance;
	@Mock
	private AbstractCamelModelElement node;

	@Test
	public void testGetDecorators_ReturnEmptyListForInvisibleElement() throws Exception {
		PictogramElement pe = new PictogramElementImpl() {

		};
		pe.setVisible(false);
		ToolBehaviourProvider tbp = new ToolBehaviourProvider(new DiagramTypeProvider());
		IDecorator[] decorators = tbp.getDecorators(pe);

		assertThat(decorators).hasSize(0);
	}

	@Test
	public void testAddValidationDecorators_concatenateMessagesForError() {
		ValidationResult validationResult = new ValidationResult();
		validationResult.addError("error1");
		validationResult.addError("error2");
		doReturn(validationResult).when(validationFactoryInstance).validate(node);
		ToolBehaviourProvider tbp = new ToolBehaviourProvider(new DiagramTypeProvider());
		List<IDecorator> decorators = new ArrayList<IDecorator>();

		tbp.addValidationDecorators(validationFactoryInstance, decorators, node);

		assertThat(decorators).hasSize(1);
		final IDecorator iDecorator = decorators.get(0);
		assertThat(iDecorator.getMessage()).isEqualTo("error1\nerror2");
		assertThat(iDecorator).isInstanceOf(ImageDecorator.class);
		assertThat(((ImageDecorator) iDecorator).getImageId()).isEqualTo(IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK);
		assertThat(((ImageDecorator) iDecorator).getX()).isEqualTo(ToolBehaviourProvider.OFFSET_X_DECORATOR);
		assertThat(((ImageDecorator) iDecorator).getY()).isEqualTo(ToolBehaviourProvider.OFFSET_Y_VALIDATION_DECORATOR);
	}

	@Test
	public void testAddValidationDecorators_concatenateMessagesForWarning() {
		ValidationResult validationResult = new ValidationResult();
		validationResult.addWarning("warning1");
		validationResult.addWarning("warning2");
		doReturn(validationResult).when(validationFactoryInstance).validate(node);
		ToolBehaviourProvider tbp = new ToolBehaviourProvider(new DiagramTypeProvider());
		List<IDecorator> decorators = new ArrayList<IDecorator>();

		tbp.addValidationDecorators(validationFactoryInstance, decorators, node);

		assertThat(decorators).hasSize(1);
		final IDecorator iDecorator = decorators.get(0);
		assertThat(iDecorator.getMessage()).isEqualTo("warning1\nwarning2");
		assertThat(iDecorator).isInstanceOf(ImageDecorator.class);
		assertThat(((ImageDecorator) iDecorator).getImageId()).isEqualTo(IPlatformImageConstants.IMG_ECLIPSE_WARNING_TSK);
		assertThat(((ImageDecorator) iDecorator).getX()).isEqualTo(ToolBehaviourProvider.OFFSET_X_DECORATOR);
		assertThat(((ImageDecorator) iDecorator).getY()).isEqualTo(ToolBehaviourProvider.OFFSET_Y_VALIDATION_DECORATOR);
	}

	@Test
	public void testAddValidationDecorators_concatenateMessagesForInfo() {
		ValidationResult validationResult = new ValidationResult();
		validationResult.addInfo("info1");
		validationResult.addInfo("info2");
		doReturn(validationResult).when(validationFactoryInstance).validate(node);
		ToolBehaviourProvider tbp = new ToolBehaviourProvider(new DiagramTypeProvider());
		List<IDecorator> decorators = new ArrayList<IDecorator>();

		tbp.addValidationDecorators(validationFactoryInstance, decorators, node);

		assertThat(decorators).hasSize(1);
		final IDecorator iDecorator = decorators.get(0);
		assertThat(iDecorator.getMessage()).isEqualTo("info1\ninfo2");
		assertThat(iDecorator).isInstanceOf(ImageDecorator.class);
		assertThat(((ImageDecorator) iDecorator).getImageId()).isEqualTo(IPlatformImageConstants.IMG_ECLIPSE_INFORMATION_TSK);
		assertThat(((ImageDecorator) iDecorator).getX()).isEqualTo(ToolBehaviourProvider.OFFSET_X_DECORATOR);
		assertThat(((ImageDecorator) iDecorator).getY()).isEqualTo(ToolBehaviourProvider.OFFSET_Y_VALIDATION_DECORATOR);
	}

}
