# Dynamic Relations

[![Java CI with Maven](https://github.com/Mom0aut/DynamicRelations/actions/workflows/maven.yml/badge.svg)](https://github.com/Mom0aut/DynamicRelations/actions/workflows/maven.yml) [![Coverage](.github/badges/jacoco.svg)](https://github.com/Mom0aut/DynamicRelations/actions/workflows/maven.yml)


In every relational Database you must always know which relations are possible to your Entities. But sometimes these Relations are unknown or could change anytime.  With Dynamic Relations you can add or delete Custom Relations between Entities during runtime.

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

A Person can have a Dog and both Entites could have Documents(Person Info Documents and Dog Info Documents). Now you
could add Dynamic Relations to all Enties which could look like this:

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

In this Scenario a Person have a Dog and both got Documents, now you could change the Relation during runtime (No
altering of your Entities or Models). For example you could delete a Person_Document(got lost):

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
  <version>1.0.3</version>
</dependency>
```

# How to use

- [Add the @Relation to your Entity](#Relation)
- [Implement RelationIdentity](#RelationIdentity)
- [Import Config Module for Component Scan](#ImportConfig)
- [Use the RelationService](#RelationService)

## <a name="Relation"></a> Add the @Relation

Simply Add the @Relation to your existing Entity and the necessary Dynamic Relations Entity will be generated. Dynamic
Relations are only working with Classed wich are **annotated with @Entity**!

```java
@Relation(sourceClass = Person.class)
@Entity
@Getter
@Setter
public class Person implements RelationIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
    public String getType() {
        return "PersonType";
    }
}

```

## <a name="RelationIdentity"></a> Implement RelationIdentity

Implement the RelationIdentity, each Dynamic Relation need an Long id and an String Type which you can define.

```java
@Relation(sourceClass = Person.class)
@Entity
@Getter
@Setter
public class Person implements RelationIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
    public String getType() {
        return "PersonType";
    }
}

```

## <a name="ImportConfig"></a> Import Config Module for Component Scan

Import the DrmConfig in your Spring Boot Application, so that you can use the RelationService

```java
@SpringBootApplication
@Import(DrmConfig.class)
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```

## <a name="RelationService"></a> Use the RelationService

### Create Relation

```java
@Autowired
private RelationService relationService;
   
void createRelation() {

    Person person = new person();
    personDao.save(person);

    Dog dog = new Dog();
    dogDao.save(dog);

    //Dynamic Relation can only be created with persisted Entites!
    RelationLink relationLinkPersonToDog = relationService.createRelation(person, dog);
}

```

**Dynamic Relation can only be created with persisted Entites!**

### Delete Relation

```java
@Autowired
private RelationService relationService;

void deleteRelation() {
    relationService.deleteRelation(relationToBeDeleted);
}

```

### Find Relations

```java
@Autowired
private RelationService relationService;

void findRelations() {
    
    Person person = new person();
    personDao.save(person);

    Dog dog = new Dog();
    dogDao.save(dog);

    Document document = new Document();
    documentDaio.save(document)

    //Dynamic Relation can only be created with persisted Entites!
    RelationLink relationLinkPersonToDog = relationService.createRelation(person, dog);
    RelationLink relationLinkPersonToDocument = relationService.createRelation(person, document);
    RelationLink relationLinkDogToDocument = relationService.createRelation(dog, document);

    //Return 1 Relation person -> dog
    RelationLink foundRelation = relationService.findRelationBySourceObjectAndRelationIdentity(person, dog);
    //Returns 2 Relations person -> dog and person -> document
    List<RelationLink> relationBySourcePerson = relationService.findRelationBySourceObject(person);
    //Returns 2 Relations from person -> document and dog -> document
    Set<RelationLink> relationByTargetDocument = relationService.findRelationByTargetRelationIdentity(document);
}

```

### Get the SourceObject by Relation

```java
@Autowired
private RelationService relationService;

void getSourceObject() {
    RelationLink foundRelation = relationService.findRelationBySourceObjectAndRelationIdentity(person, dog);
    //Can be casted to Person because we know it is from Person.class
    Person sourceObject = (Person)foundRelation.getSourceObject();
}

```

# Limitations

- Java EE with Spring
- Sql Database (tested with Postgres)

# Contribution

Every Contribution is welcome, please follow
the [Contribution Guidlines](https://github.com/Mom0aut/DynamicRelations/blob/master/Contributing.md)

# Code of Condcut

See our [Code of Conduct](https://github.com/Mom0aut/DynamicRelations/blob/master/CODE_OF_CONDUCT.md)
