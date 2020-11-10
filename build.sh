#!/bin/bash

set -e

if [ ! -z $1 ]; then
  npm --prefix server-app/ version $1
  mvn versions:set -DnewVersion=$1 versions:commit
fi

npm run-script --prefix server-app/ build

mvn clean install

docker build -t gclaussn/ssg -f Dockerfile cli/target

if [ ! -z $1 ]; then
  docker tag gclaussn/ssg gclaussn/ssg:$1
fi
