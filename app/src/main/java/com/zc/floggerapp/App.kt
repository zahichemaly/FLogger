package com.zc.floggerapp

import android.app.Application
import com.zc.flogger.FLog
import com.zc.flogger.LogRetentionPolicy

/**
 * Created by Zahi Chemaly on 4/25/2024.
 */
class App : Application() {

    companion object {
        private const val TAG = "MyApp"
    }

    override fun onCreate() {
        super.onCreate()
        FLog.configure(this)
            .setApplicationTag("testo")
            .setLogsPath("mylogs")
            .setRetentionPolicy(LogRetentionPolicy.FIXED)
            .setLogsRetentionThreshold(10)
        repeat(100) { index ->
            FLog.log(TAG, "Hello from the other side $index")
        }
        //logger.zipLogs()
        //FLog.uploadToFirebaseStorage("public")
    }
}
