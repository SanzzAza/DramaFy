# DramaFy

A modern, dark-themed Android app for short-form vertical dramas — built with **Kotlin** and **Jetpack Compose**.

## ✨ Features

- 🎬 **Home feed** — banner carousel + curated rows of trending & recommended series
- 🔎 **Search** — debounced keyword search with live suggestions and pagination
- 📖 **Detail page** — synopsis, tags, stats, episode list, related series, bookmark toggle
- ▶️ **Player** — ExoPlayer (Media3) with multi-quality selector and HLS/MP4 support
- 💜 **Bookmarks** — local persistence via Room
- 🌐 **Multi-language** — language switcher backed by `/v1/languages` API
- 🌓 **Theme** — dark-first, custom Material 3 color scheme (crimson / magenta accent)
- 🪟 **Edge-to-edge** with splash screen

## 🛠 Tech Stack

| Layer        | Library                              |
|--------------|--------------------------------------|
| UI           | Jetpack Compose · Material 3         |
| Navigation   | Navigation-Compose                    |
| DI           | Hilt                                  |
| Networking   | Retrofit · OkHttp · kotlinx.serialization |
| Persistence  | Room · DataStore Preferences         |
| Image        | Coil                                  |
| Media        | AndroidX Media3 (ExoPlayer)          |
| Build        | Gradle 8.9 · AGP 8.5 · Kotlin 2.0    |

## 🏗 Build

The project includes a GitHub Actions workflow (`.github/workflows/android-build.yml`) that builds:

- `app-debug.apk`
- `app-release-unsigned.apk`
- `app-release.aab`

Artifacts are attached to every workflow run.

### Local build

```bash
./gradlew assembleDebug
./gradlew bundleRelease
```

## ⚙️ Configuration

The Melolo API token lives in `app/src/main/java/com/sanzzaza/dramafy/di/NetworkModule.kt` — replace it with your own token before publishing.

## 📂 Project structure

```
app/src/main/java/com/sanzzaza/dramafy/
├── data/
│   ├── api/        # Retrofit service, AuthInterceptor
│   ├── model/      # DTOs
│   ├── local/      # Room + DataStore
│   └── repository/ # DramaRepository
├── di/             # Hilt modules
├── ui/
│   ├── component/  # Reusable composables
│   ├── navigation/ # Routes
│   ├── screen/     # Home, Search, Detail, Player, Bookmarks, Language
│   └── theme/      # Color, Type, Theme
└── util/           # Formatters
```

## 📄 License

MIT
