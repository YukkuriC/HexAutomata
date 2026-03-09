package io.yukkuric.hexautomata.forge

import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.HexAutomata.commonInit
import io.yukkuric.hexautomata.forge.events.HAForgeEventsListener
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod

@Mod(HexAutomata.MOD_ID)
class HexAutomataForge : HexAutomata.IAPI() {
    init {
        HAForgeEventsListener.load()
        commonInit()
    }

    override fun modLoaded(id: String) = ModList.get().isLoaded(id)
}