# FLogger ✍️

Write logs to a file on Android, and upload them to Firebase Storage!

### How it works
- Logs are written to Android's **external storage cache folder**.
- An additonal folder can be created in the cache folder (such as `logs`).
- Logs are grouped by **date**, such as `mylog_01-01-2024.log`. In case the log file already exists, subsequent logs will be appended to the file. Otherwise, a new file is created.

### Features
✅ Log message customization.

✅ Log file header customization (useful in case we want to log device/user info at the beginning of the file).

✅ Logs can be zipped and upload to Firebase Storage, if configured.
