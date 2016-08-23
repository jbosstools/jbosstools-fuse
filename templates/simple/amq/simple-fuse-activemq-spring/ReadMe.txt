Camel ActiveMQ Project
======================

This project embeds Apache ActiveMQ together with Apache Camel.

To build this project use

    mvn install

To run this project

    mvn camel:run

To run this project on JBoss Fuse

    - create a pid file etc/camel.activemq.spring.conf containing ActiveMQ
      connection options on JBoss Fuse, i.e :

      broker.url       = tpc://localhost:61616
      broker.username  = admin
      broker.password  = admin

    - install the bundle from Fuse shell

      install -s mvn:com.mycompany/camel-spring-blueprint/1.0.0-SNAPSHOT

For more help see the Apache Camel documentation

    http://camel.apache.org/
