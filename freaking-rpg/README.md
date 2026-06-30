# Freaking RPG

A Paper plugin for building a full-fledged RPG in Minecraft using plugins and datapacks.

Targets **Minecraft 26.2** on **Paper** with **Java 25**.

## Requirements

- JDK 25
- IntelliJ IDEA 2026.2 (recommended)
- [Minecraft Development](https://plugins.jetbrains.com/plugin/8327-minecraft-development) IntelliJ plugin (optional but helpful)

## Open in IntelliJ

1. Clone this repository.
2. In IntelliJ: **File → Open** and select the `freaking-rpg` folder (the one containing `build.gradle.kts`).
3. When prompted, trust the project and import the Gradle project.
4. Set the project SDK to **JDK 25** if it is not detected automatically.

## Build

```bash
./gradlew build
```

The plugin JAR is written to `build/libs/FreakingRPG-0.1.0-SNAPSHOT.jar`.

## Install on a server

1. Download [Paper 26.2](https://papermc.io/downloads/paper) (or 26.1.x for production).
2. Copy the built JAR into the server's `plugins/` folder.
3. Start the server and run `/frpg help`.

## Project layout

```
src/main/java/dev/freakingrpg/
  FreakingRpgPlugin.java    # Plugin entry point
  core/                     # Core systems (commands, services, etc.)
src/main/resources/
  plugin.yml
  config.yml
```

## License

All Rights Reserved.
