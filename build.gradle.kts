import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.3.72"
    id("java")
    id("idea")
    id("com.vaadin") version "0.8.0"
    id("org.springframework.boot") version "2.3.3.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    id("pl.allegro.tech.build.axion-release") version "1.10.0"
    id("com.bmuschko.docker-remote-api") version "6.6.1"
    id("com.bmuschko.docker-spring-boot-application") version "6.6.1"
    id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
    id("org.jmailen.kotlinter") version "2.4.1"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
    kotlin("plugin.noarg") version kotlinVersion
}

group = "com.zlrx.kafka"
version = scmVersion.version
java.sourceCompatibility = JavaVersion.VERSION_11

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

kotlinter {
    ignoreFailures = false
    disabledRules = arrayOf("import-ordering", "filename")
}

tasks.compileKotlin { dependsOn(tasks.lintKotlin) }
tasks.lintKotlin { dependsOn(tasks.formatKotlin) }

tasks.dockerBuildImage{dependsOn(tasks.vaadinBuildFrontend)}

repositories {
    mavenCentral()
    maven {
        url = uri("http://packages.confluent.io/maven/")
    }
}

extra["vaadinVersion"] = "14.3.3"

val dockerUser: String? by project
val dockerPw: String? by project
val dockerEmail: String? by project

docker {
    registryCredentials {
        url.set("https://index.docker.io/v1")
        username.set(dockerUser)
        password.set(dockerPw)
        email.set(dockerEmail)
    }
    springBootApplication {
        baseImage.set("lpicanco/java11-alpine")
        ports.set(listOf(8080))
        maintainer.set("zalerix 'zalerix@gmail.com'")
        images.set(setOf("zalerix/kafka-avro-publish-ui:latest","zalerix/kafka-avro-publish-ui:2.1.1"))
        jvmArgs.set(listOf("-Xmx512m"))
    }
}

configurations.forEach {
    it.exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jetty")
    implementation("com.vaadin:vaadin-spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.liquibase:liquibase-core")
    implementation("org.apache.kafka:kafka-clients:2.6.0")
    implementation("io.confluent:kafka-avro-serializer:5.5.1")
    implementation("org.apache.avro:avro:1.10.0")

    runtimeOnly("com.h2database:h2")
}

dependencyManagement {
    imports {
        mavenBom("com.vaadin:vaadin-bom:${property("vaadinVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
