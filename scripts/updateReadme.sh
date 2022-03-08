#!/bin/bash

TEMPLATE=Readme.md.tpl
GROUPID=com.carlosedp
ARTIFACT=scalautils_2.13

REPO=$(echo ${GROUPID} | sed "s/\./\//g")/${ARTIFACT}

# Fetch latest release and snapshot versions
LASTRELEASE=$(curl -sL https://repo1.maven.org/maven2/${REPO}/maven-metadata.xml |grep latest |head -1 |sed -e 's/<[^>]*>//g' |tr -d " ")
LASTSNAPSHOT=$(curl -sL "https://s01.oss.sonatype.org/service/local/lucene/search?g=${GROUPID}&a=${ARTIFACT}" |grep "<latestSnapshot>"|head -1 |sed -e 's/<[^>]*>//g' |tr -d " ")

# Generate new readme
cat ${TEMPLATE} | sed s/"{{.LastRelease.Name}}"/"${LASTRELEASE}"/g | sed s/"{{.LastSnapshot}}"/"${LASTSNAPSHOT}"/g > "$(basename ${TEMPLATE} .tpl)"
