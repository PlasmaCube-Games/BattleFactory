/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_243
 *  net.minecraft.class_3222
 */
package me.unariginal.cobblemonbattlefactory.managers;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.UUID;
import me.unariginal.cobblemonbattlefactory.BattleFactory;
import me.unariginal.cobblemonbattlefactory.managers.BattleFactoryInstance;
import me.unariginal.cobblemonbattlefactory.utils.TextUtils;
import net.minecraft.class_243;
import net.minecraft.class_3222;

public class TickManager {
    public static void tickTimers() throws ConcurrentModificationException {
        for (BattleFactoryInstance battleFactoryInstance : BattleFactory.INSTANCE.battleFactoryInstances) {
            ++battleFactoryInstance.instanceTimer;
            if (battleFactoryInstance.roundTransition) {
                --battleFactoryInstance.roundTimer;
                battleFactoryInstance.challenger.method_7353(TextUtils.deserialize(TextUtils.parse(BattleFactory.INSTANCE.messagesConfig().getMessage("overlay_nextRoundTimer"), battleFactoryInstance)), true);
                if (battleFactoryInstance.roundTimer > 0) continue;
                battleFactoryInstance.roundTransition = false;
                battleFactoryInstance.setupRound();
                continue;
            }
            if (!battleFactoryInstance.inBonusEncounter) {
                battleFactoryInstance.challenger.method_7353(TextUtils.deserialize(TextUtils.parse(BattleFactory.INSTANCE.messagesConfig().getMessage("overlay_currentStatus"), battleFactoryInstance)), true);
                continue;
            }
            battleFactoryInstance.challenger.method_7353(TextUtils.deserialize(TextUtils.parse(BattleFactory.INSTANCE.messagesConfig().getMessage("overlay_bonusEncounter"), battleFactoryInstance)), true);
        }
        ArrayList<class_3222> toRemove = new ArrayList<class_3222>();
        for (Map.Entry<class_3222, Long> entry : BattleFactory.INSTANCE.removeAfterTicks.entrySet()) {
            entry.setValue(entry.getValue() - 1L);
            if (entry.getValue() > 0L) continue;
            BattleFactory.INSTANCE.stopBattleFactoryInstance(entry.getKey());
            toRemove.add(entry.getKey());
        }
        for (class_3222 p : toRemove) {
            BattleFactory.INSTANCE.removeAfterTicks.remove(p);
        }
    }

    public static void preventPlayerTeleport() throws ConcurrentModificationException {
        for (BattleFactoryInstance battleFactoryInstance : BattleFactory.INSTANCE.battleFactoryInstances) {
            if (!battleFactoryInstance.preventTeleportation) continue;
            class_243 pos = new class_243(battleFactoryInstance.roundLocation.playerX(), battleFactoryInstance.roundLocation.playerY(), battleFactoryInstance.roundLocation.playerZ());
            if (battleFactoryInstance.challenger.method_51469() == battleFactoryInstance.roundLocation.world() && !(battleFactoryInstance.challenger.method_19538().method_1022(pos) > (double)BattleFactory.INSTANCE.config().maxDistanceFromBattle)) continue;
            battleFactoryInstance.challenger.method_14251(battleFactoryInstance.roundLocation.world(), battleFactoryInstance.roundLocation.playerX(), battleFactoryInstance.roundLocation.playerY(), battleFactoryInstance.roundLocation.playerZ(), battleFactoryInstance.roundLocation.playerYRot(), battleFactoryInstance.roundLocation.playerXRot());
        }
    }

    public static void tickCooldowns() throws ConcurrentModificationException {
        for (UUID uuid : BattleFactory.INSTANCE.playerCooldowns.keySet()) {
            class_3222 player;
            if (!BattleFactory.INSTANCE.config().tickOfflinePlayerCooldowns && (player = BattleFactory.INSTANCE.server().method_3760().method_14602(uuid)) == null) continue;
            BattleFactory.INSTANCE.tickCooldown(uuid);
        }
        BattleFactory.INSTANCE.clearCooldowns();
    }
}

