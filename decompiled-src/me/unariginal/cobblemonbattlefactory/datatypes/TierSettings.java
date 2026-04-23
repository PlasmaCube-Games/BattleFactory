/*
 * Decompiled with CFR 0.152.
 */
package me.unariginal.cobblemonbattlefactory.datatypes;

import java.util.Map;
import me.unariginal.cobblemonbattlefactory.BattleFactory;
import me.unariginal.cobblemonbattlefactory.datatypes.Location;
import me.unariginal.cobblemonbattlefactory.datatypes.rentalPools.PokemonPool;
import me.unariginal.cobblemonbattlefactory.datatypes.rewards.DistributionSection;
import me.unariginal.cobblemonbattlefactory.utils.RandomUtils;

public record TierSettings(String tierID, String tierName, String tierItem, int battlesForNextTier, Map<Integer, Location> battleLocations, Map<String, Double> possibleNPCS, Map<String, Double> possibleRentalPools, DistributionSection perBattleRewards, DistributionSection tierCompletionRewards, boolean hasBonusEncounter, String bonusEncounterNPC, Location bonusEncounterLocation, DistributionSection bonusEncounterRewards) {
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

