<p align="center">
  <img src=".github/assets/project_logo.svg" width="120" alt="logo" />

  <br />
  <br />

  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />

  <br />

  <img src="https://github.com/barebones/WiseSpend/actions/workflows/android.yml/badge.svg" alt="Build Status" />
  <img src="https://img.shields.io/github/v/release/barebones/WiseSpend?include_prereleases&label=Latest%20Release" alt="Latest Release" />

  <br />
  <br />

  <a href="https://github.com/barebones/WiseSpend/releases/latest/download/WiseSpend.apk">
    <img src="https://img.shields.io/badge/Download-APK-green?style=for-the-badge&logo=android" alt="Download APK" />
  </a>
</p>


# Wise Spend

Wise Spend is an Android app for tracking daily expenses and managing personal budgets.

## Features

- Record daily expenses with categories, notes, and dates.
- Organize expenses into categories like Food, Shopping, Travel, Health, and custom user-defined ones.
- Light, dark, and AMOLED themes with color schemes
- All data stored locally via Room database, nothing leaves the device.

<details>
<summary><b> Screenshots </b> </summary>

<div align="start">
  <img src=".github/screenshots/homepage.png" width="250" />
  <img src=".github/screenshots/analytics.png" width="250" />
</div>
<div align="start">
  <img src=".github/screenshots/add_expenses.png" width="250" />
  <img src=".github/screenshots/dark_light_settings.png" width="250" />
</div>
</details>

## Getting Started
### Installation
Head over to the [Releases](https://github.com/barebones/WiseSpend/releases/latest) page and 
download the latest `WiseSpend-vX.X.apk`.


## Development 
### Prerequisites

- Android Studio Koala or newer
- JDK 17
- Android SDK 24 or higher


1. Clone the repository:
   ```bash
   git clone https://github.com/barebones/WiseSpend.git
   ```
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Run the application on an emulator or a physical device.

## Project Structure

```
app/
├── data/
│   ├── local/          # Room entities, DAOs, database
│   ├── model/           # Data models
│   └── repository/      # Repository layer
├── navigation/          # Nav graph and routes
├── ui/
│   ├── components/      # Reusable composables
│   ├── screens/          # Screen-level composables
│   └── theme/            # Colors, typography, theming
├── util/                 # Helper/utility functions
├── viewmodel/            # ViewModels
└── MainActivity.kt
```


## Planned Features

- [ ] Savings goals
- [ ] View spending Day/Week/Month/Year ranges.
- [ ] Home screen widgets
- [ ] Expense search and filters
- [ ] Multiple currencies
- [ ] Data export/import
- [ ] Monthly spending insights
- [ ] Expense reminders
- [ ] Attach receipts/images to expenses


## License

This project is licensed under the GPL-2.0 license — see the [LICENSE](LICENSE) file for details.
