/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.class_1707
 *  net.minecraft.class_3917
 *  net.minecraft.class_9326
 */
package me.unariginal.cobblemonbattlefactory.config.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import me.unariginal.cobblemonbattlefactory.BattleFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1707;
import net.minecraft.class_3917;
import net.minecraft.class_9326;

public class SelectNPCPokemonGUIConfig {
    public String title = "<gold>Select The Pokemon You Want!";
    public int rows = 1;
    public List<String> slots = new ArrayList<String>(List.of("###PPP###"));
    public String backgroundItemSymbol = "#";
    public String backgroundItem = "minecraft:air";
    public String backgroundItemName = "";
    public List<String> backgroundItemLore = new ArrayList<String>();
    public class_9326 backgroundItemData = class_9326.field_49588;
    public String pokemonItemSymbol = "P";
    public String pokemonItemName = "<gray>%pokemon.name% %pokemon.gender% %pokemon.shiny%";
    public List<String> pokemonItemLore = new ArrayList<String>(List.of("<gray>Level: %pokemon.level%", "<gray>Form: %pokemon.form%", "<gray>Ability: %pokemon.ability%", "<gray>Nature: %pokemon.nature%", "<gray>IVs: HP %pokemon.ivs.hp% Atk %pokemon.ivs.atk% Def %pokemon.ivs.def% SpAt %pokemon.ivs.spatk% SpDe %pokemon.ivs.spdef% Spd %pokemon.ivs.spd%", "<gray>EVs: HP %pokemon.evs.hp% Atk %pokemon.evs.atk% Def %pokemon.evs.def% SpAt %pokemon.evs.spatk% SpDe %pokemon.evs.spdef% Spd %pokemon.evs.spd%", "<gray>Moves: %pokemon.moves.1% | %pokemon.moves.2% | %pokemon.moves.3% | %pokemon.moves.4%"));
    public class_9326 pokemonItemData = class_9326.field_49588;

    public SelectNPCPokemonGUIConfig() {
        try {
            this.loadConfig();
        }
        catch (IOException e) {
            BattleFactory.INSTANCE.logError("[BattleFactory] Failed to load npc pokemon selection gui config file!");
        }
    }

    public void loadConfig() throws IOException {
        File guisFolder;
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }
        if (!(guisFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/guis").toFile()).exists()) {
            guisFolder.mkdir();
        }
        File configFile = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/guis/select_npc_pokemon_gui.json").toFile();
        JsonObject newRoot = new JsonObject();
        JsonObject root = new JsonObject();
        if (configFile.exists()) {
            root = JsonParser.parseReader((Reader)new FileReader(configFile)).getAsJsonObject();
        }
        if (root.has("title")) {
            this.title = root.get("title").getAsString();
        }
        newRoot.addProperty("title", this.title);
        if (root.has("rows")) {
            this.rows = root.get("rows").getAsInt();
        }
        newRoot.addProperty("rows", (Number)this.rows);
        if (root.has("slots")) {
            this.slots = root.getAsJsonArray("slots").asList().stream().map(JsonElement::getAsString).toList();
        }
        JsonArray slotsArray = new JsonArray();
        for (String row : this.slots) {
            slotsArray.add(row);
        }
        newRoot.add("slots", (JsonElement)slotsArray);
        JsonObject backgroundItemObject = new JsonObject();
        if (root.has("background_item")) {
            backgroundItemObject = root.get("background_item").getAsJsonObject();
        }
        if (backgroundItemObject.has("symbol")) {
            this.backgroundItemSymbol = backgroundItemObject.get("symbol").getAsString();
        }
        backgroundItemObject.addProperty("symbol", this.backgroundItemSymbol);
        if (backgroundItemObject.has("item")) {
            this.backgroundItem = backgroundItemObject.get("item").getAsString();
        }
        backgroundItemObject.addProperty("item", this.backgroundItem);
        if (backgroundItemObject.has("item_name")) {
            this.backgroundItemName = backgroundItemObject.get("item_name").getAsString();
        }
        backgroundItemObject.addProperty("item_name", this.backgroundItemName);
        if (backgroundItemObject.has("item_lore")) {
            this.backgroundItemLore = backgroundItemObject.getAsJsonArray("item_lore").asList().stream().map(JsonElement::getAsString).toList();
        }
        JsonArray itemLoreArray = new JsonArray();
        for (String string : this.backgroundItemLore) {
            itemLoreArray.add(string);
        }
        backgroundItemObject.add("item_lore", (JsonElement)itemLoreArray);
        if (backgroundItemObject.has("item_data")) {
            this.backgroundItemData = (class_9326)((Pair)class_9326.field_49589.decode((DynamicOps)JsonOps.INSTANCE, (Object)backgroundItemObject.get("item_data")).getOrThrow()).getFirst();
        } else {
            backgroundItemObject.add("item_data", (JsonElement)new JsonObject());
        }
        newRoot.add("background_item", (JsonElement)backgroundItemObject);
        JsonObject pokemonItemObject = new JsonObject();
        if (root.has("pokemon_item")) {
            pokemonItemObject = root.get("pokemon_item").getAsJsonObject();
        }
        if (pokemonItemObject.has("symbol")) {
            this.pokemonItemSymbol = pokemonItemObject.get("symbol").getAsString();
        }
        pokemonItemObject.addProperty("symbol", this.pokemonItemSymbol);
        if (pokemonItemObject.has("item_name")) {
            this.pokemonItemName = pokemonItemObject.get("item_name").getAsString();
        }
        pokemonItemObject.addProperty("item_name", this.pokemonItemName);
        if (pokemonItemObject.has("item_lore")) {
            this.pokemonItemLore = pokemonItemObject.getAsJsonArray("item_lore").asList().stream().map(JsonElement::getAsString).toList();
        }
        itemLoreArray = new JsonArray();
        for (String lore : this.pokemonItemLore) {
            itemLoreArray.add(lore);
        }
        pokemonItemObject.add("item_lore", (JsonElement)itemLoreArray);
        if (pokemonItemObject.has("item_data")) {
            this.pokemonItemData = (class_9326)((Pair)class_9326.field_49589.decode((DynamicOps)JsonOps.INSTANCE, (Object)pokemonItemObject.get("item_data")).getOrThrow()).getFirst();
        } else {
            pokemonItemObject.add("item_data", (JsonElement)new JsonObject());
        }
        newRoot.add("pokemon_item", (JsonElement)pokemonItemObject);
        configFile.delete();
        configFile.createNewFile();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileWriter writer = new FileWriter(configFile);
        gson.toJson((JsonElement)newRoot, (Appendable)writer);
        ((Writer)writer).close();
    }

    public class_3917<class_1707> getScreenSize() {
        return switch (this.rows) {
            case 1 -> class_3917.field_18664;
            case 2 -> class_3917.field_18665;
            case 3 -> class_3917.field_17326;
            case 4 -> class_3917.field_18666;
            case 5 -> class_3917.field_18667;
            default -> class_3917.field_17327;
        };
    }

    public List<Integer> getSlotsBySymbol(String symbol) {
        ArrayList<Integer> returnSlots = new ArrayList<Integer>();
        int slotCount = 0;
        for (String row : this.slots) {
            for (char slot : row.toCharArray()) {
                if (slot == symbol.charAt(0)) {
                    returnSlots.add(slotCount);
                }
                ++slotCount;
            }
        }
        return returnSlots;
    }
}

