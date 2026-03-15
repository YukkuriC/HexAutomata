package io.yukkuric.hexautomata.fabric.events

import io.yukkuric.hexautomata.events.BuiltinEventMarker
import io.yukkuric.hexautomata.events.CommonEventsHandler
import io.yukkuric.hexautomata.items.HAItems
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.server.level.ServerPlayer

object HAFabricEventsListener {
    fun load() {
        // creative tab contents
        ItemGroupEvents.MODIFY_ENTRIES_ALL.register { tab, entries ->
            HAItems.loadCreativeTabContents(tab, entries::accept)
        }

        // hurt
        ServerLivingEntityEvents.ALLOW_DAMAGE.register { entity, source, damage ->
            if (entity !is ServerPlayer) return@register true
            CommonEventsHandler[BuiltinEventMarker.HURT](entity, HAEventsFabric.Hurt(source.entity, damage.toDouble()))
            return@register true
        }

        // that's all for fabric API, damn
    }
}