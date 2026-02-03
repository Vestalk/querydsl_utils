# QueryDSL Select Services

A reusable abstraction layer on top of **QueryDSL** that standardizes how to build **filtered, paginated, sorted, and dynamic select queries** for both **entities** and **projections (DTOs)**.

The module is designed to eliminate duplicated query logic, enforce safe field access, and provide a single extensible API for complex read operations.

---

## Key Features

* Pagination and sorting via Spring `Pageable`
* Dynamic field selection (partial selects)
* `distinct` field queries
* Entity-based and projection-based services
* Extensible QueryDSL customization (`join`, `groupBy`, `having`)

---

## Core Concepts

### Field Map (`FIELD_MAP`)

Each service defines a map of **allowed fields**:

```java
Map<String, FieldInfo>
```

* **Key** – external/public field name (used in API)
* **Value** – `FieldInfo` containing QueryDSL `Expression`

Used for:

* `select`
* `order by`
* tuple-to-map conversion
* field validation

This ensures **controlled and safe access** to entity fields.

---

## Entity Service: `AbstractEntitySelectService<T>`

### Purpose

Used for **direct entity selects**:

```sql
SELECT e FROM Entity e
```

### Supported Operations

* `List<T>` fetch
* `Page<T>` pagination
* partial selects (`Map<String,Object>`)
* distinct field values

### Typical Use Cases

* CRUD read operations
* simple filtering
* pagination without joins

### Example
- [TestEntitySelectService](src/test/java/my/utils/querydsl_utils/example/servise/TestEntitySelectService.java)
- [TestEntitySelectServiceTest](src/test/java/my/utils/querydsl_utils/example/servise/TestEntitySelectServiceTest.java)


**Capabilities:**

* Fetch full entities (`List<TestEntity>`)
* Paginated entity results (`Page<TestEntity>`)
* Dynamic field selection (`Map<String,Object>`)
* Distinct field queries

Typical usage:

```java
Page<TestEntity> page = service.getPageByFilters(filters, pageable);
```

---

## Projection Service: `AbstractProjectionSelectService<E, D>`

### Purpose

Used for **DTO / projection queries** with advanced customization.

Supports:

* joins
* group by / having
* calculated fields
* aggregated queries

### Key Extension Point

```java
protected abstract <M> JPAQuery<M> modifyQuery(JPAQuery<M> query);
```

This method is applied consistently to:

* `findAll`
* `getPage`
* `distinct` queries

### Example
- [TestEntityProjectionSelectService](src/test/java/my/utils/querydsl_utils/example/servise/TestEntityProjectionSelectService.java)
- [TestEntityProjectionSelectServiceTest](src/test/java/my/utils/querydsl_utils/example/servise/TestEntityProjectionSelectServiceTest.java)

**Capabilities:**

* DTO / projection results (`TestEntityDto`)
* Joins and complex query logic
* Paginated projection queries
* Partial selects with joins
* Distinct field values across joins

Typical usage:

```java
Page<TestEntityDto> page = service.getPageByFilters(filters, pageable);
```

---

### Service Identification (`masterType`)

Each service must define a unique identifier:

```java
@Override
public String getMasterType() {
    return "ORDER";
}
```

This allows:

* service auto-registration
* lookup via `Map<String, AbstractSelectService>`
* validation of duplicate services at startup

---

## CommonService

### Purpose

`CommonFieldService` is a **facade / registry service** that provides a unified access point to all `AbstractSelectService` implementations.

It resolves services by `masterType` and exposes common read-oriented operations that are **entity-agnostic**.
Each service **must define a unique `masterType`**.

Duplicate `masterType` values will cause application startup to fail:

```text
IllegalStateException: Duplicate Master Type: test-entity (ServiceA ServiceB)
```

- [CommonFieldService](src/main/java/my/utils/querydsl_utils/servise/CommonFieldService.java)
- [CommonFieldServiceTest](src/test/java/my/utils/querydsl_utils/example/servise/CommonFieldServiceTest.java)

Typical use cases:

* building dynamic UI filters
* retrieving available fields and metadata
* fetching distinct field values across different domains

---

### Field Metadata API

```java
List<FieldInfoDto> getFieldInfoDto(String masterType)
```

Returns metadata for all supported fields of a given service:

```json
[
  {
    "name": "id",
    "label": "ID",
    "type": "NUMERIC"
  },
  {
    "name": "name",
    "label": "Name",
    "type": "STRING"
  }
]
```

This is typically used to:

* build dynamic filter UIs
* validate client-side field names
* display human-readable labels

---

### Distinct Field Values API

```java
List<?> findDistinctFieldValuesByFilterGroups(
        String masterType,
        String field,
        List<FilterGroup> filterGroups
)
```

Resolves the correct `AbstractSelectService` by `masterType` and executes:

```sql
SELECT DISTINCT field FROM ... WHERE ...
```

Features:

* supports filters (`FilterGroup`)
* works for both entity and projection services
* validates field existence via `FIELD_MAP`

---

### Error Handling

If an unsupported `masterType` is requested, the service fails fast:

```text
RuntimeException: Unsupported MasterType: unknown-type
```

This guarantees:

* no silent fallbacks
* predictable API behavior

---

### Typical Usage

```java
List<FieldInfoDto> fields = commonFieldService.getFieldInfoDto("test-entity");

List<?> values = commonFieldService.findDistinctFieldValuesByFilterGroups(
        "test-entity",
        "name",
        filters
);
```

---

## Benefits

* No duplicated QueryDSL code
* Centralized pagination and sorting logic
* Safe and controlled dynamic selects
* Easy extensibility
* Clean separation between entity and projection queries

---

## Other
Default properties
```yaml
spring:
  mvc:
    format:
      time: HH:mm:ss
      date: dd/MM/yyyy
      date-time: dd/MM/yyyy HH:mm:ss
```
---

## Example pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.5</version>
        <relativePath/>
    </parent>

    <groupId>my.utils</groupId>
    <artifactId>table-api-core</artifactId>
    <version>0.0.1</version>
    <name>table-api-core</name>

    <properties>
        <java.version>21</java.version>
        <querydsl.version>5.1.0</querydsl.version>
        <lombok.version>1.18.34</lombok.version>
        <jakarta-persistence.version>3.1.0</jakarta-persistence.version>
        <jakarta-annotation.version>2.1.1</jakarta-annotation.version>
    </properties>

    <dependencies>
        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- Database -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>com.querydsl</groupId>
            <artifactId>querydsl-jpa</artifactId>
            <version>${querydsl.version}</version>
            <classifier>jakarta</classifier>
        </dependency>
        <dependency>
            <groupId>my.utils</groupId>
            <artifactId>querydsl-utils</artifactId>
            <version>1.0.0</version>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>1.6.3</version>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>${lombok.version}</version>
                        </path>
                        <path>
                            <groupId>com.querydsl</groupId>
                            <artifactId>querydsl-apt</artifactId>
                            <version>${querydsl.version}</version>
                            <classifier>jakarta</classifier>
                        </path>
                        <path>
                            <groupId>jakarta.persistence</groupId>
                            <artifactId>jakarta.persistence-api</artifactId>
                            <version>${jakarta-persistence.version}</version>
                        </path>
                        <path>
                            <groupId>jakarta.annotation</groupId>
                            <artifactId>jakarta.annotation-api</artifactId>
                            <version>${jakarta-annotation.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>

```
---
