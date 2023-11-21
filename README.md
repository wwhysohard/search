# Search [![GitHub license](https://img.shields.io/github/license/wwhysohard/search)](https://github.com/wwhysohard/search/blob/master/LICENSE) [![Maven Central](https://img.shields.io/maven-central/v/io.github.wwhysohard/search.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.wwhysohard%22%20AND%20a:%22search%22)
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
To use this library, add the following dependency to your `pom.xml` :

``` xml
<dependency>
    <groupId>io.github.wwhysohard</groupId>
    <artifactId>search</artifactId>
    <version>1.2.0</version>
</dependency>
```

Or, if you are using Gradle, `build.gradle` :

``` groovy
implementation 'io.github.wwhysohard:search:1.2.0'
```


## Usage
`GenericSpecification` is a base class for your JPA Specifications. All you need is `extend` your JPA Specification class from `GenericSpecification` :

``` java
public class AuthorSpecification extends GenericSpecification<Author> {

    public AuthorSpecification(SearchRequest request) {
        super(request, Author.class, true); // third argument specifies whether you want to fetch distinct records
    }
    
}
```

If you want to apply filtering and sorting on JPA related fields, joins with those fields have to be added into `joins` Map. It can be done by overriding `initializeJoins` method:

``` java
@Override
protected void initializeJoins(Root<Author> root) {
    Join<Author, Book> books = root.join("books", JoinType.LEFT);
    joins.put("books", books);
}
```

Note that join name must match field name or one of names defined in `@Filterable` annotation `names`.

Rights-based access restriction can be done by overriding `processAccess` method.

`GenericCriteriaPredicate` lets you construct JPA Criteria Predicate by provided filters:

``` java
Predicate predicate = GenericCriteriaPredicate.get(root, criteriaBuilder, joins, filters, QueryOperator.AND, genericType);
```

Predicates on given filters will be combined with `AND` operation. `genericType` here is same as `root` generic type. 

`GenericCriteriaOrder` collects list of JPA Criteria Orders on which sorting can be applied:

``` java
List<Order> orders = GenericCriteriaOrder.get(root, criteriaBuilder, joins, sorts, genericType);
```

Complete code can be found [_here_](https://github.com/wwhysohard/sample-search-usage).

Filters and sorts can be almost any kind, as long as the field to be filtered is marked as `@Filterable` and, if it is a JPA related field, the `joinable` in the annotation is set to `true`.

Examples:

``` json
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

``` json
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
            "value": "Harry"
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

``` json
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
