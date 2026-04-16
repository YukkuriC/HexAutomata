# HexAutomata

[![Curseforge](https://badges.moddingx.org/curseforge/versions/1481923) ![CurseForge](https://badges.moddingx.org/curseforge/downloads/1481923)](https://www.curseforge.com/minecraft/mc-mods/hexautomata)  
[![Modrinth](https://badges.moddingx.org/modrinth/versions/hexautomata) ![Modrinth](https://badges.moddingx.org/modrinth/downloads/hexautomata)](https://modrinth.com/mod/hexautomata)

Provides items with ability listening to various game events and triggering custom spells; along with extending more possibilities about the ancient Brainsweep spell.

[<img src="https://github.com/SamsTheNerd/HexGloop/blob/73ea39b3becd/externalassets/hexdoc-badgecozy.svg?raw=true" alt="A badge for hexdoc in the style of Devins Badges" width=180>](https://yukkuric.github.io/HexAutomata)
[<img src="https://github.com/SamsTheNerd/HexGloop/blob/73ea39b3becd/externalassets/addon-badge-cozy.svg?raw=true" alt="A badge for addons.hexxy.media in the style of Devins Badges" width=160>](https://addons.hexxy.media)

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
    - crafted with a multiblock structure, ending with another brainsweep (accepting targets with tag `#hexautomata:multi_threaded`)
      ![ritual multiblock](https://github.com/YukkuriC/HexAutomata/blob/main/doc/resources/assets/hexautomata/textures/multiblock/hexdoc/focus_bundle.png?raw=true)
- Special patterns taking effect exclusively inside triggered events

> _I am mind, and mind is media... Body is the vessel, heavy and fragile... I shall expose my mind, taking me out of the vessel, I shall see more, I ..._

- Another advancement-based progression and functions extending Brainsweep great spell :3
    - a new way of transportation

## Interop

### KubeJS

#### `HAPatches` and `PatchAction`

Allowing packmakers to patch actions the same way as `OpBrainsweep`.  
Also exposes two special errors: `USE_ORIGINAL` and `STOP_ALL` for better control over custom brainsweeps.

#### `BrainsweepCallback`

Exposes `BrainsweepCallback` to server/startup scripts binding, allowing custom brainsweep callbacks to be registered.  

**Registering a callback:**

```js
BrainsweepCallback.create(priority,
    entityId, iotaTypeId, // nullable type ids
    (entity, iota, env) => {
        // return SpellAction.Result as a normal spell action
        if (some_condition) {
            return BrainsweepCallback.buildResult(env => {
                // do something
            }, 0)
        }
        // continue to more matched callbacks with lower priority
        else if (other_condition) {
            return null
        }
        // or interrupt with `PatchAction` special errors
        throw PatchAction.STOP_ALL
    }
);
```

- `entityId` and `iotaTypeId` accept `ResourceLocation` strings. Iota type IDs under the `minecraft` namespace are automatically remapped to `hexcasting`.
- Use `BrainsweepCallback.forceSet(key, callback)` to override an existing callback registration.

### HexParse

Registers `Reactive Focus` as an item I/O handler via `HexParseAPI.CreateItemIOMethod`, enabling HexParse to read and write iotas directly from/to Reactive Focus items.
