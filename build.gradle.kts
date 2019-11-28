import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.3.50"
    id("java")
    id("idea")
    id("org.springframework.boot") version "2.2.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    id("pl.allegro.tech.build.axion-release") version "1.10.0"
    id("com.bmuschko.docker-remote-api") version "6.0.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
    kotlin("plugin.noarg") version kotlinVersion
}

group = "com.zlrx.kafka"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    maven {
        url = uri("http://packages.confluent.io/maven/")
    }
}

extra["vaadinVersion"] = "14.0.14"

configurations.forEach {
    it.exclude(group = "junit", module = "junit")
    it.exclude(module = "mockito-core")
}

val dockerUser: String by project
val dockerPw: String by project
val dockerEmail: String by project

docker {
    registryCredentials {
        username.set(dockerUser)
        password.set(dockerPw)
        email.set(dockerEmail)
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.vaadin:vaadin-spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.liquibase:liquibase-core")
    implementation("org.apache.kafka:kafka-clients:2.3.0")
    implementation("io.confluent:kafka-avro-serializer:5.3.1")
    implementation("org.apache.avro:avro:1.9.1")

    runtimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("com.ninja-squad:springmockk:1.1.2")
}

dependencyManagement {
    imports {
        mavenBom("com.vaadin:vaadin-bom:${property("vaadinVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

//val createDockerFile=tasks.create<com.bmuschko.gradle.docker.tasks.image.Dockerfile>("createDockerFile"){
//    from("openjdk:8-jre-alpine")
//    copyFile("","")
//
//    destFile=project.file("build/docker/Dockerfile")
//
//
//}
