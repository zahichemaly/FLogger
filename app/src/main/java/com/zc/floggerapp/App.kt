package com.zc.floggerapp

import android.app.Application
import com.zc.flogger.FLog

/**
 * Created by Zahi Chemaly on 4/25/2024.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        FLog.Configuration()
            .withConsoleLogger("%tag(Line:%linenumber)", "[%classname] [%filename] %message")
            .withFileLogger(this, "%date{yyyy-MM-dd HH:mm:ss.SSS} [%level{name}] [%tag] {%filename;%linenumber}: %message")
        //logger.zipLogs()
        //FLog.uploadToFirebaseStorage("public")
        repeat(10) {
            FLog.debug("MYAPP", "Test log $it")
        }
    }
}
