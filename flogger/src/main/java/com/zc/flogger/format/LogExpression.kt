package com.zc.flogger.format

import com.zc.flogger.DEFAULT_DATE_FORMAT
import com.zc.flogger.extensions.toFormat
import com.zc.flogger.models.LogMessage

/**
 * Created by Zahi Chemaly on 4/27/2024.
 */
internal sealed interface LogExpression {
    fun interpret(logMessage: LogMessage): String

    data object Tag : LogExpression {
        override fun interpret(logMessage: LogMessage): String {
            return logMessage.tag
        }
    }

    data object Message : LogExpression {
        override fun interpret(logMessage: LogMessage): String {
            return logMessage.message
        }
    }

    data class Date(private val dateFormat: String) : LogExpression {
        override fun interpret(logMessage: LogMessage): String {
            val now = java.util.Date()
            return now.toFormat(dateFormat) ?: now.toFormat(DEFAULT_DATE_FORMAT) ?: ""
        }
    }

    data class Literal(private val token: String) : LogExpression {
        override fun interpret(logMessage: LogMessage): String {
            return token
        }
    }
}
