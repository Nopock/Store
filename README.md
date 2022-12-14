<h1 align="center">Welcome to Store 👋</h1>
<p>
  <img alt="Version" src="https://img.shields.io/badge/version-1.0.0-blue.svg?cacheSeconds=2592000" />
  <img src="https://img.shields.io/badge/kotlin-1.7.21-blue.svg" />
  <a href="https://github.com/Nopock/Store#Usage" target="_blank">
    <img alt="Documentation" src="https://img.shields.io/badge/documented-yes-brightgreen.svg" />
  </a>
  <a href="https://github.com/Nopock/Store/graphs/commit-activity" target="_blank">
    <img alt="Maintenance" src="https://img.shields.io/badge/maintained-yes-brightgreen.svg" />
  </a>
  <a href="https://github.com/Nopock/Store/blob/main/LICENSE" target="_blank">
    <img alt="License: MIT" src="https://img.shields.io/github/license/Nopock/Store" />
  </a>
  
  [![Java CI](https://github.com/Nopock/Store/actions/workflows/gradle.yml/badge.svg)](https://github.com/Nopock/Store/actions/workflows/gradle.yml)
</p>

> An AIO data storage library.

## Prerequisites

- Java 8

## Installation

```gradle
    repositories {
        maven("https://jitpack.io")
    }
    
    dependencies {
        implementation("com.github.Nopock:Store:[Latest Commit]")

        // If you are using any redis repositories
        compileOnly("redis.clients:jedis:4.3.1")

        // If you are using any mongo repositories
        compileOnly("org.mongodb:mongo-java-driver:3.12.11")
        // If you are using kotlinx.serialization
        compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

        // If you are using reactive repositories
        compileOnly("io.projectreactor:reactor-core:3.5.1")
    }
    
    // You will need to include MongoDB / redis yourself as I do not want to fatten the jar
```

## Build Yourself

```sh
./gradlew build
```

```sh
./gradlew publishToMavenLocal
```

## Usage

```kt
    DataTypeResources.enableMongoRepositories("localhost", 27017, "Database")

    val controller = DataStoreController.of<ExampleData>(StorageType.MONGO)

    controller.repository.save(ExampleData(UUID.randomUUID().toString(), "Hello World!", 10))
```

```kt
    data class ExampleData(override val identifier: String, val name: String, val age: Int) : Storable
```

## Roadmap

- Better MongoDB and redis connections
- Implement a cache for dif types
- Add support for kotlinx.coroutines

![Alt](https://repobeats.axiom.co/api/embed/d9732890507abe6f645b1c954e032aea40b39386.svg "Repobeats analytics image")

## Author

👤 **Nathan Weisz**

* Twitter: [@RealRepation](https://twitter.com/RealRepation)
* Github: [@Nopock](https://github.com/Nopock)

## 🤝 Contributing

Contributions, issues and feature requests are welcome!<br />Feel free to check [issues page](https://github.com/Nopock/Store/issues). 

## Show your support

Give a ⭐️ if this project helped you!

## 📝 License

Copyright © 2022 [Nathan Weisz](https://github.com/Nopock).<br />
This project is [MIT](https://github.com/Nopock/Store/blob/main/LICENSE) licensed.

***
