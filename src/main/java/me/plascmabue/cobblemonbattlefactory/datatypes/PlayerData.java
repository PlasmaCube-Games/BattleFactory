/*
 * Decompiled with CFR 0.152.
 */
package me.plascmabue.cobblemonbattlefactory.datatypes;

import java.util.UUID;

public class PlayerData {
    public UUID uuid;
    public String username;
    public int highestStreak;
    public String highestCompletedTier;
    public long cooldownProgress;
    public long totalWins;
    public long winStreak;

    public PlayerData(UUID uuid, String username, int highestStreak, String highestCompletedTier, long cooldownProgress, long totalWins, long winStreak) {
        this.uuid = uuid;
        this.username = username;
        this.highestStreak = highestStreak;
        this.highestCompletedTier = highestCompletedTier;
        this.cooldownProgress = cooldownProgress;
        this.totalWins = totalWins;
        this.winStreak = winStreak;
    }
}

