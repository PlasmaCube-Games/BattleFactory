/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 *  net.luckperms.api.LuckPermsProvider
 *  net.luckperms.api.model.user.User
 *  net.luckperms.api.query.QueryOptions
 *  net.minecraft.server.level.ServerPlayer
 */
package me.plascmabue.cobblemonbattlefactory.utils;

import java.util.List;
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.server.level.ServerPlayer;

public class PermissionsHelper {
    private static User getLuckPermsUser(ServerPlayer player) {
        return LuckPermsProvider.get().getPlayerAdapter(ServerPlayer.class).getUser(player);
    }

    public static long getBaseCooldown(ServerPlayer player) {
        int returnCooldown = -1;
        if (FabricLoader.getInstance().isModLoaded("luckperms")) {
            List<String> cooldown = PermissionsHelper.getLuckPermsUser(player).resolveInheritedNodes(QueryOptions.nonContextual()).stream().filter(node -> node.getKey().startsWith("battlefactory.cooldown.")).map(node -> node.getKey().substring("battlefactory.cooldown.".length())).toList();
            for (String cooldownStr : cooldown) {
                try {
                    int tempCooldown = Integer.parseInt(cooldownStr);
                    if (tempCooldown * 20 <= returnCooldown) continue;
                    returnCooldown = tempCooldown * 20;
                }
                catch (NumberFormatException e) {
                    BattleFactory.INSTANCE.logError("[BattleFactory] Could not parse cooldown time: " + cooldownStr);
                }
            }
        }
        return returnCooldown == -1 ? 36000L : (long)returnCooldown;
    }
}

