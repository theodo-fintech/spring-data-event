[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]

<div align="center">
  <h1 align="center">Spring Data Event</h1>
</div>

### Features

- **Data Event Entity**: Set up your JPA Entities to be automatically sent over Kafka topics when saved, updated or deleted simply using `@DataEventEntity`

More to come later... Stay tuned ! 


## Getting Started

### Prerequisites

This library has been currently tested on projects under SpringBoot on version 3.2.4 with Java 17 or later, using Hibernate as a JPA implementation.


### Installation

You will have to add the dependency in your spring-boot-project

```xml
<dependency>
    <groupId>com.sipios</groupId>
    <artifactId>spring-data-event</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Configuration

Set up your application properties file to make spring kafka work properly

```txt
spring.kafka.bootstrap-servers=localhost:29092
```

Then, you will have to enable the library so that it will be able to work properly.
You just add the `@EnableDataEvent` on any of your `@Configuration` class already existing on your project, or directly on the `@SpringBootApplication` class.

## Usage


To mark a JPA entity to be automatically sent over event platform, put the `@DataEventEntity` on your entity

For instance

```java

@Entity
@DataEventEntity
@Table(name= "user_account")
public class UserEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


}
```

By default, the topics on which the event will be sent are : 
- For the creation : `entity_name.created` (for example : `userentity.created`)
- For the update : `entity_name.updated` (for example : `userentity.updated`)
- For the deletion : `entity_name.deleted` (for example : `userentity.deleted`)


## FAQs

No FAQ at the moment

## Roadmap

- [x] Add simple case of sending creation, update and deletion event over kafka
- [ ] Allow customizing which events should be sent or not 
- [ ] Allow customizing which attribute from the entity to be sent or not 
- [ ] Allow other event techno as RabbitMQ or Apache Pulsar

## Contributing

We are just getting started on this project and would **highly appreciate** contributions

## License

Distributed under the MIT License. See [LICENSE](/LICENSE.txt) for more information.


[stars-shield]: https://img.shields.io/github/stars/sipios/spring-data-event?style=for-the-badge
[stars-url]: https://github.com/sipios/spring-data-event/stargazers
[issues-shield]: https://img.shields.io/github/issues/sipios/spring-data-event?style=for-the-badge
[issues-url]: https://github.com/sipios/spring-data-event/issues
[license-url]: https://github.com/sipios/spring-data-event/blob/main/LICENSE
