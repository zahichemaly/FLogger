package com.zc.flogger

import android.content.Context
import com.zc.flogger.format.FormatParser
import com.zc.flogger.logging.ConsoleLogger
import com.zc.flogger.logging.FileLogger
import com.zc.flogger.logging.LoggerPipeline
import com.zc.flogger.models.LogLevel
import com.zc.flogger.models.LogMessage
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Created by Zahi Chemaly on 4/25/2024.
 */
object FLog {
    private lateinit var loggerPipeline: LoggerPipeline
    private lateinit var formatParser: FormatParser

    fun configure(context: Context, format: String) {
        loggerPipeline = LoggerPipeline()
        formatParser = FormatParser(format)
        loggerPipeline.add(ConsoleLogger())
        loggerPipeline.add(FileLogger(context))
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

    private fun addExceptionIfNotNull(t: Throwable?, result: StringBuilder) {
        if (t != null) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            t.printStackTrace(pw)
            pw.flush()
            result.append("\n Throwable: ")
            result.append(sw.toString())
        }
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
            activeStackTraceElementIndex = elementIndex
        )

        val evaluatedMessage = formatParser.parse(logMessage)
        loggerPipeline.log(tag, evaluatedMessage, logLevel)
    }

    fun debug(tag: String, message: String) {
        log(tag, message, LogLevel.DEBUG)
    }

    fun info(tag: String, message: String) {
        log(tag, message, LogLevel.INFO)
    }
}
