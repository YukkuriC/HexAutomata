# 如何创建`PatchAction`与注入已有图案逻辑

**简体中文** [English](../en_us/patch-action.md)

> 生成于GLM-5.1

## 概述

`PatchAction` 是 HexAutomata 提供的一种机制，允许你**替换/包装** HexCasting 中已注册的 Action，从而在保留原始逻辑的基础上注入自定义行为。

核心思路：将原始 Action 与补丁 Action 组合——补丁优先执行，若补丁抛出特定信号则回退到原始逻辑或终止施法。

## 关键类

### PatchAction

```kotlin
open class PatchAction(val original: Action, val patcher: Action) : Action
```

`PatchAction` 同时持有 `original`（原始 Action）和 `patcher`（补丁 Action），自身也实现了 `Action` 接口。其 `operate` 方法的执行流程如下：

1. 尝试执行 `patcher.operate()`
2. 若补丁正常返回，直接使用补丁结果
3. 若补丁抛出异常，进入异常处理分支：
   - 抛出 `PatchAction.USE_ORIGINAL` → 回退执行 `original.operate()`
   - 抛出 `PatchAction.STOP_ALL` → 静默终止施法（无额外效果）
   - 其他异常 → 正常向上抛出

### 两个控制信号

| 信号 | 效果 |
|------|------|
| `PatchAction.USE_ORIGINAL` | 放弃补丁逻辑，回退到原始 Action |
| `PatchAction.STOP_ALL` | 完全终止当前施法，不执行任何效果 |

这两个信号本质上是 `NoTraced` 的实例——一种不填充栈追踪的 `Throwable`，用于控制流而非真正的错误。

## 注册补丁：HAPatches

`HAPatches` 是 HexAutomata 内置的补丁注册表，继承自 `CustomRegisterObject<Action>`。它通过 Mixin 访问器 `AccessorActionRegistryEntry` 在运行时替换注册表中的 Action。

### 工作原理

```kotlin
object HAPatches : CustomRegisterObject<Action>() {
    init {
        // 以 HexCasting 的 brainsweep 图案为例
        this[HexAPI.modLoc("brainsweep")] = OpExtendBrainsweep
    }

    fun patchAll() {
        for (pair in MAP.entries) {
            val entry = HexActions.REGISTRY[pair.key]
                ?: throw IllegalArgumentException("invalid patch for id ${pair.key}")
            (entry as AccessorActionRegistryEntry).setAction(pair.value)
        }
    }
}
```

流程分解：

1. **声明补丁**：以 `ResourceLocation` 为键、补丁 Action 为值注册映射。键必须对应一个**已注册**的 Action，否则 `patchAll()` 会抛出异常
2. **应用补丁**：`patchAll()` 遍历所有映射，通过 Mixin 访问器将注册表条目中的 Action 替换为补丁 Action
3. **替换而非丢失**：补丁 Action 通常是 `PatchAction` 的子类，`original` 参数应传入被替换的原注册 Action，以确保 `USE_ORIGINAL` 信号能正确回退

### Mixin 访问器

```java
@Mixin(ActionRegistryEntry.class)
public interface AccessorActionRegistryEntry {
    @Mutable
    @Accessor(remap = false)
    void setAction(Action v);
}
```

该 Mixin 使 `ActionRegistryEntry` 的 `action` 字段变为可写，从而允许运行时替换。

## 实战示例

### 示例：创建一个自定义 PatchAction

以下示例展示如何为一个已有的 HexCasting 图案创建补丁，在原始逻辑前插入自定义行为：

```kotlin
import io.yukkuric.hexautomata.action_patch.PatchAction
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation

object MyCustomPatch : PatchAction(
    original = getOriginalAction(),  // 获取原始 Action
    patcher = MyPatcherAction
) {
    object MyPatcherAction : Action {
        override fun operate(
            env: CastingEnvironment,
            image: CastingImage,
            continuation: SpellContinuation
        ): OperationResult {
            // 自定义前置逻辑
            if (shouldSkip(env)) {
                // 条件满足时，回退到原始行为
                throw USE_ORIGINAL
            }

            if (shouldBlock(env)) {
                // 条件满足时，完全阻止施法
                throw STOP_ALL
            }

            // 否则执行自定义逻辑并返回结果
            return myCustomOperate(env, image, continuation)
        }
    }
}
```

### 在 HAPatches 中注册

```kotlin
// 在注册阶段之后（建议在 Common Setup 阶段内）运行
HAPatches[HexAPI.modLoc("existed_pattern_id")] = MyCustomPatch
```

> **注意**：`HAPatches` 的 `init` 块为 HexAutomata 内部使用，不可修改。自定义补丁应在注册阶段之后、施法发生之前注册，推荐在 Common Setup 生命周期事件中执行。

### 通过 KubeJS 使用

HexAutomata 通过 KubeJS 插件将 `PatchAction` 和 `HAPatches` 暴露为脚本绑定，你可以在 KubeJS 脚本中直接使用控制信号：

```js
// 在服务器/启动脚本中
// 抛出 USE_ORIGINAL 以回退到原始逻辑
throw PatchAction.USE_ORIGINAL

// 抛出 STOP_ALL 以终止施法
throw PatchAction.STOP_ALL
```

## 注意事项

- `PatchAction` 的异常处理会自动解包 KubeJS 的 `JavaScriptException`，因此从 KubeJS 脚本中抛出 `PatchAction.USE_ORIGINAL` 或 `PatchAction.STOP_ALL` 同样有效
- 补丁的注册时机很重要——`patchAll()` 需要在 Action 注册表冻结之后、施法发生之前被调用
- `USE_ORIGINAL` 和 `STOP_ALL` 不是真正的异常，它们是控制流信号，不会出现在日志或错误报告中
