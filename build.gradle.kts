import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"
    id("org.jetbrains.dokka") version "1.7.20"
    id("maven-publish")
    id("java-library")
}

group = "org.hyrical"
version = "1.0"


repositories {
    mavenCentral()
}

dependencies {
    compileOnly("redis.clients:jedis:4.3.1")
    compileOnly("org.mongodb:mongo-java-driver:3.12.11")
    implementation("org.slf4j:slf4j-api:2.0.6")
    testImplementation(kotlin("test"))
    compileOnly("io.projectreactor:reactor-core:3.5.2")
    implementation("com.google.code.gson:gson:2.10.1")
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.hyrical"
            artifactId = "store"
            version = sematicVersion

            from(components["java"])
        }
    }
}

configurations.all {
    resolutionStrategy {
        force("com.google.code.gson:gson:2.10.1")
    }
}