package taskmanager;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.equalTo;

import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

public class GreetingsTest extends CamelBlueprintTestSupport {

	@Test
	public void testHelloGreeting() throws Exception {
		get("/greetings/hello/World").then().body(equalTo("Hello World"));
	}

	@Override
	protected String getBlueprintDescriptor() {
		return "OSGI-INF/blueprint/blueprint.xml";
	}

}
