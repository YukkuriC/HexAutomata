package io.yukkuric.hexautomata.events

import io.yukkuric.hexautomata.tag.HATags
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.Projectile
import java.util.*

object CommonHelpers {
    private val OLD_TARGETS = WeakHashMap<Entity, Entity>()
    private val PROJ_HIT_GROUND = WeakHashMap<Entity, Boolean>()
    fun checkNewTarget(mob: Entity, newTarget: Entity?) = OLD_TARGETS.put(mob, newTarget) != newTarget
    fun checkProjAlreadyHitGround(proj: Projectile) = PROJ_HIT_GROUND.put(proj, true) != null

    @JvmStatic
    fun compareAndTriggerTargeted(mob: Entity, newTarget: Entity?) {
        if (!checkNewTarget(mob, newTarget) || newTarget !is ServerPlayer) return
        if (mob.type.`is`(HATags.Entity.IGNORE_TARGETING)) return
        CommonEventsHandler.trigger(BuiltinEventMarker.TARGETED, newTarget, IHAEvent.Simple(mob))
    }

    // max recursive count per tick
    val MAX_RECURSIVE = 10
    private val RECURSIVE_COUNTER = WeakHashMap<ServerPlayer, Int>()
    private val COUNTER_VERSION = WeakHashMap<ServerPlayer, Int>()
    @JvmStatic
    fun trackRecursive(player: ServerPlayer): Boolean {
        val oldVersion = COUNTER_VERSION.getOrDefault(player, 0)
        val newVersion = player.tickCount
        if (oldVersion != newVersion) {
            RECURSIVE_COUNTER[player] = 0
            COUNTER_VERSION[player] = newVersion
        }
        val newRecursed = RECURSIVE_COUNTER.getOrDefault(player, 0) + 1
        RECURSIVE_COUNTER[player] = newRecursed
        return newRecursed > MAX_RECURSIVE
    }
    @JvmStatic
    fun releaseRecursive(player: ServerPlayer) {
        RECURSIVE_COUNTER[player] = (RECURSIVE_COUNTER.getOrDefault(player, 0) - 1).coerceAtLeast(0)
    }
}