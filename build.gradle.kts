val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val hikariVersion: String by project
val mysqlConnectorVersion: String by project
val jBcryptVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.7.0"
}

group = "com.github.player13"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-gson-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("mysql:mysql-connector-java:$mysqlConnectorVersion")
    implementation("org.mindrot:jbcrypt:$jBcryptVersion")

    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}

tasks.register<Jar>("uberJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    archiveClassifier.set("uber")
    manifest {
        attributes("Main-Class" to "com.github.player13.ApplicationKt")
    }

    from(
        sourceSets.main.get().output,
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) },
    )
    dependsOn(configurations.runtimeClasspath)
}
