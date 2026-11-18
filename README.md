<p align="center">
  <img src="https://drive.google.com/thumbnail?id=1dgpFaAyLYLOSdQHRDDz3mNCNywYWgU9g&sz=w800" alt="MEDIFY icon" width="120"/>
</p>

<h1 align="center">MEDIFY</h1>

<p align="center">
  An Android healthcare app that connects patients, doctors and hospitals,<br/>
  with appointment scheduling, prescriptions, blood donation and Gemini-powered AI features.
</p>

<p align="center">
  <img src="https://drive.google.com/thumbnail?id=1wLXUDvLwQTLeZbNf3HMUAyg_LwBTP3zQ&sz=w800" alt="MEDIFY feature graphic" width="700"/>
</p>

---

## Overview

MEDIFY is a three-portal Android application built with **Java and XML**. It brings the everyday parts of healthcare into one app: finding doctors and hospitals, booking and tracking appointments, keeping prescriptions in one place, and coordinating blood donations.

| Portal | What it is for |
|--------|----------------|
| **Patient** | Browse doctors and hospitals, book appointments, view prescriptions, request or respond to blood donations, chat |
| **Doctor** | Manage a profile and schedule, handle appointments, issue prescriptions |
| **Admin** | Oversee users, doctors and hospitals on the platform |

## Features

- **Authentication:** register, log in, forgot password and reset password flows
- **Doctors:** browse all doctors, filter, and open detailed doctor profiles
- **Hospitals:** browse all hospitals and view hospital profiles
- **Schedule:** view and manage upcoming appointments
- **Prescriptions:** access prescriptions in one place
- **Blood donation:** see donation listings and create or respond to donation requests
- **Chat:** in-app messaging / assistant, integrated with the **Gemini API**
- **Settings:** manage account and app preferences

## Screenshots

### Getting started

<table>
  <tr>
    <td align="center"><img src="https://drive.google.com/thumbnail?id=1kx_5AGkTC4lVKqg_0NsUcuDAkcx0kyxy&sz=w800" width="200"/><br/><b>Login</b></td>
    <td align="center"><img src="https://drive.google.com/thumbnail?id=1atleDnYzIsQckyIB2fQ145TrdH56d8cQ&sz=w800" width="200"/><br/><b>Register</b></td>
    <td align="center"><img src="https://drive.google.com/thumbnail?id=1LbFJWANTFbC5IY4OGeKHQtOPys6CEk8m&sz=w800" width="200"/><br/><b>Forgot password</b></td>
    <td align="center"><img src="https://drive.google.com/thumbnail?id=1n_qjiCTqHtOC4Cdo9BoFOULIgDHxlzJx&sz=w800" width="200"/><br/><b>Reset password</b></td>
  </tr>
</table>

### Home and discovery

<table>
  <tr>
    <td align="center"><img src="https://drive.google.com/thumbnail?id=1lmfJYa11OtD7cOV6_DC3XWQPeLg4k3wi&sz=w800" width="200"/><br/><b>Home</b></td>
    <td align="center"><img src="https://drive.google.com/thumbnail?id=1DMCGlJF7w8uYDqmcOaBaMAo24XD8QCCm&sz=w800" width="200"/><br/><b>Doctors</b></td>
    <td align="center"><img src="https://drive.google.com/thumbnail?id=1yXSraZaTmyVDLuvI33JhM6D31ogjK1Pc&sz=w800" width="200"/><br/><b>All doctors</b></td>
    <td align="center"><img src="https://drive.google.com/thumbnail?id=1gST8s7DBH7i4Nm_hs3a9Nv6BuKGvezHM&sz=w800" width="200"/><br/><b>Doctor profile</b></td>
  </tr>
  <tr>
    <td align="center"><img src="https://drive.google.com/thumbnail?id=1YsfgbDgK8K5y9InjYhY2WapjnxpfsnJw&sz=w800" width="200"/><br/><b>All hospitals</b></td>
    <td align="center"><img src="https://drive.google.com/thumbnail?id=1yRBTgCKIIZd8mEGt1fkV-66GbYy1118a&sz=w800" width="200"/><br/><b>Hospital profile</b></td>
  </tr>
</table>

### Care and community

<table>
  <tr>
    <td align="center"><img src="https://drive.google.com/thumbnail?id=1L7k8kiqMqBbHjUD-6cFV4-ntRXt9pRph&sz=w800" width="200"/><br/><b>Schedule</b></td>
    <td align="center"><img src="https://drive.google.com/thumbnail?id=1AHvlMDytvS0Su5tvXFoHk8YbV-_6gFQ_&sz=w800" width="200"/><br/><b>Prescriptions</b></td>
    <td align="center"><img src="https://drive.google.com/thumbnail?id=1vRbUl-aU6AWaL0km_8Pc-yI7TcCmn4E7&sz=w800" width="200"/><br/><b>Blood donations</b></td>
    <td align="center"><img src="https://drive.google.com/thumbnail?id=1tRcKIsLjJRNRWT0O9k8kbTYXw9xqXoSe&sz=w800" width="200"/><br/><b>Donation requests</b></td>
  </tr>
  <tr>
    <td align="center"><img src="https://drive.google.com/thumbnail?id=1bEP1Jt_sr-Eqw-Mhi8XD7URMR-DwXSEK&sz=w800" width="200"/><br/><b>Chat</b></td>
    <td align="center"><img src="https://drive.google.com/thumbnail?id=1rFlyE7XtoSRKUrAijf-HsowEwbnhQr8p&sz=w800" width="200"/><br/><b>Settings</b></td>
  </tr>
</table>

## Tech Stack

| Area | Technology |
|------|------------|
| Language | Java |
| UI | XML layouts, Material Components |
| Platform | Android (Android Studio, Gradle) |
| AI | Google Gemini API |
| Backend / database | <!-- TODO: e.g. Firebase Auth + Firestore, or your backend --> |

## Getting Started

### Prerequisites

- Android Studio (latest stable)
- JDK 17
- An Android device or emulator (API 24+)
- A Gemini API key from [Google AI Studio](https://aistudio.google.com/)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/FatimaMazhar0/<repo-name>.git
   cd <repo-name>
   ```

2. **Add your API key.** Open (or create) `local.properties` in the project root and add:
   ```properties
   GEMINI_API_KEY=your_api_key_here
   ```
   This file is git-ignored, so your key is never committed.

3. **Add your backend config** (if you use Firebase): place your own `google-services.json` in the `app/` directory.

4. **Build and run.** Open the project in Android Studio, let Gradle sync, then press **Run**.

## Project Structure

```
app/
├── src/main/java/...      # Activities, fragments, adapters, models
├── src/main/res/          # XML layouts, drawables, strings
└── build.gradle
```

## Author

**Fatima Mazhar**
Computer Science, FAST-NUCES Lahore
[GitHub](https://github.com/FatimaMazhar0) · [Portfolio](https://fatimamazhar0.github.io/fatima-portfolio)
