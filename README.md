# BomberQuest

A Java/LibGDX game inspired by the classic **Bomberman**. Navigate through tiled maps, place bombs to destroy obstacles (and enemies), collect power-ups, and attempt to reach the exit before time runs out. An **A\* pathfinding** algorithm gives enemies intelligent movement. Enjoy custom music, custom map loading, and robust settings (including persistent key bindings).

![Gameplay Main Screen](assets/gamePlayScreenshots/gamePlayMain.png)


---

## Table of Contents

1. [Game Overview](#game-overview)
2. [Features](#features)
    1. [Menus & Screens](#menus--screens)
    2. [Settings & Key Bindings](#settings--key-bindings)
    3. [Map System](#map-system)
3. [Bonus Features](#bonus-features)
4. [Project Structure](#project-structure)
    1. [Core Classes](#core-classes)
    2. [Screens](#screens)
    3. [Game Objects](#game-objects)
    4. [Map & Bonus Packages](#map--bonus-packages)
5. [Controls](#controls)

---

## Game Overview

**BomberQuest** is a 2D tile-based game in which the player:
- Explores a grid-based map.
- Places bombs to destroy destructible walls and enemies.
- Collects power-ups that increase the number of bombs they can place and the bombs’ blast radius.
- Attempts to reach the exit to complete the level (the exit becomes available once all enemies are defeated).

It uses [LibGDX](https://libgdx.com/) for rendering, asset management, audio, and input handling.  
A custom **A\* pathfinding** algorithm is implemented to give enemies adaptive movement behavior.

---

## Features

### Menus & Screens

- **Start Screen (MenuScreen)**  
  Displays the main menu with:
    1. **Start Game** → goes to **FileSelectionScreen** to select a map or import a custom one.
    2. **Settings** → opens **SettingsScreen**.
    3. **Quit Game** → exits the application immediately.

- **Settings Screen (SettingsScreen)**  
  Provides controls for:
    - **Music Toggle** – Mutes/unmutes background music.
    - **Custom Key Bindings** – Re-bind movement (up/down/left/right), place bomb, pause, and shoot arrow. Saved to preferences and persist across sessions.

- **File Selection Screen (FileSelectionScreen)**  
  Lists **preinstalled maps** (from `maps` folder) and allows **custom map import**. The chosen file must be a `.properties` map definition.

- **Game Screen (GameScreen)**
    - Main gameplay: renders the player, enemies, walls (indestructible & destructible), bombs, explosions, power-ups, and the exit.
    - Houses a **HUD** that displays:
        - Timer (with flashing indicator when time is low).
        - Number of bombs you can place concurrently.
        - Blast radius.
        - Remaining enemies count.
        - Score.
        - Icons for Speed Power-Up and Arrow Power-Up (when active).
    - Manages camera centering, bomb/enemy updates, and pause handling.

- **Pause Screen (PauseScreen)**  
  An overlay with a semi-transparent dark background:
    - **Resume Game**
    - **To Main Menu**
    - **Settings**
    - **Quit Game**

- **Game Over Screen (GameOverScreen)**  
  Triggered if time runs out or player dies (explosion or enemy collision). Shows random “Game Over” messages. Options:
    - **Restart** the same map.
    - **Return to Menu**.

- **Game Won Screen (GameWonScreen)**  
  Triggered when all enemies are destroyed and the player steps on the exit.  
  Shows random “You Won” messages. Options:
    - **Start New Game** (restarts or selects a new map).
    - **Return to Menu**.

### Settings & Key Bindings

- **Mute Music**  
  Toggle in the settings screen; calls `MusicTrack.mute()` or `MusicTrack.unmute()`.
- **Custom Key Bindings**  
  Defaults:
    - **W / A / S / D** for movement
    - **Space** for placing bombs
    - **Esc** for pause
    - **Left Shift** for shooting arrows
      These can be changed in the settings screen. New bindings persist via LibGDX preferences.

### Map System

- **Map Files**  
  `.properties` format, each line like: x,y = tileType

The `tileType` is an integer describing which object should appear (e.g., 0 = indestructible wall, 1 = destructible wall, 2 = entrance, etc.).

- **Map Parser (MapParser.java)**  
  Reads the `.properties` file line by line, converting tile types into actual game objects.  
  If no exit is found, one random destructible wall is converted to hide an exit.  
  Also spawns randomly:
- Up to 4 **Speed Power-Ups** on destructible walls.
- Up to 6 **Arrow Power-Ups** on destructible walls

- **Dynamic Exit**  
  If tile type `4` is present, it indicates a destructible wall with an exit beneath it. The exit becomes usable only if all enemies are gone.

---

## Bonus Features

Beyond minimal requirements, BomberQuest also includes:

1. **A-Star**  
- The custom pathfinder calculates paths from an enemy’s tile to the player’s tile.
- Enemies chase the player if within range, otherwise they wander randomly.
- Logic is in `Enemy.tick(...)`, calling `AStarPathFinder.calculatePath(...)`.

2. **Speed Power-Up**
- Up to 4 can appear on the map.
- Doubles the player's movement speed for **30 seconds** once collected.
- HUD shows an icon when active.
- Handled in `SpeedPowerUp.java` and integrated in the collision code.

2. **Arrow Power-Up**
- Up to 6 can appear on the map.
- Enables the player to shoot arrows (default key: Left Shift) for **30 seconds**.
- Arrows travel in the direction the player is facing, can kill enemies on contact, and disappear after a short duration.
- HUD shows an icon when active.
- Managed in `ArrowPowerUp.java` & `Arrow.java`.

3. **Score System**
- Implemented in `Score.java`.
- Earn points for actions:
    - +15 for destroying a destructible wall
    - +100 for each enemy killed
    - +85 for each power-up collected
    - +2 for each second remaining when you reach the exit
- The final score is displayed on Game Over / Game Won screens.

4. **Audio & Visual Enhancements**
- Music toggles and button click sounds
- Retro visual effects using [`gdx-vfx`](https://github.com/crashinvaders/gdx-vfx) (CRT overlay, vignette, etc.)
- Random “Game Over” and “You Won” messages

5. **Persistent Key Mappings**
- Save custom key bindings for movement, bomb placement, pause, and arrow shooting.


---

## Project Structure

A simplified overview of packages and key classes:




```
├── de.tum.cit.ase.bomberquest
│   ├── BomberQuestGame.java        # Main LibGDX entry point & screen transitions
│   ├── Hud.java                    # Heads-up display for timer, power-ups, score, etc.
│   ├── audio/
│   │   └── MusicTrack.java         # Manages looping, volume, mute state
│   ├── bonusFeatures/
│   │   ├── AStarPathFinder.java    # A* pathfinding for enemy AI
│   │   ├── ArrowPowerUp.java       # Special power-up enabling arrow shooting
│   │   ├── SpeedPowerUp.java       # Special power-up doubling player speed
│   │   ├── Score.java              # Manages the scoring logic
│   │   └── ui/
│   │       ├── KeyBindings.java    # Handles custom key mapping + persistent saving
│   │       └── MenuButton.java     # Button widget (plays click sound, uses nine-patch)
│   ├── map/
│   │   ├── GameMap.java            # Holds game objects & manages physics world
│   │   └── MapParser.java          # Reads .properties map files; spawns objects & power-ups
│   ├── objects/
│   │   ├── Player.java             # Player logic (movement, bomb/arrow usage, power-up states)
│   │   ├── Enemy.java              # Enemy logic + pathfinding
│   │   ├── Bomb.java               # Bomb logic & explosion
│   │   ├── Arrow.java              # Actual arrow projectile
│   │   ├── DestructibleWall.java   # Destroyable block (may hide power-up or exit)
│   │   ├── IndestructibleWall.java # Permanent, immovable block
│   │   ├── PowerUp.java            # Generic power-up (blast radius, concurrent bombs)
│   │   ├── Exit.java               # Exit tile, becomes active once all enemies are dead
│   │   └── Entrance.java           # Entrance tile where the player starts
│   ├── screens/
│   │   ├── BaseScreen.java         # Shared background & visual effects
│   │   ├── MenuScreen.java         # Main menu UI
│   │   ├── SettingsScreen.java     # Music toggle, key rebinding
│   │   ├── FileSelectionScreen.java# Preinstalled map list & custom map import
│   │   ├── GameScreen.java         # Main gameplay logic & rendering
│   │   ├── PauseScreen.java        # Pause overlay
│   │   ├── GameOverScreen.java     # Random messages upon losing
│   │   └── GameWonScreen.java      # Random messages upon winning
│   ├── textures/
│   │   ├── Textures.java           # References to texture regions
│   │   └── Animations.java         # Animation sets for bombs, players, enemies, etc.
│   └── ...                         # Other files and resources
```

### Core Classes

- **BomberQuestGame**  
  Manages global assets, creates screens, and orchestrates transitions.

- **Hud**  
  Displays timer, bombs, blast radius, enemy count, **score**, and icons for Speed/Arrow power-ups.

### Screens

- **MenuScreen**: Main menu.
- **FileSelectionScreen**: Lists available maps, allows custom file import.
- **SettingsScreen**: Key bindings, music toggle.
- **GameScreen**: Main update loop & rendering.
- **PauseScreen**: Overlays a semi-transparent UI.
- **GameOverScreen**, **GameWonScreen**: End-of-round screens with random messages, final score, and replay/return options.

### Game Objects

- **Player**: Movable character with bomb concurrency limits, arrow-shooting, speed boosts, etc.
- **Enemy**: Pathfinds or wanders. Deals damage on contact.
- **Bomb**: Explodes after ~3 seconds in a cross pattern.
- **Arrow**: Projectile that can kill enemies; expires after short flight.
- **DestructibleWall**: Destroyable, can reveal power-ups or exit.
- **IndestructibleWall**: Permanent obstacle.
- **PowerUp** (generic) plus specialized **SpeedPowerUp** and **ArrowPowerUp**.
- **Exit**: Activated once all enemies are dead.
- **Entrance**: Starting point tile.

### Map & Bonus Packages

- **GameMap**  
  Maintains all active objects, bombs, arrows, enemies, plus Box2D physics stepping.
- **MapParser**  
  Reads `.properties` files, places destructible/indestructible walls, enemies, entrance, exit, and up to 4 Speed Power-Ups + 6 Arrow Power-Ups.
- **AStarPathFinder** (in bonusFeatures)  
  Implements A* for enemy pathfinding.
- **Score**  
  Central scoring logic (walls destroyed, enemies killed, power-ups, time bonus).
- **SpeedPowerUp**, **ArrowPowerUp**  
  Specialized power-ups that grant temporary abilities (double speed, arrow shooting).

---

## Controls

*(Re-bindable in **Settings**; defaults listed.)*
- **W, A, S, D**: Move
- **Space**: Place Bomb
- **Left Shift**: Shoot Arrow
- **Esc**: Pause / Resume

---

## AI Usage in This Project

AI tools were utilized during the development of this project to assist with specific tasks that complemented our own efforts. These tasks included:

- Drafting and improving this documentation for clarity and completeness.
- Writing JavaDoc comments to enhance the readability of the code.
- Generating verbose and meaningful names for classes, methods, and attributes to align with best practices in code readability.
- Conducting research and troubleshooting complex issues, such as debugging errors or understanding unfamiliar libraries.
- Generating artwork for the menu screen to provide a creative and polished visual design.
- Brainstorming creative and engaging "game over" and "game won" messages for player feedback.
- Advising on edge cases and best practices that extend beyond the course's scope, such as:
    - Proper disposal of native resources for improved performance and memory management.
    - Cross plattform support
    - Frame rate independence for consistent performance

---

*Enjoy blowing up walls, outsmarting enemies, collecting power-ups, and racking up a high score in BomberQuest!*  
