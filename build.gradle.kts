import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    //id("org.jetbrains.dokka") version "1.7.20"
    id("maven-publish")
}

group = "org.hyrical"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("redis.clients:jedis:4.3.1")
    implementation("org.mongodb:mongo-java-driver:3.12.11")
    implementation("org.slf4j:slf4j-api:2.0.4")
    //dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.7.20")
    testImplementation(kotlin("test"))
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
            version = "1.0"

            from(components["java"])
        }
    }
}