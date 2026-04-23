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

public class TierSelectionGUIConfig {
    public String title = "<gold>Choisis un niveau de difficulté";
    public int rows = 5;
    public List<String> slots = new ArrayList<String>(List.of("TTTTTTTTT", "TTTTTTTTT", "TTTTTTTTT", "TTTTTTTTT", "####C####"));
    public String backgroundItemSymbol = "#";
    public String backgroundItem = "minecraft:air";
    public String backgroundItemName = "";
    public List<String> backgroundItemLore = new ArrayList<String>();
    public DataComponentPatch backgroundItemData = DataComponentPatch.EMPTY;
    public String tierItemSymbol = "T";
    public String tierItemName = "<gray>%tier%";
    public List<String> tierItemLore = new ArrayList<String>();
    public DataComponentPatch tierItemData = DataComponentPatch.EMPTY;
    public String unavailableTierItem = "minecraft:barrier";
    public List<String> unavailableTierItemLore = new ArrayList<String>();
    public DataComponentPatch unavailableItemData = DataComponentPatch.EMPTY;
    public String cancelItemSymbol = "C";
    public String cancelItem = "minecraft:red_concrete";
    public String cancelItemName = "<red>Annuler";
    public List<String> cancelItemLore = new ArrayList<String>();
    public DataComponentPatch cancelItemData = DataComponentPatch.EMPTY;

    public TierSelectionGUIConfig() {
        try {
            this.loadConfig();
        }
        catch (IOException e) {
            BattleFactory.INSTANCE.logError("[BattleFactory] Failed to load tier selection gui config file!");
        }
    }

    /*
     * WARNING - void declaration
     */
    public void loadConfig() throws IOException {
        File guisFolder;
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }
        if (!(guisFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/guis").toFile()).exists()) {
            guisFolder.mkdir();
        }
        File configFile = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/guis/tier_selection_gui.json").toFile();
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
        JsonObject tierItemObject = new JsonObject();
        if (root.has("tier_item")) {
            tierItemObject = root.get("tier_item").getAsJsonObject();
        }
        if (tierItemObject.has("symbol")) {
            this.tierItemSymbol = tierItemObject.get("symbol").getAsString();
        }
        tierItemObject.addProperty("symbol", this.tierItemSymbol);
        if (tierItemObject.has("item_name")) {
            this.tierItemName = tierItemObject.get("item_name").getAsString();
        }
        tierItemObject.addProperty("item_name", this.tierItemName);
        if (tierItemObject.has("item_lore")) {
            this.tierItemLore = tierItemObject.getAsJsonArray("item_lore").asList().stream().map(JsonElement::getAsString).toList();
        }
        itemLoreArray = new JsonArray();
        for (String string : this.tierItemLore) {
            itemLoreArray.add(string);
        }
        tierItemObject.add("item_lore", (JsonElement)itemLoreArray);
        if (tierItemObject.has("item_data")) {
            this.tierItemData = (DataComponentPatch)((Pair)DataComponentPatch.CODEC.decode((DynamicOps)JsonOps.INSTANCE, tierItemObject.get("item_data")).getOrThrow()).getFirst();
        } else {
            tierItemObject.add("item_data", (JsonElement)new JsonObject());
        }
        if (tierItemObject.has("unavailable_item")) {
            this.unavailableTierItem = tierItemObject.get("unavailable_item").getAsString();
        }
        tierItemObject.addProperty("unavailable_item", this.unavailableTierItem);
        if (tierItemObject.has("unavailable_lore")) {
            this.unavailableTierItemLore = tierItemObject.getAsJsonArray("unavailable_lore").asList().stream().map(JsonElement::getAsString).toList();
        }
        itemLoreArray = new JsonArray();
        for (String string : this.unavailableTierItemLore) {
            itemLoreArray.add(string);
        }
        tierItemObject.add("unavailable_lore", (JsonElement)itemLoreArray);
        if (tierItemObject.has("unavailable_data")) {
            this.unavailableItemData = (DataComponentPatch)((Pair)DataComponentPatch.CODEC.decode((DynamicOps)JsonOps.INSTANCE, tierItemObject.get("unavailable_data")).getOrThrow()).getFirst();
        } else {
            tierItemObject.add("unavailable_data", (JsonElement)new JsonObject());
        }
        newRoot.add("tier_item", (JsonElement)tierItemObject);
        JsonObject cancelItemObject = new JsonObject();
        if (root.has("cancel_item")) {
            cancelItemObject = root.get("cancel_item").getAsJsonObject();
        }
        if (cancelItemObject.has("symbol")) {
            this.cancelItemSymbol = cancelItemObject.get("symbol").getAsString();
        }
        cancelItemObject.addProperty("symbol", this.cancelItemSymbol);
        if (cancelItemObject.has("item")) {
            this.cancelItem = cancelItemObject.get("item").getAsString();
        }
        cancelItemObject.addProperty("item", this.cancelItem);
        if (cancelItemObject.has("item_name")) {
            this.cancelItemName = cancelItemObject.get("item_name").getAsString();
        }
        cancelItemObject.addProperty("item_name", this.cancelItemName);
        if (cancelItemObject.has("item_lore")) {
            this.cancelItemLore = cancelItemObject.getAsJsonArray("item_lore").asList().stream().map(JsonElement::getAsString).toList();
        }
        itemLoreArray = new JsonArray();
        for (String lore : this.cancelItemLore) {
            itemLoreArray.add(lore);
        }
        cancelItemObject.add("item_lore", (JsonElement)itemLoreArray);
        if (cancelItemObject.has("item_data")) {
            this.cancelItemData = (DataComponentPatch)((Pair)DataComponentPatch.CODEC.decode((DynamicOps)JsonOps.INSTANCE, cancelItemObject.get("item_data")).getOrThrow()).getFirst();
        } else {
            cancelItemObject.add("item_data", (JsonElement)new JsonObject());
        }
        newRoot.add("cancel_item", (JsonElement)cancelItemObject);
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

