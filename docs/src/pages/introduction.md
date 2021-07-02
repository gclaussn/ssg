---
data:
  title: Introduction
---

[SSG](https://github.com/gclaussn/ssg) is a flexible and extensible static site generator, that processes user-defined [Yaml](https://yaml.org/) models, [Markdown](https://spec.commonmark.org/0.28/) files with Yaml front matter and [Jade](https://jade-lang.com/) templates to generate HTML pages.

SSG can be used to generate any HTML output and any kind of static site, since pages and their templates are freely definable.

# Features

- Separation of template code and data
- [Page sets](models/page-set) to utilize one template for multiple pages, each providing specific data
- Reusable data via [page includes](models/page-include)
- **Data selectors** to include dynamic data
- Reusable template code via **Jade** `include` and `mixin`
- Template inheritance via **Jade** `extends` and `block`
- [CLI](cli) for automation
- Integrated development server with file watcher and hot reload
- Plugin mechanism to provide additional:
  - Page data selector, filter and/or processor implementations
  - Generator extensions (custom functions that be used within **Jade** templates)
  - Plugin actions to execute custom tasks

# Comparision
In comparision to other static site generators, SSG does not provide any default layouts or styles.
All pages of a site are crafted and organized individually.
Beside SSG, no other tools need to be installed - no `npm` or `yarn` is required.

When developing a site, only declarative languages are used:
- Templates are written in [Jade](https://naltatis.github.io/jade-syntax-docs/)
- Models are written in [Yaml](https://yaml.org/) or in [Markdown](https://spec.commonmark.org/0.28/) with an optional Yaml front matter

Actual programming is not required, unless JavaScript is added to a page via `.script` or SSG is extended by developing a custom plugin.