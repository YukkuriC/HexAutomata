package io.yukkuric.hexautomata.forge

import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.HexAutomata.commonInit
import io.yukkuric.hexautomata.HexAutomataClient
import io.yukkuric.hexautomata.fabric.collector.CuriosCollector
import io.yukkuric.hexautomata.forge.events.HAForgeEventsListener
import io.yukkuric.hexautomata.items.collector.FocusCollector
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@Mod(HexAutomata.MOD_ID)
class HexAutomataForge : HexAutomata.IAPI() {
    init {
        HAForgeEventsListener.load()
        HAConfigForge.register(ModLoadingContext.get())
        FocusCollector.register("curios", CuriosCollector)
        commonInit()
    }

    override fun modLoaded(id: String) = ModList.get().isLoaded(id)
}

object HexAutomataForgeClient {
    @SubscribeEvent
    fun OnClientInit(e: FMLClientSetupEvent) {
        HexAutomataClient.load()
    }
}