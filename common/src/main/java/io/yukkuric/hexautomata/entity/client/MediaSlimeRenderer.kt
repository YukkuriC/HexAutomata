package io.yukkuric.hexautomata.entity.client

import com.mojang.blaze3d.vertex.PoseStack
import io.yukkuric.hexautomata.entity.MediaSlime
import net.minecraft.client.model.LavaSlimeModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

class MediaSlimeRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<MediaSlime, LavaSlimeModel<MediaSlime>>(
        context,
        LavaSlimeModel(context.bakeLayer(ModelLayers.MAGMA_CUBE)),
        0.25f
    ) {
    companion object {
        val MAGMACUBE_LOCATION = ResourceLocation("textures/entity/slime/magmacube.png")
    }

    override fun getBlockLightLevel(entity: MediaSlime, blockPos: BlockPos): Int {
        return 15
    }

    override fun getTextureLocation(entity: MediaSlime): ResourceLocation {
        return MAGMACUBE_LOCATION
    }

    override fun render(
        magmaCube: MediaSlime,
        f: Float,
        g: Float,
        poseStack: PoseStack,
        multiBufferSource: MultiBufferSource,
        i: Int
    ) {
        this.shadowRadius = 0.25f * magmaCube.size.toFloat()
        super.render(magmaCube, f, g, poseStack, multiBufferSource, i)
    }

    override fun scale(magmaCube: MediaSlime, poseStack: PoseStack, f: Float) {
        val i = magmaCube.size
        val g = Mth.lerp(f, magmaCube.oSquish, magmaCube.squish) / (i.toFloat() * 0.5f + 1.0f)
        val h = 1.0f / (g + 1.0f)
        poseStack.scale(h * i.toFloat(), 1.0f / h * i.toFloat(), h * i.toFloat())
    }
}
