# Loomi (Android)

Native Android version of Loomi, built with Kotlin + Jetpack Compose.

There's also a web version at https://loomi-pied.vercel.app/  
Source: https://github.com/unitreign/loomi-web

This repository is public so anyone can inspect the code, review security, and learn from the implementation.

---

## ✨ Features

- Fully native Android UI (no WebView wrapper)
- Jetpack Compose screens + dialogs
- MVVM state management
- Streaming radio + ambience mixer + equalizer
- Theme system, sleep timer, and stats tracking

---

## 🛠 Tech Stack

- Kotlin
- Jetpack Compose
- AndroidX Navigation
- Coroutines + StateFlow
- ExoPlayer (Media3)
- Coil
- DataStore

---

## 🚀 Getting Started

### Requirements

- Android Studio (latest stable recommended)
- Android SDK 36
- JDK 17+

### Run

1. Open this `loomi` folder in Android Studio
2. Wait for Gradle sync to complete
3. Run the `app` module on an emulator or physical device

If prompted for SDK path, configure it once and re-sync.

---

## 📁 Project Structure

- `app/` — Android app module  
- `app/src/main/java/` — Source code  
- `app/src/main/res/` — Resources (fonts, audio, drawables, etc.)  
- `gradle/`, `gradlew*` — Gradle wrapper  

---

## 📜 License

This project is licensed under the GNU General Public License v3.0.

You are free to:
- Use, study, and modify the code
- Distribute your own versions
- Use it commercially

Under the following conditions:
- You must provide source code for any distributed version
- You must keep the same GPL license
- You must include proper attribution to the original author

Full license text: [LICENSE](./LICENSE)

---

## ⚠️ Note on Usage

This project is open source under the GPL v3.0. You are welcome to use it in any way permitted by the license.

If you build something on top of it, a credit back to the original project is appreciated.

---

## ☕ Support

If you found this project useful:

- https://buymeacoffee.com/YOURNAME
- https://ko-fi.com/YOURNAME

---

## ❤️ Credits

Created by Reign

If you use or build upon this project, a credit back to the original repository is appreciated.