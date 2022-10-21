import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.4"
	id("io.spring.dependency-management") version "1.0.14.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	id("org.openapi.generator") version "6.2.0"
}

group = "com.redirect"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	// swagger open api
	implementation("io.swagger.core.v3:swagger-annotations:2.2.3")
	implementation("io.swagger.parser.v3:swagger-parser:2.1.3")
	implementation("io.springfox:springfox-swagger-ui:3.0.0")
	implementation("io.springfox:springfox-swagger2:3.0.0")
	implementation("org.springdoc:springdoc-openapi-data-rest:1.6.11")
	implementation("org.springdoc:springdoc-openapi-ui:1.6.11")
	implementation("org.springdoc:springdoc-openapi-kotlin:1.6.11")

	// kafka
	implementation("org.apache.kafka:kafka-streams")
	implementation("org.springframework.kafka:spring-kafka")
	implementation("org.rocksdb:rocksdbjni:7.6.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.apache.kafka:kafka-streams-test-utils:3.3.1")
	testImplementation("org.apache.curator:curator-test:5.3.0")
	testImplementation("org.testcontainers:kafka:1.15.3")

}

openApiGenerate {
	generatorName.set("kotlin-spring")
	inputSpec.set("${projectDir}/src/main/resources/openapi.yml")
	apiPackage.set("com.redirect.generated.api")
	modelPackage.set("com.redirect.generated.model")
	outputDir.set("$projectDir")
	configOptions.put("interfaceOnly", "true")
	configOptions.put("gradleBuildFile", "false")
	configOptions.put("delegatePattern", "false")
	configOptions.put("documentationProvider", "none")
	configOptions.put("sourceFolder", "src/main/kotlin")
}


tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
