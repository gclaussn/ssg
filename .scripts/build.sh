#!/bin/bash

set -e

cd "$(dirname "$0")/.."

npm run-script --prefix server-app/ build
mvn clean install

docker build -t ssg -f Dockerfile cli/target
