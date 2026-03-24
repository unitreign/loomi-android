# Loomi (Android)

Native Android version of Loomi, built with Kotlin + Jetpack Compose.

There's also a web version at [loomi-pied.vercel.app](https://loomi-pied.vercel.app/) ([source code](https://github.com/unitreign/loomi-web)).

This repo is public so people can inspect the code, review security, and learn from the implementation.

## What This Project Includes

- Fully native Android UI (no WebView wrapper)
- Jetpack Compose screens + dialogs
- MVVM state management
- Streaming radio + ambience mixer + equalizer
- Theme system, timer, and stats tracking

## Tech Stack

- Kotlin
- Jetpack Compose
- AndroidX Navigation
- Coroutines + StateFlow
- ExoPlayer (Media3)
- Coil
- DataStore

## Getting Started

### Requirements

- Android Studio (recent stable)
- Android SDK 36
- JDK 17+

### Run

1. Open this `published-ready` folder in Android Studio.
2. Wait for Gradle sync to finish.
3. Run the `app` module on an emulator or device.

If Android Studio asks for SDK path, set it once and sync again.

## Project Structure

- `app/` - Android app module
- `app/src/main/java/` - app source code
- `app/src/main/res/` - Android resources (fonts, audio, drawables, etc.)
- `gradle/` + `gradlew*` - Gradle wrapper

## License

You can use this for personal stuff, learn from it, modify it however you want. Just don't use it commercially without asking first.

See [LICENSE](./LICENSE) for the full text.