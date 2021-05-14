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
