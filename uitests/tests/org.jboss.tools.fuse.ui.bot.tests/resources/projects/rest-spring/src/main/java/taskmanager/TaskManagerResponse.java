package taskmanager;

import java.util.List;

public class TaskManagerResponse {

	private String status;
	private Object data;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public TaskManagerResponse success() {
		setStatus("success");
		return this;
	}

	public TaskManagerResponse fail() {
		setStatus("fail");
		return this;
	}

	public TaskManagerResponse title(String titleString) {
		Title title = new Title();
		title.setTitle(titleString);
		setData(title);
		return this;
	}

	public TaskManagerResponse singleTask(Task task) {
		SingleTask singleTask = new SingleTask();
		singleTask.setTask(task);
		setData(singleTask);
		return this;
	}

	public TaskManagerResponse taskList(List<Task> tasks) {
		TaskList taskList = new TaskList();
		taskList.setTasks(tasks);
		setData(taskList);
		return this;
	}

	private class SingleTask {

		private Task task;

		public Task getTask() {
			return task;
		}

		public void setTask(Task task) {
			this.task = task;
		}

	}

	private class TaskList {

		private List<Task> tasks;

		public List<Task> getTasks() {
			return tasks;
		}

		public void setTasks(List<Task> tasks) {
			this.tasks = tasks;
		}

	}

	private class Title {

		private String title;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

	}

}
