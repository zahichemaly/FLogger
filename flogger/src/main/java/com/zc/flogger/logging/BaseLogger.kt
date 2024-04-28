package com.zc.flogger.logging

import com.zc.flogger.models.LogMessage

/**
 * Created by Zahi Chemaly on 4/28/2024.
 */
internal abstract class BaseLogger : Logger {

    var next: Logger? = null

    override fun log(logMessage: LogMessage) {
        handle(logMessage)
        next?.log(logMessage)
    }

    abstract fun handle(logMessage: LogMessage)
}
