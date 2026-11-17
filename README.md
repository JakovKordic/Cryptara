# Cryptara

**English** | [Hrvatski](README.hr.md)

Cryptara is a desktop application for encrypting, decrypting, hashing and digitally signing files. It is written in plain Java (Swing, no external libraries) and supports both classic algorithms (AES, RSA) and the new post-quantum standards (ML-KEM, ML-DSA).

---

## Contents

1. [Features at a glance](#features-at-a-glance)
2. [Requirements](#requirements)
3. [Building and running](#building-and-running)
4. [Quick start](#quick-start)
5. [Using the app, tab by tab](#using-the-app-tab-by-tab)
   - [Keys](#keys-tab)
   - [Symmetric](#symmetric-tab)
   - [Asymmetric](#asymmetric-tab)
   - [Hash](#hash-tab)
   - [Signature](#signature-tab)
6. [Files the app creates](#files-the-app-creates)
7. [Sharing keys with other people](#sharing-keys-with-other-people)
8. [Messages and troubleshooting](#messages-and-troubleshooting)
9. [Security notes and limitations](#security-notes-and-limitations)
10. [Technical details](#technical-details)
11. [Project structure](#project-structure)
12. [License](#license)

---

## Features at a glance

| Tab | What it does | Algorithm |
|---|---|---|
| **Keys** | Generates every key the app needs, in one click | AES-256, RSA-3072 (×2), ML-KEM-768, ML-DSA-65 |
| **Symmetric** | Encrypts/decrypts a file with a shared secret key | AES-256-GCM |
| **Asymmetric** | Encrypts a file with a public key, decrypts with the private key | RSA-OAEP (3072-bit) or ML-KEM-768 (post-quantum), both combined with AES-256-GCM |
| **Hash** | Computes a file's fingerprint and later checks whether the file changed | SHA-256 |
| **Signature** | Signs a file with a private key and verifies the signature with the public key | RSA-PSS (3072-bit) or ML-DSA-65 (post-quantum) |

---

## Requirements

- **Java JDK 24 or newer** (JDK 25 LTS recommended).
  The post-quantum algorithms ML-KEM and ML-DSA were added to the standard Java library in JDK 24. On older versions the code does not compile.
  Check your version with:
  ```
  java -version
  javac -version
  ```
  If you need a JDK, [Eclipse Temurin](https://adoptium.net/) is a free option.
- Any operating system that runs Java with a graphical desktop (Windows, macOS, Linux).
- No other libraries, build tools or internet connection are needed.

---

## Building and running

The project has no build tool (no Maven/Gradle); it is compiled directly with `javac`. Run the commands below from the project's root folder (the one containing `src/`).

### Windows (PowerShell)

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out cryptara.Main
```

### macOS / Linux / Git Bash

```bash
javac -d out $(find src -name "*.java")
java -cp out cryptara.Main
```

### Optional: build a runnable JAR

After compiling into `out/`:

```
jar --create --file cryptara.jar --main-class cryptara.Main -C out cryptara
java -jar cryptara.jar
```

### From an IDE

Open the folder in VS Code (with the Java extension pack), IntelliJ IDEA or Eclipse, mark `src` as the source folder, set the project JDK to 24+ and run the `main` method in `src/cryptara/Main.java`.

> **Important — working directory.** The default keys folder is the relative path `keys`, which is resolved against the folder **from which you started the app**. If you always launch the app from the project root, keys will be in `<project>/keys/`. If you start it from somewhere else, either type a full path in the *Keys folder* fields or use *Browse...*.

The window opens maximized with a dark theme and five tabs: **Keys, Symmetric, Asymmetric, Hash, Signature**.

---

## Quick start

1. Start the app.
2. Open the **Keys** tab and click **Generate keys**. Wait for `Keys saved to ...`.
3. Open the **Symmetric** tab, click **Browse...** next to *Input file*, choose any file and click **Encrypt**. A file named `<name>.aes` appears next to the original.
4. Select the `.aes` file as the input and click **Decrypt**. A file named `decrypted_<name>` appears — identical to the original.

That's it. The other tabs work the same way: pick the keys folder (default `keys` is usually fine), pick a file, choose an algorithm where offered, and press a button. Results and errors appear in the log area at the bottom of each tab.

---

## Using the app, tab by tab

Every tab has:
- **Path fields** with a **Browse...** button. You can type or paste a path, or browse to it.
- **Action buttons.** While an operation runs, the buttons are disabled; they re-enable when it finishes.
- **A log area** (bottom) that shows what happened. Logs are not saved anywhere; they are cleared when you close the app.
- **Overwrite protection.** If the output file already exists, the app asks *"... already exists. Overwrite it?"* before writing. Choose **No** to cancel.

### Keys tab

Generates a full set of keys and saves them to a folder.

| Field / button | Meaning |
|---|---|
| **Output folder** | Where to save the keys. Default: `keys`. The folder is created if it doesn't exist. |
| **Browse...** | Pick a folder. |
| **Generate keys** | Generates all keys. RSA key generation may take a few seconds. |

One click generates **all** of these:

| Key | Used by |
|---|---|
| AES-256 secret key | Symmetric tab |
| RSA-3072 key pair (RSA-OAEP) | Asymmetric tab, RSA-OAEP |
| ML-KEM-768 key pair | Asymmetric tab, ML-KEM |
| RSA-3072 key pair (RSA-PSS) — a separate pair from the one above | Signature tab, RSA-PSS |
| ML-DSA-65 key pair | Signature tab, ML-DSA |

They are written to three files:

| File | Contains | Keep it secret? |
|---|---|---|
| `tajni_kljuc.txt` ("secret key") | AES-256 key | **Yes.** Share only with people who should be able to decrypt your symmetric files. |
| `javni_kljuc.txt` ("public key") | Public halves of the four key pairs | No — this is meant to be shared. |
| `privatni_kljuc.txt` ("private key") | Private halves of the four key pairs | **Yes — never share it.** |

> ⚠️ **Regenerating keys replaces all three files.** If the folder already contains keys, the app asks for confirmation first. After regenerating, files that were encrypted with the old keys **can no longer be decrypted**, and old signatures will no longer verify. Back up the old key files if you still need them.

### Symmetric tab

Encrypts and decrypts files with the AES-256 secret key (`tajni_kljuc.txt`). The same key is used for both directions.

| Field / button | Meaning |
|---|---|
| **Keys folder** | Folder containing `tajni_kljuc.txt`. Default: `keys`. |
| **Input file** | The file to encrypt, or the `.aes` file to decrypt. |
| **Encrypt** | Writes `<input>.aes` next to the input. Example: `report.pdf` → `report.pdf.aes`. |
| **Decrypt** | Writes `decrypted_<name>` next to the input, removing the `.aes` ending if present. Example: `report.pdf.aes` → `decrypted_report.pdf`. |

AES-GCM is *authenticated* encryption: if the encrypted file was changed even by one byte, or the wrong key is used, decryption fails with *"Wrong key or the file was modified"* instead of producing garbage.

Encrypting the same file twice produces different output each time — this is normal (a random value is used every time).

### Asymmetric tab

Public-key encryption: **anyone with the public key can encrypt** a file, but **only the holder of the private key can decrypt** it.

| Field / button | Meaning |
|---|---|
| **Keys folder** | Folder with `javni_kljuc.txt` (for encryption) and/or `privatni_kljuc.txt` (for decryption). |
| **Input file** | The file to encrypt, or the encrypted file to decrypt. |
| **Algorithm** | `RSA-OAEP (3072-bit)` or `ML-KEM-768 (post-quantum)`. |
| **Encrypt** | Uses the **public** key. Writes `<input>.rsa` or `<input>.kem`. |
| **Decrypt** | Uses the **private** key. Writes `decrypted_<name>` (removing `.rsa`/`.kem` if it matches the selected algorithm). |

**Which algorithm should I choose?**
- **RSA-OAEP** — the classic, widely used choice.
- **ML-KEM-768** — a NIST-standardized (FIPS 203) *post-quantum* algorithm, designed to stay secure even against future quantum computers.

> You must decrypt with the **same algorithm** you used to encrypt. A `.rsa` file needs *RSA-OAEP* selected; a `.kem` file needs *ML-KEM-768*. Choosing the wrong one gives an error.

How it works internally (hybrid encryption): the file itself is always encrypted with a fresh, random AES-256-GCM key. Only that small AES key is protected with RSA-OAEP (or established with ML-KEM). Thanks to that, files of any size can be encrypted and the file is also protected against tampering.

### Hash tab

Computes the SHA-256 fingerprint of a file. A hash lets you check later whether a file has changed (e.g. after a download or a copy). **A hash is not encryption** — it does not hide the content and needs no keys.

| Field / button | Meaning |
|---|---|
| **Input file** | The file to hash or check. |
| **Hash file** | *Optional.* Where the hash is saved / read from. If left empty, `<input>.sha256` next to the input is used. |
| **Compute hash** | Shows the hash in the log and saves it to the hash file. |
| **Verify hash** | Recomputes the hash and compares it to the one in the hash file. |

Results:
- `OK: the file matches the stored hash.` — the file is unchanged.
- `MISMATCH: the file or the hash file has been changed.` — something differs.

The hash file format is the same as the standard `sha256sum` tool (`<64 hex characters>  <file name>`), so you can verify Cryptara hash files with `sha256sum -c`, and Cryptara can verify hash files produced by `sha256sum` (only the first hash in the file is read).

> A hash only proves the file matches the hash file. Someone who can change the file can also change the hash file. If you need proof of **who** created the file, use the **Signature** tab.

### Signature tab

Digital signatures prove that a file was signed by the holder of a particular private key and that it hasn't been changed since.

| Field / button | Meaning |
|---|---|
| **Keys folder** | Folder with `privatni_kljuc.txt` (to sign) and/or `javni_kljuc.txt` (to verify). |
| **Input file** | The file to sign or verify (the original file, not the signature). |
| **Signature file** | *Optional.* Where the signature is saved / read from. If empty: `<input>.rsa.sig` (RSA-PSS) or `<input>.mldsa.sig` (ML-DSA). |
| **Algorithm** | `RSA-PSS (3072-bit)` or `ML-DSA-65 (post-quantum)` (FIPS 204). |
| **Sign** | Signs the file with the **private** key and saves the signature. The original file is not modified. |
| **Verify** | Checks the signature with the **public** key. |

Results:
- `VALID: the file is unchanged and the signature matches the public key.`
- `NOT VALID: the file or the signature has been changed, or it was made with another key.`
- `NOT VALID: the signature file is damaged.`

To let someone verify your file, send them **the file, the `.sig` file and your `javni_kljuc.txt`**. Verify with the same algorithm that was used to sign.

---

## Files the app creates

All output files are written **into the same folder as the input file**.

| Action | Input | Output |
|---|---|---|
| Generate keys | — | `tajni_kljuc.txt`, `javni_kljuc.txt`, `privatni_kljuc.txt` in the chosen folder |
| Symmetric encrypt | `file.ext` | `file.ext.aes` |
| Symmetric decrypt | `file.ext.aes` | `decrypted_file.ext` |
| RSA-OAEP encrypt | `file.ext` | `file.ext.rsa` |
| ML-KEM encrypt | `file.ext` | `file.ext.kem` |
| Asymmetric decrypt | `file.ext.rsa` / `file.ext.kem` | `decrypted_file.ext` |
| Compute hash | `file.ext` | `file.ext.sha256` (unless a hash file is given) |
| RSA-PSS sign | `file.ext` | `file.ext.rsa.sig` (unless a signature file is given) |
| ML-DSA sign | `file.ext` | `file.ext.mldsa.sig` (unless a signature file is given) |

Notes:
- The original file is never modified or deleted. If you want only the encrypted copy to remain, delete the original yourself.
- Input and output must be different files — the app refuses to overwrite the input with its own output.
- The key and signature files are plain text, one `NAME=Base64` entry per line, for example `AES-256=6suL...`. Don't edit them by hand; a broken line makes the file unreadable for the app.

---

## Sharing keys with other people

The app always looks for keys by their fixed file names (`tajni_kljuc.txt`, `javni_kljuc.txt`, `privatni_kljuc.txt`) inside the selected **Keys folder**. To work with someone else's key, put their file in a separate folder and point the *Keys folder* field at it.

**Someone wants to send you an encrypted file (Asymmetric):**
1. You generate keys and send them your `javni_kljuc.txt`.
2. They save it in a folder (e.g. `keys_from_friend/javni_kljuc.txt`), select that folder in the Asymmetric tab, and encrypt.
3. They send you the `.rsa` / `.kem` file; you decrypt it with your own keys folder (which has `privatni_kljuc.txt`).

**You want to prove a file came from you (Signature):**
1. Sign the file with your keys folder.
2. Send the file, the `.sig` file and your `javni_kljuc.txt`.
3. The recipient puts `javni_kljuc.txt` in a folder, selects it, and clicks **Verify**.

**Sharing a symmetric key:** both sides need the same `tajni_kljuc.txt`. Anyone who has it can decrypt all your `.aes` files, so transfer it only over a channel you trust (preferably in person, or encrypted with the Asymmetric tab).

**Never send `privatni_kljuc.txt` to anyone.**

---

## Messages and troubleshooting

| Message / problem | Cause and solution |
|---|---|
| Code does not compile, `ML-KEM` / `ML-DSA` / `KEM` not found | JDK is older than 24. Install JDK 24+ (25 LTS recommended). |
| `package cryptara.keys does not exist` when compiling a fresh clone | The `src/cryptara/keys/` folder is missing from the repository (see the note in [Project structure](#project-structure)). |
| `Error: choose an input file.` | The *Input file* field is empty. |
| `Error: invalid path.` | The path contains characters that aren't allowed. Use *Browse...*. |
| `Error: choose a file, not a folder root.` | You entered a drive/root (e.g. `C:\`) instead of a file. |
| `Error: java.nio.file.NoSuchFileException: keys\tajni_kljuc.txt` (or another key file) | Keys haven't been generated, or the *Keys folder* points to the wrong place. Generate keys or select the correct folder. Remember that `keys` is relative to the folder the app was started from. |
| `Error: java.nio.file.NoSuchFileException: ...` (input / hash / signature file) | The selected file doesn't exist. Check the path. |
| `No RSA-OAEP key found in javni_kljuc.txt` (or similar) | The key file exists but doesn't contain the needed key — it was edited or comes from elsewhere. Regenerate keys or get a correct file. |
| `Invalid key file line` / `Invalid Base64 in key file line` | A key or signature file was damaged (e.g. edited by hand). Restore it from a backup. |
| `Error: decryption failed. Wrong key or the file was modified.` (Symmetric) | Different `tajni_kljuc.txt` than the one used to encrypt (e.g. keys were regenerated), or the `.aes` file was changed/corrupted. |
| `Error: decryption failed. Wrong key, wrong algorithm, or the file was modified.` (Asymmetric) | Wrong private key, the wrong algorithm is selected (e.g. RSA-OAEP for a `.kem` file), or the file was changed. |
| `Envelope is too short.` / `Envelope is corrupted.` / `Encrypted data is too short.` | The file you're decrypting isn't a valid Cryptara encrypted file, or it is truncated. |
| `Input and output files must be different.` | Input and output paths point to the same file. Choose a different file. |
| `Hash file does not contain a valid SHA-256 digest.` | The selected hash file isn't a SHA-256 hash file. |
| `MISMATCH` when verifying a hash | The file (or the hash file) has changed since the hash was computed. |
| `NOT VALID` when verifying a signature | The file or the signature changed, the wrong public key is used, or the wrong algorithm is selected. |
| `java.lang.OutOfMemoryError` | The file is too large to fit in memory (see limitations). Run with more memory, e.g. `java -Xmx4g -cp out cryptara.Main`. |

---

## Security notes and limitations

- **Key files are stored unprotected.** `tajni_kljuc.txt` and `privatni_kljuc.txt` are plain Base64 text with no password. Anyone who gets these files can decrypt your data or sign in your name. Keep them on a device you trust and make backups.
- **Losing keys means losing data.** There is no recovery. If you lose or overwrite `tajni_kljuc.txt` or `privatni_kljuc.txt`, the corresponding encrypted files cannot be decrypted.
- **Originals are left behind.** Encryption creates a new file and leaves the original readable. Delete the original if it should not remain on disk (normal deletion may still leave recoverable data).
- **File names are not encrypted.** `secret_plans.pdf.aes` still reveals the name.
- **Public-key authenticity.** Before using someone's `javni_kljuc.txt`, make sure it really comes from them (e.g. compare its SHA-256 hash over the phone). Otherwise an attacker could give you their own public key.
- **Memory use.** Encryption and decryption read the whole file into memory, so very large files (multiple GB) may require more Java heap memory. Hashing and signing read files in chunks and work with files of any size.
- The app is a **study project** (FOI, course *Napredni operacijski sustavi*). It uses the standard, well-reviewed Java cryptography implementations, but it has not been independently audited. Don't rely on it as your only protection for highly sensitive data.

---

## Technical details

All cryptography comes from the standard Java library (JCA/JCE); no third-party crypto code is used.

| Purpose | Algorithm and parameters |
|---|---|
| Symmetric encryption | AES-256 in GCM mode (`AES/GCM/NoPadding`), random 12-byte IV per encryption, 128-bit authentication tag |
| RSA encryption | RSA-3072, `RSA/ECB/OAEPPadding`, OAEP with SHA-256 and MGF1-SHA-256 |
| Post-quantum KEM | ML-KEM-768 (FIPS 203) via `javax.crypto.KEM`; the 32-byte shared secret is used directly as the AES-256 key |
| Hash | SHA-256 |
| RSA signature | RSASSA-PSS, RSA-3072, SHA-256, MGF1-SHA-256, 32-byte salt |
| Post-quantum signature | ML-DSA-65 (FIPS 204) |
| Key encoding | Public keys X.509 (SubjectPublicKeyInfo), private keys PKCS#8, stored as Base64 |

### File formats

**`.aes`** (binary):
```
[12-byte IV][AES-GCM ciphertext][16-byte authentication tag]
```

**`.rsa` / `.kem`** (binary "envelope"):
```
[4-byte big-endian length N][N bytes of key material][AES-GCM data as in .aes]
```
Key material is the RSA-OAEP-encrypted AES key (384 bytes for RSA-3072) or the ML-KEM-768 encapsulation (1088 bytes). A new random AES key is used for every file.

**Key files** (`tajni_kljuc.txt`, `javni_kljuc.txt`, `privatni_kljuc.txt`) and **signature files** (`.sig`), UTF-8 text, one entry per line:
```
LABEL=Base64Data
```
Labels: `AES-256`, `RSA-OAEP`, `ML-KEM-768`, `RSA-PSS`, `ML-DSA-65`. A signature file contains a single entry labelled `RSA-PSS` or `ML-DSA-65`.

**`.sha256`** (text, `sha256sum` compatible):
```
<64 lowercase hex characters><two spaces><file name>
```

---

## Project structure

```
Cryptara/
├── src/cryptara/
│   ├── Main.java                  Entry point: applies the theme, opens the window
│   ├── keys/                      Key generation, saving and loading
│   │   ├── KeyGeneration.java     Generates AES, RSA, ML-KEM and ML-DSA keys
│   │   ├── KeyManager.java        Generates all keys and writes the three key files
│   │   ├── KeyLoader.java         Reads keys back from the key files
│   │   └── KeyFiles.java          LABEL=Base64 text file format
│   ├── symmetric/                 AES-GCM encryption of bytes and files
│   ├── asymmetric/                RSA-OAEP, ML-KEM, envelope format, file encryption
│   ├── hash/                      SHA-256 computing, saving and verifying
│   ├── signature/                 RSA-PSS / ML-DSA signing and signature files
│   ├── util/PathChecks.java       Ensures input and output files differ
│   └── ui/                        Swing window, one panel per tab, dark theme
├── LICENSE
├── README.md                      This file (English)
└── README.hr.md                   Croatian version
```


---

## License

Released under the [MIT License](LICENSE). © 2026 Jakov Kordić.
