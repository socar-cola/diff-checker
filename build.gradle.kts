group = "kr.cola"
version = "0.0.4"

repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version "1.7.20"
    `java-gradle-plugin`
    `maven-publish`
}
gradlePlugin {
    plugins {
        create("diffCehcker") {
            id = "kr.cola.diff.checker"
            implementationClass = "kr.cola.diff.checker.DiffCheckerPlugin"
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.20")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.20")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    // https://mvnrepository.com/artifact/org.apache.ant/ant
    implementation("org.apache.ant:ant:1.10.13")
    // https://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple
    implementation("com.flipkart.zjsonpatch:zjsonpatch:0.4.14")
    // string to json
    implementation("com.google.code.gson:gson:2.10.1")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")

    // Use the Kotlin JUnit 5 integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

publishing {
    // publications {
    //     register<MavenPublication>("gpr") {
    //         // groupId = "kr.cola"
    //         // artifactId = "diff-checker"
    //         // version = "0.0.1"
    //         from(components["java"])
    //     }
    // }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/socar-cola/diff-checker")
            credentials {
                username = project.findProperty("repo.user") as String? ?: System.getenv("SOCAR_REPO_ID")
                password = project.findProperty("repo.key") as String? ?: System.getenv("SOCAR_REPO_KEY")
            }
        }
    }
}
