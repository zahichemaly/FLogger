package com.zc.floggerapp

import android.app.Application
import com.zc.flogger.FLog
import com.zc.flogger.logging.console.ConsoleLoggerConfig
import com.zc.flogger.logging.file.FileLoggerConfig

/**
 * Created by Zahi Chemaly on 4/25/2024.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        FLog.Configuration()
            .withConsoleLogger(
                ConsoleLoggerConfig(
                    tagFormat = "%tag(Line:%linenumber)",
                    messageFormat = "[%classname] [%filename] %message"
                )
            )
            .withFileLogger(
                FileLoggerConfig(
                    context = this,
                    logFormat = "%date{yyyy-MM-dd HH:mm:ss.SSS} [%level{name}] [%tag] {%filename;%linenumber}: %message"
                )
            )
        repeat(10) {
            FLog.debug("MYAPP", "Test log $it")
        }
    }
}
