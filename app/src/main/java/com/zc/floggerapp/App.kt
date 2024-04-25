package com.zc.floggerapp

import android.app.Application
import com.zc.flogger.FLog

/**
 * Created by Zahi Chemaly on 4/25/2024.
 */
class App : Application() {

    companion object {
        private const val TAG = "MyApp"
    }

    override fun onCreate() {
        super.onCreate()
        val logger = AppLogger(this)
        FLog.setup(logger)
        repeat(100) { index ->
            FLog.log(TAG, "Hello from the other side $index")
        }
        //logger.zipLogs()
        //FLog.uploadToFirebaseStorage("public")
    }
}
