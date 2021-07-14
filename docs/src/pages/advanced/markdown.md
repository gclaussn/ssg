---
data:
  title: Markdown support
---

SSG's Markdown support is built on [flexmark-java](https://github.com/vsch/flexmark-java) - a [CommonMark](https://spec.commonmark.org/0.28/) implementation that provides Markdown parsing and rendering, which can be customized using extensions.

Currently used extensions:
- [flexmark-ext-admonition](https://github.com/vsch/flexmark-java/tree/master/flexmark-ext-admonition)
- [flexmark-ext-tables](https://github.com/vsch/flexmark-java/tree/master/flexmark-ext-tables)

# Yaml Front Matter

A [page](pages/models/page)

!!! example

``` markdown
```

!!! example

``` pug
!{_.renderMarkdown(_md)}
```

# Jade Filter

`:md` or `:markdown`

!!! example "Jade Markdown filter"

``` pug
:md
  # Heading

  **This is bold text**

  __This is bold text__

  *This is italic text*
```

# Page Links

!!! example

``` markdown
[My Page](my-page)
```

# Admonitions

Admonition support is provided by a Flexmark-Java extension called [flexmark-ext-admonition](https://github.com/vsch/flexmark-java/tree/master/flexmark-ext-admonition).

``` markdown
!!! note
    a note...
```

## Qualifiers

Following qualifiers are possible:

!!! abstract
    Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua.

!!! bug
    Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua.

!!! danger
    Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua.

!!! example
    Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua.

!!! fail
    Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua.

!!! faq
    Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua.

!!! note
    Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua.

!!! quote
    Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua.

!!! success
    Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua.

!!! tip
    Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua.

!!! warning
    Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua.

## Styling

Admonitions are styled using following CSS classes:

``` css
.adm-hidden {
  display: none;
}
.adm-block {}
.adm-icon {}
.adm-heading {}
.adm-heading span {}
.adm-body {}
.adm-body p {}
```

Each qualifier has to be styled separately.

``` css
.adm-<qualifier> {
  border-left: 0.2rem solid #00b0ff;
}
.adm-<qualifier> .adm-heading {
  background-color: #e5f7ff;
}
.adm-<qualifier> .adm-icon {
  color: #00b0ff;
}
```
