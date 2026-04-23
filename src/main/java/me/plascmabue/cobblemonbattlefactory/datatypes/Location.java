/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 */
package me.plascmabue.cobblemonbattlefactory.datatypes;

import net.minecraft.server.level.ServerLevel;

public record Location(ServerLevel world, double playerX, double playerY, double playerZ, float playerYRot, float playerXRot, double npcX, double npcY, double npcZ) {
}

