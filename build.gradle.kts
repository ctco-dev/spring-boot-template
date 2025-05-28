plugins {
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "7.0.3"
    java
    jacoco
}

group = "lv.ctco"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21


repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testImplementation("com.tngtech.archunit:archunit:1.4.0")
    testImplementation("com.tngtech.archunit:archunit-junit5-api:1.4.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo.spring3x:4.11.0")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // ensure report runs after tests

    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
}

spotless {
    java {
        googleJavaFormat()
        target("src/**/*.java")
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        html.required.set(false)
        csv.required.set(false)
        xml.required.set(true)
    }
}
