/*
 * Decompiled with CFR 0.152.
 */
package me.plascmabue.cobblemonbattlefactory.datatypes;

import java.time.LocalDateTime;
import java.util.UUID;

public class LeaderboardSection {
    public int placement;
    public UUID uuid;
    public String name;
    public int highestStreak;
    public LocalDateTime dateAchieved;

    public LeaderboardSection(int placement, UUID uuid, String name, int highestStreak, LocalDateTime dateAchieved) {
        this.placement = placement;
        this.uuid = uuid;
        this.name = name;
        this.highestStreak = highestStreak;
        this.dateAchieved = dateAchieved;
    }
}

