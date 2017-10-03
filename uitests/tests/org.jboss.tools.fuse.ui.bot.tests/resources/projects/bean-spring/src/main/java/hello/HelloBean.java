package hello;

/**
 * @author apodhrad
 *
 */
public class HelloBean {

	private String name;

	public HelloBean() {
		this("default");
	}

	public HelloBean(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String hello() {
		return "Hey " + getName();
	}

	void defaultVoid() {
	}

	public void publicVoid() {
	}

	protected void protectedVoid() {
	}

	private void privateVoid() {
	}

	static void defaultStaticVoid() {
	}

	public static void publicStaticVoid() {
	}

	protected static void protectedStaticVoid() {
	}

	private static void privateStaticVoid() {
	}

	HelloBean defaultFactory() {
		return new HelloBean("defaultFactory");
	}

	public HelloBean publicFactory() {
		return new HelloBean("publicFactory");
	}

	protected HelloBean protectedFactory() {
		return new HelloBean("protectedFactory");
	}

	private HelloBean privateFactory() {
		return new HelloBean("protectedFactory");
	}

	static HelloBean defaultStaticFactory() {
		return new HelloBean("defaultStaticFactory");
	}

	public static HelloBean publicStaticFactory() {
		return new HelloBean("publicStaticFactory");
	}

	protected static HelloBean protectedStaticFactory() {
		return new HelloBean("protectedStaticFactory");
	}

	private static HelloBean privateStaticFactory() {
		return new HelloBean("privateStaticFactory");
	}
}
