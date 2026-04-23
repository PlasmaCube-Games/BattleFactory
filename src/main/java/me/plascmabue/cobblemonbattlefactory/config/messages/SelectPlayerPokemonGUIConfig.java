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
 *  net.minecraft.world.inventory.ChestMenu
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.core.component.DataComponentPatch
 */
package me.plascmabue.cobblemonbattlefactory.config.messages;

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
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.core.component.DataComponentPatch;

public class SelectPlayerPokemonGUIConfig {
    public String title = "<gold>Choisis le Pokémon à remplacer !";
    public int rows = 1;
    public List<String> slots = new ArrayList<String>(List.of("###PPP###"));
    public String backgroundItemSymbol = "#";
    public String backgroundItem = "minecraft:air";
    public String backgroundItemName = "";
    public List<String> backgroundItemLore = new ArrayList<String>();
    public DataComponentPatch backgroundItemData = DataComponentPatch.EMPTY;
    public String pokemonItemSymbol = "P";
    public String pokemonItemName = "<gray>%pokemon.name% %pokemon.gender% %pokemon.shiny%";
    public List<String> pokemonItemLore = new ArrayList<String>(List.of("<gray>Niveau : %pokemon.level%", "<gray>Forme : %pokemon.form%", "<gray>Talent : %pokemon.ability%", "<gray>Nature : %pokemon.nature%", "<gray>IVs : PV %pokemon.ivs.hp% Att %pokemon.ivs.atk% Déf %pokemon.ivs.def% AttSp %pokemon.ivs.spatk% DéfSp %pokemon.ivs.spdef% Vit %pokemon.ivs.spd%", "<gray>EVs : PV %pokemon.evs.hp% Att %pokemon.evs.atk% Déf %pokemon.evs.def% AttSp %pokemon.evs.spatk% DéfSp %pokemon.evs.spdef% Vit %pokemon.evs.spd%", "<gray>Attaques : %pokemon.moves.1% | %pokemon.moves.2% | %pokemon.moves.3% | %pokemon.moves.4%"));
    public DataComponentPatch pokemonItemData = DataComponentPatch.EMPTY;

    public SelectPlayerPokemonGUIConfig() {
        try {
            this.loadConfig();
        }
        catch (IOException e) {
            BattleFactory.INSTANCE.logError("[BattleFactory] Failed to load player pokemon selection gui config file!");
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
        File configFile = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/guis/select_player_pokemon_gui.json").toFile();
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
            this.backgroundItemData = (DataComponentPatch)((Pair)DataComponentPatch.CODEC.decode((DynamicOps)JsonOps.INSTANCE, backgroundItemObject.get("item_data")).getOrThrow()).getFirst();
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
            this.pokemonItemData = (DataComponentPatch)((Pair)DataComponentPatch.CODEC.decode((DynamicOps)JsonOps.INSTANCE, pokemonItemObject.get("item_data")).getOrThrow()).getFirst();
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

    public MenuType<ChestMenu> getScreenSize() {
        return switch (this.rows) {
            case 1 -> MenuType.GENERIC_9x3;
            case 2 -> MenuType.GENERIC_9x4;
            case 3 -> MenuType.GENERIC_9x1;
            case 4 -> MenuType.GENERIC_9x5;
            case 5 -> MenuType.GENERIC_9x6;
            default -> MenuType.GENERIC_9x2;
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

