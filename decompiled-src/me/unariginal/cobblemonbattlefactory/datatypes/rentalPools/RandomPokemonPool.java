/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cobblemon.mod.common.api.moves.Move
 *  com.cobblemon.mod.common.pokemon.Pokemon
 */
package me.unariginal.cobblemonbattlefactory.datatypes.rentalPools;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.pokemon.Pokemon;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import me.unariginal.cobblemonbattlefactory.datatypes.rentalPools.PokemonPool;
import me.unariginal.cobblemonbattlefactory.utils.PokemonUtils;

public class RandomPokemonPool
extends PokemonPool {
    public int minLevel;
    public int maxLevel;
    public List<String> blacklistSpecies;
    public List<String> blacklistFormIDs;
    public List<String> blacklistAbilities;
    public List<String> blacklistMoves;

    public RandomPokemonPool(UUID uuid, String name, String type, int minLevel, int maxLevel, List<String> blacklistSpecies, List<String> blacklistFormIDs, List<String> blacklistAbilities, List<String> blacklistMoves) {
        super(uuid, name, type);
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.blacklistSpecies = blacklistSpecies;
        this.blacklistFormIDs = blacklistFormIDs;
        this.blacklistAbilities = blacklistAbilities;
        this.blacklistMoves = blacklistMoves;
    }

    public Pokemon createRandomPokemon() {
        int level = new Random().nextInt(this.minLevel, this.maxLevel + 1);
        boolean validPokemon = false;
        Pokemon pokemon = PokemonUtils.getRandomPokemon(level);
        int triesRemaining = 500;
        block0: while (!validPokemon) {
            if (triesRemaining <= 0) {
                return null;
            }
            --triesRemaining;
            pokemon = PokemonUtils.getRandomPokemon(level);
            for (String species : this.blacklistSpecies) {
                if (!pokemon.getSpecies().getName().equalsIgnoreCase(species)) continue;
                continue block0;
            }
            for (String formID : this.blacklistFormIDs) {
                if (!pokemon.getForm().formOnlyShowdownId().equalsIgnoreCase(formID)) continue;
                continue block0;
            }
            for (String abilities : this.blacklistAbilities) {
                if (!pokemon.getAbility().getName().equalsIgnoreCase(abilities)) continue;
                continue block0;
            }
            for (String moves : this.blacklistMoves) {
                for (Move move : pokemon.getMoveSet()) {
                    if (!move.getName().equalsIgnoreCase(moves)) continue;
                    continue block0;
                }
                for (Move move : pokemon.getBenchedMoves()) {
                    if (!move.getMoveTemplate().getName().equalsIgnoreCase(moves)) continue;
                    continue block0;
                }
            }
            validPokemon = true;
        }
        return pokemon;
    }
}

