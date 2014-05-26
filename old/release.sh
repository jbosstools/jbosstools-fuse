#!/bin/bash
export PERFECUT_ARTIFACT=ide

. `dirname $0 2> /dev/null`/common.sh

ACTION=$1
cd "${basedir}"


BUILDNO=`cat ${PERFECUT_ARTIFACT}-buildno.txt`
export BUILD=`printf "%02d" "${BUILDNO}"`
if [[ -z "${ACTION}" ||  "${ACTION}" == "resume" ]] ; then
  echo ============================================================================
  echo " Resuming build train: ${PERFECUT_ARTIFACT}-$BUILD"
  echo ============================================================================
elif [[ "${ACTION}" = "increment" ]] ; then
  BUILDNO=$((${BUILDNO}+1))
  BUILD=`printf "%02d" "$((${BUILDNO}))"` 
  echo ============================================================================
  echo " Incrementing build train to: ${PERFECUT_ARTIFACT}-${BUILD}"
  echo ============================================================================
  
  echo ${BUILDNO} > ${PERFECUT_ARTIFACT}-buildno.txt
  run git add ${PERFECUT_ARTIFACT}-buildno.txt
  git commit -m "Incremented build to ${BUILD}"
  run git push origin HEAD:master  
  
  echo cleaning working directory
  run rm -Rf "${PERFECTUS_TARGET}"
  
else
  echo ============================================================================
  echo " Requested action ${ACTION} is not supported."
  echo ============================================================================
  exit 1
fi

export KARAF_BRANCH="karaf-2.1.x-fuse"
export KARAF_VERSION="2.1.4-fuse-00-${BUILD}"

export CXF_BRANCH="2.3.x-fuse"
export CXF_VERSION="2.3.3-fuse-01-${BUILD}"

export ACTIVEMQ_BRANCH="5.4.x-fuse"
export ACTIVEMQ_VERSION="5.4.2-fuse-03-${BUILD}"

export CAMEL_BRANCH="2.6.x-fuse"
export CAMEL_VERSION="2.6.0-fuse-01-${BUILD}"

export UTILS_BRANCH="utils-1.4.x-fuse"
export UTILS_VERSION="1.4.0-fuse-01-${BUILD}"

export NMR_BRANCH="nmr-1.4.x-fuse"
export NMR_VERSION="1.4.0-fuse-01-${BUILD}"

export COMPONENTS_BRANCH="components-2011.01.0-fuse"
export COMPONENTS_VERSION="2011.01.0-fuse-01-${BUILD}"

export FEATURES_BRANCH="features-4.3.1-fuse"
export FEATURES_VERSION="4.3.1-fuse-01-${BUILD}"

export ARCHETYPES_BRANCH="archetypes-2011.01.x-fuse"
export ARCHETYPES_VERSION=$COMPONENTS_VERSION


export REPO_ARCHIVE_FILE="${PERFECUT_ARTIFACT}-${BUILD}-offline-repo.tar.gz"

release "karaf" \
           "ssh://git@forge.fusesource.com/karaf.git" \
           "${KARAF_BRANCH}" \
           "${KARAF_VERSION}" \
           "org/apache/karaf/apache-karaf"

release "cxf" \
           "ssh://git@forge.fusesource.com/cxf.git" \
           "${CXF_BRANCH}" \
           "${CXF_VERSION}" \
           "org/apache/cxf/apache-cxf"

release "camel" \
           "ssh://git@forge.fusesource.com/camel.git" \
           "${CAMEL_BRANCH}" \
           "${CAMEL_VERSION}" \
           "org/apache/camel/apache-camel"


release "activemq" \
           "ssh://git@forge.fusesource.com/activemq.git" \
           "${ACTIVEMQ_BRANCH}" \
           "${ACTIVEMQ_VERSION}" \
           "org/apache/activemq/apache-activemq"

release "smx-utils" \
           "ssh://git@forge.fusesource.com/esbutils.git" \
           "${UTILS_BRANCH}" \
           "${UTILS_VERSION}" \
           "org/apache/servicemix/servicemix-utils"

release "smx-components" \
           "ssh://git@forge.fusesource.com/esbcomponents.git" \
           "${COMPONENTS_BRANCH}" \
           "${COMPONENTS_VERSION}" \
           "org/apache/servicemix/servicemix-common"

release "nmr" \
           "ssh://git@forge.fusesource.com/esbnmr.git" \
           "${NMR_BRANCH}" \
           "${NMR_VERSION}" \
           "org/apache/servicemix/nmr/apache-servicemix-nmr"

release "smx4-features" \
           "ssh://git@forge.fusesource.com/esbfeatures.git" \
           "${FEATURES_BRANCH}" \
           "${FEATURES_VERSION}" \
           "org/apache/servicemix/apache-servicemix"

release "archetypes" \
           "ssh://git@forge.fusesource.com/esbarchetypes.git" \
           "${ARCHETYPES_BRANCH}" \
           "${ARCHETYPES_VERSION}" \
           "org/apache/servicemix/archetypes"
           

echo
echo ============================================================================
echo " Supplementing Offline Repo"
echo ============================================================================
post-release "karaf"          "${KARAF_VERSION}"
post-release "cxf"            "${CXF_VERSION}"
post-release "camel"          "${CAMEL_VERSION}"
post-release "activemq"       "${ACTIVEMQ_VERSION}"
post-release "smx-utils"      "${UTILS_VERSION}"
post-release "smx-components" "${COMPONENTS_VERSION}"
post-release "nmr"            "${NMR_VERSION}"
post-release "smx4-features"  "${FEATURES_VERSION}"
post-release "archetypes"     "${ARCHETYPES_VERSION}"

download-optional-deps

echo 
echo 
echo ============================================================================
echo " Creating/Deploying: ${REPO_ARCHIVE_FILE}"
echo ============================================================================
echo
cd "${PERFECTUS_TARGET}"
run tar --exclude _maven.repositories -zcf "${REPO_ARCHIVE_FILE}" repo sources
run s3cmd put -f -M "${REPO_ARCHIVE_FILE}" s3://fusesource-releases/
echo 
echo ============================================================================
echo " Download the offline buildable distribution from: "
echo "   http://repo.fusesource.com/restricted/releases/${REPO_ARCHIVE_FILE}"
echo ============================================================================
echo
