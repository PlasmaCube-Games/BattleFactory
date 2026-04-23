/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.commands.Commands
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.MinecraftServer
 */
package me.plascmabue.cobblemonbattlefactory.datatypes.rewards;

import java.util.List;
import java.util.UUID;
import me.plascmabue.cobblemonbattlefactory.datatypes.rewards.Reward;
import me.plascmabue.cobblemonbattlefactory.utils.TextUtils;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;

public class CommandReward
extends Reward {
    public List<String> commands;

    public CommandReward(UUID uuid, String name, String type, List<String> commands) {
        super(uuid, name, type);
        this.commands = commands;
    }

    @Override
    public void grant_reward(ServerPlayer player) {
        MinecraftServer minecraftServer = player.getServer();
        if (minecraftServer != null) {
            Commands commandManager = minecraftServer.getCommands();
            for (String command : this.commands) {
                String resolved = TextUtils.parse(command.replaceAll("%player%", player.getScoreboardName()), player);
                me.plascmabue.cobblemonbattlefactory.BattleFactory.LOGGER.info("[BattleFactory] Running reward command: {}", resolved);
                commandManager.performPrefixedCommand(minecraftServer.createCommandSourceStack(), resolved);
            }
        }
    }
}

