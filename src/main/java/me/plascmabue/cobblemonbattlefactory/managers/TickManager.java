/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.server.level.ServerPlayer
 */
package me.plascmabue.cobblemonbattlefactory.managers;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.UUID;
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import me.plascmabue.cobblemonbattlefactory.managers.BattleFactoryInstance;
import me.plascmabue.cobblemonbattlefactory.utils.TextUtils;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerPlayer;

public class
TickManager {
    public static void tickTimers() throws ConcurrentModificationException {
        for (BattleFactoryInstance battleFactoryInstance : BattleFactory.INSTANCE.battleFactoryInstances) {
            ++battleFactoryInstance.instanceTimer;
            if (battleFactoryInstance.roundTransition) {
                --battleFactoryInstance.roundTimer;
                battleFactoryInstance.challenger.displayClientMessage(TextUtils.deserialize(TextUtils.parse(BattleFactory.INSTANCE.messagesConfig().getMessage("overlay_nextRoundTimer"), battleFactoryInstance)), true);
                if (battleFactoryInstance.roundTimer > 0) continue;
                battleFactoryInstance.roundTransition = false;
                battleFactoryInstance.setupRound();
                continue;
            }
            if (!battleFactoryInstance.inBonusEncounter) {
                battleFactoryInstance.challenger.displayClientMessage(TextUtils.deserialize(TextUtils.parse(BattleFactory.INSTANCE.messagesConfig().getMessage("overlay_currentStatus"), battleFactoryInstance)), true);
                continue;
            }
            battleFactoryInstance.challenger.displayClientMessage(TextUtils.deserialize(TextUtils.parse(BattleFactory.INSTANCE.messagesConfig().getMessage("overlay_bonusEncounter"), battleFactoryInstance)), true);
        }
        ArrayList<ServerPlayer> toRemove = new ArrayList<ServerPlayer>();
        for (Map.Entry<ServerPlayer, Long> entry : BattleFactory.INSTANCE.removeAfterTicks.entrySet()) {
            entry.setValue(entry.getValue() - 1L);
            if (entry.getValue() > 0L) continue;
            BattleFactory.INSTANCE.stopBattleFactoryInstance(entry.getKey());
            toRemove.add(entry.getKey());
        }
        for (ServerPlayer p : toRemove) {
            BattleFactory.INSTANCE.removeAfterTicks.remove(p);
        }
    }

    public static void preventPlayerTeleport() throws ConcurrentModificationException {
        for (BattleFactoryInstance battleFactoryInstance : BattleFactory.INSTANCE.battleFactoryInstances) {
            if (!battleFactoryInstance.preventTeleportation) continue;
            Vec3 pos = new Vec3(battleFactoryInstance.roundLocation.playerX(), battleFactoryInstance.roundLocation.playerY(), battleFactoryInstance.roundLocation.playerZ());
            if (battleFactoryInstance.challenger.serverLevel() == battleFactoryInstance.roundLocation.world() && !(battleFactoryInstance.challenger.position().distanceTo(pos) > (double)BattleFactory.INSTANCE.config().maxDistanceFromBattle)) continue;
            battleFactoryInstance.challenger.teleportTo(battleFactoryInstance.roundLocation.world(), battleFactoryInstance.roundLocation.playerX(), battleFactoryInstance.roundLocation.playerY(), battleFactoryInstance.roundLocation.playerZ(), battleFactoryInstance.roundLocation.playerYRot(), battleFactoryInstance.roundLocation.playerXRot());
        }
    }

    public static void tickCooldowns() throws ConcurrentModificationException {
        for (UUID uuid : BattleFactory.INSTANCE.playerCooldowns.keySet()) {
            ServerPlayer player;
            if (!BattleFactory.INSTANCE.config().tickOfflinePlayerCooldowns && (player = BattleFactory.INSTANCE.server().getPlayerList().getPlayer(uuid)) == null) continue;
            BattleFactory.INSTANCE.tickCooldown(uuid);
        }
        BattleFactory.INSTANCE.clearCooldowns();
    }
}

