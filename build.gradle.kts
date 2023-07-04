plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "de.zottig"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.1")
    implementation("com.opencsv:opencsv:5.4")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("Main")
}

