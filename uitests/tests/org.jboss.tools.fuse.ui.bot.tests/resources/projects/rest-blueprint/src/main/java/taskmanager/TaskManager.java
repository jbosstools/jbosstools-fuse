package taskmanager;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {

	private List<Task> tasks;
	private int indexSequence;

	public TaskManager() {
		init();
	}

	public void init() {
		indexSequence = 1;
		tasks = new ArrayList<>();

		Task t1 = new Task();
		t1.setDescription("Initial task");
		Task t2 = new Task();
		t2.setDescription("Another task");

		addTask(t1);
		addTask(t2);
	}

	public TaskManagerResponse getTasks() {
		return new TaskManagerResponse().success().taskList(tasks);
	}

	public TaskManagerResponse getTask(int id) {
		Task task = findTask(id);
		if (task.exists()) {
			return new TaskManagerResponse().success().singleTask(task);
		} else {
			return new TaskManagerResponse().fail().title("Cannot find any task with id=" + id);
		}
	}

	public TaskManagerResponse addTask(Task task) {
		task.setId(indexSequence++);
		tasks.add(task);

		return new TaskManagerResponse().success().singleTask(task);
	}

	public TaskManagerResponse updateTask(Task task) {
		Task foundTask = findTask(task.getId());
		foundTask.setDescription(task.getDescription());

		return new TaskManagerResponse().success().singleTask(foundTask);
	}

	public TaskManagerResponse deleteTask(int id) {
		tasks.remove(findTask(id));

		return new TaskManagerResponse().success();
	}

	private Task findTask(int id) {
		return tasks.stream().filter(t -> t.getId() == id).findFirst().orElse(new Task());
	}
}
