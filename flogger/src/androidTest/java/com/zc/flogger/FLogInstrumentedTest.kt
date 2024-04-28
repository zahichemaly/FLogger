package com.zc.flogger

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Zahi Chemaly on 4/28/2024.
 */
@RunWith(AndroidJUnit4::class)
class FLogInstrumentedTest {

    @Before
    fun setupFLog() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        FLog.Configuration()
            .withConsoleLogger("%linenumber %methodname{3} %filename{10}", "%message")
            .withFileLogger(context, "[%tag] [%level{name}]: %message")
    }

    @Test
    fun is_log_correct() {
        FLog.debug("ZAHI", "This is a test message")
    }
}
