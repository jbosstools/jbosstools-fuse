#!/bin/bash

basedir="$(cd $(/usr/bin/dirname "${BASH_SOURCE[0]}"); pwd -P)/$(/usr/bin/basename "${BASH_SOURCE[0]}")"
[[ ! -f "$basedir" ]] && basedir="$(cd $(/usr/bin/dirname "$0"); pwd -P)/$(/usr/bin/basename "$0")"
[[ ! -f "$basedir" ]] && basedir="" && echo 'No full path to running script found!' && exit 1

basedir="${basedir%/*}"
cd $basedir
export basedir=`pwd`
cd - > /dev/null

# ============================================================================
# Define the versions used by this run of the release train.
# ============================================================================

if [ -z "$JAVA_HOME" ] ; then
  export JAVA_HOME="/usr/lib/jvm/java-6-sun"
  export PATH="${JAVA_HOME}/bin:${PATH}"
fi

if [ -z "$M2_HOME" ] ; then
  export M2_HOME="/opt/apache-maven-3.0.2"
fi
export MVN=${M2_HOME}/bin/mvn

if [ -f "$HOME/.m2/m3-release-settings.xml" ] ; then
  # Use a maven 3 specific settings file if it exists. 
  export MVN="$MVN --settings $HOME/.m2/m3-release-settings.xml"
fi

export PERFECTUS_TARGET="${basedir}/target/${PERFECUT_ARTIFACT}"
export MAVEN_OPTS="-Xmx800m -XX:MaxPermSize=192m -Dmaven.artifact.threads=5"
export MVN="$MVN -Dmaven.repo.local=${PERFECTUS_TARGET}/repo -V -B"

mkdir -p "${PERFECTUS_TARGET}"

home=`cd ; pwd`
settings=`sed -n '/<servers>/,/<\/servers>/p' ${home}/.m2/settings.xml | sed -n '/<id>fusesource-nexus-staging<\/id>/,/<\/server>/p'`
NEXUS_USER=`echo $settings | sed -e '/<username>/!d;s/.*<username>\(.*\)<\/username>.*/\1/g'`
NEXUS_PASSWORD=`echo $settings | sed -e '/<password>/!d;s/.*<password>\(.*\)<\/password>.*/\1/g'`
NEXUS_URL="http://repo.fusesource.com/nexus"

if [ "x${NEXUS_USER}" == "x" ]; then
  echo "Variable NEXUS_USER not defined. Aborting."
  exit 1
fi
if [ "x${NEXUS_PASSWORD}" == "x" ]; then
  echo "Variable NEXUS_PASSWORD not defined. Aborting."
  exit 1
fi

# ============================================================================
# Common functions and setup.
# ============================================================================

function try {
  echo "Running: $*"
  $* 2> /dev/null | tee "${PERFECTUS_TARGET}/run.log" 
  return ${PIPESTATUS[0]}
}

function run {
  echo "Running: $*"
  $* | tee "${PERFECTUS_TARGET}/run.log"
  rc=${PIPESTATUS[0]}
  if [ ${rc} -ne 0 ]; then
    echo "Got unexpected result code ${rc} from command:"
    echo ""
    echo "   $*"
    echo ""
    exit ${rc}
  fi
}

function update-version {
  sed "s/<\($2\)>.*-fuse-.*<\/\1>/<\1>$3<\/\1>/g" "$1" > "$1".new
  rm "$1"
  mv "$1".new "$1"
}

function update-versions {
  echo "Updating versions found in: $1"
  update-version "$1" "felix.configadmin.version" "${FELIX_CONFIGADMIN_VERSION}"
  update-version "$1" "felix.eventadmin.version" "${FELIX_EVENTADMIN_VERSION}"
  update-version "$1" "felix.framework.version" "${FELIX_FRAMEWORK_VERSION}"  
  update-version "$1" "karaf.version" "${KARAF_VERSION}"
  update-version "$1" "activemq.version" "${ACTIVEMQ_VERSION}"
  update-version "$1" "camel.version" "${CAMEL_VERSION}"
  update-version "$1" "cxf.version" "${CXF_VERSION}"
  update-version "$1" "servicemix.nmr.version" "${NMR_VERSION}"
  update-version "$1" "servicemix.components.version" "${COMPONENTS_VERSION}"
  update-version "$1" "servicemix.utils.version" "${UTILS_VERSION}"
}

function close-nexus {
  NEXUS_ARTIFACT="$1"
  
  echo ============================================================================
  echo " Closing Nexus Repository"
  echo ============================================================================

  cd ${basedir}
  wget --http-user="${NEXUS_USER}" --http-password="${NEXUS_PASSWORD}" -O ${PERFECTUS_TARGET}/wget.tmp "${NEXUS_URL}/service/local/staging/profiles" >> ${PERFECTUS_TARGET}/wget.log 2>&1
  profiles=`sed -e '/<id>/!d' -e 's|.*<id>\(.*\)</id>.*|\1|g' ${PERFECTUS_TARGET}/wget.tmp`
  sed -n -e ":a" -e "$ s/\n//gp;N;b a" ${PERFECTUS_TARGET}/wget.tmp > ${PERFECTUS_TARGET}/wget2.tmp
  for profile in $profiles
  do
    #echo "Checking profile $profile"
    stages=`sed -n '/<id>'$profile'</,/stagingProfile>/p' ${PERFECTUS_TARGET}/wget.tmp | \
          sed -n '/<stagingRepositoryIds>/,/<\/stagingRepositoryIds>/p' |
          sed -e '/<string>/!d;s/.*<string>\(.*\)<\/string>.*/\1/g'`
    for stage in $stages
    do
      echo "Check stage $stage in profile $profile"
      wget --http-user="${NEXUS_USER}" --http-password="${NEXUS_PASSWORD}" -O ${PERFECTUS_TARGET}/wget3.tmp "${NEXUS_URL}/service/local/repositories/${stage}/content/${NEXUS_ARTIFACT}/?isLocal" >> ${PERFECTUS_TARGET}/wget.log 2>&1
      nb=`cat ${PERFECTUS_TARGET}/wget3.tmp | grep resourceURI | wc -l`
      if [ $nb -gt 0 ]; then
        echo "Found matching repository $stage. Closing..."
        wget -v -d --http-user="${NEXUS_USER}" --http-password="${NEXUS_PASSWORD}" -O ${PERFECTUS_TARGET}/wget4.tmp \
            --header='Content-Type: application/xml' \
            --header='Accept: application/xml' \
                --post-data="<promoteRequest><data><stagedRepositoryId>${stage}</stagedRepositoryId></data></promoteRequest>" \
                "${NEXUS_URL}/service/local/staging/profiles/${profile}/finish" >> ${PERFECTUS_TARGET}/wget.log 2>&1
      fi
    done
  done
  
}

function release {
  
  PROJECT="$1"
  REPO="$2"
  BRANCH="$3"
  RELEASE_VERSION="$4"
  NEXUS_ARTIFACT="$5"
  
  WORK_BRANCH="perfectus-work-${PROJECT}-${RELEASE_VERSION}"
  TAG="${PROJECT}-${RELEASE_VERSION}"
  
  echo ============================================================================
  echo " Loading ${PROJECT} SCM data"
  echo ============================================================================

  if [ ! -d "${PERFECTUS_TARGET}/${PROJECT}" ]; then
    mkdir -p "${PERFECTUS_TARGET}/${PROJECT}"
    cd "${PERFECTUS_TARGET}/${PROJECT}"
    run git init
    run git remote add origin "${REPO}"
  fi

  cd "${PERFECTUS_TARGET}/${PROJECT}"

  try git tag -d "${TAG}"
  run git fetch -q origin --force > /dev/null
  run git fetch -q origin --force --tags > /dev/null

  # only do the release if the tag does not exist
  # to avoid steping over an existing release
  try git show-ref "refs/tags/${TAG}" > /dev/null
  if [ "$?" -eq 0 ]; then
    
    echo ""
    echo "   Release already exists in SCM, if you really want me to rebuild the"
    echo "   project, delete the release tag in the git repo.  For example:"
    echo ""
    echo "      git clone ${REPO} ${PROJECT}"
    echo "      cd ${PROJECT}"
    echo "      git push origin :${TAG}"
    echo ""
    
  else
    
    echo
    echo ============================================================================
    echo " Releasing ${PROJECT} version ${RELEASE_VERSION}"
    echo ============================================================================
    
    try git reset -q --hard
    try git clean -q -f
    run git checkout -q -f origin/${BRANCH} 2> /dev/null
    run git branch -f "${WORK_BRANCH}" origin/${BRANCH}
    run git checkout -q -f "${WORK_BRANCH}"
    
    # If a submodule has been specified, this would be a good time to cd into that directory
    if [ "x" != "x$6" ]; then
      cd "${PERFECTUS_TARGET}/${PROJECT}/$6"
    fi

    echo
    echo Updating pom version data...
    # Update the versions of the dependencies used.
    if [ -f parent/pom.xml ] ; then
      update-versions parent/pom.xml
    else 
      update-versions pom.xml
    fi
    
    # For FUSE ESB 4.4.0, we need to upgrade the parent POM instead of a property
    # Also, for both features and nmr, the versions:set needs to be invoked on the parent/pom.xml instead
    # TODO: refactor this to be less of a hack!
    if [ $PERFECUT_ARTIFACT == "esb-4.4.0-fuse" -a $PROJECT == "smx4-features" ]; then
       $basedir/update-parent.rb parent/pom.xml $NMR_VERSION
       run ${MVN} -q \
           org.codehaus.mojo:versions-maven-plugin:1.2:set \
           org.codehaus.mojo:versions-maven-plugin:1.2:commit \
           -DnewVersion="${RELEASE_VERSION}" \
           --file parent/pom.xml
    fi
    
    if [ $PERFECUT_ARTIFACT == "esb-4.4.0-fuse" -a $PROJECT == "nmr" ]; then
       run ${MVN} -q \
           org.codehaus.mojo:versions-maven-plugin:1.2:set \
           org.codehaus.mojo:versions-maven-plugin:1.2:commit \
           -DnewVersion="${RELEASE_VERSION}" \
           --file parent/pom.xml
    fi
    
    # Switch from the snapshot version to the release version.
    run ${MVN} -q \
        org.codehaus.mojo:versions-maven-plugin:1.2:set \
        org.codehaus.mojo:versions-maven-plugin:1.2:commit \
        -DnewVersion="${RELEASE_VERSION}"
    
    # Push the version update in our work branch to the origin
    git commit -q -am "[perfectus] Update to release version"
    run git push --force origin "${WORK_BRANCH}"
    
    # Try to load the resume args.
    resume_args=`cat ${PERFECTUS_TARGET}/${PROJECT}.resume 2> /dev/null`
    rm "${PERFECTUS_TARGET}/${PROJECT}.resume" 2> /dev/null
    
    echo ""
    if [ ! -z "${resume_args}" ] ; then
      echo "Resuming maven reactor build from the module that previously failed."
    else
      echo "Building maven project"
    fi
    
    # Do the releae build and deployment.
    try ${MVN} \
        -P release -P enable-schemagen \
        -Dtest=false \
        -DfailIfNoTests=false \
        clean deploy \
        ${resume_args}

    if [ $? -ne 0 ]; then
      
      # try to store the resume args..
      resume_args=`grep "\[ERROR\]   mvn <goals> -rf :" "${PERFECTUS_TARGET}/run.log" | cut -c 23-1000`
      if [ ! -z "${resume_args}" ] ; then
        echo "${resume_args}" > "${PERFECTUS_TARGET}/${PROJECT}.resume"
      fi
      
      echo
      echo ============================================================================
      echo " Build Failed for ${PROJECT} on branch ${WORK_BRANCH}"
      echo ============================================================================
      echo
      exit 1
    fi
    
    # tag it..
    run git tag "${TAG}"
    run git push origin "${TAG}" ":${WORK_BRANCH}"
    
    #
    # Close the nexus release repo... so the deployed deps can be loaded but subsequent builds.
    close-nexus "${NEXUS_ARTIFACT}/${RELEASE_VERSION}"
    
    echo
    echo Triggering hudson to perform cross platform testing of the tag ${TAG}
    echo
    run curl -q -f -#   "http://localhost:8080/hudson/job/${PROJECT}-perfectus-tests/buildWithParameters?token=a83dc6303ed1ddae7405a32f490522a6&TAG=${TAG}"

  fi
}

function post-release {
  PROJECT="$1"
  RELEASE_VERSION="$2"
  TAG="${PROJECT}-${RELEASE_VERSION}"

  cd "${PERFECTUS_TARGET}/${PROJECT}"
  try git reset -q --hard
  try git clean -q -f
  run git checkout -q -f ${TAG} 2> /dev/null
  
  echo ============================================================================
  echo " Downloading ${PROJECT}-${RELEASE_VERSION} dependency sources and javadoc"
  echo ============================================================================
  run ${MVN} -fae -T 4C org.apache.maven.plugins:maven-dependency-plugin:2.2:resolve -Dclassifier=sources
  run ${MVN} -fae -T 4C org.apache.maven.plugins:maven-dependency-plugin:2.2:resolve -Dclassifier=javadoc
  echo
  echo ============================================================================
  echo " Exporting ${PROJECT}-${RELEASE_VERSION} sources"
  echo ============================================================================
  try mkdir -p "${PERFECTUS_TARGET}/sources"
  
  echo "Running: git archive --prefix=${TAG}/ ${TAG} | tar -x -C ../sources/"
  git archive --prefix=${TAG}/ ${TAG} | tar -x -C ../sources/
  if [ $? -ne 0 ]; then
    echo ""
    echo "git export failed."
    echo ""
    exit 1
  fi
}

function download-optional-deps {
  echo
  echo ============================================================================
  echo " Downloading optional features"
  echo ============================================================================
  run mkdir -p "${PERFECTUS_TARGET}/offline"
  cd "${PERFECTUS_TARGET}/offline"
  run tar -xzf ../repo/org/apache/servicemix/apache-servicemix/${FEATURES_VERSION}/apache-servicemix-${FEATURES_VERSION}.tar.gz
  
  #
  # Generate pom
  #
  echo "<project><modelVersion>4.0.0</modelVersion><groupId>perfectus</groupId><artifactId>optional</artifactId><version>features</version><dependencies>" > pom.xml
  find . -name *features.xml -exec cat {} \; | \
  sed -e '/<bundle>/!d;/mvn:/!d;s/^.*<bundle>//g;s/<\/bundle>.*$//g;s/^wrap://g;s/war:\(.*\)?.*/\1/g;s/mvn://g;s/^.*!//g;s/\//\:/g' | \
  sort -u | \
  grep -v "com.microsoft.sqlserver:sqljdbc" | \
  grep -v "oracle:ojdbc5" | \
  grep -v "org.apache.ode.examples" | \
  grep -v "org.apache.woden:woden" | \
  sed -e 's/\(.*\)\:\(.*\)\:\(.*\)\:\(.*\)/<dependency><groupId>\1<\/groupId><artifactId>\2<\/artifactId><version>\3<\/version><type>\4<\/type><\/dependency>/g;s/\(.*\)\:\(.*\)\:\(.*\)/<dependency><groupId>\1<\/groupId><artifactId>\2<\/artifactId><version>\3<\/version><\/dependency>/g' \
  >> pom.xml
  echo "</dependencies></project>" >> pom.xml
  
  run ${MVN} -fae -T 4C org.apache.maven.plugins:maven-dependency-plugin:2.2:resolve -Dclassifier=sources
  run ${MVN} -fae -T 4C org.apache.maven.plugins:maven-dependency-plugin:2.2:resolve -Dclassifier=javadoc
}
