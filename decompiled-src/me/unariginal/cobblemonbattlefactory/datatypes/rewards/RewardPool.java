/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_3222
 */
package me.unariginal.cobblemonbattlefactory.datatypes.rewards;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import me.unariginal.cobblemonbattlefactory.datatypes.rewards.Reward;
import net.minecraft.class_3222;

public record RewardPool(UUID uuid, String name, boolean allowDuplicates, int minRolls, int maxRolls, Map<Reward, Double> rewards) {
    public List<Reward> distributeRewards(class_3222 player) {
        ArrayList<Reward> rewardsToDistribute = new ArrayList<Reward>();
        ArrayList<UUID> appliedRewards = new ArrayList<UUID>();
        int rolls = new Random().nextInt(this.minRolls, this.maxRolls + 1);
        for (int i = 0; i < rolls; ++i) {
            double totalWeight = 0.0;
            for (Reward reward : this.rewards.keySet()) {
                if (!this.allowDuplicates && appliedRewards.contains(reward.uuid)) continue;
                totalWeight += this.rewards.get(reward).doubleValue();
            }
            if (!(totalWeight > 0.0)) continue;
            double randomWeight = new Random().nextDouble(totalWeight);
            totalWeight = 0.0;
            Reward rewardToGive = null;
            for (Reward reward : this.rewards.keySet()) {
                if (!this.allowDuplicates && appliedRewards.contains(reward.uuid) || !(randomWeight < (totalWeight += this.rewards.get(reward).doubleValue()))) continue;
                rewardToGive = reward;
                break;
            }
            if (rewardToGive == null) continue;
            rewardsToDistribute.add(rewardToGive);
            appliedRewards.add(rewardToGive.uuid);
        }
        return rewardsToDistribute;
    }
}

