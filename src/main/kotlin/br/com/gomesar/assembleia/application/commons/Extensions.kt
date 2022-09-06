package br.com.gomesar.assembleia.application.commons

import java.util.Locale

fun String.toSnakeCase(): String {
    return "(?<=[a-zA-Z])[A-Z]".toRegex().replace(this) {
        "_${it.value}"
    }.lowercase(Locale.getDefault())
}

fun String.removeCaracteresEspeciais() = this.replace("[^a-zA-Z0-9]".toRegex(), "")
