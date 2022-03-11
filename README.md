# Search [![GitHub license](https://img.shields.io/github/license/wwhysohard/search)](https://github.com/wwhysohard/search/blob/master/LICENSE) 
> [_Sample library usage_](https://github.com/wwhysohard/sample-search-usage). 

## Table of Contents
* [General Info](#general-information)
* [Technologies Used](#technologies-used)
* [Features](#features)
* [Setup](#setup)
* [Usage](#usage)
* [Project Status](#project-status)
* [Acknowledgements](#acknowledgements)


## General Information
This library is intended to help you create JPA Specifications faster. Basic applications are shown in **Usage** section.


## Technologies Used
- Java - version 1.8
- Spring Boot - version 2.6.4


## Features
- Very flexible utility classes capable of being used in building JPA Criteria Predicates and Orders
- Limiting the permissibility of fields for filtering and sorting


## Setup
To use this library, add the following dependency in your `pom.xml` :

```
<dependency>
    <groupId>io.github.wwhysohard</groupId>
    <artifactId>search</artifactId>
    <version>1.0.0</version>
</dependency>
```


## Usage
`GenericCriteriaPredicate` lets you construct JPA Criteria Predicate by provided filters:

```
Predicate predicate = GenericCriteriaPredicate.get(root, criteriaBuilder, joins, filters, QueryOperator.AND, genericType);
```

Predicates on given filters will be combined with `AND` operation. `genericType` here is same as `root` generic type. 

`GenericCriteriaOrder` collects list of JPA Criteria Orders on which sorting can be applied:

```
List<Order> orders = GenericCriteriaOrder.get(root, criteriaBuilder, joins, sorts, genericType);
```

Filters and sorts can be almost any kind, as long as the field to be filtered is marked as `@Filterable` and, if it is a JPA related field, the `joinable` in the annotation must to be set to `true`.

Complete code can be found [_here_](https://github.com/wwhysohard/sample-search-usage).

Examples:

```
{
    "filters": [
        {
            "field": "id",
            "operator": "EQUALS",
            "value": 1
        }
    ]
}
```

```
{
    "filters": [
        {
            "field": "id",
            "operator": "IN",
            "values": [ 1, 2, 3, 4, 5 ]
        },
        {
            "field": "name",
            "operator": "LIKE",
            "value": "ger"
        }
    ],
    "sorts": [
        {
            "field": "id",
            "order": "DESC"
        }
    ]
}
```

```
{
    "filters": [
        {
            "field": "author.name",
            "operator": "EQUALS",
            "value": "J. K. Rowling"
        }
    ],
    "sorts": [
        {
            "field": "id",
            "order": "ASC"
        },
        {
            "field": "author.id",
            "order": "DESC"
        }
    ]
}
```


## Project Status
Project is: _in progress_. 

Contributions are widely welcomed.


## Acknowledgements
- Many thanks to Askar Zhakenov for his help with designing the concept
