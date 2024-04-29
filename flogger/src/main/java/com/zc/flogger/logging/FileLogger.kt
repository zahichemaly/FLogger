package com.zc.flogger.logging

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.google.firebase.storage.storageMetadata
import com.zc.flogger.TAG
import com.zc.flogger.extensions.toFormat
import com.zc.flogger.format.FormatParser
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
import java.util.Date

/**
 * Created by Zahi Chemaly on 4/25/2024.
 */
internal class FileLogger(
    private val context: Context,
    logFormat: String = DEFAULT_LOG_FORMAT,
) : Logger {

    private var fileTag: String = DEFAULT_FILE_TAG
    private var logsFilePath: String = LOGS_FILE_PATH
    private var fileRetentionPolicy: FileRetentionPolicy = FileRetentionPolicy.FIXED
    private var maxFilesAllowed: Int = MAX_FILES_ALLOWED

    private var logHeaderLines: List<String> = emptyList()

    private val logFormatParser = FormatParser(logFormat)

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

    private fun getLogPath(): String =
        "${context.externalCacheDir}/$logsFilePath"

    private fun getLogFileName(): String =
        "${fileTag}_${Date().toFormat(DEFAULT_FILE_DATE_FORMAT)}"

    @Throws
    private fun getExistingLogFileOrCreate(): File {
        // configure log folder if it does not exist
        val logPath = getLogPath()
        val logFolder = File(logPath)
        if (!logFolder.exists()) {
            Log.d(TAG, "Create log folder $logPath")
            logFolder.mkdir()
        }

        // configure log file if it does not exist
        val logFileName = getLogFileName() + ".log"
        val logFile = File(logPath, logFileName)

        if (!logFile.exists()) {
            val files = logFolder.listFiles() ?: arrayOf()
            val filesCount = files.size
            Log.d(TAG, "Log files found: $filesCount / $maxFilesAllowed")

            if (fileRetentionPolicy != FileRetentionPolicy.DISABLED) {
                if (fileRetentionPolicy == FileRetentionPolicy.LATEST_ONLY || filesCount == maxFilesAllowed) {
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

    fun zipLogs(): File? {
        return ZipManager.zipFolder(getLogPath(), context.externalCacheDir.toString(), getLogFileName())
    }

    fun uploadToFirebaseStorage(
        firebaseFolder: String,
        onLoading: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {},
    ) {
        if (!FirebaseUtils.isFirebaseInitialized()) return
        zipLogs()?.let { file ->
            val storage = Firebase.storage
            val storageRef = storage.reference
            val fileName = file.name
            val fileRef = storageRef.child("$firebaseFolder/$fileName")
            val metadata = storageMetadata {
                contentType = "application/zip"
            }
            file.inputStream().use { inputStream ->
                onLoading.invoke()
                val task = fileRef.putStream(inputStream, metadata)
                task.addOnSuccessListener {
                    Log.d(TAG, "Upload successful")
                    onSuccess.invoke()
                }.addOnFailureListener { ex ->
                    Log.e(TAG, "Upload successful")
                    onError.invoke(ex)
                }
            }
        } ?: run {
            onError.invoke(IOException("Zip file is invalid or does not exist."))
        }
    }

    companion object {
        private const val DEFAULT_FILE_TAG = "FLogger_"
        private const val DEFAULT_FILE_DATE_FORMAT = "yyyy-MM-dd"
        private const val DEFAULT_LOG_FORMAT = "%date{yyyy-MM-dd HH:mm:ss.SSS} [%level] [%tag]: %message"

        private const val LOGS_FILE_PATH = "logs"
        private const val MAX_FILES_ALLOWED = 10
    }
}
