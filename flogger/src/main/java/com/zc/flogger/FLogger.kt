package com.zc.flogger

import android.content.Context
import android.util.Log
import com.zc.flogger.extensions.toFormat
import com.zc.flogger.utils.ZipManager
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Date

/**
 * Created by Zahi Chemaly on 4/25/2024.
 */
abstract class FLogger(private val context: Context) {

    open val applicationTag: String = ""
    open val logsFilePath: String = LOG_FILE_PATH
    open val maxFilesAllowed: Int = MAX_FILES_ALLOWED

    private var logHeaderLines: List<String> = emptyList()

    fun log(tag: String?, message: String) {
        logInternal(tag, message)
    }

    abstract fun buildLog(tag: String?, message: String): String

    private fun logInternal(tag: String?, message: String) {
        try {
            val logFile = getExistingLogFileOrCreate()
            writeToLogFile(logFile, buildLog(tag, message))
        } catch (ex: Exception) {
            Log.e(TAG, ex.stackTraceToString())
        }
    }

    private fun getLogPath(): String =
        "${context.externalCacheDir}/$logsFilePath"

    open fun getLogFileName(): String =
        "${applicationTag}_${Date().toFormat(LOG_FILE_DATE_FORMAT)}"

    @Throws
    private fun getExistingLogFileOrCreate(): File {
        // create log folder if it does not exist
        val logPath = getLogPath()
        val logFolder = File(logPath)
        if (!logFolder.exists()) {
            Log.d(TAG, "Create log folder $logPath")
            logFolder.mkdir()
        }

        // create log file if it does not exist
        val logFileName = getLogFileName() + ".log"
        val logFile = File(logPath, logFileName)

        if (!logFile.exists()) {
            val files = logFolder.listFiles() ?: arrayOf()
            val filesCount = files.size
            Log.d(TAG, "Log files found: $filesCount / $maxFilesAllowed")

            if (filesCount == maxFilesAllowed) {
                val oldestFile = files.minByOrNull { it.lastModified() }
                val isDeleted = oldestFile?.delete()
                if (isDeleted == true) {
                    Log.d(TAG, "Deleting log file ${oldestFile.absolutePath}")
                }
            }

            // create file
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

    fun zipLogs(): File? {
        return ZipManager.zipFolder(getLogPath(), context.externalCacheDir.toString(), getLogFileName())
    }
}
