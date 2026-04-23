/*
 * Decompiled with CFR 0.152.
 */
package me.unariginal.cobblemonbattlefactory.datatypes.rentalPools;

import java.util.Map;
import java.util.UUID;
import me.unariginal.cobblemonbattlefactory.datatypes.rentalPools.PokemonPool;
import me.unariginal.cobblemonbattlefactory.datatypes.rentalPools.PokemonPreset;
import me.unariginal.cobblemonbattlefactory.utils.RandomUtils;

public class SetPokemonPool
extends PokemonPool {
    public Map<PokemonPreset, Double> pool;

    public SetPokemonPool(UUID uuid, String name, String type, Map<PokemonPreset, Double> pool) {
        super(uuid, name, type);
        this.pool = pool;
    }

    public PokemonPreset getRandomPokemon() {
        Map.Entry<?, Double> entry = RandomUtils.getRandomEntry(this.pool);
        if (entry == null) {
            return null;
        }
        return (PokemonPreset)entry.getKey();
    }
}

