package com.zc.flogger

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.google.firebase.storage.storageMetadata
import com.zc.flogger.logging.base.Logger
import com.zc.flogger.logging.console.ConsoleLogger
import com.zc.flogger.logging.console.ConsoleLoggerConfig
import com.zc.flogger.logging.file.FileLogger
import com.zc.flogger.logging.file.FileLoggerConfig
import com.zc.flogger.models.LogLevel
import com.zc.flogger.models.LogMessage
import com.zc.flogger.utils.FirebaseUtils
import com.zc.flogger.utils.ZipManager
import java.io.File
import java.io.IOException

/**
 * Created by Zahi Chemaly on 4/25/2024.
 */
object FLog {
    private val loggers = mutableListOf<Logger>()

    class Configuration {

        fun withFileLogger(config: FileLoggerConfig): Configuration {
            loggers.add(FileLogger(config))
            return this
        }

        fun withConsoleLogger(config: ConsoleLoggerConfig): Configuration {
            loggers.add(ConsoleLogger(config))
            return this
        }
    }

    private fun getElementIndex(stackTrace: Array<StackTraceElement>?): Int {
        if (stackTrace == null) return 0
        for (i in 2..stackTrace.size) {
            val className = stackTrace[i].className ?: ""
            if (className.contains(this.javaClass.simpleName)) continue
            return i
        }
        return 0
    }

    private fun log(tag: String, message: String, logLevel: LogLevel) {
        val thread = Thread.currentThread()
        val stackTrace = thread.stackTrace

        val elementIndex: Int = getElementIndex(stackTrace)
        if (elementIndex == 0) return

        val logMessage = LogMessage(
            tag = tag,
            message = message,
            thread = thread,
            level = logLevel,
            mainStackTraceElement = stackTrace[elementIndex]
        )

        loggers.forEach { logger -> logger.log(logMessage) }
    }

    /**
     * Create a zipped file of the current logs located at [FileLoggerConfig.logsFilePath]
     * in the external cache folder.
     */
    fun zipLogs(): File? {
        return loggers.filterIsInstance<FileLogger>().firstOrNull()?.config?.run {
            return ZipManager.zipFolder(
                getLogPath(),
                context.externalCacheDir.toString(),
                getLogFileName()
            )
        }
    }

    /**
     * Zips and uploads the zipped file to Firebase Cloud Storage,
     * in the [firebaseFolder] container.
     */
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

    fun verb(tag: String, message: String) = log(tag, message, LogLevel.VERBOSE)

    fun debug(tag: String, message: String) = log(tag, message, LogLevel.DEBUG)

    fun info(tag: String, message: String) = log(tag, message, LogLevel.INFO)

    fun warn(tag: String, message: String) = log(tag, message, LogLevel.WARNING)

    fun error(tag: String, message: String) = log(tag, message, LogLevel.ERROR)

    fun fatal(tag: String, message: String) = log(tag, message, LogLevel.FATAL)
}
