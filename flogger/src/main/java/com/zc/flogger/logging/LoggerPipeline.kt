package com.zc.flogger.logging

import com.zc.flogger.models.LogMessage

/**
 * Created by Zahi Chemaly on 4/28/2024.
 */
internal class LoggerPipeline : Logger {
    private var first: BaseLogger? = null
    private var current: BaseLogger? = null

    fun add(next: BaseLogger): LoggerPipeline {
        if (first == null) {
            first = next
            current = first
            return this
        }
        current!!.next = next
        current = next
        return this
    }

    override fun log(logMessage: LogMessage) {
        first?.handle(logMessage)
    }
}
