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

public class TierSelectionGUIConfig {
    public String title = "<gold>Select A Difficulty Tier";
    public int rows = 5;
    public List<String> slots = new ArrayList<String>(List.of("TTTTTTTTT", "TTTTTTTTT", "TTTTTTTTT", "TTTTTTTTT", "####C####"));
    public String backgroundItemSymbol = "#";
    public String backgroundItem = "minecraft:air";
    public String backgroundItemName = "";
    public List<String> backgroundItemLore = new ArrayList<String>();
    public class_9326 backgroundItemData = class_9326.field_49588;
    public String tierItemSymbol = "T";
    public String tierItemName = "<gray>%tier%";
    public List<String> tierItemLore = new ArrayList<String>();
    public class_9326 tierItemData = class_9326.field_49588;
    public String unavailableTierItem = "minecraft:barrier";
    public List<String> unavailableTierItemLore = new ArrayList<String>();
    public class_9326 unavailableItemData = class_9326.field_49588;
    public String cancelItemSymbol = "C";
    public String cancelItem = "minecraft:red_concrete";
    public String cancelItemName = "<red>Cancel";
    public List<String> cancelItemLore = new ArrayList<String>();
    public class_9326 cancelItemData = class_9326.field_49588;

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
        void var10_15;
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
            this.backgroundItemData = (class_9326)((Pair)class_9326.field_49589.decode((DynamicOps)JsonOps.INSTANCE, (Object)backgroundItemObject.get("item_data")).getOrThrow()).getFirst();
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
            this.tierItemData = (class_9326)((Pair)class_9326.field_49589.decode((DynamicOps)JsonOps.INSTANCE, (Object)tierItemObject.get("item_data")).getOrThrow()).getFirst();
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
            this.unavailableItemData = (class_9326)((Pair)class_9326.field_49589.decode((DynamicOps)JsonOps.INSTANCE, (Object)tierItemObject.get("unavailable_data")).getOrThrow()).getFirst();
        } else {
            tierItemObject.add("unavailable_data", (JsonElement)new JsonObject());
        }
        newRoot.add("tier_item", (JsonElement)tierItemObject);
        JsonObject jsonObject = new JsonObject();
        if (root.has("cancel_item")) {
            JsonObject jsonObject2 = root.get("cancel_item").getAsJsonObject();
        }
        if (var10_15.has("symbol")) {
            this.cancelItemSymbol = var10_15.get("symbol").getAsString();
        }
        var10_15.addProperty("symbol", this.cancelItemSymbol);
        if (var10_15.has("item")) {
            this.cancelItem = var10_15.get("item").getAsString();
        }
        var10_15.addProperty("item", this.cancelItem);
        if (var10_15.has("item_name")) {
            this.cancelItemName = var10_15.get("item_name").getAsString();
        }
        var10_15.addProperty("item_name", this.cancelItemName);
        if (var10_15.has("item_lore")) {
            this.cancelItemLore = var10_15.getAsJsonArray("item_lore").asList().stream().map(JsonElement::getAsString).toList();
        }
        itemLoreArray = new JsonArray();
        for (String lore : this.cancelItemLore) {
            itemLoreArray.add(lore);
        }
        var10_15.add("item_lore", (JsonElement)itemLoreArray);
        if (var10_15.has("item_data")) {
            this.cancelItemData = (class_9326)((Pair)class_9326.field_49589.decode((DynamicOps)JsonOps.INSTANCE, (Object)var10_15.get("item_data")).getOrThrow()).getFirst();
        } else {
            var10_15.add("item_data", (JsonElement)new JsonObject());
        }
        newRoot.add("cancel_item", (JsonElement)var10_15);
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

