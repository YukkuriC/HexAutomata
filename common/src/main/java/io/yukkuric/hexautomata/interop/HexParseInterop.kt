package io.yukkuric.hexautomata.interop

import io.yukkuric.hexautomata.items.ItemReactiveFocus
import io.yukkuric.hexparse.api.HexParseAPI

object HexParseInterop : Runnable {
    override fun run() {
        HexParseAPI.CreateItemIOMethod(ItemReactiveFocus::class.java)
    }
}