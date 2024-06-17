// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false

}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()  // Ensure this is included for the Gradle Plugin Portal
        maven { url = uri("https://maven.google.com") }
    }
    dependencies {
        classpath("com.google.gms:google-services:4.3.14")
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}

