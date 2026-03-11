package io.yukkuric.hexautomata.mixin_interface

import com.llamalad7.mixinextras.MixinExtrasBootstrap
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class HAMixinPlugin : IMixinConfigPlugin {
    override fun onLoad(p0: String?) {
        MixinExtrasBootstrap.init()
    }

    override fun shouldApplyMixin(p0: String?, p1: String?): Boolean {
        return true
    }

    override fun getRefMapperConfig() = null
    override fun acceptTargets(p0: MutableSet<String>?, p1: MutableSet<String>?) {}
    override fun getMixins() = null
    override fun preApply(p0: String?, p1: ClassNode?, p2: String?, p3: IMixinInfo?) {}
    override fun postApply(p0: String?, p1: ClassNode?, p2: String?, p3: IMixinInfo?) {}
}