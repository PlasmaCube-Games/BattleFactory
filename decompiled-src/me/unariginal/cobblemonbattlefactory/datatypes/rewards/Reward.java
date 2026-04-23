/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_3222
 */
package me.unariginal.cobblemonbattlefactory.datatypes.rewards;

import java.util.UUID;
import net.minecraft.class_3222;

public class Reward {
    public UUID uuid;
    public String name;
    public String type;

    public Reward(UUID uuid, String name, String type) {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
    }

    public void grant_reward(class_3222 player) {
    }
}

