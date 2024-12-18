# FLogger ✍️

Write logs to a file on Android, and upload them to [Firebase Cloud Storage](https://firebase.google.com/docs/storage)!

![Screenshot_2024-04-26_121150-removebg-preview2](https://github.com/zahichemaly/FLogger/assets/41119320/40b58d51-f1c3-4171-8850-2a3c8209c04e)
## How it works
- Logs are written to Android's **external storage cache folder**.
- An additional folder can be created in the cache folder (such as `logs`).
- Logs are grouped by **date**, such as `mylog_01-01-2024.log`. In case the log file already exists, subsequent logs will be appended to the file. Otherwise, a new file is created.

## Coming 🔜
- Option to group log files and customize log messages based on the context, such as API requests/responses grouped in an `api` folder etc.
- Encrypt logs before writing them

## Features
✅ Log message customization.

✅ Log file header customization (useful in case we want to log device/user info at the beginning of the file).

✅ Logs can be zipped and upload to Firebase Cloud Storage, if configured.

## How to Set Up

1. Add the following repo to your `build.gradle`:
   
   `implementation("io.github.zahichemaly:1.0.0")`

2. In your Application class, you can configure FLogger by defining:
   
   * Type of Logger (Console, File or both)
   * Log format (refer to [Log Formats](#log-formats) section)
  
   and then start logging using `FLog.debug`, `FLog.warn` etc...

### Console Logger (logcat)
Example:
   ```
    FLog.Configuration()
        .withConsoleLogger(
            ConsoleLoggerConfig(
                tagFormat = "%tag(Line:%linenumber)",
                messageFormat = "[%classname{simple}] [%filename] %message"
            )
        )
   ```

This will output the following logs to the console:

*`MyTag(Line:96) [MyClass] [MyClass.kt] This is a log message`*

### File Logger
Example:
```
        .withFileLogger(
            FileLoggerConfig(
                context = this,
                logFormat = "%date{yyyy-MM-dd HH:mm:ss.SSS} [%level{name}] [%tag] {%filename;%linenumber}: %message"
            )
        )
```

This will write the following logs to a file:

*`2024-01-01 10:15:06.112 [DEBUG] [MYTAG] {MyClass.kt;96}: This is a log message`*

### Advanced File Logger Configuration

`FileLoggerConfig` provides advanced configuration for your File Logger, such as:

- `fileTag`: Tag that is prepended to the log file. Useful to separate files based on modules.
- `logsFilePath`: Folder name that should be created in the `cache` directory in the external storage. Useful to separate files based on modules.
- `fileRetentionPolicy`: Defines the retention policy for the logs. Enabled by default. Can be disabled, or set to fixed which will always keep a single log file at a time.
- `maxFilesAllowed`: If file retention policy is enabled, retain the files based on the provided amount. For example, if the value is 50, then when the 51th log file needs to be created, the oldest one will be deleted beforehand.

### Configure multiple loggers:
Example:
```
FLog.Configuration()
  .withConsoleLogger(...)
  .withFileLogger(...)
```

### Log Formats
| Format  | Description |
|---|---|
|`%tag`|Logs the tag provided as argument when using `FLog.debug("MyTag",...)`.|
|`%message`|Logs the message provided as argument when using `FLog.debug(...,"My message")`|
|`%date{date_format}`|Logs the current date, formatted using the specified format.<br>Defaults to `dd-MM-yyyy-HH:mm:ss` if not provided/invalid|
|`%level{type}`|Logs the log level using the provided level type:<br>- `number` (0 = Verbose, 1 = Debug...)<br>- `letter` (V, D, W, E...)<br>- `name` (DEBUG, INFO...).<br>Defaults to `number` if not provided/invalid.|
|`%thread{type}`|Logs info about the thread.<br>- `id` logs the thread ID.<br>- `name` logs the thread name.<br>Defaults to `id` if not provided/invalid.
|`%classname{type}`|Logs the classname.<br>- `full` logs the full class name, including the package.<br>- `simple` only logs the class name.<br>Defaults to `full`.
|`%filename{wrap_limit}`|Logs the file name. If wrap limit is provided, truncate the string based on the value.|
|`%methodname{wrap_limit}`|Logs the method name. If wrap limit is provided, truncate the string based on the value.|
|`%linenumber`|Logs the line number of the `FLog` function call.|

### Archive the logs

The logs files can be archived using:

`FLog.zip()`. 

The zipped file can be retrieved in the `cache` folder, in the external storage of the application.

### Archive and upload to Firebase

The logs can be archived and uploaded to Firebase Cloud Storage in one go using:

`FLog.uploadToFirebaseStorage("mycontainer", onSuccess = { }, onError = { })`

⚠️ Make sure to configure Firebase in your project first!

## License
```
Copyright 2024 Zahi Chemaly

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
