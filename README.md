# Breach

A polished Fabric mod for a large-scale multiplayer story event set in the **Sculk Dimension**.

Built for **Minecraft 26.2** with support for **30+ concurrent players**, custom storyline phases, dungeons, bosses, cutscenes, UI, blocks, items, and enemies.

## Requirements

- JDK 25
- Git
- Minecraft 26.2 + Fabric Loader 0.19.3

## Development

```powershell
.\gradlew runClient   # test in singleplayer
.\gradlew runServer   # test multiplayer / event hosting
.\gradlew build       # produce mod jar in build/libs/
```

Open the project in IntelliJ IDEA and import the Gradle project. Gradle will download Minecraft and dependencies on first sync.

## Project layout

```
src/main/java/dev/breach/
  BreachMod.java                 # Server/common entry
  core/
    event/                       # Event session + story phases (30+ players)
    network/                     # Server-authoritative sync packets
  content/
    block/ item/ entity/         # Game content registries
    dimension/                   # Sculk Dimension keys + data
src/client/java/dev/breach/client/
  BreachClient.java              # Client entry (UI, cutscenes)
  EventStateHandler.java         # Applies synced event state
```

## Event flow

The server drives story progression through `EventPhase`:

`INACTIVE → BRIEFING → DESCENT → EXPLORATION → DUNGEON → BOSS → ESCAPE → EPILOGUE`

Phase changes broadcast to all connected clients for UI and cutscene hooks.

## License

All Rights Reserved.
