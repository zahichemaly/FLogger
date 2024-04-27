package com.zc.flogger.format

import com.zc.flogger.RSV_WORD_DATE
import com.zc.flogger.RSV_WORD_LEVEL
import com.zc.flogger.RSV_WORD_MESSAGE
import com.zc.flogger.RSV_WORD_TAG
import com.zc.flogger.extensions.extractFromBrackets
import com.zc.flogger.models.LogMessage

/**
 * Created by Zahi Chemaly on 4/27/2024.
 */
internal class FormatParser(private val format: String) {

    fun parse(logMessage: LogMessage): String {
        var index = 0
        val expressions = mutableListOf<LogExpression>()
        while (index < format.length) {
            val char = format[index]
            if (char == '%') {
                if (index < format.length - 1) {
                    val reservedWord = format.subSequence(index + 1, format.length).toString()
                    when {
                        reservedWord.startsWith(RSV_WORD_TAG, ignoreCase = true) -> {
                            expressions.add(LogExpression.Tag)
                            index += RSV_WORD_TAG.length + 1
                        }

                        reservedWord.startsWith(RSV_WORD_MESSAGE, ignoreCase = true) -> {
                            expressions.add(LogExpression.Message)
                            index += RSV_WORD_MESSAGE.length + 1
                        }

                        reservedWord.startsWith("$RSV_WORD_DATE{", ignoreCase = true) -> {
                            val dateFormat = reservedWord.extractFromBrackets() ?: ""
                            expressions.add(LogExpression.Date(dateFormat))
                            index += RSV_WORD_DATE.length + dateFormat.length + 3
                        }

                        reservedWord.startsWith("$RSV_WORD_LEVEL{", ignoreCase = true) -> {
                            val logFormat = reservedWord.extractFromBrackets() ?: ""
                            expressions.add(LogExpression.Level(logFormat))
                            index += RSV_WORD_LEVEL.length + logFormat.length + 3
                        }

                        else -> {
                            expressions.add(LogExpression.Literal(char.toString()))
                            index++
                        }
                    }
                }
            } else {
                expressions.add(LogExpression.Literal(char.toString()))
                index++
            }
        }
        return expressions.joinToString(separator = "") { it.interpret(logMessage) }
    }
}
