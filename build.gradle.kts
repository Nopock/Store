import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.dokka") version "1.7.20"
    id("maven-publish")
    id("java-library")
}

val sematicVersion = "1.54"

group = "org.hyrical"
version = sematicVersion


repositories {
    mavenCentral()
}

dependencies {
    compileOnly("redis.clients:jedis:4.3.1")
    compileOnly("org.mongodb:mongo-java-driver:3.12.11")
    implementation("org.slf4j:slf4j-api:2.0.6")
    testImplementation(kotlin("test"))
    compileOnly("io.projectreactor:reactor-core:3.5.2")
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.10.1")
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
