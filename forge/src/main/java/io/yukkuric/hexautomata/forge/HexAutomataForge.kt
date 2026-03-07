package io.yukkuric.hexautomata.forge

import at.petrak.hexcasting.common.lib.HexRegistries
import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.HexAutomata.commonInit
import io.yukkuric.hexautomata.actions.HexAutomataActions.Companion.registerActions
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.RegisterEvent

@Mod(HexAutomata.MOD_ID)
class HexAutomataForge : HexAutomata.IAPI() {
    init {
        val modBus = FMLJavaModLoadingContext.get().modEventBus
        modBus.addListener { event: RegisterEvent ->
            val key = event.registryKey
            if (key == HexRegistries.ACTION) {
                registerActions { k, v -> event.register(HexRegistries.ACTION, k) { v } }
            }
        }
        commonInit()
    }

    override fun modLoaded(id: String) = ModList.get().isLoaded(id)
}