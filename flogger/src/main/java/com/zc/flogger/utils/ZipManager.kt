package com.zc.flogger.utils

import android.util.Log
import com.zc.flogger.TAG
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Created by Zahi Chemaly on 4/25/2024.
 */
internal object ZipManager {

    private const val BUFFER_SIZE = 4096

    fun zipFolder(inputPath: String, outputPath: String, outputFileName: String): File? {
        val archiveFileName = "$outputFileName.zip"
        try {
            val archiveFile = File(outputPath, archiveFileName)

            val logsDir = File(inputPath)
            val logFiles = logsDir.listFiles()
            val logsFilesPath = ArrayList<String>()

            logFiles?.forEach { file ->
                logsFilesPath.add(file.absolutePath)
            }

            zip(logsFilesPath.toTypedArray(), archiveFile.absolutePath)
            Log.d(TAG, "Zipped log folder $inputPath to ${outputPath}/${archiveFileName}")
            return archiveFile
        } catch (ex: Exception) {
            Log.e(TAG, ex.stackTraceToString())
            return null
        }
    }

    @Throws
    private fun zip(files: Array<String>, zipFile: String) {
        FileOutputStream(zipFile).use { dest ->
            ZipOutputStream(BufferedOutputStream(dest)).use { out ->
                val data = ByteArray(BUFFER_SIZE)
                for (fileName in files) {
                    FileInputStream(fileName).use { fi ->
                        BufferedInputStream(fi, BUFFER_SIZE).use { origin ->
                            val entry = ZipEntry(fileName.substring(fileName.lastIndexOf("/") + 1))
                            out.putNextEntry(entry)
                            var count: Int
                            while (origin.read(data, 0, BUFFER_SIZE).also { count = it } != -1) {
                                out.write(data, 0, count)
                            }
                        }
                    }
                }
                Log.d(TAG, "Zipped files to $zipFile")
            }
        }
    }
}
