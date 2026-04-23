/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 */
package me.plascmabue.cobblemonbattlefactory.datatypes.rewards;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import me.plascmabue.cobblemonbattlefactory.datatypes.rewards.Reward;
import me.plascmabue.cobblemonbattlefactory.datatypes.rewards.RewardPool;
import net.minecraft.server.level.ServerPlayer;

public record DistributionSection(boolean allowDuplicates, int minRolls, int maxRolls, Map<RewardPool, Double> rewardPools) {
    public List<Reward> distributeRewards(ServerPlayer player) {
        ArrayList<Reward> rewardsToDistribute = new ArrayList<Reward>();
        ArrayList<UUID> appliedRewards = new ArrayList<UUID>();
        int rolls = new Random().nextInt(this.minRolls, this.maxRolls + 1);
        for (int i = 0; i < rolls; ++i) {
            double totalWeight = 0.0;
            for (RewardPool rewardPool : this.rewardPools.keySet()) {
                if (!this.allowDuplicates && appliedRewards.contains(rewardPool.uuid())) continue;
                totalWeight += this.rewardPools.get(rewardPool).doubleValue();
            }
            if (!(totalWeight > 0.0)) continue;
            double randomWeight = new Random().nextDouble(totalWeight);
            totalWeight = 0.0;
            RewardPool rewardToGive = null;
            for (RewardPool rewardPool : this.rewardPools.keySet()) {
                if (!this.allowDuplicates && appliedRewards.contains(rewardPool.uuid()) || !(randomWeight < (totalWeight += this.rewardPools.get(rewardPool).doubleValue()))) continue;
                rewardToGive = rewardPool;
                break;
            }
            if (rewardToGive == null) continue;
            BattleFactory.INSTANCE.logInfo("[BattleFactory] Distribution Section for " + player.getScoreboardName() + ". Rolling reward pool [" + String.valueOf(rewardToGive.uuid()) + ", " + rewardToGive.name() + "]");
            rewardsToDistribute.addAll(rewardToGive.distributeRewards(player));
            appliedRewards.add(rewardToGive.uuid());
        }
        BattleFactory.INSTANCE.logInfo("[BattleFactory] Distribution Section for " + player.getScoreboardName() + ". Adding the following rewards to reward collection:");
        for (Reward reward : rewardsToDistribute) {
            BattleFactory.INSTANCE.logInfo("  " + String.valueOf(reward.uuid) + ", " + reward.name + ", " + reward.type);
        }
        return rewardsToDistribute;
    }
}

