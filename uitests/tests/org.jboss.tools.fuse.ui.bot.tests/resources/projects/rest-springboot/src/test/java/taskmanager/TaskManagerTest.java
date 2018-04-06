package taskmanager;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.head;
import static io.restassured.RestAssured.options;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@TestPropertySource(locations = "classpath:application.properties")
public class TaskManagerTest {

	@Before
	public void initTaskManager() {
		delete("/taskmanager/tasks").andReturn();
	}

	@Test
	public void testGettingInitialTasks() throws Exception {
		get("/taskmanager/tasks").then().body("status", equalTo("success")).body("data.tasks.description",
				equalTo(Arrays.asList("Initial task", "Another task")));
	}

	@Test
	public void testGettingOneTask() throws Exception {
		get("/taskmanager/tasks/2").then().body("status", equalTo("success")).body("data.task.id", equalTo(2))
				.body("data.task.description", equalTo("Another task"));
	}

	@Test
	public void testGettingOneTaskViaVerb() throws Exception {
		get("/taskmanager/task/1").then().body("status", equalTo("success")).body("data.task.id", equalTo(1))
				.body("data.task.description", equalTo("Initial task"));
	}

	@Test
	public void testHeadingExistingTask() throws Exception {
		head("/taskmanager/tasks/2").then().header("Content-length", equalTo("74"));
	}

	@Test
	public void testGettingNonExistingTask() throws Exception {
		get("/taskmanager/tasks/3").then().body("status", equalTo("fail")).body("data.title",
				equalTo("Cannot find any task with id=3"));
	}

	@Test
	public void testAddingNewTask() throws Exception {
		given().contentType(ContentType.JSON).body("{\"description\": \"New task\"}").post("/taskmanager/tasks").then()
				.body("data.task.id", equalTo(3)).body("data.task.description", equalTo("New task"));
		get("/taskmanager/tasks").then().body("data.tasks.description",
				equalTo(Arrays.asList("Initial task", "Another task", "New task")));
	}

	@Test
	public void testDeletingTask() throws Exception {
		delete("/taskmanager/tasks/1").then().body("data", Matchers.isEmptyOrNullString());
		get("/taskmanager/tasks").then().body("data.tasks.description", equalTo(Arrays.asList("Another task")));
	}

	@Test
	public void testUpdatingTask() throws Exception {
		given().contentType(ContentType.JSON).body("{\"id\":\"2\", \"description\": \"Second task\"}")
				.put("/taskmanager/tasks").then().body("data.task.description", equalTo("Second task"));
		get("/taskmanager/tasks").then().body("data.tasks.description",
				equalTo(Arrays.asList("Initial task", "Second task")));
	}

	@Test
	public void testPatchingTask() throws Exception {
		given().contentType(ContentType.JSON).body("{\"id\":\"2\", \"description\": \"Cool task\"}")
				.patch("/taskmanager/tasks").then().body("data.task.description", equalTo("Cool task"));
		get("/taskmanager/tasks").then().body("data.tasks.description",
				equalTo(Arrays.asList("Initial task", "Cool task")));
	}

	@Test
	public void testOptionsForAllTasks() throws Exception {
		options("/taskmanager/tasks").then().header("Allow", equalTo("DELETE, POST, GET, OPTIONS, PUT, PATCH"));
	}

	@Test
	public void testOptionsForOneTask() throws Exception {
		options("/taskmanager/tasks/1").then().header("Allow", equalTo("HEAD, DELETE, GET, OPTIONS"));
	}

}
