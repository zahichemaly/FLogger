package com.zc.flogger

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.google.firebase.storage.storageMetadata
import com.zc.flogger.utils.FirebaseUtils
import java.io.IOException

/**
 * Created by Zahi Chemaly on 4/25/2024.
 */
object FLog {
    private lateinit var _fLogger: Lazy<FLogger>
    private val fLogger get() = _fLogger.value

    fun setup(fLogger: FLogger) {
        _fLogger = lazy { fLogger }
    }

    /***
     * Make sure that [FLogger] is initialized first!
     */
    fun log(tag: String?, message: String) {
        fLogger.log(tag, message)
    }

    fun uploadToFirebaseStorage(
        firebaseFolder: String,
        onLoading: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {},
    ) {
        if (!FirebaseUtils.isFirebaseInitialized()) return
        fLogger.zipLogs()?.let { file ->
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
}
