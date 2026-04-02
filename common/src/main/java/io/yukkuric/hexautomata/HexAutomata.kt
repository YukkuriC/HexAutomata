package io.yukkuric.hexautomata

import at.petrak.hexcasting.xplat.IClientXplatAbstractions
import com.mojang.logging.LogUtils
import io.yukkuric.hexautomata.actions.patch.HAPatches
import io.yukkuric.hexautomata.interop.HexOPInterop
import io.yukkuric.hexautomata.interop.HexParseInterop
import io.yukkuric.hexautomata.items.HAItems
import io.yukkuric.hexautomata.items.ItemFocusBundle
import io.yukkuric.hexautomata.items.ItemReactiveFocus
import io.yukkuric.hexautomata.multiblock.HARituals
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import org.slf4j.Logger

object HexAutomata {
    const val MOD_ID: String = "hexautomata"
    val LOGGER: Logger = LogUtils.getLogger()
    @JvmStatic
    fun modLoc(path: String): ResourceLocation {
        return ResourceLocation(MOD_ID, path)
    }

    fun commonInit() {
        HARituals.load()
        tryLoadInterop("hexparse", HexParseInterop::run)
        tryLoadInterop("hexoverpowered", HexOPInterop::run)
    }

    fun commonLateInit() {
        HAPatches.patchAll()
    }

    fun tryLoadInterop(modId: String, loadFunc: () -> Any) {
        if (!API.modLoaded(modId)) return
        try {
            loadFunc()
        } catch (e: Throwable) {
            LOGGER.error("error trying to load interop of $modId; error: ${e.stackTraceToString()}")
        }
    }

    lateinit var API: IAPI

    abstract class IAPI {
        init {
            API = this
        }

        abstract fun modLoaded(id: String): Boolean
        abstract fun revertBrainsweep(mob: Mob)

        open fun forceRefresh(mob: Mob) = mob.level().let { level ->
            mob.type.create(level)?.let {
                it.load(mob.saveWithoutId(CompoundTag()))
                mob.discard()
                level.addFreshEntity(it)
            }
        }
    }
}

// client stuff
object HexAutomataClient {
    fun load() {
        // all focus model variant
        for (focus in HAItems.AllFocuses()) {
            IClientXplatAbstractions.INSTANCE.registerItemProperty(
                focus, ItemReactiveFocus.DATA_PRED
            ) { stack, _, holder, _ ->
                if (holder is Player &&
                    focus.readIotaTag(stack) != null
                ) 1F else 0F
            }
        }
        IClientXplatAbstractions.INSTANCE.registerItemProperty(
            HAItems.FOCUS_BUNDLE,
            ItemFocusBundle.CONTENTS_PRED,
            ItemFocusBundle.Companion.Client::contentsPredicate
        )
    }
}