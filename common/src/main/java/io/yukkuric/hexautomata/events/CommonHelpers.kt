package io.yukkuric.hexautomata.events

import net.minecraft.world.entity.Entity
import java.util.*

object CommonHelpers {
    private val OLD_TARGETS = WeakHashMap<Entity, Entity>()
    fun checkNewTarget(mob: Entity, newTarget: Entity?) = OLD_TARGETS.put(mob, newTarget) != newTarget
}