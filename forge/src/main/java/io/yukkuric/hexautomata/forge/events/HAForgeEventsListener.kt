package io.yukkuric.hexautomata.forge.events

import at.petrak.hexcasting.common.lib.HexRegistries
import io.yukkuric.hexautomata.HexAutomata.commonInit
import io.yukkuric.hexautomata.actions.HAActions
import io.yukkuric.hexautomata.blocks.BrainsweepIntermediate
import io.yukkuric.hexautomata.events.BuiltinEventMarker
import io.yukkuric.hexautomata.events.CommonEventsHandler
import io.yukkuric.hexautomata.forge.HexAutomataForgeClient
import io.yukkuric.hexautomata.items.HAItems
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.projectile.Projectile
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.event.entity.EntityJoinLevelEvent
import net.minecraftforge.event.entity.ProjectileImpactEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.Bindings
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.RegisterEvent


class HAForgeEventsListener {
    private object ForgeBus {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        fun OnHurt(e: LivingHurtEvent) {
            if (e.isCanceled || e.amount <= 0) return
            // player hurt
            e.entity.let { it as? ServerPlayer }?.let {
                CommonEventsHandler[BuiltinEventMarker.HURT](it, HAEventsForge.Hurt(e))
            }
            // player melee
            if (e.source.`is`(DamageTypes.PLAYER_ATTACK)) e.source.entity.let { it as? ServerPlayer }?.let {
                CommonEventsHandler[BuiltinEventMarker.MELEE_HIT](it, HAEventsForge.PlayerAttack(e))
            }
        }
        @SubscribeEvent(priority = EventPriority.LOWEST)
        fun OnEntitySpawn(e: EntityJoinLevelEvent) {
            if (e.isCanceled || e.loadedFromDisk()) return
            // check player projectile shoot
            (e.entity as? Projectile)?.owner?.let { it as? ServerPlayer }?.let {
                CommonEventsHandler[BuiltinEventMarker.SHOOT](it, HAEventsForge.Shoot(e))
            }
        }
        @SubscribeEvent(priority = EventPriority.LOWEST)
        fun OnProjectileHit(e: ProjectileImpactEvent) {
            if (e.isCanceled) return
            e.projectile.owner.let { it as? ServerPlayer }?.let {
                val event = HAEventsForge.ProjectileHit(e)
                if (event.invalid) return
                CommonEventsHandler[BuiltinEventMarker.PROJECTILE_HIT](it, event)
            }
        }
        @SubscribeEvent(priority = EventPriority.LOWEST)
        fun OnEntityDie(e: LivingDeathEvent) {
            if (e.isCanceled) return
            e.source.entity.let { it as? ServerPlayer }?.let {
                CommonEventsHandler[BuiltinEventMarker.KILL](it, HAEventsForge.Kill(e))
            }
        }
    }

    private object ModBus {
        @SubscribeEvent
        fun OnRegisterAll(e: RegisterEvent) {
            fun <T : Any> bindReg(
                key: ResourceKey<Registry<T>>, regFunc: ((ResourceLocation, T) -> Any?) -> Any?
            ) {
                if (e.registryKey != key) return
                regFunc { id, obj -> e.register(key, id) { obj } }
            }
            bindReg(HexRegistries.ACTION, HAActions::registerActions)
            bindReg(Registries.ITEM, HAItems::registerItems)
            bindReg(Registries.CREATIVE_MODE_TAB, HAItems.Tabs::registerCreativeTabs)

            // brainsweep intermediate
            bindReg(Registries.BLOCK, BrainsweepIntermediate::registerBlocks)
            bindReg(Registries.BLOCK_ENTITY_TYPE, BrainsweepIntermediate::registerBETypes)
        }
        @SubscribeEvent
        fun OnAddCreativeTabItems(e: BuildCreativeModeTabContentsEvent) {
            HAItems.loadCreativeTabContents(e.tab, e)
        }
        @SubscribeEvent
        fun OnCommonSetup(e: FMLCommonSetupEvent) {
            commonInit()
        }
    }

    companion object {
        fun load() {
            Bindings.getForgeBus().get().register(ForgeBus)
            val modBus = Mod.EventBusSubscriber.Bus.MOD.bus().get()
            modBus.register(ModBus)

            DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT
            ) {
                Runnable { modBus.register(HexAutomataForgeClient) }
            }
        }
    }
}