package io.yukkuric.hexautomata.items

import at.petrak.hexcasting.common.items.magic.ItemCreativeUnlocker
import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.HexAutomata.modLoc
import io.yukkuric.hexautomata.blocks.BrainsweepIntermediate
import io.yukkuric.hexautomata.events.BuiltinEventMarker
import io.yukkuric.hexautomata.events.EventMarker
import io.yukkuric.hexautomata.register
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity


object HAItems {
    private val ITEMS: MutableMap<ResourceLocation, Item> = LinkedHashMap()
    private val ITEMS_BY_TAB: MutableMap<CreativeModeTab, MutableList<() -> ItemStack>> = HashMap()
    fun registerItems(r: (ResourceLocation, Item) -> Any?) = ITEMS.register(r)

    fun loadCreativeTabContents(tab: CreativeModeTab, output: CreativeModeTab.Output) {
        val content = ITEMS_BY_TAB[tab] ?: return
        for (getter in content) output.accept(getter())
    }

    private fun <T : Item> create(
        name: String,
        item: T,
        tab: CreativeModeTab? = Tabs.MAIN,
        createIntermediate: Boolean = false
    ): T {
        val id = modLoc(name)
        ITEMS[id] = item
        if (tab != null) {
            val list = ITEMS_BY_TAB.computeIfAbsent(tab) { _ -> ArrayList() }
            list.add(item::getDefaultInstance)
        }
        if (createIntermediate) BrainsweepIntermediate.create(id)
        return item
    }

    private fun reactiveFocus(type: EventMarker) =
        create("reactive_focus/${type.name.lowercase()}", ItemReactiveFocus(type), createIntermediate = true)

    // load all focuses by event type
    private val FOCUSES_BY_TYPE = HashMap<EventMarker, ItemReactiveFocus>()
    fun AllFocuses() = FOCUSES_BY_TYPE.values.toList()

    init {
        for (type in EventMarker.all()) {
            FOCUSES_BY_TYPE.computeIfAbsent(type, ::reactiveFocus)
        }
    }

    operator fun get(type: EventMarker) = FOCUSES_BY_TYPE[type]

    // other items
    val LOGO = create("logo", ItemCreativeUnlocker(Props.STACK_ONE), null)
    val FOCUS_BUNDLE = create("focus_bundle", ItemFocusBundle())

    object Tabs {
        private val TABS: LinkedHashMap<ResourceLocation, CreativeModeTab> = LinkedHashMap()
        fun registerCreativeTabs(r: (ResourceLocation, CreativeModeTab) -> Any?) = TABS.register(r)

        val MAIN = create("main",
            CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 7)
                .icon { FOCUSES_BY_TYPE[BuiltinEventMarker.HURT]!!.defaultInstance })

        private fun create(name: String, tabBuilder: CreativeModeTab.Builder): CreativeModeTab {
            var tab = tabBuilder.title(Component.translatable("itemGroup.${HexAutomata.MOD_ID}.$name")).build()
            TABS[modLoc(name)] = tab
            return tab
        }
    }

    object Props {
        val STACK_ONE = Properties().rarity(Rarity.RARE).stacksTo(1)
        val STACK_ONE_EPIC = Properties().rarity(Rarity.EPIC).stacksTo(1)
    }
}