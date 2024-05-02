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
                    val sentence = format.subSequence(index + 1, format.length).toString()
                    when {
                        sentence.startsWith(RSV_WORD_TAG, ignoreCase = true) -> {
                            expressions.add(LogExpression.Tag)
                            index += RSV_WORD_TAG.length
                        }

                        sentence.startsWith(RSV_WORD_MESSAGE, ignoreCase = true) -> {
                            expressions.add(LogExpression.Message)
                            index += RSV_WORD_MESSAGE.length
                        }

                        sentence.startsWith(RSV_WORD_DATE, ignoreCase = true) -> {
                            val arg = extractArguments(sentence, RSV_WORD_DATE, DEFAULT_DATE_FORMAT)
                            expressions.add(LogExpression.Date(arg.first))
                            index += RSV_WORD_DATE.length + arg.second
                        }

                        sentence.startsWith(RSV_WORD_LEVEL, ignoreCase = true) -> {
                            val arg = extractArguments(sentence, RSV_WORD_LEVEL, "number")
                            expressions.add(LogExpression.Level(arg.first))
                            index += RSV_WORD_LEVEL.length + arg.second
                        }

                        sentence.startsWith(RSV_WORD_THREAD, ignoreCase = true) -> {
                            val arg = extractArguments(sentence, RSV_WORD_THREAD, "id")
                            expressions.add(LogExpression.ThreadInfo(arg.first))
                            index += RSV_WORD_THREAD.length + arg.second
                        }

                        sentence.startsWith(RSV_WORD_CLASS_NAME, ignoreCase = true) -> {
                            val arg = extractArguments(sentence, RSV_WORD_CLASS_NAME, "simple")
                            expressions.add(LogExpression.ClassName(arg.first))
                            index += RSV_WORD_CLASS_NAME.length + arg.second
                        }

                        sentence.startsWith(RSV_WORD_FILE_NAME, ignoreCase = true) -> {
                            val arg = extractArguments(sentence, RSV_WORD_FILE_NAME, "")
                            expressions.add(LogExpression.FileName(arg.first.toIntOrNull() ?: WRAP_LENGTH_DISABLED))
                            index += RSV_WORD_FILE_NAME.length + arg.second
                        }

                        sentence.startsWith(RSV_WORD_METHOD_NAME, ignoreCase = true) -> {
                            val arg = extractArguments(sentence, RSV_WORD_METHOD_NAME, "")
                            expressions.add(LogExpression.MethodName(arg.first.toIntOrNull() ?: WRAP_LENGTH_DISABLED))
                            index += RSV_WORD_METHOD_NAME.length + arg.second
                        }

                        sentence.startsWith(RSV_WORD_LINE_NUMBER, ignoreCase = true) -> {
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

    /**
     * Returns a pair of argument value and its index offset (2 if it contains brackets).
     */
    private fun extractArguments(sentence: String, word: String, defaultArgumentValue: String): Pair<String, Int> {
        val extractedArgument = sentence.extractFromBrackets()
        if (extractedArgument.isNullOrBlank()) return Pair(defaultArgumentValue, 0)
        val argumentAfterWord =
            sentence.subSequence(word.length + 1, (word.length + 1) + extractedArgument.length).toString()
        if (extractedArgument.compareTo(argumentAfterWord, true) != 0) {
            // arguments do not match, meaning the argument does not belong to the word.
            // return the default arg.
            return Pair(defaultArgumentValue, 0)
        }
        return Pair(extractedArgument, extractedArgument.length + 2) // 2 for brackets
    }

    fun parse(logMessage: LogMessage): String =
        expressions.joinToString(separator = "") { it.interpret(logMessage) }

    companion object {
        private const val RSV_WORD_TAG = "tag"
        private const val RSV_WORD_MESSAGE = "message"
        private const val RSV_WORD_DATE = "date"
        private const val RSV_WORD_LEVEL = "level"
        private const val RSV_WORD_THREAD = "thread"
        private const val RSV_WORD_CLASS_NAME = "classname"
        private const val RSV_WORD_METHOD_NAME = "methodname"
        private const val RSV_WORD_FILE_NAME = "filename"
        private const val RSV_WORD_LINE_NUMBER = "linenumber"
    }
}
