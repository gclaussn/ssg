---
data:
  title: Admonitions
---

Admonition support is provided by a Flexmark-Java extension called [flexmark-ext-admonition](https://github.com/vsch/flexmark-java/tree/master/flexmark-ext-admonition).

# Usage

``` md
!!! note
    a note...
```

# Qualifiers

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

# Styling

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
