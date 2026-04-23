/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.loader.api.FabricLoader
 */
package me.plascmabue.cobblemonbattlefactory.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import me.plascmabue.cobblemonbattlefactory.datatypes.rentalPools.PokemonPool;
import me.plascmabue.cobblemonbattlefactory.datatypes.rentalPools.PokemonPreset;
import me.plascmabue.cobblemonbattlefactory.datatypes.rentalPools.RandomPokemonPool;
import me.plascmabue.cobblemonbattlefactory.datatypes.rentalPools.SetPokemonPool;
import net.fabricmc.loader.api.FabricLoader;

public class RentalPoolsConfig {
    public List<PokemonPool> rentalPools = new ArrayList<PokemonPool>();

    public RentalPoolsConfig() {
        try {
            this.loadConfig();
        }
        catch (IOException e) {
            BattleFactory.INSTANCE.logError("[BattleFactory] Failed to load rental pools config file. Error: " + e.getMessage());
        }
    }

    public void loadConfig() throws IOException {
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }
        File configFile = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/rental_pools.json").toFile();
        JsonObject root = new JsonObject();
        if (configFile.exists()) {
            root = JsonParser.parseReader((Reader)new FileReader(configFile)).getAsJsonObject();
            this.rentalPools.clear();
            for (String key : root.keySet()) {
                String type;
                JsonObject rentalPoolObject = root.getAsJsonObject(key);
                if (!rentalPoolObject.has("type")) continue;
                switch (type = rentalPoolObject.get("type").getAsString()) {
                    case "random": {
                        int minLevel = 1;
                        int maxLevel = 1;
                        JsonObject levelRange = new JsonObject();
                        if (rentalPoolObject.has("level_range")) {
                            levelRange = rentalPoolObject.get("level_range").getAsJsonObject();
                        }
                        if (levelRange.has("min")) {
                            minLevel = levelRange.get("min").getAsInt();
                        }
                        if (levelRange.has("max")) {
                            maxLevel = levelRange.get("max").getAsInt();
                        }
                        if (maxLevel < minLevel) {
                            maxLevel = minLevel;
                        }
                        List<String> speciesBlacklist = new ArrayList<String>();
                        List<String> formIDBlacklist = new ArrayList<String>();
                        List<String> abilitiesBlacklist = new ArrayList<String>();
                        List<String> movesBlacklist = new ArrayList<String>();
                        if (rentalPoolObject.has("blacklist")) {
                            JsonObject blacklistObject = rentalPoolObject.get("blacklist").getAsJsonObject();
                            if (blacklistObject.has("species")) {
                                speciesBlacklist = blacklistObject.getAsJsonArray("species").asList().stream().map(JsonElement::getAsString).toList();
                            }
                            if (blacklistObject.has("forms")) {
                                formIDBlacklist = blacklistObject.getAsJsonArray("forms").asList().stream().map(JsonElement::getAsString).toList();
                            }
                            if (blacklistObject.has("abilities")) {
                                abilitiesBlacklist = blacklistObject.getAsJsonArray("abilities").asList().stream().map(JsonElement::getAsString).toList();
                            }
                            if (blacklistObject.has("moves")) {
                                movesBlacklist = blacklistObject.getAsJsonArray("moves").asList().stream().map(JsonElement::getAsString).toList();
                            }
                        }
                        this.rentalPools.add(new RandomPokemonPool(UUID.randomUUID(), key, type, minLevel, maxLevel, speciesBlacklist, formIDBlacklist, abilitiesBlacklist, movesBlacklist));
                        break;
                    }
                    case "set": {
                        if (!rentalPoolObject.has("pokemon")) break;
                        HashMap<PokemonPreset, Double> setPool = new HashMap<PokemonPreset, Double>();
                        for (JsonElement pokemonElement : rentalPoolObject.get("pokemon").getAsJsonArray()) {
                            JsonObject pokemonObject = pokemonElement.getAsJsonObject();
                            if (!pokemonObject.has("weight")) continue;
                            double weight = pokemonObject.get("weight").getAsDouble();
                            if (pokemonObject.has("pokemon_preset")) {
                                String pokemonPreset = pokemonObject.get("pokemon_preset").getAsString();
                                PokemonPreset pokemon = BattleFactory.INSTANCE.pokemonPresetsConfig().getPokemonPreset(pokemonPreset);
                                if (pokemon == null) continue;
                                setPool.put(pokemon, weight);
                                continue;
                            }
                            if (!pokemonObject.has("pokemon")) continue;
                            JsonObject pokemonObj = pokemonObject.getAsJsonObject("pokemon");
                            setPool.put(new PokemonPreset(UUID.randomUUID(), null, pokemonObj), weight);
                        }
                        this.rentalPools.add(new SetPokemonPool(UUID.randomUUID(), key, type, setPool));
                    }
                }
            }
        } else {
            configFile.createNewFile();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter(configFile);
            gson.toJson((JsonElement)root, (Appendable)writer);
            ((Writer)writer).close();
        }
    }

    public PokemonPool getPokemonPool(String name) {
        for (PokemonPool pokemonPool : this.rentalPools) {
            if (!pokemonPool.name.equalsIgnoreCase(name)) continue;
            return pokemonPool;
        }
        return null;
    }
}

