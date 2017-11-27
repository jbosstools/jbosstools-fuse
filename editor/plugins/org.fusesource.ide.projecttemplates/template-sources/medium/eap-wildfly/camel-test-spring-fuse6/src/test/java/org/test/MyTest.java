package org.test;

import java.io.File;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MyTest {

    @Deployment
    public static JavaArchive createDeployment() {
        final JavaArchive  archive = ShrinkWrap.create(JavaArchive.class, "camel-tests.jar");
        archive.addPackage(MyBean.class.getPackage());
        archive.addAsResource(new File("src/main/webapp/META-INF/jboss-camel-context.xml"), "jboss-camel-context.xml");
        return archive;
    }

    @Test
    public void testMyRoute() throws NamingException {
        InitialContext context = new InitialContext();
        CamelContext camelContext = (CamelContext) context.lookup("java:jboss/camel/context/spring-context");
        Assert.assertNotNull("Expecting camelContext to not be null", camelContext);

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        String result = producerTemplate.requestBody("direct:start", "Kermit", String.class);

        Assert.assertEquals("Hello Kermit", result);
    }
}
