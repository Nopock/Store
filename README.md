<h1 align="center">Welcome to Store 👋</h1>
<p>
  <img alt="Version" src="https://img.shields.io/badge/version-1.0.0-blue.svg?cacheSeconds=2592000" />
  <img src="https://img.shields.io/badge/kotlin-1.7.21-blue.svg" />
  <a href="https://nopox.gitbook.io/libraries/products/store" target="_blank">
    <img alt="Documentation" src="https://img.shields.io/badge/documented-yes-brightgreen.svg" />
  </a>
  <a href="https://github.com/Nopock/Store/graphs/commit-activity" target="_blank">
    <img alt="Maintenance" src="https://img.shields.io/badge/maintained-yes-brightgreen.svg" />
  </a>
  <a href="https://github.com/Nopock/Store/blob/main/LICENSE" target="_blank">
    <img alt="License: MIT" src="https://img.shields.io/github/license/Nopock/Store" />
  </a>
  <a href="https://jitpack.io/#Nopock/Store" target="_blank">
    <img alt="Release" src="https://jitpack.io/v/Nopock/Store.svg" />
  </a>
  <a href="https://github.com/Nopock/Store/actions/workflows/gradle.yml" target="_blank">
    <img alt="Java CI" src="https://github.com/Nopock/Store/actions/workflows/gradle.yml/badge.svg" />
  </a>
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
        implementation("redis.clients:jedis:4.3.1")

        // If you are using any mongo repositories
        implementation("org.mongodb:mongo-java-driver:3.12.11")

        // If you are using reactive repositories
        implementation("io.projectreactor:reactor-core:3.5.1")
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
- Add support for kotlinx.coroutines
- Wrapper for return types
- Add support for FileConnection
- Code cleanups
- More CI/CD
- Write tests that get ran on commit so that I don't break shit

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

Database connection system skidded from Growly <3
