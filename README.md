# Static Site Generator (SSG)
SSG is a flexible and extensible static site generator, that processes user-defined [YAML](https://yaml.org/) models, [Markdown](https://spec.commonmark.org/0.28/) files with YAML front matter and [JADE](https://jade-lang.com/) templates to generate HTML pages.

Implementation based on / made possible by:
- [OpenJDK 14](https://openjdk.java.net/projects/jdk/14/)
- [SnakeYAML](https://bitbucket.org/asomov/snakeyaml) via [Jackson](https://github.com/FasterXML/jackson-dataformats-text/tree/master/yaml)
- [jade4j](https://github.com/neuland/jade4j)
- [flexmark-java](https://github.com/vsch/flexmark-java)
- [JCommander](https://jcommander.org/)
- [Undertow](https://github.com/undertow-io/undertow)

and many more!

## Features
- Separation of template code and data
- **Page sets** to utilize one template for multiple pages, each providing specific data
- Reusable data via **page includes**
- **Data selectors** to include dynamic data
- Reusable template code via JADE `include` and `mixin`
- Template inheritance via JADE `extends` and `block`
- CLI for automation
- Integrated development server with file watcher and hot reload
- Plugin mechanism to provide additional:
  - Page data selector, filter and/or processor implementations
  - Generator extensions (custom functions that be used within JADE templates)
  - Plugin actions to execute custom tasks

## Documentation
A documentation can be found [here](https://gclaussn.github.io/ssg/).

## Quickstart
1. Ensure that Java 14 is installed (`java -version`)
2. [Download](https://github.com/gclaussn/ssg/releases/latest/download/ssg.zip) latest release
3. Unpackage ZIP file
4. Add `SSG_HOME` environment variable, and point it to your SSG installation
5. Extend `PATH` variable

Windows:

    set PATH=%PATH%;%SSG_HOME%\bin

Unix:

    export PATH=${PATH}:${SSG_HOME}/bin

6. To verify the installation run:

    `ssg --help`

7. Initialize default site:

    `ssg init`

8. Start development server:

    `ssg server`

9. Open [application](http://localhost:8080/app) in browser

## Docker
SSG is available as pre-built Docker image on [Docker Hub](https://hub.docker.com/r/gclaussn/ssg). Simply run:

    docker pull gclaussn/ssg
    docker run --rm -v $(pwd):/site gclaussn/ssg init
    docker run --rm -v $(pwd):/site -p 8080:8080 gclaussn/ssg server

Prepand `MSYS_NO_PATHCONV=1` for Git Bash (MinGW)

The container's working directory is `/site`.

## License
The source files in this repository are made available under the [Apache License Version 2.0](./LICENSE).
