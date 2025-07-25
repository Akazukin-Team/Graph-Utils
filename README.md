# Graph Utils

[![Build Status](https://github.com/Akazukin-Team/Graph-Utils/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/Akazukin-Team/Graph-Utils/actions/workflows/build.yml?query=branch:main)

A collection of utilities for graph algorithms and animations


---

## Table of Contents

- [Features](#features)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
- [Contributing](#contributing)
- [Build Instructions](#build-instructions)
- [Continuous Integration](#continuous-integration)
- [License](#license)
- [Contact](#contact)

---

## Features

- Add to calc graph functions
- Add easing functions

---

## Getting Started

### Prerequisites

Make sure you have the following installed:

- **Java Development Kit (JDK)** version 8 or later.

---

### Installation

#### Using Maven

1. Add the following repository to the `<repositories>` block in your `pom.xml` file:
    ```xml
    <repository>
        <id>akazukin-repo</id>
        <name>Akazukin Repository</name>
        <url>https://maven.akazukin.org/refer/maven-public-libraries/</url>
    </repository>
    ```

2. Add the dependency to the `<dependencies>` block in your `pom.xml` file:
    ```xml
    <dependency>
        <groupId>org.akazukin</groupId>
        <artifactId>graph-utils</artifactId>
        <version>VERSION</version>
    </dependency>
    ```

---

#### Using Gradle

1. Add the repository to the `repositories` block in your `build.gradle` file:
    ```groovy
    maven {
        name = 'Akazukin Repository'
        url = 'https://maven.akazukin.org/refer/maven-public-libraries/'
    }
    ```

2. Add the dependency to the `dependencies` block in your `build.gradle` file:
    ```groovy
    implementation 'org.akazukin:graph-utils:<VERSION>'
    ```

---

## Contributing

Please read the [Contribution Guide](./.github/CONTRIBUTING.md) carefully and follow the coding conventions and
guidelines when making your changes.


---

## Build Instructions

To build the project from source, follow these steps:

1. Clone the repository:
    ```shell
    git clone https://github.com/Akazukin-Team/Graph-Utils.git
    cd Graph-Utils
    ```

2. Build the project with Gradle:
    ```shell
    ./gradlew build
    ```
   The compiled JAR file will be located in the `build/libs/` directory.


3. Publish to the local Maven repository using the `maven-publish` plugin:
    ```shell
    ./gradlew publishToMavenLocal
    ```

---

## Continuous Integration

This project uses GitHub Actions for Continuous Integration (CI).
Every push to the `main` branch automatically triggers the build and test workflow.


---

## License

This project is licensed under the terms described in the [License](LICENSE) file.


---

## Contact

If you need further assistance or wish to contact us directly,
please refer to the [Support](./.github/SUPPORT.md) page.


---
