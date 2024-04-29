package com.zc.flogger

import android.content.Context
import com.zc.flogger.logging.ConsoleLogger
import com.zc.flogger.logging.FileLogger
import com.zc.flogger.logging.Logger
import com.zc.flogger.models.LogLevel
import com.zc.flogger.models.LogMessage

/**
 * Created by Zahi Chemaly on 4/25/2024.
 */
object FLog {
    private val loggers = mutableListOf<Logger>()

    class Configuration {

        fun withFileLogger(context: Context, format: String): Configuration {
            loggers.add(FileLogger(context, format))
            return this
        }

        fun withConsoleLogger(tagFormat: String, messageFormat: String): Configuration {
            loggers.add(ConsoleLogger(tagFormat, messageFormat))
            return this
        }
    }

    private fun getElementIndex(stackTrace: Array<StackTraceElement>?): Int {
        if (stackTrace == null) return 0
        for (i in 2..stackTrace.size) {
            val className = stackTrace[i].className ?: ""
            if (className.contains(this.javaClass.simpleName)) continue
            return i
        }
        return 0
    }

    private fun log(tag: String, message: String, logLevel: LogLevel) {
        val thread = Thread.currentThread()
        val stackTrace = thread.stackTrace

        val elementIndex: Int = getElementIndex(stackTrace)
        if (elementIndex == 0) return

        val logMessage = LogMessage(
            tag = tag,
            message = message,
            thread = thread,
            level = logLevel,
            mainStackTraceElement = stackTrace[elementIndex]
        )

        loggers.forEach { logger -> logger.log(logMessage) }
    }

    fun verb(tag: String, message: String) = log(tag, message, LogLevel.VERBOSE)

    fun debug(tag: String, message: String) = log(tag, message, LogLevel.DEBUG)

    fun info(tag: String, message: String) = log(tag, message, LogLevel.INFO)

    fun warn(tag: String, message: String) = log(tag, message, LogLevel.WARNING)

    fun error(tag: String, message: String) = log(tag, message, LogLevel.ERROR)

    fun fatal(tag: String, message: String) = log(tag, message, LogLevel.FATAL)
}
