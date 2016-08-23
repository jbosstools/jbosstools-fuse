Camel ActiveMQ Project
======================

This project embeds Apache ActiveMQ together with Apache Camel.

To build this project use

    mvn install

To run this project

    mvn camel:run

To run this project from JBoss Fuse shell :

    - create a properties file named camel.activemq.spring.conf in etc folder of JBoss Fuse containing ActiveMQ
      connection options, i.e :

      broker.url       = tpc://localhost:61616
      broker.username  = admin
      broker.password  = admin

    - install the bundle from Fuse shell :

      install -s mvn:com.mycompany/camel-spring-blueprint/1.0.0-SNAPSHOT

For more help see the Apache Camel documentation

    http://camel.apache.org/
    
For more help on ActiveMQ Camel component see the Apache Camel documentation for ActiveMQ

	http://camel.apache.org/activemq.html
