package com.zc.flogger.extensions


internal fun String.extractFromRegex(regex: String): String? {
    val matchResult = Regex(regex).find(this)
    return matchResult?.groupValues?.getOrNull(1)
}
