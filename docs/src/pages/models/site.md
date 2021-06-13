---
data:
  title: Site
---

A site is defined through a YAML file, using a list of keywords.

| Keyword                  | Description                         |
|--------------------------|-------------------------------------|
| [`pages`](#pages)        | List of pages to load               |
| [`pageSets`](#page-sets) | List of page sets to load           |
| [`node`](#node)          | Optional Node package specification |

# Keywords
The following section explains how to define a site.

## Pages
...

``` yaml
pages:
  - index
  - page-a
  - page-b
```

## Page Sets
...

``` yaml
pageSets:
  - posts
  - events
```

## Node
Use `node` to configure Node packages that are downloaded from the default [NPM](https://www.npmjs.com/) registry.

``` yaml
node:
  packages:
    jquery: 3.6.0
```

...

``` yaml
node:
  includes:
    - prismjs/prism.js
    - prismjs/components/prism-yaml.min.js
```
