/*
 * Decompiled with CFR 0.152.
 */
package me.plascmabue.cobblemonbattlefactory.datatypes;

import java.util.Map;
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import me.plascmabue.cobblemonbattlefactory.datatypes.Location;
import me.plascmabue.cobblemonbattlefactory.datatypes.rentalPools.PokemonPool;
import me.plascmabue.cobblemonbattlefactory.datatypes.rewards.DistributionSection;
import me.plascmabue.cobblemonbattlefactory.utils.RandomUtils;

public record TierSettings(String tierID, String tierName, String tierItem, int battlesForNextTier, Map<Integer, Location> battleLocations, Map<String, Double> possibleNPCS, Map<String, Double> possibleRentalPools, DistributionSection perBattleRewards, Map<Integer, DistributionSection> perRoundRewards, DistributionSection tierCompletionRewards, boolean hasBonusEncounter, String bonusEncounterNPC, Location bonusEncounterLocation, DistributionSection bonusEncounterRewards) {
    public PokemonPool getPokemonPool() {
        Map.Entry<?, Double> entry = RandomUtils.getRandomEntry(this.possibleRentalPools);
        if (entry == null) {
            return null;
        }
        String pokemonPoolId = (String)entry.getKey();
        return BattleFactory.INSTANCE.rentalPoolsConfig().getPokemonPool(pokemonPoolId);
    }

    public String getNPCId() {
        Map.Entry<?, Double> entry = RandomUtils.getRandomEntry(this.possibleNPCS);
        if (entry == null) {
            return null;
        }
        return (String)entry.getKey();
    }
}

