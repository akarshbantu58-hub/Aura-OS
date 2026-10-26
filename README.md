# Aura OS

A standalone Liquid Glass-inspired Android home launcher for Samsung Galaxy Tab A9.

## Project goals
- Native Android app built with Kotlin and Jetpack Compose.
- Tablet-first adaptive layout for portrait and landscape.
- Glass-inspired translucent surfaces, subtle borders, gradients, and responsive cards.
- Register as an Android Home launcher so the user can choose it under Settings > Apps > Default apps > Home app.
- Never modifies or depends on any JARVIS project.

## Build
GitHub Actions downloads Gradle and Android SDK, then builds a debug APK. Open the **Actions** tab and download the `Aura-OS-debug-apk` artifact from a successful workflow run.

## Status
Initial scaffold. The first milestone focuses on a launchable home-screen experience; system notification shade and system settings replacement are outside a normal third-party launcher's permissions.
