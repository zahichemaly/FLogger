package com.zc.flogger.logging.base

/**
 * Created by Zahi Chemaly on 5/2/2024.
 *
 * Template to create a [Logger] implementation using a [LoggerConfig].
 */
internal abstract class BaseLogger(private val loggerConfig: LoggerConfig) : Logger
