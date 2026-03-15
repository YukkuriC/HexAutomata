package io.yukkuric.hexautomata.fabric

import at.petrak.hexcasting.common.lib.hex.HexActions
import io.yukkuric.hexautomata.HexAutomata.IAPI
import io.yukkuric.hexautomata.HexAutomata.commonInit
import io.yukkuric.hexautomata.HexAutomataClient
import io.yukkuric.hexautomata.actions.HAActions
import io.yukkuric.hexautomata.blocks.BrainsweepIntermediate
import io.yukkuric.hexautomata.fabric.collector.TrinketsCollector
import io.yukkuric.hexautomata.fabric.events.HAFabricEventsListener
import io.yukkuric.hexautomata.items.HAItems
import io.yukkuric.hexautomata.items.collector.FocusCollector
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation

class HexAutomataFabric : IAPI(), ModInitializer {
    private fun <T : Any> bindReg(reg: Registry<T>, loader: ((ResourceLocation, T) -> Any?) -> Any?) {
        loader { k, v -> Registry.register(reg, k, v) }
    }

    override fun onInitialize() {
        bindReg(HexActions.REGISTRY, HAActions::registerActions)
        bindReg(BuiltInRegistries.ITEM, HAItems::registerItems)
        bindReg(BuiltInRegistries.CREATIVE_MODE_TAB, HAItems.Tabs::registerCreativeTabs)
        bindReg(BuiltInRegistries.BLOCK, BrainsweepIntermediate::registerBlocks)
        bindReg(BuiltInRegistries.BLOCK_ENTITY_TYPE, BrainsweepIntermediate::registerBETypes)
        HAFabricEventsListener.load()
        FocusCollector.register("trinkets", TrinketsCollector)
        commonInit()
    }

    override fun modLoaded(id: String) = FabricLoader.getInstance().isModLoaded(id)
}

class HexAutomataFabricClient : ClientModInitializer {
    override fun onInitializeClient() {
        HexAutomataClient.load()
    }
}