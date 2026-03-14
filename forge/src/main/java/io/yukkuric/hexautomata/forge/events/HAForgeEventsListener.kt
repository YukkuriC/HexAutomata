package io.yukkuric.hexautomata.forge.events

import at.petrak.hexcasting.common.lib.HexRegistries
import io.yukkuric.hexautomata.actions.HAActions
import io.yukkuric.hexautomata.blocks.BrainsweepIntermediate
import io.yukkuric.hexautomata.events.CommonEventsHandler
import io.yukkuric.hexautomata.events.EventMarker
import io.yukkuric.hexautomata.forge.HexAutomataForgeClient
import io.yukkuric.hexautomata.items.HAItems
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.projectile.Projectile
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.event.entity.EntityJoinLevelEvent
import net.minecraftforge.event.entity.ProjectileImpactEvent
import net.minecraftforge.event.entity.living.LivingHurtEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.Bindings
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.RegisterEvent


class HAForgeEventsListener {
    private object ForgeBus {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        fun OnPlayerHurt(e: LivingHurtEvent) {
            if (e.isCanceled) return
            val player = e.entity
            if (player !is ServerPlayer || e.amount <= 0) return
            CommonEventsHandler[EventMarker.HURT](player, HAEventsForge.Hurt(e))
        }
        @SubscribeEvent(priority = EventPriority.LOWEST)
        fun OnEntitySpawn(e: EntityJoinLevelEvent) {
            if (e.isCanceled || e.loadedFromDisk()) return
            // check player projectile shoot
            (e.entity as? Projectile)?.owner?.let { it as? ServerPlayer }?.let {
                CommonEventsHandler[EventMarker.SHOOT](it, HAEventsForge.Shoot(e))
            }
        }
        @SubscribeEvent(priority = EventPriority.LOWEST)
        fun OnProjectileHit(e: ProjectileImpactEvent) {
            if (e.isCanceled) return
            var proj = e.projectile
            val owner = proj.owner
            if (owner is ServerPlayer) {
                val event = HAEventsForge.ProjectileHit(e)
                if (event.invalid) return
                CommonEventsHandler[EventMarker.PROJECTILE_HIT](owner, event)
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