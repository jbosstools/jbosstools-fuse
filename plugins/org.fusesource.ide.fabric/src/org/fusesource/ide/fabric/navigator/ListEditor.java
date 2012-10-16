package org.fusesource.ide.fabric.navigator;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.fusesource.ide.fabric.FabricPlugin;


public abstract class ListEditor<T> {
	private String addButtonLabel = "Add";
	private String addButtonTooltip;
	private String editButtonLabel = "Edit";
	private String editButtonTooltip;
	private String deleteButtonLabel = "Delete";
	private String deleteButtonTooltip;

	private String addDialogLabel = "Add";
	private String addDialogText = "New value";
	private String editDialogLabel = "Edit";
	private String editDialogText = "Edit value";
	private String deleteDialogLabel = "Delete";


	public ListEditor() {
	}

	public ListEditor(String addButtonLabel, String addButtonTooltip, String editButtonLabel, String editButtonTooltip,
			String deleteButtonLabel, String deleteButtonTooltip, String addDialogLabel, String addDialogText,
			String editDialogLabel, String editDialogText, String deleteDialogLabel) {
		this.addButtonLabel = addButtonLabel;
		this.addButtonTooltip = addButtonTooltip;
		this.editButtonLabel = editButtonLabel;
		this.editButtonTooltip = editButtonTooltip;
		this.deleteButtonLabel = deleteButtonLabel;
		this.deleteButtonTooltip = deleteButtonTooltip;
		this.addDialogLabel = addDialogLabel;
		this.addDialogText = addDialogText;
		this.editDialogLabel = editDialogLabel;
		this.editDialogText = editDialogText;
		this.deleteDialogLabel = deleteDialogLabel;
	}

	public abstract List<T> getList();

	public abstract void setList(List<T> list);

	public void addValue(T value) {
		List<T> list = getList();
		list.add(value);
		setList(list);
	}

	public void editValue(T value, T oldValue) {
		List<T> list = getList();
		list.remove(oldValue);
		list.add(value);
		setList(list);
	}

	public void removeValue(T value) {
		List<T> list = getList();
		list.remove(value);
		setList(list);
	}


	// Properties

	public ImageDescriptor getAddImageDescriptor() {
		return FabricPlugin.getPlugin().getImageDescriptor("add_obj.gif");
	}

	public ImageDescriptor getEditImageDescriptor() {
		return FabricPlugin.getPlugin().getImageDescriptor("prop_ps.gif");
	}

	public ImageDescriptor getDeleteImageDescriptor() {
		return FabricPlugin.getPlugin().getImageDescriptor("delete.gif");
	}

	public String getAddButtonLabel() {
		return addButtonLabel;
	}

	public void setAddButtonLabel(String addButtonLabel) {
		this.addButtonLabel = addButtonLabel;
	}

	public String getAddButtonTooltip() {
		return addButtonTooltip;
	}

	public void setAddButtonTooltip(String addButtonTooltip) {
		this.addButtonTooltip = addButtonTooltip;
	}

	public String getEditButtonLabel() {
		return editButtonLabel;
	}

	public void setEditButtonLabel(String editButtonLabel) {
		this.editButtonLabel = editButtonLabel;
	}

	public String getEditButtonTooltip() {
		return editButtonTooltip;
	}

	public void setEditButtonTooltip(String editButtonTooltip) {
		this.editButtonTooltip = editButtonTooltip;
	}

	public String getDeleteButtonLabel() {
		return deleteButtonLabel;
	}

	public void setDeleteButtonLabel(String deleteButtonLabel) {
		this.deleteButtonLabel = deleteButtonLabel;
	}

	public String getDeleteButtonTooltip() {
		return deleteButtonTooltip;
	}

	public void setDeleteButtonTooltip(String deleteButtonTooltip) {
		this.deleteButtonTooltip = deleteButtonTooltip;
	}

	public String getAddDialogLabel() {
		return addDialogLabel;
	}

	public void setAddDialogLabel(String addDialogLabel) {
		this.addDialogLabel = addDialogLabel;
	}

	public String getEditDialogLabel() {
		return editDialogLabel;
	}

	public void setEditDialogLabel(String editDialogLabel) {
		this.editDialogLabel = editDialogLabel;
	}

	public String getDeleteDialogLabel() {
		return deleteDialogLabel;
	}

	public void setDeleteDialogLabel(String deleteDialogLabel) {
		this.deleteDialogLabel = deleteDialogLabel;
	}

	public String getAddDialogText() {
		return addDialogText;
	}

	public void setAddDialogText(String addDialogText) {
		this.addDialogText = addDialogText;
	}

	public String getEditDialogText() {
		return editDialogText;
	}

	public void setEditDialogText(String editDialogText) {
		this.editDialogText = editDialogText;
	}




}
