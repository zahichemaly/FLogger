package com.zc.floggerapp

import android.content.Context
import com.zc.flogger.FLogger
import java.util.Date

/**
 * Created by Zahi Chemaly on 4/25/2024.
 */
class AppLogger(context: Context) : FLogger(context) {
    override val applicationTag: String
        get() = "test"

    override fun buildLog(tag: String?, message: String): String {
        return "${Date()}: [$tag] ~> $message"
    }
}
