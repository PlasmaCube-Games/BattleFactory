/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2170
 *  net.minecraft.class_3222
 *  net.minecraft.server.MinecraftServer
 */
package me.unariginal.cobblemonbattlefactory.datatypes.rewards;

import java.util.List;
import java.util.UUID;
import me.unariginal.cobblemonbattlefactory.datatypes.rewards.Reward;
import me.unariginal.cobblemonbattlefactory.utils.TextUtils;
import net.minecraft.class_2170;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public class CommandReward
extends Reward {
    public List<String> commands;

    public CommandReward(UUID uuid, String name, String type, List<String> commands) {
        super(uuid, name, type);
        this.commands = commands;
    }

    @Override
    public void grant_reward(class_3222 player) {
        MinecraftServer minecraftServer = player.method_5682();
        if (minecraftServer != null) {
            class_2170 commandManager = minecraftServer.method_3734();
            for (String command : this.commands) {
                commandManager.method_44252(minecraftServer.method_3739(), TextUtils.parse(command.replaceAll("%player%", player.method_5820()), player));
            }
        }
    }
}

