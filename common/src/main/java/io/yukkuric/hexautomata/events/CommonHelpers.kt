package io.yukkuric.hexautomata.events

import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedMishapEnv
import io.yukkuric.hexautomata.HAConfig
import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.casting.MishapOutEvent
import io.yukkuric.hexautomata.helpers.ADV_STACK_OVERFLOW
import io.yukkuric.hexautomata.helpers.grantAdvancement
import io.yukkuric.hexautomata.tag.HATags
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.Projectile
import java.util.*

object CommonHelpers {
    private val OLD_TARGETS = WeakHashMap<Entity, Entity>()
    private val PROJ_HIT_GROUND = WeakHashMap<Entity, Boolean>()
    private fun checkNewTarget(mob: Entity, newTarget: Entity?): Boolean {
        val oldTarget = OLD_TARGETS[mob]
        if (oldTarget == newTarget) return true
        OLD_TARGETS[mob] = newTarget
        return false
    }

    fun checkProjAlreadyHitGround(proj: Projectile) = PROJ_HIT_GROUND.put(proj, true) != null

    @JvmStatic
    fun shouldIgnoreHurt(src: DamageSource): Boolean {
        HexAutomata.LOGGER.error("damage check ${src.type()}, ${src.`is`(HATags.Damage.IGNORE_HURT)}")
        return src.`is`(HATags.Damage.IGNORE_HURT) || (src.entity?.type?.`is`(HATags.Entity.IGNORE_HURT) == true)
    }

    @JvmStatic
    fun compareAndTriggerTargeted(mob: Entity, newTarget: Entity?) {
        if (checkNewTarget(mob, newTarget) || newTarget !is ServerPlayer) return
        if (mob.type.`is`(HATags.Entity.IGNORE_TARGETING)) return
        CommonEventsHandler.trigger(BuiltinEventMarker.TARGETED, newTarget, IHAEvent.Simple(mob))
    }

    // max recursive count per tick
    private val RECURSIVE_COUNTER = WeakHashMap<ServerPlayer, Int>()
    private val COUNTER_VERSION = WeakHashMap<ServerPlayer, Int>()
    private val RECURSIVE_MISHAP_AGE = WeakHashMap<ServerPlayer, Int>()
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
        val overflow = newRecursed > HAConfig.MaxRecursiveEventsPerTick()
        if (overflow) recursiveMishap(player)
        return overflow
    }
    @JvmStatic
    fun releaseRecursive(player: ServerPlayer) {
        RECURSIVE_COUNTER[player] = (RECURSIVE_COUNTER.getOrDefault(player, 0) - 1).coerceAtLeast(0)
    }

    private fun recursiveMishap(player: ServerPlayer) {
        val oldVersion = RECURSIVE_MISHAP_AGE.getOrDefault(player, 0)
        val newVersion = player.tickCount
        if (oldVersion == newVersion) return
        RECURSIVE_MISHAP_AGE[player] = newVersion

        // advnacements
        player.grantAdvancement(ADV_STACK_OVERFLOW)

        // manually call mishap
        val mishapEnv = PlayerBasedMishapEnv(player)
        mishapEnv.drown()
        player.sendSystemMessage(MishapOutEvent.ERROR_MSG)
    }
}