package com.zc.flogger.logging

import com.zc.flogger.models.LogLevel

/**
 * Created by Zahi Chemaly on 4/28/2024.
 */
internal abstract class BaseLogger : Logger {
    var next: Logger? = null

    override fun log(tag: String, message: String, logLevel: LogLevel) {
        handle(tag, message, logLevel)
        next?.log(tag, message, logLevel)
    }

    abstract fun handle(tag: String, message: String, logLevel: LogLevel)
}
