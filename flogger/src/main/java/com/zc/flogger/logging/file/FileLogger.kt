package com.zc.flogger.logging.file

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.google.firebase.storage.storageMetadata
import com.zc.flogger.TAG
import com.zc.flogger.format.FormatParser
import com.zc.flogger.logging.base.BaseLogger
import com.zc.flogger.models.LogMessage
import com.zc.flogger.utils.FirebaseUtils
import com.zc.flogger.utils.ZipManager
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * Created by Zahi Chemaly on 4/25/2024.
 */
internal class FileLogger(val config: FileLoggerConfig) : BaseLogger(config) {

    private var logHeaderLines: List<String> = emptyList()

    private val logFormatParser = FormatParser(config.logFormat)

    private val mainScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, ex ->
        Log.e(TAG, "Coroutine error: ${ex.stackTraceToString()}")
    }

    private val channel = Channel<LogMessage>(capacity = Channel.UNLIMITED).apply {
        mainScope.launch(coroutineExceptionHandler) {
            consumeEach { logMessage ->
                logInternal(logMessage)
            }
        }
    }

    override fun log(logMessage: LogMessage) {
        val result = channel.trySend(logMessage)
        if (!result.isSuccess) {
            Log.e(TAG, "Failed to send log message $logMessage")
        }
    }

    private fun buildLog(logMessage: LogMessage): String {
        return logFormatParser.parse(logMessage)
    }

    private fun logInternal(logMessage: LogMessage) {
        try {
            val logFile = getExistingLogFileOrCreate()
            writeToLogFile(logFile, buildLog(logMessage))
        } catch (ex: Exception) {
            Log.e(TAG, "Failed to log message $logMessage: ${ex.stackTraceToString()}")
        }
    }

    @Throws
    private fun getExistingLogFileOrCreate(): File {
        // configure log folder if it does not exist
        val logPath = config.getLogPath()
        val logFolder = File(logPath)
        if (!logFolder.exists()) {
            Log.d(TAG, "Create log folder $logPath")
            logFolder.mkdir()
        }

        // configure log file if it does not exist
        val logFileName = config.getLogFileName() + ".log"
        val logFile = File(logPath, logFileName)

        if (!logFile.exists()) {
            val files = logFolder.listFiles() ?: arrayOf()
            val filesCount = files.size
            Log.d(TAG, "Log files found: $filesCount / ${config.maxFilesAllowed}")

            if (config.fileRetentionPolicy != FileRetentionPolicy.DISABLED) {
                if (config.fileRetentionPolicy == FileRetentionPolicy.LATEST_ONLY || filesCount == config.maxFilesAllowed) {
                    val oldestFile = files.minByOrNull { it.lastModified() }
                    val isDeleted = oldestFile?.delete()
                    if (isDeleted == true) {
                        Log.d(TAG, "Deleting log file ${oldestFile.absolutePath}")
                    }
                }
            }

            // configure file
            Log.d(TAG, "Creating new log file $logFileName")
            logFile.createNewFile()

            // add custom header
            appendLogHeader(logFile)
        }
        return logFile
    }

    @Throws
    private fun appendLogHeader(file: File) {
        if (logHeaderLines.isEmpty()) {
            Log.i(TAG, "Header is empty!")
        }
        BufferedWriter(FileWriter(file, true)).use { buf ->
            logHeaderLines.forEach { line ->
                Log.d(TAG, "Added log header line: $line")
                buf.append(line)
            }
        }
    }

    @Throws(IOException::class)
    private fun writeToLogFile(file: File, message: String) {
        BufferedWriter(FileWriter(file, true)).use { buf ->
            Log.d(TAG, message)
            buf.append(message)
            buf.newLine()
        }
    }
}
