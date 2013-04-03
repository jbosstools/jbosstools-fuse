#!/bin/bash

STAGINGDIR=plugins/rcp_build/org.fusesource.ide.rcp.product/target/zips
SRCSNAME=FuseIDE-sources.zip

rm ${STAGINGDIR}/${SRCSNAME}
zip ${STAGINGDIR}/${SRCSNAME} -q -r * -x \*.sh -x \*.idea -x \*.iml -x /etc/\* -x /grunt.js -x /addUpdateSiteVersion \
    -x /ide-buildno.txt -x /web/\* -x \*target/\* -x \*.class -x \*.svn\* -x \*classes\* -x \*bin\* -x \*.zip \
    -x \*.git\* -x \*/lib/\*.jar -x \*/.idea/\* -x \*.DS_Store -x \*.keystore -x \*RemoteSystemsTempFiles/\* \
    -x /plugins/Notices.txt -x /plugins/samples/\* -x \*.project -x \*testing/\* -x \*workspace/\* \
    -x \*.classpath -x \*.settings/\*
