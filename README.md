# Quiz Pro 🎓

A modern, high-performance Android Quiz application built with **Jetpack Compose** and **Firebase**. Featuring a premium Glassmorphism UI, real-time data syncing, and a dedicated Admin Portal.

---

## ✨ Features

### 👤 User Side
- **Modern UI/UX**: Aesthetic Glassmorphism design with soft animated backgrounds.
- **Real-time Tests**: Take tests with live timers and instant score calculation.
- **Progress Tracking**: View past results and performance history.
- **Staggered Animations**: Smooth entry animations for a premium feel.

### 🔐 Admin Side
- **Secure Access**: Dedicated admin login with unique credentials.
- **Test Management**: Create, edit, publish, or delete test series.
- **Bulk Import**: Add 30+ questions in seconds using the JSON import feature.
- **Submission Monitoring**: Real-time view of all user scores and submissions.
- **Score Publishing**: Control when students see their final results.

---

## 🛠 Tech Stack

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Language**: [Kotlin](https://kotlinlang.org/)
- **Backend**: [Firebase Firestore](https://firebase.google.com/products/firestore) (Real-time Database)
- **Themes**: Always-on Light Theme for a clean, professional look.
- **Architecture**: Repository Pattern with State Management.

---

## 🚀 Getting Started

1. **Clone the project**
   ```bash
   git clone https://github.com/ujjaval01/Quiz-Pro-QA-App.git
   ```

2. **Add Firebase**
   - Create a project on [Firebase Console](https://console.firebase.google.com/).
   - Add an Android App with your package name.
   - Download `google-services.json` and place it in the `app/` directory.

3. **Configure Firestore**
   - Enable Firestore Database in Test Mode.
   - Create two collections: `testSeries` and `submissions`.

4. **Build and Run**
   - Open the project in **Android Studio (Ladybug or newer)**.
   - Sync Gradle and Run.

---

## 👨‍💻 Author

**Ujjaval**  
[GitHub](https://github.com/ujjaval01)

---

## 📜 License
This project is for educational purposes. Feel free to use and modify it!
