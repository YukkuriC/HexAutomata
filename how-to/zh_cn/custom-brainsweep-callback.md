# 如何创建自定义`BrainsweepCallback`

**简体中文** [English](../en_us/custom-brainsweep-callback.md)

> 生成于GLM-5.1

## 概述

`BrainsweepCallback` 是 HexAutomata 提供的扩展机制，允许你为 Brainsweep（意识剥离）卓越法术注册自定义回调。当 Brainsweep 被施放时，所有匹配的回调会按优先级依次执行，第一个返回非 `null` 结果的回调将决定最终行为。

## BrainsweepCallback 结构

```kotlin
abstract class BrainsweepCallback<E : Entity, I : Iota>(
    var priority: Int,           // 优先级，数值越小越先执行
    val limitEntity: EntityType<E>?,  // 限制实体类型，null 表示不限
    val limitIota: IotaType<I>?,      // 限制 Iota 类型，null 表示不限
)
```

### 核心方法

```kotlin
abstract fun call(entity: E, iota: I, env: CastingEnvironment): SpellAction.Result?
```

- 返回 `SpellAction.Result?` —— 返回非 `null` 表示该回调处理了此次施法
- 返回 `null` 表示跳过，继续尝试下一个匹配的回调
- 抛出 `PatchAction.USE_ORIGINAL` → 回退到原始 Brainsweep 逻辑
- 抛出 `PatchAction.STOP_ALL` → 终止施法

## 使用 buildResult 简易创建返回值

`BrainsweepCallback.buildResult` 是一个便捷方法，用于快速构建 `SpellAction.Result`：

```kotlin
companion object {
    @JvmStatic
    fun buildResult(
        action: (CastingEnvironment) -> Unit,  // 施法时执行的操作
        cost: Long,                             // 媒质消耗
        vararg particles: ParticleSpray         // 粒子效果
    ): SpellAction.Result
}
```

### 示例：在 Kotlin 中使用 buildResult

```kotlin
import io.yukkuric.hexautomata.action_patch.brainsweep.BrainsweepCallback
import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.monster.Zombie
import net.minecraft.world.phys.Vec3

object ZombieCallback : BrainsweepCallback<Zombie, Iota>(
    priority = 100,
    limitEntity = EntityType.ZOMBIE as EntityType<Zombie>,
    limitIota = null
) {
    override fun call(entity: Zombie, iota: Iota, env: CastingEnvironment): SpellAction.Result? {
        // 自定义条件判断
        if (!entity.isBaby) {
            return buildResult(
                action = { _ ->
                    // 对成年僵尸执行自定义逻辑
                    entity.setBaby(true)
                },
                cost = MediaConstants.CRYSTAL_UNIT,
                ParticleSpray.cloud(entity.position(), 1.0)
            )
        }
        // 返回 null，让后续回调继续处理
        return null
    }
}
```

### 注册回调

```kotlin
BrainsweepCallback["my_mod/zombie_baby"] = ZombieCallback
```

键名为字符串，用于标识回调。若键名已被占用会抛出异常；如需覆盖，使用 `forceSet`：

```kotlin
BrainsweepCallback.forceSet("my_mod/zombie_baby", ZombieCallback)
```

## 通过 KubeJS 注册回调

HexAutomata 将 `BrainsweepCallback` 暴露为 KubeJS 绑定，你可以在服务器/启动脚本中直接注册回调。

### 使用 create 方法

```js
let myCallback = BrainsweepCallback.create(
    priority,           // 优先级（整数）
    entityId,           // 实体类型 ResourceLocation 字符串，null 表示不限
    iotaTypeId,         // Iota 类型 ResourceLocation 字符串，null 表示不限
    (entity, iota, env) => {
        // 返回 SpellAction.Result 或 null
        if (someCondition) {
            return BrainsweepCallback.buildResult(env => {
                // 施法效果
            }, cost)
        }
        return null  // 继续尝试后续回调
    }
);
BrainsweepCallback.forceSet("my_mod/my_callback", myCallback);
```

### 完整 KubeJS 示例

```js
// server_scripts/brainsweep_callbacks.js

let zombieCallback = BrainsweepCallback.create(
    50,
    "minecraft:zombie",     // 仅匹配僵尸
    null,                    // 不限制 Iota 类型
    (entity, iota, env) => {
        // 让僵尸着火
        return BrainsweepCallback.buildResult(env => {
            entity.setSecondsOnFire(5)
        }, 1000)  // 消耗 1000 媒质单位
    }
);
BrainsweepCallback.forceSet("my_mod/zombie_fire", zombieCallback);
```

```js
// 不限制实体和 Iota 类型的通用回调
let fallbackCallback = BrainsweepCallback.create(
    1919810, // 低优先级，作为兜底
    null,    // 不限制实体类型
    null,    // 不限制 Iota 类型
    (entity, iota, env) => {
        // 记录日志并阻止施法
        console.info(`Brainsweep blocked for ${entity.type}`)
        throw PatchAction.STOP_ALL
    }
);
BrainsweepCallback.forceSet("my_mod/fallback", fallbackCallback);
```

### ID 命名空间自动映射

KubeJS 中，筛选参数接受字符串输入，将自动转换为 `ResourceLocation`。若字符串不含命名空间部分：

- `entityId` 将补充 `minecraft:`（Minecraft 默认行为）
- `iotaTypeId` 会进一步替换为 `hexcasting:`，以支持简写

例如：

- `"zombie"` → entityType 查找 `minecraft:zombie`
- `"entity"` → iotaType 查找 `hexcasting:entity`
- `"hexcasting:vec3"` → 直接使用 `hexcasting:vec3`

## 回调匹配与优先级

当 Brainsweep 被施放时，`BrainsweepCallback.callAll()` 的工作流程：

1. 根据当前实体类型和 Iota 类型，从缓存中筛选匹配的回调
2. 匹配规则：回调的 `limitEntity` 和 `limitIota` 为 `null` 或与当前类型一致
3. 按优先级升序排列（数值越小越先执行）
4. 依次调用，第一个返回非 `null` 的回调决定结果
5. 若所有回调均返回 `null`，则回退到原始 Brainsweep 逻辑

## 注意事项

- `priority` 数值越小越先执行；`Int.MIN_VALUE` 是最高优先级，`Int.MAX_VALUE` 是最低
- 回调结果会被缓存，注册新回调后缓存会自动清除
- 在 KubeJS 中抛出 `PatchAction.USE_ORIGINAL` 或 `PatchAction.STOP_ALL` 同样有效
- `buildResult` 的 `action` 参数接收 `CastingEnvironment`，你可以在其中访问施法者和世界信息
