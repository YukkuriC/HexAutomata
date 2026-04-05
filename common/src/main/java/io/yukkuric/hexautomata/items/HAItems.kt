package io.yukkuric.hexautomata.items

import at.petrak.hexcasting.common.items.magic.ItemCreativeUnlocker
import io.yukkuric.hexautomata.HexAutomata
import io.yukkuric.hexautomata.HexAutomata.modLoc
import io.yukkuric.hexautomata.blocks.BrainsweepIntermediate
import io.yukkuric.hexautomata.blocks.BrainsweepIntermediateType
import io.yukkuric.hexautomata.blocks.BrainsweepIntermediateType.*
import io.yukkuric.hexautomata.blocks.BrainsweepRitualIntermediate
import io.yukkuric.hexautomata.events.BuiltinEventMarker
import io.yukkuric.hexautomata.events.EventMarker
import io.yukkuric.hexautomata.helpers.CustomRegisterObject
import net.minecraft.network.chat.Component
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity


object HAItems : CustomRegisterObject<Item>() {
    private val ITEMS_BY_TAB: MutableMap<CreativeModeTab, MutableList<() -> ItemStack>> = HashMap()

    fun loadCreativeTabContents(tab: CreativeModeTab, output: CreativeModeTab.Output) {
        val content = ITEMS_BY_TAB[tab] ?: return
        for (getter in content) output.accept(getter())
    }

    private fun <T : Item> create(
        name: String,
        item: T,
        tab: CreativeModeTab? = Tabs.MAIN,
        createIntermediate: BrainsweepIntermediateType = NONE,
    ): T {
        val id = modLoc(name)
        this[id] = item
        if (tab != null) {
            val list = ITEMS_BY_TAB.computeIfAbsent(tab) { _ -> ArrayList() }
            list.add(item::getDefaultInstance)
        }
        when (createIntermediate) {
            SIMPLE -> BrainsweepIntermediate.create(id)
            RITUAL -> BrainsweepRitualIntermediate.create(id)
            NONE -> {}
        }
        return item
    }

    private fun reactiveFocus(type: EventMarker) =
        create("reactive_focus/${type.name.lowercase()}", ItemReactiveFocus(type), createIntermediate = SIMPLE)

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
    val LOGO = create("logo", ItemCreativeUnlocker(Props.LOGO), null)
    val FOCUS_BUNDLE = create("focus_bundle", ItemFocusBundle(), createIntermediate = RITUAL)

    object Tabs : CustomRegisterObject<CreativeModeTab>() {
        val MAIN = create("main",
            CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 7)
                .icon { FOCUSES_BY_TYPE[BuiltinEventMarker.HURT]!!.defaultInstance })

        private fun create(name: String, tabBuilder: CreativeModeTab.Builder): CreativeModeTab {
            var tab = tabBuilder.title(Component.translatable("itemGroup.${HexAutomata.MOD_ID}.$name")).build()
            this[modLoc(name)] = tab
            return tab
        }
    }

    object Props {
        val STACK_ONE = Properties().rarity(Rarity.RARE).stacksTo(1)
        val STACK_ONE_EPIC = Properties().rarity(Rarity.EPIC).stacksTo(1)
        val LOGO = Properties().stacksTo(1)
            .food(FoodProperties.Builder().fast().alwaysEat().nutrition(20).saturationMod(1f).build())
    }
}