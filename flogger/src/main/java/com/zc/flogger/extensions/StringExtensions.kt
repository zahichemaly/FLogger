package com.zc.flogger.extensions


private const val REGEX_BRACKETS = "\\{([^{}]+)\\}"

private fun String.extractFromRegex(regex: String): String? {
    val matchResult = Regex(regex).find(this)
    return matchResult?.groupValues?.getOrNull(1)
}

internal fun String.extractFromBrackets(): String? =
    extractFromRegex(REGEX_BRACKETS)

internal fun String.wrap(maxLength: Int): String =
    substring(0, length.coerceAtMost(maxLength))
