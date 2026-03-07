package io.yukkuric.hexautomata

import com.mojang.logging.LogUtils
import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger

object HexAutomata {
    const val MOD_ID: String = "hexautomata"
    val LOGGER: Logger = LogUtils.getLogger()
    @JvmStatic
    fun modLoc(path: String): ResourceLocation {
        return ResourceLocation(MOD_ID, path)
    }

    @JvmStatic
    fun commonInit() {
        tryLoadInterop("hexoverpowered") {
            // TODO
        }
    }

    private fun tryLoadInterop(modId: String, loadFunc: () -> Any) {
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
    }
}