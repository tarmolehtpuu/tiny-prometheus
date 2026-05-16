plugins {
    java
    jacoco
    `maven-publish`
}

group = "ee.moo"
version = rootProject.file("VERSION")
    .readText()
    .replace("\r", "")
    .replace("\n", "")
    .trim()

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:6.0.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:6.0.3")
    testImplementation("org.junit.platform:junit-platform-launcher:6.0.3")
}

tasks.jacocoTestReport {
    dependsOn(tasks.withType<Test>())
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.withType<JavaCompile> {
    options.release.set(17)
}

tasks.jar {
    archiveBaseName.set(project.name)
    archiveVersion.set(project.version.toString())

    from(project.rootDir) {
        include("LICENSE")
        include("NOTICE")
        into("META-INF")
    }

    from(sourceSets.main.get().output)

    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Sealed" to true,
            "Bundle-Copyright" to "Copyright 2026 Tarmo Lehtpuu",
            "Bundle-License" to "http://www.apache.org/licenses/LICENSE-20.0"
        )
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/tarmolehtpuu/tiny-prometheus")
            credentials {
                username = project.findProperty("github.user") as String? ?: System.getenv("GITHUB_USER")
                password = project.findProperty("github.token") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
        publications {
            register<MavenPublication>("gpr") {
                from(components["java"])
            }
        }
    }
}

tasks.register<Exec>("release") {
    dependsOn(tasks.jar)

    group = "publishing"
    description = "Releases current version of tiny-prometheus"
    workingDir = file("build/libs")

    val version = project.version.toString()
    val token = project.findProperty("github.token") as String? ?: System.getenv("GITHUB_TOKEN")
    if (token != null) {
        environment("GH_TOKEN", token)
    }

    commandLine = listOf(
        "gh", "release", "create", "v$version",
        "--repo", "tarmolehtpuu/tiny-prometheus",
        "--title", "tiny-prometheus-$version",
        "--generate-notes",
        "tiny-prometheus-$version.jar"
    )

    doFirst {
        if (token.isNullOrBlank()) {
            throw GradleException("GITHUB_TOKEN not found in gradle.properties or ENV")
        }
    }
}


