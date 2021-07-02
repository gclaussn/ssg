---
data:
  title: Installation
---

SSG requires at minimum a Java 14 runtime - see [OpenJDK archive](https://jdk.java.net/archive/).

# Manual installation

1. Ensure that Java 14 is installed (`java -version`)
2. [Download](https://github.com/gclaussn/ssg/releases/latest/download/ssg.zip) latest release
3. Unpackage ZIP file
4. Add `SSG_HOME` environment variable, and point it to your SSG installation
5. Extend `PATH` variable

Windows:

`set PATH=%PATH%;%SSG_HOME%\bin`

Unix:

`export PATH=${PATH}:${SSG_HOME}/bin`

6. To verify the installation run:

    `ssg --help`

7. Initialize default site:

    `ssg init`

8. Start development server:

    `ssg server`

9. Open [application](http://localhost:8080/app) in browser

# Docker
SSG is available as pre-built Docker image on [Docker Hub](https://hub.docker.com/r/gclaussn/ssg). Simply run:

``` bash
docker pull gclaussn/ssg
docker run --rm -v $(pwd):/site gclaussn/ssg init
docker run --rm -v $(pwd):/site -p 8080:8080 gclaussn/ssg server
```

Prepand `MSYS_NO_PATHCONV=1` for Git Bash (MinGW)

The container's working directory is `/site`.