---
data:
  title: Site Structure
---

When building a site using SSG, the source code of the site must follow a given structure. A site consists of:

**site.yaml**

[Site model](models/site) file that lists [Pages](models/page) and [Page Sets](models/page-set) to load. 
<br />
<br />

**src/**

Source directory, which contains **Yaml** models, **Markdown** files and **Jade** templates that are used to define [Pages](models/page), [Page Sets](models/page-set) and [Page Includes](models/page-include) - the elements of a site.
<br />
<br />

**pub/**

Public directory, which provides public assets like scripts, style sheets and images.
<br />
<br />

**node_modules/**

Optional directory, which provides Node packages installed via `ssg install` when specified in `site.yaml` or via [npm](https://www.npmjs.com/) when specified in a `package.json`.
<br />
<br />

**out/**

Output directory, used by the static site generator as target directory for the HTML output.
<br />
<br />

**ext/**

Optional directory, used to register site specific extensions (e.g. plugins) in form of JAR files.
<br />
<br />

!!! note
    A version control system should ignore following directories: **node_modules/**, **out/** and **ext/**
