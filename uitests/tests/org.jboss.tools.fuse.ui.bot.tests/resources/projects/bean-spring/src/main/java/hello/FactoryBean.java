package hello;

/**
 * @author apodhrad
 *
 */
public class FactoryBean {

	public FactoryBean() {
	}

	public void voidMethod() {

	}

	public static void staticVoidMethod() {

	}

	HelloBean defaultCreateHelloBean() {
		return new HelloBean();
	}

	public HelloBean publicCreateHelloBean() {
		return new HelloBean("publicCreateHelloBean");
	}

	protected HelloBean protectedCreateHelloBean() {
		return new HelloBean("protectedCreateHelloBean");
	}

	private HelloBean privateCreateHelloBean() {
		return new HelloBean("privateCreateHelloBean");
	}

	static HelloBean defaultStaticCreateHelloBean() {
		return new HelloBean("defaultStaticCreateHelloBean");
	}

	public static HelloBean publicStaticCreateHelloBean() {
		return new HelloBean("publicStaticCreateHelloBean");
	}

	protected static HelloBean protectedStaticCreateHelloBean() {
		return new HelloBean("protectedStaticCreateHelloBean");
	}

	private static HelloBean privateStaticCreateHelloBean() {
		return new HelloBean("privateStaticCreateHelloBean");
	}
}
