package com.zc.flogger.format

import com.zc.flogger.DEFAULT_DATE_FORMAT
import com.zc.flogger.WRAP_LENGTH_DISABLED
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

    data class Date(private val format: String) : LogExpression {
        override fun interpret(logMessage: LogMessage): String {
            val now = java.util.Date()
            return now.toFormat(format) ?: now.toFormat(DEFAULT_DATE_FORMAT) ?: ""
        }
    }

    data class Literal(private val token: String) : LogExpression {
        override fun interpret(logMessage: LogMessage): String {
            return token
        }
    }

    data class Level(private val format: String) : LogExpression {
        override fun interpret(logMessage: LogMessage): String {
            return when (format) {
                "number" -> logMessage.level.ordinal.toString()
                "letter" -> logMessage.level.name.first().toString()
                "name" -> logMessage.level.name
                else -> logMessage.level.ordinal.toString()
            }
        }
    }

    data class ThreadInfo(private val format: String) : LogExpression {

        override fun interpret(logMessage: LogMessage): String {
            return when (format) {
                "id" -> logMessage.thread.id.toString()
                "name" -> logMessage.thread.name.toString()
                else -> logMessage.thread.id.toString()
            }
        }
    }

    data class ClassName(private val format: String) : LogExpression {

        override fun interpret(logMessage: LogMessage): String {
            // example: FormatParser$parse$1
            // in this case, parse the $ signs
            val fullClassName = logMessage.mainStackTraceElement?.className ?: ""
            return when (format) {
                "full" -> fullClassName
                "simple" -> fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
                else -> fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
            }
        }
    }

    data class FileName(private val maxLength: Int = WRAP_LENGTH_DISABLED) : LogExpression {
        override fun interpret(logMessage: LogMessage): String {
            return logMessage.mainStackTraceElement?.fileName ?: ""
        }
    }

    data class MethodName(private val maxLength: Int = WRAP_LENGTH_DISABLED) : LogExpression {
        override fun interpret(logMessage: LogMessage): String {
            return logMessage.mainStackTraceElement?.methodName ?: ""
        }
    }

    data object LineNumber : LogExpression {
        override fun interpret(logMessage: LogMessage): String {
            return logMessage.mainStackTraceElement?.lineNumber?.toString() ?: ""
        }
    }
}
