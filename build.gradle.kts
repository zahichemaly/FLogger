import java.io.FileInputStream
import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false
    `java-library`
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

val props by lazy {
    val propFile = rootProject.file("./local.properties")
    val properties = Properties()
    properties.load(FileInputStream(propFile))
    val map = mutableMapOf<String, String>()
    properties.forEach { map[it.key.toString()] = it.value.toString() }
    map.toMap()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "io.github.zahichemaly"
            artifactId = "flogger"
            version = "1.0.0"

            from(components["java"])

            pom {
                name.set("FLogger")
                description.set("Write logs to a file on Android, and upload them to Firebase Cloud Storage!")
                url.set("https://github.com/zahichemaly/FLogger")
                licenses {
                    license {
                        name.set("Apache License")
                        url.set("https://github.com/zahichemaly/FLogger/blob/master/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set(props["developerId"])
                        name.set(props["developerName"])
                        email.set(props["developerEmail"])
                    }
                }
                scm {
                    connection.set("scm:git:github.com/zahichemaly/FLogger.git")
                    developerConnection.set("scm:git:ssh://github.com/zahichemaly/FLogger.git")
                    url.set("https://github.com/zahichemaly/FLogger/tree/main")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        create("myNexus") {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            stagingProfileId.set(props["sonatypeStagingProfileId"])
            username.set(props["myNexusUsername"])
            password.set(props["myNexusPassword"])
        }
    }
}

signing {
//    val signingKeyId = props["signing.keyId"]
//    val signingKey = props["signing.secretKeyRingFile"]
//    val signingPassword = props["signing.password"]
//    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(publishing.publications["mavenJava"])
}
