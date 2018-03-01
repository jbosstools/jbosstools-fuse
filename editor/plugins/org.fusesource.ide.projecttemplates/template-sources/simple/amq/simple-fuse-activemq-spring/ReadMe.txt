Camel ActiveMQ Project
======================

This project embeds Apache ActiveMQ together with Apache Camel.

To build this project use

    mvn install

To run this project

    mvn camel:run

To run this project from Red Hat Fuse shell :

    - create a properties file named camel.activemq.spring.cfg in etc folder of Red Hat Fuse containing ActiveMQ
      connection options, i.e :

      broker.url       = tcp://localhost:61616
      broker.username  = admin
      broker.password  = admin
	
	- install dependencies from Fuse shell:
	
	  features:install activemq-camel
	
    - install the bundle from Fuse shell :

      install -s mvn:com.mycompany/camel-activemq-spring/1.0.0-SNAPSHOT

For more help see the Apache Camel documentation

    http://camel.apache.org/
    
For more help on ActiveMQ Camel component see the Apache Camel documentation for ActiveMQ

	http://camel.apache.org/activemq.html
