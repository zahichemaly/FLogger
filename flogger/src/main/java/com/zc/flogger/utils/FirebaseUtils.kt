package com.zc.flogger.utils

import android.util.Log
import com.google.firebase.FirebaseApp
import com.zc.flogger.TAG

/**
 * Created by Zahi Chemaly on 4/25/2024.
 */
object FirebaseUtils {

    fun isFirebaseInitialized(): Boolean {
        return try {
            FirebaseApp.getInstance()
            true
        } catch (ex: IllegalStateException) {
            Log.e(TAG, "FirebaseApp not initialized!")
            false
        }
    }
}
