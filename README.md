# Static Site Generator (SSG)
SSG is a flexible and extensible static site generator, that processes user-defined [YAML](https://yaml.org/) models and [JADE](https://jade-lang.com/) templates to generate HTML pages.

Implementation based on / made possible by:
- [OpenJDK 14](https://openjdk.java.net/projects/jdk/14/)
- [SnakeYAML](https://bitbucket.org/asomov/snakeyaml) via [Jackson](https://github.com/FasterXML/jackson-dataformats-text/tree/master/yaml)
- [jade4j](https://github.com/neuland/jade4j)
- [flexmark-java](https://github.com/vsch/flexmark-java)
- [JCommander](https://jcommander.org/)

and many more!

## Features
- Separation of template code and data
- **Page sets** to utilize one template for multiple pages, each providing specific data
- Reusable data via **page includes**
- **Data selectors** to include dynamic data sources
- Reusable template code via JADE `include` and `mixin`
- Template inheritance via JADE `extends` and `block`
- CLI for automation
- Integrated development server with file watcher and hot reload
- Plugin mechanism to provide additional:
  - Page data selector, filter and/or processor types
  - Generator extensions e.g. functions that be used within templates
  - Plugin goals to execute specific tasks

## Quickstart
1. Ensure that Java 14 is installed (`java -version`)
2. [Download](https://github.com/gclaussn/ssg/releases/latest/download/ssg.zip) latest release
3. Unpackage ZIP file
4. Add `SSG_HOME` environment variable, and point it to your SSG folder
5. Extend `PATH` variable

Windows:

    set PATH=%PATH%;%SSG_HOME%\bin

Unix:

    export PATH=${PATH}:${SSG_HOME}/bin

6. To verify the installation run:

    ssg --help

7. Initialize default site:

    ssg init

8. Start development server:

    ssg server

9. Open [application](http://localhost:8080/app) in browser

## Docker

    docker pull gclaussn/ssg
    docker run --rm -v $(pwd):/site gclaussn/ssg init
    docker run --rm -v $(pwd):/site -p 8080:8080 gclaussn/ssg server -h 0.0.0.0

Prepand `MSYS_NO_PATHCONV=1` for Git Bash (MinGW)

The container's working directory is `/site`.

## Documentation 
The [default site](./templates/default) is a good starting point.

### Site structure

    <site>
    ├── site.yaml     # Site model, that lists pages and page sets to load
    ├── src/          # YAML and JADE source files
    ├── public/       # Public assets like scripts, style sheets and images
    ├── node_modules/ # Node modules, used within the site
    ├── out/          # Target location for generated HTML output

### site.yaml
| Name                   | Type   | Description                                    |
|:-----------------------|:-------|:-----------------------------------------------|
| pages                  | List   | List of pages to load                          |
| pages.*                | String | ID of a page, within the src/ folder           |
| pageSets               | List   | List of page sets to load                      |
| pageSets.*             | String | ID of a page set, within the src/ folder       |
| nodeModules            | Dict   |                                                |
| nodeModuels.includes   | List   | List of resources to include from node_modules |
| nodeModuels.includes.* | String | Glob pattern                                   |

### Page
A page within a static site that can be generated.
A page must provide a JADE template.
The YAML file is optional.

YAML layout:
| Name                | Type    | Description                                            |
|:--------------------|:--------|:-------------------------------------------------------|
| data                | Dict    | Page specific data                                     |
| dataSelectors       | List    | List of data selectors to execute during generation    |
| dataSelectors.id    | String  | ID of the data selector (must be unique within a page) |
| dataSelectors.class | String  | Simple or fully qualified name of the Java class       |
| dataSelectors.model | String  | Data selector specific data                            |
| includes            | List    | List of page includes, the page relies on              |
| includes.*          | String  | ID of a page include, within the src/ folder           |
| out                 | String  | Name of the generated HTML output file                 |
| skip                | Boolean | Determines if the page is skipped during generation    |

### Page set
A page set, provides default data and a template for contained pages that are part of the page set.
There must be a directory with the same name as the page set, beside the YAML file.
The directory contains the pages of the page set.
A page set should provide a default JADE template for it's pages.
The YAML file is optional.

YAML layout:
| Name                | Type    | Description                                             |
|:--------------------|:--------|:--------------------------------------------------------|
| base                | Dict    | Custom base path for the page set's HTML output         |
| data                | Dict    | Default data, if a page provides no data                |
| dataSelectors       | List    | Default list of data selectors                          |
| dataSelectors.id    | String  | ID of the data selector (must be unique)                |
| dataSelectors.class | String  | Simple or fully qualified name of the Java class        |
| dataSelectors.model | String  | Data selector specific data                             |
| filters             | List    | List of page filters to execute during load             |
| filters.id          | String  | ID of the filter (must be unique)                       |
| filters.class       | String  | Simple or fully qualified name of the Java class        |
| filters.model       | String  | Page filter specific data                               |
| includes            | List    | Default list of page includes, the page set relies on   |
| includes.*          | String  | ID of a page include, within the src/ folder            |
| processors          | List    | List of page processors to execute during load          |
| processors.id       | String  | ID of the processor (must be unique)                    |
| processors.class    | String  | Simple or fully qualified name of the Java class        |
| processors.model    | String  | Page processor specific data                            |
| skip                | Boolean | Determines if the page set is skipped during generation |

### Page include
A reusable source, providing data and/or template code via JADE `include` or `extends` syntax.

YAML layout:
| Name                | Type    | Description                                       |
|:--------------------|:--------|:--------------------------------------------------|
| data                | Dict    | Page include specific data                        |
| includes            | List    | List of page includes, the page include relies on |
| includes.*          | String  | ID of a page include, within the src/ folder      |

### Node modules
SSG supports `node_modules` folder, installed by [npm](https://www.npmjs.com/) using a `package.json`.
Resources can be referenced via `/node_modules/` - for example:

    link(rel="stylesheet", href="/node_modules/@fortawesome/fontawesome-free/css/all.min.css")

in `site.yaml` includes can be specified based on the glob pattern syntax:

    nodeModules:
      includes:
      - "**/@fortawesome/fontawesome-free/css/all.min.css"
      - "**/@fortawesome/fontawesome-free/webfonts/*"

### Generate and output site

    ssg generate
    ssg cp /target

Use `-s` or `--site-path`, if the site is in a different directory

## License
The source files in this repository are made available under the [Apache License Version 2.0](./LICENSE).
