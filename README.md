# CMDWhatsappControl

**Control your computer through WhatsApp Web messages using Java & Selenium.**

---

## 🔹 Overview

CMDWhatsappControl is a Java application that listens to WhatsApp Web via Selenium. When it detects a message starting with `CMD:` from a predefined contact or group, it extracts the command and executes it directly on the host machine — Windows or macOS.

---

## 🚀 Features

- Execute terminal/command-line commands through WhatsApp.
- Cross-platform: macOS and Windows.
- Configurable WhatsApp contact or group name.
- Chrome profile support for persistent WhatsApp Web sessions.
- External `config.properties` file for flexible configuration.
- **Restricted commands** mode — limit execution to only preset commands.
- Sends the list of available commands automatically at startup (unless you send `CMD: help` yourself).
- Built with Maven — no need to manually install ChromeDriver.

---

## 🧱 Requirements

- Java Runtime Environment (JRE 11+)
- [Google Chrome](https://www.google.com/chrome/)
- Maven (for building the project)
- A Chrome user profile already logged in to WhatsApp Web

> **Note:** ChromeDriver is handled automatically via Maven dependencies. No manual installation required.

**How to find your Chrome profile path:**
1. Open Chrome.
2. Go to: `chrome://version`
3. Copy the **Profile Path** (e.g., `\AppData\Local\Google\Chrome\User Data\Profile 2`)

---

## ⚙️ Configuration

Create a file named `config.properties` in the **same directory as the JAR**.

Example:
```properties
# For macOS:
chrome.profile.mac.path = /Library/Application Support/Google/Chrome/Profile 1

# For Windows:
chrome.profile.windows.path = \\AppData\\Local\\Google\\Chrome\\User Data\\Profile 2

# WhatsApp chat name (person or group)
whatsapp.contact = <contact_or_group>

# MacOS user password (for sudo commands, used only where relevant)
mac.password = <your_password>

# Restrict commands to only those preset in the program (true/false)
restricted.commands = true
