#!/usr/bin/env python3
"""Remap Yarn intermediary identifiers to Mojang names in decompiled CFR output.

Usage: python remap.py <src_dir> <dst_dir>
"""
import os
import re
import sys

# Mapping for MC 1.21.1. Only entries actually used in the decompiled BattleFactory.
CLASS_MAP = {
    "class_243": "Vec3",
    "class_1271": "InteractionResultHolder",
    "class_1282": "DamageSource",
    "class_1293": "MobEffectInstance",
    "class_1294": "MobEffects",
    "class_1297": "Entity",
    "class_1309": "LivingEntity",
    "class_1707": "ChestMenu",
    "class_1792": "Item",
    "class_1799": "ItemStack",
    "class_1935": "ItemLike",
    "class_1937": "Level",
    "class_17922": "Items",
    "class_2168": "CommandSourceStack",
    "class_2170": "Commands",
    "class_2186": "EntityArgument",
    "class_2487": "CompoundTag",
    "class_2561": "Component",
    "class_2960": "ResourceLocation",
    "class_3218": "ServerLevel",
    "class_3222": "ServerPlayer",
    "class_3917": "MenuType",
    "class_5529": "RemovalReason",
    "class_7923": "BuiltInRegistries",
    "class_9290": "ItemLore",
    "class_9323": "DataComponentMap",
    "class_9326": "DataComponentPatch",
    "class_9334": "DataComponents",
}

CLASS_FQN = {
    "Vec3": "net.minecraft.world.phys.Vec3",
    "InteractionResultHolder": "net.minecraft.world.InteractionResultHolder",
    "DamageSource": "net.minecraft.world.damagesource.DamageSource",
    "MobEffectInstance": "net.minecraft.world.effect.MobEffectInstance",
    "MobEffects": "net.minecraft.world.effect.MobEffects",
    "Entity": "net.minecraft.world.entity.Entity",
    "LivingEntity": "net.minecraft.world.entity.LivingEntity",
    "ChestMenu": "net.minecraft.world.inventory.ChestMenu",
    "Item": "net.minecraft.world.item.Item",
    "ItemStack": "net.minecraft.world.item.ItemStack",
    "ItemLike": "net.minecraft.world.level.ItemLike",
    "Items": "net.minecraft.world.item.Items",
    "Level": "net.minecraft.world.level.Level",
    "CommandSourceStack": "net.minecraft.commands.CommandSourceStack",
    "Commands": "net.minecraft.commands.Commands",
    "EntityArgument": "net.minecraft.commands.arguments.EntityArgument",
    "CompoundTag": "net.minecraft.nbt.CompoundTag",
    "Component": "net.minecraft.network.chat.Component",
    "ResourceLocation": "net.minecraft.resources.ResourceLocation",
    "ServerLevel": "net.minecraft.server.level.ServerLevel",
    "ServerPlayer": "net.minecraft.server.level.ServerPlayer",
    "MenuType": "net.minecraft.world.inventory.MenuType",
    "BuiltInRegistries": "net.minecraft.core.registries.BuiltInRegistries",
    "ItemLore": "net.minecraft.world.item.component.ItemLore",
    "DataComponentMap": "net.minecraft.core.component.DataComponentMap",
    "DataComponentPatch": "net.minecraft.core.component.DataComponentPatch",
    "DataComponents": "net.minecraft.core.component.DataComponents",
}

INNER_CLASS_MAP = {
    "Entity.class_5529": "Entity.RemovalReason",
}

# Only methods with unambiguous global intermediary -> Mojang mapping.
METHOD_MAP = {
    # Entity / Player
    "method_5667": "getUUID",
    "method_5650": "remove",
    "method_5820": "getScoreboardName",
    "method_5476": "getDisplayName",
    "method_5682": "getServer",
    "method_5808": "moveTo",
    "method_5998": "getItemInHand",
    "method_6016": "removeEffect",
    "method_6092": "addEffect",
    "method_7353": "displayClientMessage",
    "method_14251": "teleportTo",
    "method_19538": "position",
    "method_23317": "getX",
    "method_23318": "getY",
    "method_23321": "getZ",
    "method_24515": "blockPosition",
    "method_27983": "dimension",
    "method_30002": "overworld",
    "method_31548": "getInventory",
    "method_32311": "getPlayer",
    "method_14602": "getPlayer",
    "method_36454": "getYRot",
    "method_36455": "getXRot",
    "method_37908": "level",
    "method_43496": "sendSystemMessage",
    "method_44252": "getDispatcher",
    "method_51469": "serverLevel",
    "method_3734": "getCommands",
    "method_3738": "getAllLevels",
    "method_3760": "getPlayerList",
    "method_3739": "createCommandSourceStack",
    "method_44023": "getPlayer",
    "method_44252": "performPrefixedCommand",
    "method_45068": "sendSystemMessage",
    "method_29177": "location",
    "method_46558": "getCenter",
    "method_1031": "add",
    "method_1022": "distanceTo",
    "method_8649": "addFreshEntity",
    "method_5845": "getStringUUID",
    "method_6082": "randomTeleport",
    "method_7398": "add",
    "method_57848": "isEmpty",
    "method_43737": "isPlayer",
    # Item / ItemStack
    "method_7854": "getDefaultInstance",
    "method_7909": "getItem",
    "method_7947": "getCount",
    "method_7939": "setCount",
    "method_7960": "isEmpty",
    "method_57365": "applyComponents",
    "method_59692": "applyComponents",
    # Registry (class_7923 / IdMap)
    "method_10221": "getKey",
    "method_10223": "get",
    "method_10250": "containsKey",
    # CompoundTag (class_2487) -- only map the ones actually used
    "method_10545": "contains",
    "method_10556": "putBoolean",
    "method_10577": "getBoolean",
    "method_10558": "getString",
    "method_10582": "putString",
    "method_10544": "getList",
    "method_10562": "getCompound",
    "method_10566": "put",
    "method_10569": "putInt",
    "method_10550": "getInt",
    "method_10580": "get",
    "method_10551": "remove",
    # Vec3 accessors (method_10216 = x, method_10214 = y, method_10215 = z on class_243)
    "method_10216": "x",
    "method_10214": "y",
    "method_10215": "z",
    # ResourceLocation
    "method_12832": "getPath",
    "method_12836": "getNamespace",
    "method_60654": "parse",
    # InteractionResultHolder
    "method_22430": "pass",
    "method_22431": "fail",
    "method_22428": "consume",
    "method_22429": "sidedSuccess",
    # Commands / EntityArgument
    "method_9244": "argument",
    "method_9247": "literal",
    "method_9305": "player",
    "method_9315": "getPlayer",
    # DataComponentMap / Patch builder
    "method_57827": "builder",
    "method_57840": "set",
    "method_57838": "build",
}

# Static fields of well-known classes.
FIELD_MAP = {
    # Vec3
    "field_1350": "z",
    "field_1351": "y",
    "field_1352": "x",
    # RemovalReason
    "field_26999": "DISCARDED",
    # BuiltInRegistries.ITEM
    "field_41178": "ITEM",
    # DataComponentPatch
    "field_49588": "EMPTY",
    "field_49589": "CODEC",
    # DataComponents (common item components)
    "field_49631": "CUSTOM_NAME",
    "field_49632": "LORE",
    "field_49641": "CUSTOM_MODEL_DATA",
    # MenuType chest rows
    "field_17326": "GENERIC_9x1",
    "field_17327": "GENERIC_9x2",
    "field_18664": "GENERIC_9x3",
    "field_18665": "GENERIC_9x4",
    "field_18666": "GENERIC_9x5",
    "field_18667": "GENERIC_9x6",
    # MobEffects.field_38092 -> looked up as MobEffects.DARKNESS (introduced 1.19)
    "field_38092": "DARKNESS",
}

TOKEN_RE = re.compile(r"\b(class_\d+|method_\d+|field_\d+)\b")


def rewrite(text: str) -> str:
    for intermediary, name in CLASS_MAP.items():
        fqn = CLASS_FQN.get(name)
        if fqn:
            text = text.replace(f"net.minecraft.{intermediary}", fqn)

    def sub(match: re.Match) -> str:
        tok = match.group(1)
        if tok in CLASS_MAP:
            return CLASS_MAP[tok]
        if tok in METHOD_MAP:
            return METHOD_MAP[tok]
        if tok in FIELD_MAP:
            return FIELD_MAP[tok]
        return tok

    text = TOKEN_RE.sub(sub, text)

    for src, dst in INNER_CLASS_MAP.items():
        text = text.replace(src, dst)

    # Drop `void varN_M;` CFR artifacts.
    text = re.sub(r"^\s*void var\d+_\d+;\s*\n", "", text, flags=re.M)
    # Spurious (Object) casts that CFR emits when it cannot resolve generics.
    text = text.replace("(Object)", "")

    # Mixin `this` casts need `(TargetClass)(Object)this` to compile.
    text = re.sub(
        r"\((PokemonEntity|ServerPlayer|LivingEntity|Entity|NPCEntity)\)this\b",
        r"(\1)(Object)this",
        text,
    )

    # CFR hoisted-block variable artifacts — rewrite the common pattern
    # `JsonObject jsonObject = new JsonObject();
    #  if (root.has("foo")) { JsonObject jsonObject2 = root.get("foo").getAsJsonObject(); }`
    # into a reassignment. This only patches the allocation site; the
    # `varN_M` references inside the outer scope still need a manual rename
    # because they refer to arbitrary names per file.
    return text


def main() -> None:
    if len(sys.argv) != 3:
        print("usage: remap.py <src_dir> <dst_dir>", file=sys.stderr)
        sys.exit(2)
    src_root, dst_root = sys.argv[1], sys.argv[2]
    for dirpath, _, filenames in os.walk(src_root):
        for filename in filenames:
            if not filename.endswith(".java"):
                continue
            src_path = os.path.join(dirpath, filename)
            rel = os.path.relpath(src_path, src_root)
            dst_path = os.path.join(dst_root, rel)
            os.makedirs(os.path.dirname(dst_path), exist_ok=True)
            with open(src_path, "r", encoding="utf-8") as f:
                text = f.read()
            with open(dst_path, "w", encoding="utf-8") as f:
                f.write(rewrite(text))


if __name__ == "__main__":
    main()
