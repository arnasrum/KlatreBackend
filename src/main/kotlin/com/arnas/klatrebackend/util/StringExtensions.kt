package com.arnas.klatrebackend.util

fun String.toSnakeCase(): String {
    return this.replace(Regex("(?<=[a-z])([A-Z])")) {
        "_" + it.value
    }.lowercase()
}