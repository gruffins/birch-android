<p align="center">
<img src="https://user-images.githubusercontent.com/381273/204187386-ec93e173-a6fa-40b1-8b74-c52a0c5048b3.png" />
</p>

# Birch
![Tests](https://github.com/gruffins/birch-android/actions/workflows/tests.yml/badge.svg)
![Release](https://jitpack.io/v/com.gruffins/birch-android.svg)
[![codecov](https://codecov.io/gh/gruffins/birch-android/branch/main/graph/badge.svg?token=CZ0HQ8VI6T)](https://codecov.io/gh/gruffins/birch-android)

Simple, lightweight remote logging for Android.

Sign up for your free account at [Birch](https://birch.ryanfung.com).

Birch allows you to log to a variety of drains regardless of whether they have a native implementation or not. On top of that, Birch provides the ability to remotely adjust log configurations on any of your apps in production.

Birch can drain to
- New Relic
- Datadog
- Logtail
- Loggly
- Elasticsearch
- Papertrail
- Logz
- CloudWatch
- A custom webhook

# Installation

Add jitpack to your project build.gradle.
```
allprojects {
    repositories {
        ...
        maven { url 'https://www.jitpack.io' }
    }
}
```

Add birch to your module build.gradle.
```
implementation 'com.gruffins.birch-android:birch:1.3.0'
implementation 'com.gruffins.birch-android:birch-timber:1.3.0' // (optional Tree to plug into Timber)
```

# Setup

In your application class, initialize the logger.
```kotlin
class MyApp: Application() {

  override fun onCreate() {
    super.onCreate()

    Birch.init(this, "YOUR_API_KEY", "YOUR_PUBLIC_ENCRYPTION_KEY")
    Birch.debug = true // this should be turned off in a production build. Debug mode allows you to see Birch operating and artificially lowers the log level and flush period.
    Birch.identifier = "your_user_id" // this is optional but highly recommended
  }
}
```

# Logging
Use the logger as you would with the default Android logger.

```kotlin
Birch.t("trace message") // simplest
Birch.t { "trace message" } // most performant especially if it's expensive to build the log message.

Birch.d("debug message")
Birch.d { "debug message" }

Birch.i("info message")
Birch.i { "info message" }

Birch.w("warn message")
Birch.w { "warn message" }

Birch.e("error message")
Birch.e { "error message" }
```

Block based logging is more performant since the blocks do not get executed unless the current log level includes the level of the log. See the following example:

```kotlin
Birch.d {
  var message = "hello"
  repeat(10000) { message = message + "hello" }
  return message
}
```

If the current log level is `INFO`, the log will not get constructed.

# Configuration
Device level configuration is left to the server so you can remotely control it. There are a few things you can control on the client side.

### Debugging
Debug mode will lower the log level to `TRACE` and set the upload period to every 30 seconds. You should turn this __OFF__ in a production build otherwise you will not be able to modify the log settings remotely.
```kotlin
Birch.debug = true
```

### Default Configuration

The default configuration is `ERROR` and log flushing every hour. This means any logs lower than `ERROR` are skipped and logs will only be delivered once an hour to preserve battery life. You can change these settings on a per source level by visiting your Birch dashboard.

### Encryption

We **HIGHLY** recommend using encryption to encrypt your logs at rest. If you leave out the public encryption key, Birch will save logs on the device in clear text.

An invalid public key will throw an exception.

To learn more, see our [Encryption](https://github.com/gruffins/birch-android/wiki/Encryption) documentation.

# Identification
You should set an identifier so you can identify the source in the dashboard. If you do not set one, you will only be able to find devices by the assigned uuid via `Birch.uuid`.

You can also set custom properties on the source that will propagate to all drains.

```kotlin
fun onLogin(user: User) {
  Birch.identifier = user.id
  Birch.customProperties = mapOf("country" to user.country)
}
```

# Opt Out

To comply with different sets of regulations such as GDPR or CCPA, you may be required to allow users to opt out of log collection.

```kotlin
Birch.optOut = true
```

Your application is responsible for changing this and setting it to the correct value at launch. Birch will not remember the last setting and it defaults to `false`.

# Log Scrubbing

Birch comes preconfigured with an email and password scrubber to ensure sensitive data is __NOT__ logged. Emails and passwords are replaced with `[FILTERED]` at the logger level so the data never reaches Birch servers.

If you wish to configure additional scrubbers, implement the `Scrubber` interface and initialize the logger with all the scrubbers you want to use.

```kotlin
import com.gruffins.birch.Scrubber

class YourScrubber: Scrubber {
    override fun scrub(input: String): String {
        return input.replace("your_regex".toRegex(), "[FILTERED]")
    }
}
```

```kotlin
Birch.init(this, "API_KEY", "YOUR_PUBLIC_ENCRYPTION_KEY", listOf(PasswordScrubber(), EmailScrubber(), YourScrubber()))
```

# Timber
You can use the supplied tree if you want to send your logs from Timber to Birch.

```kotlin
Timber.plant(BirchTree())
```
