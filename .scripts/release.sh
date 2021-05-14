#!/bin/bash

set -e

if [ -z "$1" ]; then
  echo "release version must be provided" && exit 1
fi

VERSION=$1

git checkout -b release/${VERSION}

cd ..

# set release version
npm --prefix server-app/ version ${VERSION} --force
mvn versions:set -DnewVersion=${VERSION} versions:commit

# build
npm run-script --prefix server-app/ build
mvn clean install

docker build -t ssg -f Dockerfile cli/target

# tag and push Docker image
docker tag ssg gclaussn/ssg:${VERSION}
docker tag ssg gclaussn/ssg:latest

docker push gclaussn/ssg:${VERSION}
docker push gclaussn/ssg:latest

# deploy Github packages
mvn -am -pl impl clean deploy

git add .
git commit -m "Set version to ${VERSION}"
git push origin release/${VERSION}

# tag and push
git tag ${VERSION}
git push origin ${VERSION}
