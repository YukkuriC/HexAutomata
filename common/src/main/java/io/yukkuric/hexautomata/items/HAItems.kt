package io.yukkuric.hexautomata.items

import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.HexAutomata.modLoc
import io.yukkuric.hexautomata.events.EventMarker
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack


object HAItems {
    private val ITEMS: MutableMap<ResourceLocation, Item> = LinkedHashMap()
    private val ITEMS_BY_TAB: MutableMap<CreativeModeTab, MutableList<() -> ItemStack>> = HashMap()
    fun registerItems(r: (ResourceLocation, Item) -> Any?) {
        for (e in ITEMS.entries) {
            r(e.key, e.value)
        }
    }

    fun loadCreativeTabContents(tab: CreativeModeTab, output: CreativeModeTab.Output) {
        val content = ITEMS_BY_TAB[tab] ?: return
        for (getter in content) output.accept(getter())
    }

    private fun <T : Item> create(name: String, item: T, tab: CreativeModeTab = Tabs.MAIN): T {
        ITEMS[modLoc(name)] = item
        if (tab != null) {
            val list = ITEMS_BY_TAB.computeIfAbsent(tab) { _ -> ArrayList() }
            list.add(item::getDefaultInstance)
        }
        return item
    }

    private fun reactiveFocus(type: EventMarker) =
        create("reactive_focus/${type.name.lowercase()}", ItemReactiveFocus(type))

    // load all focuses by event type
    private val FOCUSES_BY_TYPE = HashMap<EventMarker, ItemReactiveFocus>()
    fun AllFocuses() = FOCUSES_BY_TYPE.values.toList()

    init {
        for (type in EventMarker.all()) {
            FOCUSES_BY_TYPE.computeIfAbsent(type, ::reactiveFocus)
        }
    }

    operator fun get(type: EventMarker) = FOCUSES_BY_TYPE[type]

    object Tabs {
        private val TABS: LinkedHashMap<ResourceLocation, CreativeModeTab> = LinkedHashMap()
        fun registerCreativeTabs(r: (ResourceLocation, CreativeModeTab) -> Any?) {
            for (e in TABS.entries) {
                r(e.key, e.value)
            }
        }

        val MAIN = create("main",
            CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 7)
                .icon { FOCUSES_BY_TYPE[EventMarker.HURT]!!.defaultInstance })

        private fun create(name: String, tabBuilder: CreativeModeTab.Builder): CreativeModeTab {
            var tab = tabBuilder.title(Component.translatable("itemGroup.${HexAutomata.MOD_ID}.$name")).build()
            TABS[modLoc(name)] = tab
            return tab
        }
    }
}