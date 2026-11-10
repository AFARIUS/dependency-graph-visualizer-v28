plugins {
    java
    application
}

group = "ru.mirea.ikbo2025"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("Main")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "Main"
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}