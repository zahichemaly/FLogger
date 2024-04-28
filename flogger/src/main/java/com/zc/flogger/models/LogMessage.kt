package com.zc.flogger.models

/**
 * Created by Zahi Chemaly on 4/26/2024.
 */
internal data class LogMessage(
    val tag: String,
    val message: String,
    val level: LogLevel = LogLevel.DEBUG,
    val thread: Thread = Thread.currentThread(),
    val activeStackTraceElementIndex: Int = -1,
) {
    val activeStackTraceElement: StackTraceElement get() = thread.stackTrace[activeStackTraceElementIndex]
}
