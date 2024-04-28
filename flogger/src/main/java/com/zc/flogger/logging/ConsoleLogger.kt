package com.zc.flogger.logging

import android.util.Log
import com.zc.flogger.WRAP_MAX_LENGTH
import com.zc.flogger.extensions.wrap
import com.zc.flogger.format.FormatParser
import com.zc.flogger.models.LogLevel
import com.zc.flogger.models.LogMessage

/**
 * Created by Zahi Chemaly on 4/28/2024.
 */
internal class ConsoleLogger(tagFormat: String, messageFormat: String) : BaseLogger() {
    private val tagFormatParser = FormatParser(tagFormat)
    private val messageFormatParser = FormatParser(messageFormat)

    override fun handle(logMessage: LogMessage) {
        val tag = tagFormatParser.parse(logMessage.copy(tag = "", message = logMessage.tag)).wrap(WRAP_MAX_LENGTH)
        val message = messageFormatParser.parse(logMessage.copy(tag = "", message = logMessage.message))
        when (logMessage.level) {
            LogLevel.INFO -> Log.i(tag, message)
            LogLevel.VERBOSE -> Log.v(tag, message)
            LogLevel.DEBUG -> Log.d(tag, message)
            LogLevel.WARNING -> Log.w(tag, message)
            LogLevel.ERROR -> Log.e(tag, message)
            LogLevel.FATAL -> Log.wtf(tag, message)
        }
    }
}
