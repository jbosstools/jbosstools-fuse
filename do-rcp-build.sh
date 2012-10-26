#!/bin/bash

export PERFECUT_ARTIFACT=ide

MVN_SETTINGS="--settings ${HOME}/.m2/m3-settings.xml"

. `dirname $0 2> /dev/null`/common.sh

ACTION=$1

cd "${basedir}"

git stash
git checkout master
git stash
git pull

BUILDNO=`cat ${PERFECUT_ARTIFACT}-buildno.txt`

BUILDNO=$((${BUILDNO}+1))
BUILD=`printf "%02d" "$((${BUILDNO}))"` 

echo ============================================================================
echo " Incrementing build train to: ${PERFECUT_ARTIFACT}-${BUILD}"
echo "Using MVN_SETTINGS: ${MVN_SETTINGS}"
echo ============================================================================

echo ${BUILDNO} > ${PERFECUT_ARTIFACT}-buildno.txt
VERSION=3.0.${BUILDNO}
git stash clear

echo "Creating release branch for ${VERSION}"

# root project uses regular maven versions
find * -name 'pom.xml' | xargs perl -pi -e "s/<version>3.0.0-SNAPSHOT<\/version>/<version>${VERSION}<\/version>/g"

# replace the OSGi version names in poms
find plugins -name 'pom.xml' | xargs perl -pi -e "s/<version>3.0.0.qualifier<\/version>/<version>${VERSION}<\/version>/g"

# replace manifest versions
find plugins -name '*.xml' | xargs perl -pi -e "s/3.0.0.qualifier/${VERSION}/g"
find plugins -name 'bundle.properties' | xargs perl -pi -e "s/3.0.0.qualifier/${VERSION}/g"
find plugins -name 'MANIFEST.MF' | xargs perl -pi -e "s/3.0.0.qualifier/${VERSION}/g"
find plugins -name 'fuse*.product' | xargs perl -pi -e "s/3.0.0.qualifier/${VERSION}/g"

# replace IDE version
perl -pi -e "s/<ide-version>.*<\/ide-version>/<ide-version>${VERSION}<\/ide-version>/g" plugins/pom.xml

#echo "Zapping P2 cache of fusesource and snapshot stuff"
rm -rf /mnt/hudson/.m2/repository/p2/osgi/bundle/*/*SNAPSHOT

echo ============================================================================
echo "Creating the RCP build"
echo ============================================================================

cd plugins/rcp_build
mvn ${MVN_SETTINGS} clean install

if [ $? -eq 0 ]         # Test exit status of "mvn" command.
then
  echo "Build succeeded. Progress to integration testing..."
else  
  echo "Build failed. Exit now..."
  exit $?
fi



