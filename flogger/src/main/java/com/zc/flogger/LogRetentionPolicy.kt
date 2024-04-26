package com.zc.flogger

/**
 * Created by Zahi Chemaly on 4/26/2024.
 *
 * Retention policy for the log files.
 */
enum class LogRetentionPolicy {
    /**
     * Indicates that logs should be retained indefinitely.
     */
    DISABLED,

    /**
     * Indicates that logs should be retained based on the [FLogger.maxFilesAllowed] set.
     * If the threshold is reached, the oldest log is deleted.
     */
    FIXED,

    /**
     * Indicates that only the latest log should be retained. In other words, only one log file
     * will be written, which is always the latest.
     */
    LATEST_ONLY,
}
