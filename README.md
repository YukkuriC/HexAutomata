# HexAutomata

[![Curseforge](https://badges.moddingx.org/curseforge/versions/1481923) ![CurseForge](https://badges.moddingx.org/curseforge/downloads/1481923)](https://www.curseforge.com/minecraft/mc-mods/hexautomata)  
[![Modrinth](https://badges.moddingx.org/modrinth/versions/hexautomata) ![Modrinth](https://badges.moddingx.org/modrinth/downloads/hexautomata)](https://modrinth.com/mod/hexautomata)

Provides items with ability listening to various game events and triggering custom spells.

[Online HexBook](https://yukkuric.github.io/HexAutomata)

## Features

> _I found a way - using the intelligence from a settled villager mind, with some special "**concepts**" from wild creatures - to create a kind of special spell-casters: **Reactive Focus**._

- `Reactive Focus`: focus bound with certain game events happening on player
    - Events to listen to:
        - enemy targeting player (without tag `#hexautomata:ignore_targeting`)
        - player taking damage (both source and attacker without tag `#hexautomata:ignore_hurt`)
        - player shooting projectile
        - projectile hit something
        - melee attack something
        - killing something
        - being teleported
    - Crafted by brainsweep recipes
        - all from `Akashic Record` as source block
    - Places to take effect:
        - Inventory (default: false)
        - Main/Off hand (default: true; takes no effect if `Inventory` set to true)
        - Ender Chest (default: false)
        - Curios/Trinkets slots (default: true)
        - _config entries controlling whether sources above are enabled_
- `Reactive Focus Nexus`: an item holding multiple Reactive Focuses
    - all inner focuses take effect as if they're outside
    - crafted with a multiblock structure, ending with another brainsweep
      ![ritual multiblock](https://github.com/YukkuriC/HexAutomata/blob/main/doc/resources/assets/hexautomata/textures/multiblock/hexdoc/focus_bundle.png?raw=true)
- Special patterns taking effect exclusively inside triggered events
