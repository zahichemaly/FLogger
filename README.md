# FLogger ✍️

Write logs to a file on Android, and upload them to [Firebase Cloud Storage](https://firebase.google.com/docs/storage)!

### How it works
- Logs are written to Android's **external storage cache folder**.
- An additional folder can be created in the cache folder (such as `logs`).
- Logs are grouped by **date**, such as `mylog_01-01-2024.log`. In case the log file already exists, subsequent logs will be appended to the file. Otherwise, a new file is created.

### Coming 🔜
- Option to set up FLogger directly with [Timber](https://github.com/JakeWharton/timber).
- Option to group log files and customize log messages based on the context, such as API requests/responses grouped in an `api` folder etc.

### Features
✅ Log message customization.

✅ Log file header customization (useful in case we want to log device/user info at the beginning of the file).

✅ Logs can be zipped and upload to Firebase Cloud Storage, if configured.
