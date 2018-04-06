package taskmanager;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.equalTo;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GreetingsTest extends CamelSpringTestSupport {

	@Test
	public void testHelloGreeting() throws Exception {
		get("/greetings/hello/World").then().body(equalTo("Hello World"));
	}

	@Override
	protected ClassPathXmlApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
	}
}
