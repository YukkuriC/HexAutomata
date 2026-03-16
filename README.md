# HexAutomata

[![Curseforge](https://badges.moddingx.org/curseforge/versions/1481923) ![CurseForge](https://badges.moddingx.org/curseforge/downloads/1481923)](https://www.curseforge.com/minecraft/mc-mods/hexautomata)  
[![Modrinth](https://badges.moddingx.org/modrinth/versions/hexautomata) ![Modrinth](https://badges.moddingx.org/modrinth/downloads/hexautomata)](https://modrinth.com/mod/hexautomata)

Provides items with ability listening to various game events and triggering custom spells.

[Online HexBook](https://yukkuric.github.io/HexAutomata)

## Features

- `Reactive Focus`: focus bound with certain game events happening on player
    - Events to listen to:
        - enemy targeting player
        - player taking damage
        - player shooting projectile
        - projectile hit something
        - melee attack something
        - killing something
        - _TBD_
    - Crafted by brainsweep recipes
        - all from `Akashic Record` as source block
    - Places to take effect:
        - inventory
        - Curios/Trinkets slots
        - _config entries controlling whether sources above are enabled_
- Special patterns taking effect exclusively inside triggered events