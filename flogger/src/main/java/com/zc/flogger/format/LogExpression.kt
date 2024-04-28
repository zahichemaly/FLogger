package com.zc.flogger.format

import com.zc.flogger.DEFAULT_DATE_FORMAT
import com.zc.flogger.WRAP_LENGTH_DISABLED
import com.zc.flogger.extensions.toFormat
import com.zc.flogger.extensions.wrap
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

    data class Thread(private val format: String) : LogExpression {
        override fun interpret(logMessage: LogMessage): String {
            return when (format) {
                "id" -> logMessage.thread.id.toString()
                "name" -> logMessage.thread.name.toString()
                else -> logMessage.thread.id.toString()
            }
        }
    }

    data class ClassName(private val maxLength: Int = WRAP_LENGTH_DISABLED, private val format: String) : LogExpression {

        override fun interpret(logMessage: LogMessage): String {
            val fullClassName = logMessage.activeStackTraceElement.className
            return when (format) {
                "full" -> logMessage.activeStackTraceElement.className.wrap(maxLength)
                "simple" -> fullClassName.substring(fullClassName.lastIndexOf(".") + 1).wrap(maxLength)
                else -> fullClassName.substring(fullClassName.lastIndexOf(".") + 1).wrap(maxLength)
            }
        }
    }

    data class FileName(private val maxLength: Int = WRAP_LENGTH_DISABLED) : LogExpression {
        override fun interpret(logMessage: LogMessage): String {
            return logMessage.activeStackTraceElement.fileName.wrap(maxLength)
        }
    }

    data class MethodName(private val maxLength: Int = WRAP_LENGTH_DISABLED) : LogExpression {
        override fun interpret(logMessage: LogMessage): String {
            return logMessage.activeStackTraceElement.methodName.wrap(maxLength)
        }
    }

    data object LineNumber : LogExpression {
        override fun interpret(logMessage: LogMessage): String {
            return logMessage.activeStackTraceElement.lineNumber.toString()
        }
    }
}
