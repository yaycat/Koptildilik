// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}
// Top-level build.gradle (Project-level)
buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.0")
        classpath("com.google.gms:google-services:4.3.15") // Add the correct version of google-services plugin
    }
}
