package io.yukkuric.hexautomata.fabric

import at.petrak.hexcasting.common.lib.hex.HexActions
import io.yukkuric.hexautomata.HexAutomata.IAPI
import io.yukkuric.hexautomata.HexAutomata.commonInit
import io.yukkuric.hexautomata.actions.HexAutomataActions.Companion.registerActions
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.Registry

class HexAutomataFabric : IAPI(), ModInitializer {
    override fun onInitialize() {
        registerActions { k, v -> Registry.register(HexActions.REGISTRY, k, v) }
        commonInit()
    }

    override fun modLoaded(id: String) = FabricLoader.getInstance().isModLoaded(id)

}

class HexAutomataFabricClient : ClientModInitializer {
    override fun onInitializeClient() {
    }
}