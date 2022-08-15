# Dynamic Relations

[![Java CI with Maven](https://github.com/Mom0aut/DynamicRelations/actions/workflows/maven.yml/badge.svg)](https://github.com/Mom0aut/DynamicRelations/actions/workflows/maven.yml) [![Coverage](.github/badges/jacoco.svg)](https://github.com/Mom0aut/DynamicRelations/actions/workflows/maven.yml)


Often Relations can be dynamic and difficult to predict and implement. With Dynamic Relations you can add or delete Custom Relations between Entities during runtime. The Target of the Dynamic Relation can be complete dynamic.

# What is a Dynamic Relation?

A Dynamic Relation can be viewed as a directed Graph with a fixed Input (SourceObject) and a dynmic Output (Target).

```mermaid
flowchart LR
    subgraph DynamicRelation
    direction LR
    SourceObject-->Target
    end
```

For Example with following Entities:

- Person
- Dog
- Document

A Person can have a Dog and both Entites could have Documents(Person Info Documents and Dog Info Documents). Now you could add Dynamic Relations to all Enties which could look like this: 

```mermaid
graph TD;
    Person-->Dog;
    Person-->Person_Document
    Dog-->Dog_Document;
```

Each Connection is a Dynamic Relation and following Relations would be generated:

- Person Relation with SourceObject Person
- Person_Document Relation with SourceObject Person_Document
- Dog Relation with SourceObject Dog
- Dog_Document Relation with SourceObject Dog_Document

Each Relation got an Dynamic Target, that means you could create an Relation to any other Relation.

In this Scenario a Person have a Dog and both got Documents, now you could change the Relation during runtime (No altering of your Entities or Models). For example you could delete a Person_Document(got lost):

```mermaid
graph TD;
    Person-->Dog;
    Dog-->Dog_Document;
```



# Maven Dependency

```
<dependency>
  <groupId>io.github.Mom0aut</groupId>
  <artifactId>dynamic-relations</artifactId>
  <version>1.0.2</version>
</dependency>
```

# How to use
Simply Add the @Relation to your existing Entity and necessary Dynamic Relations Entity will be generated. 




# Contribution

Every Contribution is welcome, pls follow the [Contribution Guidlines](https://github.com/Mom0aut/DynamicRelations/blob/master/Contributing.md)
