package com.zc.flogger

import com.zc.flogger.extensions.toFormat
import com.zc.flogger.format.FormatParser
import com.zc.flogger.models.LogMessage
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun log_format_correct() {
        val tag = "API"
        val message = "This is a log test"
        val date = Date().toFormat("dd-MM-yyyy")

        val logMessage = LogMessage(tag, message)
        val logFormat = "test_%tag %level %date{dd-MM-yyyy}: %message"
        val actual = FormatParser(logFormat).parse(logMessage)

        val expected = "test_$tag %level $date: $message"

        assertEquals(expected, actual)
    }
}
