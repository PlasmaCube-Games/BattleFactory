/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cobblemon.mod.common.api.pokemon.PokemonProperties
 *  com.cobblemon.mod.common.api.pokemon.PokemonSpecies
 *  com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature
 *  com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature
 *  com.cobblemon.mod.common.pokemon.FormData
 *  com.cobblemon.mod.common.pokemon.Pokemon
 *  com.cobblemon.mod.common.pokemon.Species
 */
package me.plascmabue.cobblemonbattlefactory.utils;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import java.util.Random;

public class PokemonUtils {
    public static Pokemon getRandomPokemon(int level) {
        PokemonProperties pokemonProperties = new PokemonProperties();
        Species species = (Species)PokemonSpecies.INSTANCE.getImplemented().get(new Random().nextInt(PokemonSpecies.INSTANCE.getImplemented().size()));
        pokemonProperties.setSpecies(species.showdownId());
        if (!species.getForms().isEmpty()) {
            pokemonProperties.setForm(((FormData)species.getForms().get(new Random().nextInt(species.getForms().size()))).formOnlyShowdownId());
        } else {
            pokemonProperties.setForm(species.getStandardForm().formOnlyShowdownId());
        }
        pokemonProperties.setLevel(Integer.valueOf(level));
        PokemonUtils.fixProperties(pokemonProperties);
        return pokemonProperties.create();
    }

    public static void fixProperties(PokemonProperties properties) {
        for (String aspect : properties.getAspects()) {
            properties.getCustomProperties().add(new FlagSpeciesFeature(aspect, true));
            String[] split = aspect.split("-");
            String region = split[split.length - 1];
            if (region.equalsIgnoreCase("alolan")) {
                region = "alola";
                properties.getCustomProperties().add(new StringSpeciesFeature("region_bias", region));
            }
            if (region.equalsIgnoreCase("galarian")) {
                region = "galar";
                properties.getCustomProperties().add(new StringSpeciesFeature("region_bias", region));
            }
            if (region.equalsIgnoreCase("hisuian")) {
                region = "hisui";
                properties.getCustomProperties().add(new StringSpeciesFeature("region_bias", region));
            }
            if (!aspect.contains("striped")) continue;
            properties.getCustomProperties().add(new StringSpeciesFeature("fish_stripes", aspect.substring(0, aspect.indexOf("striped"))));
        }
    }
}

