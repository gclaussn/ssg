#!/bin/bash

set -e

VERSION=$1

if [ ! -z ${VERSION} ]; then
  npm --prefix server-app/ version ${VERSION}
  mvn versions:set -DnewVersion=${VERSION} versions:commit
fi

npm run-script --prefix server-app/ build

mvn clean install

docker build -t ssg -f Dockerfile cli/target

if [ ! -z ${VERSION} ]; then
  docker tag ssg gclaussn/ssg:${VERSION}
  docker tag gclaussn/ssg:${VERSION} gclaussn/ssg:latest
fi
