/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 */
package me.plascmabue.cobblemonbattlefactory.datatypes.rewards;

import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;

public class Reward {
    public UUID uuid;
    public String name;
    public String type;

    public Reward(UUID uuid, String name, String type) {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
    }

    public void grant_reward(ServerPlayer player) {
    }
}

