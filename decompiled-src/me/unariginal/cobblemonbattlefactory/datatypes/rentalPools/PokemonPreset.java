/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cobblemon.mod.common.pokemon.Pokemon
 *  com.google.gson.JsonObject
 */
package me.unariginal.cobblemonbattlefactory.datatypes.rentalPools;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.JsonObject;
import java.util.UUID;
import me.unariginal.cobblemonbattlefactory.utils.ConfigUtils;

public record PokemonPreset(UUID uuid, String name, JsonObject pokemonPreset) {
    public Pokemon getPokemon() {
        return ConfigUtils.getPokemon(this.pokemonPreset);
    }
}

