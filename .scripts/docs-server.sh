#!/bin/bash

cd "$(dirname "$0")/../docs"

MSYS_NO_PATHCONV=1 docker run \
--rm \
-v $(pwd):/site \
-p 8080:8080 \
-m 512MB \
ssg \
server
