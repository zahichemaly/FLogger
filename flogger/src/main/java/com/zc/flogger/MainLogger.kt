package com.zc.flogger

import android.util.Log
import com.zc.flogger.MainLogger.log
import java.io.PrintWriter
import java.io.StringWriter


/**
 * Created by Zahi Chemaly on 4/27/2024.
 */
internal object MainLogger {

    private fun getElementIndex(stackTrace: Array<StackTraceElement>?): Int {
        if (stackTrace == null) return 0
        for (i in 2..stackTrace.size) {
            val className = stackTrace[i].className ?: ""
            if (className.contains(this.javaClass.simpleName)) continue
            return i
        }
        return 0
    }

    private fun addExceptionIfNotNull(t: Throwable?, result: StringBuilder) {
        if (t != null) {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            t.printStackTrace(pw)
            pw.flush()
            result.append("\n Throwable: ")
            result.append(sw.toString())
        }
    }

    internal fun log(message: String?) {
        val result = StringBuilder()

        val thread = Thread.currentThread()
        val threadName = thread.name
        val threadId = thread.id
        val stackTrace = thread.stackTrace

        val elementIndex: Int = getElementIndex(stackTrace)
        if (elementIndex == 0) return

        val stackTraceElement = stackTrace[elementIndex]
        val fullClassName = stackTraceElement.className
        val className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
        val methodName = stackTraceElement.methodName
        val lineNumber = stackTraceElement.lineNumber
        Log.d("$className.$methodName():$lineNumber", message!!)
    }
}

fun debug(tag: String, message: String) {
    log(message)
}

fun debug(message: String) {
    log(message)
}
