package io.yukkuric.hexautomata.events

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import java.util.*

object CommonHelpers {
    private val OLD_TARGETS = WeakHashMap<Entity, Entity>()
    fun checkNewTarget(mob: Entity, newTarget: Entity?) = OLD_TARGETS.put(mob, newTarget) != newTarget

    @JvmStatic
    fun compareAndTriggerTargeted(mob: Entity, newTarget: Entity?) {
        if (!checkNewTarget(mob, newTarget) || newTarget !is ServerPlayer) return
        CommonEventsHandler[EventMarker.TARGETED](newTarget, IHAEvent.Simple(mob))
    }
}