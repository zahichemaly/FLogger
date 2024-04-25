package com.zc.flogger.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by Zahi Chemaly on 4/25/2024.
 */
private val locale = Locale.US

internal fun Date.toFormat(format: String): String? {
    return try {
        val sdf = SimpleDateFormat(format, locale)
        sdf.format(this)
    } catch (ex: Exception) {
        ""
    }
}
