#!/bin/bash

if [ ! -z $1 ]; then
  npm version $1
  mvn versions:set -DnewVersion=$1 versions:commit
fi

npm run --prefix server-app/ install
npm run --prefix server-app/ build

mvn clean package

docker build -t gclaussn/ssg -f Dockerfile cli/target

if [ ! -z $1 ]; then
  docker tag gclaussn/ssg gclaussn/ssg:$1
fi
