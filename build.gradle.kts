plugins {
    kotlin("jvm") version "1.9.22"
//    id("com.github.johnrengelman.shadow") version "7.1.2" // Adjust version as needed
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10" // Adjust version as needed
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.amazonaws:aws-lambda-java-core:1.2.2")
    implementation("com.amazonaws:aws-lambda-java-events:3.11.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("com.amazonaws:aws-java-sdk-dynamodb:1.12.356")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("com.amazonaws:aws-java-sdk-lambda:1.12.356")  // Adjust version as needed
    implementation("software.amazon.awssdk:url-connection-client:2.19.7")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1") // Adjust version as needed
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.1")
    implementation("dev.kord:kord-core:0.13.1")
    implementation("org.slf4j:slf4j-simple:2.0.6") // Simple logging
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")  // Engine for running tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2") // Adjust the version as needed
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0") // Or your desired version
}

tasks.test {
    useJUnitPlatform()
}

//tasks {
//    shadowJar {
//        manifest {
//            attributes(mapOf("Main-Class" to "Main")) // Your main class
//        }
//        // Other customizations if needed (e.g., relocate packages)
//    }
//}

kotlin {
    jvmToolchain(11)
}