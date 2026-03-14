package io.yukkuric.hexautomata.events

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
        CommonEventsHandler[EventMarker.TARGETED](newTarget, IHAEvent.Simple(mob))
    }
}