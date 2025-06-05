# CMDWhatsappControl

**Control your computer through WhatsApp Web messages using Java & Selenium.**

---

## 🔹 Overview

CMDWhatsappControl is a Java application that listens to WhatsApp Web via Selenium. When it detects a message starting with `CMD:` from a predefined contact or group, it extracts the command and executes it directly on the host machine — Windows or macOS.

---

## 🚀 Features

- Execute terminal/command-line commands through WhatsApp.
- Cross-platform support: macOS and Windows.
- Configurable WhatsApp contact or group name.
- Chrome profile support for persistent WhatsApp Web sessions.
- Configuration via external `config.properties` file.
- Built using Maven — no need to manually install ChromeDriver.

---

## 🧱 Requirements

- Java Runtime Environment (JRE 11+)
- [Google Chrome](https://www.google.com/chrome/)
- Maven (to build the project)
- A Chrome user profile that is already logged in to WhatsApp Web

> **Note:** ChromeDriver is handled automatically via the Maven dependencies. No manual download required.

To find your Chrome profile path:

1. Open Chrome.
2. Navigate to: `chrome://version`
3. Copy the **Profile Path** (e.g., `\AppData\Local\Google\Chrome\User Data\Profile 2`)

---

## ⚙️ Configuration File: `config.properties`

Place the following file in the **same directory as the JAR**:

```properties
# For macOS:
chrome.profile.mac.path = /Library/Application Support/Google/Chrome/Profile 1

# For Windows:
chrome.profile.windows.path = \\AppData\\Local\\Google\\Chrome\\User Data\\profile 2

# WhatsApp chat name (can be a person or group)
whatsapp.contact = My Phone


