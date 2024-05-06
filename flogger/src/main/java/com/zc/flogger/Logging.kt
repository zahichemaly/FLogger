package com.zc.flogger

/**
 * Created by Zahi Chemaly on 4/27/2024.
 */
internal interface Logging {
    fun verb(tag: String, message: String)
    fun debug(tag: String, message: String)
    fun info(tag: String, message: String)
    fun warn(tag: String, message: String)
    fun error(tag: String, message: String)
    fun fatal(tag: String, message: String)
}

internal interface LoggingMinimal {
    fun verb(message: String)
    fun debug(message: String)
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String)
    fun fatal(message: String)
}
