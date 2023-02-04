import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"
    id("org.jetbrains.dokka") version "1.7.20"
    id("maven-publish")
    id("java-library")
}

group = "org.hyrical.store"
version = "1.0"


repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.jedis)
    compileOnly(libs.mongo)
    compileOnly(libs.reactor.core)
    implementation(libs.gson)
    compileOnly(libs.coroutines)

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
