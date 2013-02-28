## Generating code

This module generates code for various things like Fuse IDE or hawtio.

To run the code generation use:

    mvn test-compile exec:java

To use a specific generator, e.g. for hawtio, use

    mvn test-compile exec:java -DcodeGen=hawtio

