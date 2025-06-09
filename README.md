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
- Restricted commands flag - restrict to using only the commands that are preset in the program.
- The program is sending manual at starting (if not send "CMD: manual").
- Built using Maven — no need to manually install ChromeDriver.
- Secure password handling for sudo commands on macOS with optional encrypted storage.

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
whatsapp.contact = <contact_or_group>

# Restricted commands - When enabled, you can use only the commands that are preset in the program.
restricted.commands = true
```

### Password Handling for macOS

The application now includes a secure password handling system for sudo commands on macOS:

1. **Interactive Mode**: When a sudo command is executed, the application will prompt for the password in the terminal.
2. **Secure Storage**: You can choose to save the password securely using AES encryption. The encrypted password is stored in your home directory under `.whatsappcmd/`.
3. **Security**: The password is never stored in plain text, and the encryption key is derived using PBKDF2 with a random salt.

To clear a stored password, you can delete the files in `~/.whatsappcmd/` or use the application's built-in password management.

---

## 🛠️ Build & Run

1. **Build the JAR with Maven**

   ```bash
   mvn clean package
   ```

   The JAR file will be generated under the `target/` folder.

2. **Place `config.properties` next to the JAR file**
   Config file placed inside the project under resources.
   If you will not place a config.properties file next to the jar, this file will be used.
   https://github.com/talevi83/CMDTermWhatsappControl/blob/main/src/main/resources/config.properties

   Do **not** include it inside the JAR — the app loads it externally at runtime.

4. **Run the JAR**

   ```bash
   java -jar CMDWhatsappControl.jar
   ```

---
## ⚙️ Manual

| Command          | Description                                    |
|------------------|------------------------------------------------|
| sleep            | Put the computer to sleep                      |
| internal ip      | Get the internal (local) IP address            |
| cancel shutdown  | Cancel a pending shutdown (Windows only)       |
| external ip      | Get the external (public) IP address           |
| shutdown         | Shutdown the computer                          |
| close program    | Closing the program and shutdown WebDriver.    |
| manual           | Get this manual.                               |
