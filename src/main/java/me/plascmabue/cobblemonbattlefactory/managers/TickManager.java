/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.server.level.ServerPlayer
 */
package me.plascmabue.cobblemonbattlefactory.managers;

import com.cobblemon.mod.common.battles.BattleRegistry;
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
    // Maintenance counter for the periodic instance-count log / offline purge (see tickTimers).
    private static int maintenanceTick = 0;

    public static void tickTimers() throws ConcurrentModificationException {
        // Périodique (toutes les ~30 s) : on log le nombre d'instances actives et on purge celles
        // dont le joueur est hors-ligne. L'overlay tourne à chaque tick PAR instance ; une instance
        // « morte » qui traîne = coût CPU permanent (cf. crash watchdog max-tick-time).
        if (++maintenanceTick >= 600) {
            maintenanceTick = 0;
            int count = BattleFactory.INSTANCE.battleFactoryInstances.size();
            if (count > 0) {
                BattleFactory.LOGGER.info("[BattleFactory] Instances actives: {}", count);
            }
            if (count > 25) {
                BattleFactory.LOGGER.warn("[BattleFactory] Beaucoup d'instances BF actives ({}) — fuite possible ?", count);
            }
            ArrayList<ServerPlayer> offline = new ArrayList<ServerPlayer>();
            for (BattleFactoryInstance instance : BattleFactory.INSTANCE.battleFactoryInstances) {
                ServerPlayer challenger = instance.challenger;
                if (challenger == null || BattleFactory.INSTANCE.server().getPlayerList().getPlayer(challenger.getUUID()) == null) {
                    if (challenger != null) offline.add(challenger);
                }
            }
            for (ServerPlayer p : offline) {
                BattleFactory.LOGGER.warn("[BattleFactory] Purge instance orpheline (joueur hors-ligne): {}", p.getScoreboardName());
                BattleFactory.INSTANCE.stopBattleFactoryInstance(p);
            }
        }
        for (BattleFactoryInstance battleFactoryInstance : BattleFactory.INSTANCE.battleFactoryInstances) {
            ++battleFactoryInstance.instanceTimer;
            // Orphan-battle watchdog: if we have an active battle id but the battle is gone from
            // the registry (Showdown error / player flee) while not transitioning, the BATTLE_VICTORY
            // handler never ran. Clean up the leftover NPC pokemon and end the run as a loss.
            if (!battleFactoryInstance.roundTransition
                    && battleFactoryInstance.currentBattleID != null
                    && BattleRegistry.getBattle(battleFactoryInstance.currentBattleID) == null) {
                if (++battleFactoryInstance.missingBattleTicks >= 3) {
                    BattleFactory.LOGGER.warn("[BattleFactory] Orphan battle detected for {} (battleId={} gone) — forcing cleanup.",
                            battleFactoryInstance.challenger.getScoreboardName(), battleFactoryInstance.currentBattleID);
                    battleFactoryInstance.currentBattleID = null;
                    battleFactoryInstance.removeNpc();
                    BattleFactory.INSTANCE.removeAfterTicks.put(battleFactoryInstance.challenger, 5L);
                    continue;
                }
            } else {
                battleFactoryInstance.missingBattleTicks = 0;
            }
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

