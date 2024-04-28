package com.zc.flogger

import com.zc.flogger.extensions.toFormat
import com.zc.flogger.format.FormatParser
import com.zc.flogger.models.LogLevel
import com.zc.flogger.models.LogMessage
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

/**
 * Unit tests to validate the log format conversion.
 */
class FormatParserUnitTest {

    @Test
    fun is_date_format_correct() {
        val tag = "API"
        val message = "This is a log test"
        val date = Date().toFormat("dd-MM-yyyy")

        val logMessage = LogMessage(tag, message)
        val logFormat = "%date{dd-MM-yyyy} [MYAPP] [%tag]: %message"
        val actual = FormatParser(logFormat).parse(logMessage)

        val expected = "$date [MYAPP] [$tag]: $message"
        assertEquals(expected, actual)
    }

    @Test
    fun is_invalid_format_a_literal() {
        val tag = "API"
        val message = "This is a log test"

        val logMessage = LogMessage(tag, message)
        val logFormat = "[%tag] %invalid: %message"
        val actual = FormatParser(logFormat).parse(logMessage)

        val expected = "[$tag] %invalid: $message"

        assertEquals(expected, actual)
    }

    @Test
    fun is_log_level_format_number_correct() {
        val tag = "API"
        val message = "This is a log test"
        val level = LogLevel.INFO

        val logMessage = LogMessage(tag, message, level)
        val logFormat = "[%tag] [%level{number}]: %message"
        val actual = FormatParser(logFormat).parse(logMessage)

        val expected = "[$tag] [${level.ordinal}]: $message"

        assertEquals(expected, actual)
    }

    @Test
    fun is_log_level_format_name_correct() {
        val tag = "API"
        val message = "This is a log test"
        val level = LogLevel.INFO

        val logMessage = LogMessage(tag, message, level)
        val logFormat = "[%tag] [%level{name}]: %message"
        val actual = FormatParser(logFormat).parse(logMessage)

        val expected = "[$tag] [${level.name}]: $message"

        assertEquals(expected, actual)
    }

    @Test
    fun is_class_name_correct() {
        val tag = "API"
        val message = "This is a log test"
        val level = LogLevel.INFO

        val logMessage = LogMessage(tag, message, level)
        val logFormat = "[%linenumber]: %message"
        val actual = FormatParser(logFormat).parse(logMessage)

        val expected = "[$tag] [${level.name}]: $message"

        assertEquals(expected, actual)
    }
}
