/*
 * This file was generated by the Gradle 'init' task.
 */
rootProject.name = "Orbis"
include("orbis-core")
include("orbis-paper")
include("orbis-minestom")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}