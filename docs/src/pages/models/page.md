---
data:
  title: Page
---

A page is defined by a **Jade** template that is for its generation.
An optional model can be defined using a **Yaml** or a **Markdown** file with a **Yaml** front matter.

All keywords are optional.

| Keyword                          | Description                                         |
|----------------------------------|-----------------------------------------------------|
| [data](#data)                    | Page specific data, accessible within the template  |
| [dataSelectors](#data-selectors) | List of data selectors to execute during generation |
| [includes](#includes)            | List of page includes, the page relies on           |
| [out](#out)                      | Name of the generated HTML output file              |
| [skip](#skip)                    | Determines if the page is skipped during generation |

# Keywords

## Data
Use `data` to separate data from the template code.

Example:

``` yaml
```

## Data Selectors
Use `dataSelectors`

!!! example

``` yaml
dataSelectors:
  types:
    class: PageSetAggregator
    model:
      pageSetId: events
      distinct: type
```

## Includes
Use `includes` to list IDs of page includes, a page relies on.
A page include provides additional data and/or a **Jade** template that can be used via `include` or `extends`.

!!! example "common.yaml"

``` yaml
data:
  contact:
    emailAddress: info@example.org
```

The data of an page include is accessed using its ID - in this case `common` e.g. `span= common.contact.emailAddress`
If the page include is located in a sub directory or the filename contains a hyphen, the page include ID is normalized.

!!! example "common.jade"

``` pug
mixin label(text)
  label= text
```

The template of an page include can be included using its ID followed by the file extension `.jade` - in this case `include common.jade`.
A template can provide a HTML snippet or **Jade** mixins that can be invoked like functions.

## Out
Use `out` to specifiy the name of the generated HTML file.
Default value: `<pageId>.html`

## Skip
Use the `skip` boolean flag to exclude a page from generation.
Default value: `false`
