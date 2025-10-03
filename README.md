# SyncCache Java Binding

Java bindings for **SyncCache**.

---

## 🚀 Quick Start

### 1) Renew the core library
Run the helper script to clone or refresh the core library:

```bash
  bash sh_scripts/renew_core_library.sh
```

### 2) Rebuild the linked library (and add it to your PATH)
Build the linked library and update your PATH via:

```bash
  bash sh_scripts/build_linked_library.sh
```

---

## 🧪 Build & Test with Docker
Build the Docker image (includes running the project/tests inside the container as configured):

```bash
  docker build -t synccache-java .
```

---

## 🧰 IntelliJ IDEA Integration
Use the provided IntelliJ configuration (includes Docker integration and pre-built run configurations):

```bash
  rm -rf .idea || true && cp -r ide_settings .idea
```

> After copying, open the project in IntelliJ and select the included run configurations.

---

## 📎 Notes
- The scripts above will refresh the **core library** and rebuild the **linked library** used by the Java bindings.
- Ensure **Docker** is installed if you plan to build/test via Docker.
- On macOS/Linux, run scripts with `bash`. On Windows, use **WSL** or **Git Bash**.

---

## ✅ Happy coding!
