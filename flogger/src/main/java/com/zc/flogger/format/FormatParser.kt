package com.zc.flogger.format

import com.zc.flogger.DEFAULT_DATE_FORMAT
import com.zc.flogger.WRAP_LENGTH_DISABLED
import com.zc.flogger.extensions.extractFromBrackets
import com.zc.flogger.models.LogMessage

/**
 * Created by Zahi Chemaly on 4/27/2024.
 */
internal class FormatParser(format: String) {
    private val expressions = mutableListOf<LogExpression>()

    init {
        var index = 0
        while (index < format.length) {
            val char = format[index]
            if (char == '%') {
                if (index < format.length - 1) {
                    val reservedWord = format.subSequence(index + 1, format.length).toString()
                    when {
                        reservedWord.startsWith(RSV_WORD_TAG, ignoreCase = true) -> {
                            expressions.add(LogExpression.Tag)
                            index += RSV_WORD_TAG.length
                        }

                        reservedWord.startsWith(RSV_WORD_MESSAGE, ignoreCase = true) -> {
                            expressions.add(LogExpression.Message)
                            index += RSV_WORD_MESSAGE.length
                        }

                        reservedWord.startsWith(RSV_WORD_DATE, ignoreCase = true) -> {
                            val dateFormat = reservedWord.extractFromBrackets() ?: DEFAULT_DATE_FORMAT
                            expressions.add(LogExpression.Date(dateFormat))
                            index += RSV_WORD_DATE.length + dateFormat.length + 2
                        }

                        reservedWord.startsWith(RSV_WORD_LEVEL, ignoreCase = true) -> {
                            val logFormat = reservedWord.extractFromBrackets() ?: "number"
                            expressions.add(LogExpression.Level(logFormat))
                            index += RSV_WORD_LEVEL.length + logFormat.length + 2
                        }

                        reservedWord.startsWith(RSV_WORD_THREAD, ignoreCase = true) -> {
                            val type = reservedWord.extractFromBrackets() ?: "id"
                            expressions.add(LogExpression.ThreadInfo(type))
                            index += RSV_WORD_THREAD.length + type.length + 2
                        }

                        reservedWord.startsWith(RSV_WORD_CLASS_NAME, ignoreCase = true) -> {
                            val type = reservedWord.extractFromBrackets() ?: "simple"
                            expressions.add(LogExpression.ClassName(type))
                            index += RSV_WORD_CLASS_NAME.length
                        }

                        reservedWord.startsWith(RSV_WORD_FILE_NAME, ignoreCase = true) -> {
                            val maxLengthStr = reservedWord.extractFromBrackets() ?: ""
                            val maxLength = maxLengthStr.toIntOrNull() ?: WRAP_LENGTH_DISABLED
                            expressions.add(LogExpression.FileName(maxLength))
                            index += RSV_WORD_FILE_NAME.length + maxLengthStr.length + 2
                        }

                        reservedWord.startsWith(RSV_WORD_METHOD_NAME, ignoreCase = true) -> {
                            val maxLengthStr = reservedWord.extractFromBrackets() ?: ""
                            val maxLength = maxLengthStr.toIntOrNull() ?: WRAP_LENGTH_DISABLED
                            expressions.add(LogExpression.MethodName(maxLength))
                            index += RSV_WORD_METHOD_NAME.length + maxLengthStr.length + 2
                        }

                        reservedWord.startsWith(RSV_WORD_LINE_NUMBER, ignoreCase = true) -> {
                            expressions.add(LogExpression.LineNumber)
                            index += RSV_WORD_LINE_NUMBER.length
                        }

                        else -> {
                            expressions.add(LogExpression.Literal(char.toString()))
                        }
                    }
                    index++
                }
            } else {
                expressions.add(LogExpression.Literal(char.toString()))
                index++
            }
        }
    }

    fun parse(logMessage: LogMessage): String =
        expressions.joinToString(separator = "") { it.interpret(logMessage) }

    companion object {
        const val RSV_WORD_TAG = "tag"
        const val RSV_WORD_MESSAGE = "message"
        const val RSV_WORD_DATE = "date"
        const val RSV_WORD_LEVEL = "level"
        const val RSV_WORD_THREAD = "thread"
        const val RSV_WORD_CLASS_NAME = "classname"
        const val RSV_WORD_METHOD_NAME = "methodname"
        const val RSV_WORD_FILE_NAME = "filename"
        const val RSV_WORD_LINE_NUMBER = "linenumber"
    }
}
