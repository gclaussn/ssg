---
data:
  title: Site
---

A site is defined by a YAML file, using a list of keywords.

| Keyword                | Description                         |
|------------------------|-------------------------------------|
| [pages](#pages)        | List of pages to load               |
| [pageSets](#page-sets) | List of page sets to load           |
| [node](#node)          | Optional Node package specification |

# Keywords
The following section explains how to define a site.

## Pages
Use `pages` to list IDs of pages that should be loaded.
A page ID is the relative path from the **src/** directory to a [page](pages/models/page) definition (the page's template and/or model files).

!!! example

``` yaml
pages:
  - index
  - about/me
  - legal-information
```

## Page Sets
Use `pageSets` to list IDs of page sets that should be loaded.
A page set ID is the relative path from the **src/** directory to a [page set](pages/models/page-set) definition.

!!! example

``` yaml
pageSets:
  - posts
```

## Node
Use `node` to configure Node packages to download from the default [NPM](https://www.npmjs.com/) registry ([https://registry.npmjs.org](https://registry.npmjs.org)).

!!! example "Node package definition"

``` yaml
node:
  packages:
    jquery: 3.6.0
```

When a static site does not need all files of a Node package, an optional list of includes can be defined.
An include can be a relative path from the **node_modules/** directory to a file or a pattern, using the [glob syntax](https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob) to match one or multiple files.

!!! example "Node package file includes"

``` yaml
node:
  includes:
    - prismjs/prism.js
    - prismjs/components/prism-yaml.min.js
    - prismjs/themes/*.css
```
