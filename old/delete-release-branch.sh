#!/bin/bash
export PERFECUT_ARTIFACT=ide

. `dirname $0 2> /dev/null`/common.sh

ACTION=$1
cd "${basedir}"


BUILDNO=`cat ${PERFECUT_ARTIFACT}-buildno.txt`
VERSION=1.1.${BUILDNO}

echo "Deleting release branch for ${VERSION}"

read -p "Are you sure you want to continue? <y/N> " prompt
if [[ $prompt == "y" || $prompt == "Y" || $prompt == "yes" || $prompt == "Yes" ]]
then
  git stash
  git checkout master
  git branch -D release-${VERSION}
  echo "Branch release-${VERSION} deleted"
else
  exit 0
fi






