package com.zc.flogger.logging

import com.zc.flogger.models.LogLevel

/**
 * Created by Zahi Chemaly on 4/28/2024.
 */
internal interface Logger {
    fun log(tag: String, message: String, logLevel: LogLevel)
}