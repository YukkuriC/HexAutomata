package io.yukkuric.hexautomata.action_patch.brainsweep

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.common.casting.actions.spells.great.OpBrainsweep
import at.petrak.hexcasting.common.lib.HexDamageTypes
import at.petrak.hexcasting.common.recipe.BrainsweepRecipe
import at.petrak.hexcasting.common.recipe.HexRecipeStuffRegistry
import io.yukkuric.hexautomata.action_patch.PatchAction
import io.yukkuric.hexautomata.mixin.AccessorDamageSources
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

object  OpExtendBrainsweep : PatchAction(OpBrainsweep, BrainsweepEx) {
    object BrainsweepEx : SpellAction {
        override val argc = 2

        override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
            val world = env.world
            val sacrifice = args.getEntity(0, argc)
            env.assertEntityInRange(sacrifice)
            val data = args[1]

            // 1. non-mob recipes
            if (data is Vec3Iota && sacrifice !is Mob) {
                val pos = BlockPos.containing(data.vec3)
                env.assertPosInRangeForEditing(pos)

                // fetch recipe
                val state = world.getBlockState(pos)
                val recipe = world.recipeManager.getAllRecipesFor(HexRecipeStuffRegistry.BRAINSWEEP_TYPE)
                    .find { it.matches(state, sacrifice, world) }
                if (recipe != null) {
                    return SpellAction.Result(
                        NonMobRecipe(pos, state, sacrifice, recipe),
                        recipe.mediaCost,
                        listOf(
                            ParticleSpray.cloud(sacrifice.position(), 1.0),
                            ParticleSpray.burst(Vec3.atCenterOf(pos), 0.3, 100)
                        )
                    )
                }
            }

            // 2. general callback
            val entry = BrainsweepCallback[Pair(sacrifice.type, data.type)]
            (entry as? BCFunc<Entity, Iota>)?.let { it(sacrifice, data, env) }?.let { return it }

            throw USE_ORIGINAL
        }

        data class NonMobRecipe(
            val pos: BlockPos,
            val state: BlockState,
            val sacrifice: Entity,
            val recipe: BrainsweepRecipe
        ) : RenderedSpell {
            override fun cast(env: CastingEnvironment) {
                env.world.let {
                    it.setBlockAndUpdate(pos, BrainsweepRecipe.copyProperties(state, recipe.result))
                    it.playSound(null, sacrifice, SoundEvents.PLAYER_LEVELUP, SoundSource.AMBIENT, 0.5f, 0.8f)

                    // destroy sacrifice
                    val src = (it.damageSources() as AccessorDamageSources).callSource(
                        HexDamageTypes.OVERCAST,
                        env.castingEntity
                    )
                    sacrifice.hurt(src, if (sacrifice is LivingEntity) sacrifice.health else 114514f)
                }
            }
        }
    }
}