import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt.gradleplugin)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jacoco)
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}


val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("android/key.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
} else {
    //TODO fix this, since this *is* a problem for manually built release builds
    // however I have zero plans of doing that, so it's kind of a non-issue at the moment
    project.logger.warn("File not found: $keystorePropertiesFile")
    project.logger.warn("Using default values for signing config.")
    keystoreProperties["keyAlias"] = "default"
    keystoreProperties["keyPassword"] = "password"
    keystoreProperties["storeFile"] = file("default.jks")
    keystoreProperties["storePassword"] = "storePassword"
}


android {
    signingConfigs {
        // "debug" already exists, so lets just do the "release" one
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile =
                keystoreProperties["storeFile"]?.let {
                    // It's already a file, so just return it
                    return@let file(it)
                }
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    namespace = "com.nsoft.github"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.nsoft.github"

        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        compose = true
        renderScript = true // Required for blur on pre-Android 12 devices
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("reports/compose_compiler")
    stabilityConfigurationFiles = listOf(rootProject.layout.projectDirectory.file("stability_config.conf"))
}

// Turn on test logging in console for all tasks of type "test"
tasks.withType<Test> {

    testLogging {
        // Set options for log level LIFECYCLE
        events = setOf(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.STANDARD_OUT
        )
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true

        // Set options for log level DEBUG and INFO
        debug {
            events = setOf(
                TestLogEvent.STARTED,
                TestLogEvent.FAILED,
                TestLogEvent.PASSED,
                TestLogEvent.SKIPPED,
                TestLogEvent.STANDARD_ERROR,
                TestLogEvent.STANDARD_OUT
            )
            exceptionFormat = TestExceptionFormat.FULL
        }
        info {
            events = debug.events
            exceptionFormat = debug.exceptionFormat
        }

        afterSuite(KotlinClosure2<TestDescriptor, TestResult, Unit>({ desc, result ->
            if (desc.parent == null) { // Will match the outermost suite
                val output =
                    "Results: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} passed, ${result.failedTestCount} failed, ${result.skippedTestCount} skipped)"
                val startItem = "|  "
                val endItem = "  |"
                val repeatLength = startItem.length + output.length + endItem.length
                println(
                    "\n${"-".repeat(repeatLength)}\n$startItem$output$endItem\n${
                        "-".repeat(
                            repeatLength
                        )
                    }"
                )
            }
        }))
    }

    finalizedBy(tasks.named<JacocoReport>("jacocoTestReport")) // Ensure the report is generated after tests run
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn(
        "testDebugUnitTest",
//        "createDebugCoverageReport" // For instrumentation tests (Android Test)
    )

    group = "Reporting"
    description = "Generate Jacoco coverage reports"

    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco"))
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "**/*.\$*",                         // Lambda expressions
//        "**/android/**/*.*",  // funny how this is where all of my things are
        "**/di/**",                         // Exclude Dagger modules
        "**/BR.*",                          // DataBinding
        "**/dagger/**/*.*",                 // dagger generated classes
        "**/hilt_aggregated_deps/**/*.*"    // hilt/dagger somethings
    )

    val javaClasses = fileTree(
        layout.buildDirectory.dir("intermediates/javac/debug/compileDebugJavaWithJavac/classes").get()
    ) {
        exclude(fileFilter)
    }

    val kotlinClasses = fileTree(
        layout.buildDirectory.dir("tmp/kotlin-classes/debug").get()
    ) {
        exclude(fileFilter)
    }

    classDirectories.setFrom(files(javaClasses, kotlinClasses))
    sourceDirectories.setFrom(
        files(
            project.fileTree("src/main/java"),
            project.fileTree("src/main/kotlin")
        )
    )

    executionData.setFrom(
        fileTree(layout.buildDirectory.asFile) {
            include(
                "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
                "outputs/code_coverage/debugAndroidTest/connected/**/*.ec",
                // And the actual folder where they end up in
                "jacoco/*.exec"
            )
        }
    )
}


dependencies {
    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.metadata.jvm)

    // Dagger (hilt)
    implementation(libs.dagger.hilt.plugin)
    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.dagger.hilt.navigation.compose)

    // Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Navigation
    implementation(libs.navigation.compose)
    implementation(libs.navigation.runtime)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.iconsextended)
    implementation(libs.compose.runtime.livedata)
    implementation(libs.compose.preview)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)

    // Networking
    implementation(libs.retrofit.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Timber
    implementation(libs.timber)

    // Backported java.time features to lower APIs
    implementation(libs.threetenabp)

    // Image loading library coil-compose
    implementation(libs.coil.compose)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.google.truth)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.core.testing)
}
