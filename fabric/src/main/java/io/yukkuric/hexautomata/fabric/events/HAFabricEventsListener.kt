package io.yukkuric.hexautomata.fabric.events

import io.yukkuric.hexautomata.events.BuiltinEventMarker
import io.yukkuric.hexautomata.events.CommonEventsHandler
import io.yukkuric.hexautomata.items.HAItems
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageTypes

object HAFabricEventsListener {
    fun load() {
        // creative tab contents
        ItemGroupEvents.MODIFY_ENTRIES_ALL.register { tab, entries ->
            HAItems.loadCreativeTabContents(tab, entries::accept)
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