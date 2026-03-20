package io.yukkuric.hexautomata.fabric.events

import io.yukkuric.hexautomata.events.BuiltinEventMarker
import io.yukkuric.hexautomata.events.CommonEventsHandler
import io.yukkuric.hexautomata.items.HAItems
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.projectile.Projectile

object HAFabricEventsListener {
    private val HACK_PROJ_LAUNCH_RANGESQR = 16 * 16 // TODO really check loadFromDisk

    fun load() {
        // creative tab contents
        ItemGroupEvents.MODIFY_ENTRIES_ALL.register { tab, entries ->
            HAItems.loadCreativeTabContents(tab, entries::accept)
        }

        // spawn
        ServerEntityEvents.ENTITY_LOAD.register { entity, _ ->
            if (entity is Projectile) {
                val owner = entity.owner
                if (owner is ServerPlayer && owner.position()
                        .distanceToSqr(entity.position()) < HACK_PROJ_LAUNCH_RANGESQR
                ) CommonEventsHandler[BuiltinEventMarker.SHOOT].invoke(owner, HAEventsFabric.Shoot(entity))
            }
        }

        // hurt
        ServerLivingEntityEvents.ALLOW_DAMAGE.register { entity, source, damage ->
            if (damage <= 0) return@register true
            // player hurt
            entity.let { it as? ServerPlayer }?.let {
                CommonEventsHandler[BuiltinEventMarker.HURT](
                    it, HAEventsFabric.Hurt(source.entity, damage.toDouble())
                )
            }
            // player melee
            if (source.`is`(DamageTypes.PLAYER_ATTACK)) source.entity.let { it as? ServerPlayer }?.let {
                CommonEventsHandler[BuiltinEventMarker.MELEE_HIT](
                    it, HAEventsFabric.Hurt(entity, damage.toDouble())
                )
            }
            return@register true
        }

        // die
        ServerLivingEntityEvents.ALLOW_DEATH.register { entity, source, _ ->
            source.entity.let { it as? ServerPlayer }?.let {
                CommonEventsHandler[BuiltinEventMarker.KILL](it, HAEventsFabric.Kill(entity))
            }
            return@register true
        }

        // that's all for fabric API, damn
    }
}