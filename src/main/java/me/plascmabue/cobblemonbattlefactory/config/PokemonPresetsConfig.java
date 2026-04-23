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
import java.util.List;
import java.util.UUID;
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import me.plascmabue.cobblemonbattlefactory.datatypes.rentalPools.PokemonPreset;
import net.fabricmc.loader.api.FabricLoader;

public class PokemonPresetsConfig {
    public List<PokemonPreset> pokemonPresets = new ArrayList<PokemonPreset>();

    public PokemonPresetsConfig() {
        try {
            this.loadConfig();
        }
        catch (IOException e) {
            BattleFactory.INSTANCE.logError("[BattleFactory] Failed to load pokemon presets config file. Error: " + e.getMessage());
        }
    }

    public void loadConfig() throws IOException {
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }
        File configFile = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/pokemon_presets.json").toFile();
        JsonObject root = new JsonObject();
        if (configFile.exists()) {
            root = JsonParser.parseReader((Reader)new FileReader(configFile)).getAsJsonObject();
            this.pokemonPresets.clear();
            for (String key : root.keySet()) {
                JsonObject pokemonPresetObject = root.getAsJsonObject(key);
                this.pokemonPresets.add(new PokemonPreset(UUID.randomUUID(), key, pokemonPresetObject));
            }
        } else {
            configFile.createNewFile();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter(configFile);
            gson.toJson((JsonElement)root, (Appendable)writer);
            ((Writer)writer).close();
        }
    }

    public PokemonPreset getPokemonPreset(String name) {
        for (PokemonPreset pokemonPreset : this.pokemonPresets) {
            if (!pokemonPreset.name().equalsIgnoreCase(name)) continue;
            return pokemonPreset;
        }
        return null;
    }
}

