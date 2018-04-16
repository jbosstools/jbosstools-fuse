
#!/bin/bash

#########################################
# this script can be used to change the #
# Eclipse version                       #
#                                       #
# Initial version from lhein, 2018      #
#########################################

# check parameter count and provide help if count doesn't match
if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters"
    echo "Please specify the old and the new Eclipse name like this:"
    echo "      changeVersion.sh <oldEclipseName> <newEclipseName>"
    echo "Example:"
    echo "      changeVersion.sh photon photon"
    exit 0
fi

# uppercase the versions
CMD_1=$1
CMD_2=$2
OLD_ECLIPSE=${CMD_1,,}
NEW_ECLIPSE=${CMD_2,,}

echo "Using the following settings:"
echo "  Old Eclipse Name:       $OLD_ECLIPSE"
echo "  New Eclipse Name:       $NEW_ECLIPSE"
echo "Replacing now..."

###################
## REPLACE LOGIC ##
###################

# first replace all lowercase words like in URLs or identifiers
find * -name '*.xml' | xargs perl -pi -e "s/$OLD_ECLIPSE/$NEW_ECLIPSE/g"
find * -name '*.target' | xargs perl -pi -e "s/$OLD_ECLIPSE/$NEW_ECLIPSE/g"
find * -name '*.md' | xargs perl -pi -e "s/$OLD_ECLIPSE/$NEW_ECLIPSE/g"

# now replace all other occurrences with first letter capitalized
find * -name '*.xml' | xargs perl -pi -e "s/${OLD_ECLIPSE^}/${NEW_ECLIPSE^}/g"
find * -name '*.target' | xargs perl -pi -e "s/${OLD_ECLIPSE^}/${NEW_ECLIPSE^}/g"
find * -name '*.md' | xargs perl -pi -e "s/${OLD_ECLIPSE^}/${NEW_ECLIPSE^}/g"

echo "DONE!"

