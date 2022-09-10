#!/bin/bash

GROUPID=com.carlosedp
ARTIFACT=riscvassembler_2.13

REPO=$(echo ${GROUPID} | sed "s/\./\//g")/${ARTIFACT}

# Fetch latest release and snapshot versions
LASTRELEASE=$(curl -sL https://repo1.maven.org/maven2/"${REPO}"/maven-metadata.xml |grep latest |head -1 |sed -e 's/<[^>]*>//g' |tr -d " ")
LASTSNAPSHOT=$(curl -sL "https://s01.oss.sonatype.org/service/local/lucene/search?g=${GROUPID}&a=${ARTIFACT}" |grep "<latestSnapshot>"|head -1 |sed -e 's/<[^>]*>//g' |tr -d " ")

# Update Readme
## Release
sed -i "s/ivy.*ReleaseVerMill/ivy\"com.carlosedp::riscvassembler:${LASTRELEASE}\"  \/\/ReleaseVerMill/" Readme.md
sed -i "s/libraryDependencies.*ReleaseVerSBT/libraryDependencies += \"com.carlosedp\" %% \"riscvassembler\" % \"${LASTRELEASE}\"  \/\/ReleaseVerSBT/" Readme.md

## Snapshots
sed -i "s/ivy.*SnapshotVerMill/ivy\"com.carlosedp::riscvassembler:${LASTSNAPSHOT}\"  \/\/SnapshotVerMill/" Readme.md
sed -i "s/libraryDependencies.*SnapshotVerSBT/libraryDependencies += \"com.carlosedp\" %% \"riscvassembler\" % \"${LASTSNAPSHOT}\"  \/\/SnapshotVerSBT/" Readme.md
