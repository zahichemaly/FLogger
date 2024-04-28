package com.zc.flogger.logging

import android.util.Log
import com.zc.flogger.models.LogLevel

/**
 * Created by Zahi Chemaly on 4/28/2024.
 */
internal class ConsoleLogger : BaseLogger() {

    override fun handle(tag: String, message: String, logLevel: LogLevel) {
        when (logLevel) {
            LogLevel.INFO -> Log.i(tag, message)
            LogLevel.VERBOSE -> Log.v(tag, message)
            LogLevel.DEBUG -> Log.d(tag, message)
            LogLevel.WARNING -> Log.w(tag, message)
            LogLevel.ERROR -> Log.e(tag, message)
            LogLevel.FATAL -> Log.wtf(tag, message)
        }
    }
}
