package com.zc.flogger.extensions

import com.zc.flogger.WRAP_LENGTH_DISABLED


private const val REGEX_BRACKETS = "\\{([^{}]+)\\}"

private fun String.extractFromRegex(regex: String): String? {
    val matchResult = Regex(regex).find(this)
    return matchResult?.groupValues?.getOrNull(1)
}

private fun String.extractAllFromRegex(regex: String): List<String> {
    val matchResult = Regex(regex).findAll(this)
    return matchResult
        .toList()
        .mapNotNull { it.groupValues.getOrNull(1) }
}

internal fun String.extractFromBrackets(): String? =
    extractFromRegex(REGEX_BRACKETS)

internal fun String.extractAllFromBrackets(): List<String> =
    extractAllFromRegex(REGEX_BRACKETS)

internal fun String.wrap(maxLength: Int): String =
    if (maxLength == WRAP_LENGTH_DISABLED) this else substring(0, length.coerceAtMost(maxLength))
