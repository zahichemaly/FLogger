package com.zc.flogger.logging.file

import android.content.Context
import com.zc.flogger.extensions.toFormat
import com.zc.flogger.logging.base.LoggerConfig
import java.util.Date

/**
 * Created by Zahi Chemaly on 5/2/2024.
 */
class FileLoggerConfig(
    val context: Context,
    val logFormat: String = DEFAULT_LOG_FORMAT,
    val fileTag: String = DEFAULT_FILE_TAG,
    val logsFilePath: String = LOGS_FILE_PATH,
    val fileRetentionPolicy: FileRetentionPolicy = FileRetentionPolicy.FIXED,
    val maxFilesAllowed: Int = MAX_FILES_ALLOWED,
) : LoggerConfig {

    fun getLogPath(): String =
        "${context.externalCacheDir}/$logsFilePath"

    fun getLogFileName(): String =
        "${fileTag}_${Date().toFormat(DEFAULT_FILE_DATE_FORMAT)}"

    companion object {
        private const val DEFAULT_FILE_TAG = "FLogger_"
        private const val DEFAULT_FILE_DATE_FORMAT = "yyyy-MM-dd"
        private const val DEFAULT_LOG_FORMAT = "%date{yyyy-MM-dd HH:mm:ss.SSS} [%level] [%tag]: %message"

        private const val LOGS_FILE_PATH = "logs"
        private const val MAX_FILES_ALLOWED = 10
    }
}
