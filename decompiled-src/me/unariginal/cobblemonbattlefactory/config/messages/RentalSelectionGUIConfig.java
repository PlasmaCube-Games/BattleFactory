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

public class RentalSelectionGUIConfig {
    public String title = "<gold>Rental Selection";
    public int rows = 5;
    public List<String> slots = new ArrayList<String>(List.of("#########", "#RRR#RRR#", "#########", "#########", "###C#S###"));
    public String backgroundItemSymbol = "#";
    public String backgroundItem = "minecraft:air";
    public String backgroundItemName = "";
    public List<String> backgroundItemLore = new ArrayList<String>();
    public class_9326 backgroundItemData = class_9326.field_49588;
    public boolean enchantRentalItemOnSelect = true;
    public String rentalItemSymbol = "R";
    public String rentalItemName = "<gray>%pokemon.name% %pokemon.gender% %pokemon.shiny%";
    public List<String> rentalItemLore = new ArrayList<String>(List.of("<gray>Level: %pokemon.level%", "<gray>Form: %pokemon.form%", "<gray>Ability: %pokemon.ability%", "<gray>Nature: %pokemon.nature%", "<gray>IVs: HP %pokemon.ivs.hp% Atk %pokemon.ivs.atk% Def %pokemon.ivs.def% SpAt %pokemon.ivs.spatk% SpDe %pokemon.ivs.spdef% Spd %pokemon.ivs.spd%", "<gray>EVs: HP %pokemon.evs.hp% Atk %pokemon.evs.atk% Def %pokemon.evs.def% SpAt %pokemon.evs.spatk% SpDe %pokemon.evs.spdef% Spd %pokemon.evs.spd%", "<gray>Moves: %pokemon.moves.1% | %pokemon.moves.2% | %pokemon.moves.3% | %pokemon.moves.4%"));
    public class_9326 rentalItemData = class_9326.field_49588;
    public boolean showSelectedItemAbove = true;
    public boolean showSelectedItemBelow = true;
    public String selectedItem = "minecraft:lime_stained_glass_pane";
    public String selectedItemName = "<green>Selected!";
    public List<String> selectedItemLore = new ArrayList<String>();
    public class_9326 selectedItemData = class_9326.field_49588;
    public String cancelItemSymbol = "C";
    public String cancelItem = "minecraft:red_concrete";
    public String cancelItemName = "<red>Cancel";
    public List<String> cancelItemLore = new ArrayList<String>();
    public class_9326 cancelItemData = class_9326.field_49588;
    public String startItemSymbol = "S";
    public String startItem = "minecraft:green_concrete";
    public String startItemName = "<green>Start";
    public List<String> startItemLore = new ArrayList<String>();
    public class_9326 startItemData = class_9326.field_49588;

    public RentalSelectionGUIConfig() {
        try {
            this.loadConfig();
        }
        catch (IOException e) {
            BattleFactory.INSTANCE.logError("[BattleFactory] Failed to load rental selection gui config file!");
        }
    }

    /*
     * WARNING - void declaration
     */
    public void loadConfig() throws IOException {
        void var12_24;
        void var11_19;
        void var10_14;
        File guisFolder;
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }
        if (!(guisFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/guis").toFile()).exists()) {
            guisFolder.mkdir();
        }
        File configFile = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/guis/rental_selection_gui.json").toFile();
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
        }
        newRoot.add("background_item", (JsonElement)backgroundItemObject);
        JsonObject rentalItemObject = new JsonObject();
        if (root.has("rentalPokemon_item")) {
            rentalItemObject = root.get("rentalPokemon_item").getAsJsonObject();
        }
        if (rentalItemObject.has("enchant_on_select")) {
            this.enchantRentalItemOnSelect = rentalItemObject.get("enchant_on_select").getAsBoolean();
        }
        rentalItemObject.addProperty("enchant_on_select", Boolean.valueOf(this.enchantRentalItemOnSelect));
        if (rentalItemObject.has("symbol")) {
            this.rentalItemSymbol = rentalItemObject.get("symbol").getAsString();
        }
        rentalItemObject.addProperty("symbol", this.rentalItemSymbol);
        if (rentalItemObject.has("item_name")) {
            this.rentalItemName = rentalItemObject.get("item_name").getAsString();
        }
        rentalItemObject.addProperty("item_name", this.rentalItemName);
        if (rentalItemObject.has("item_lore")) {
            this.rentalItemLore = rentalItemObject.getAsJsonArray("item_lore").asList().stream().map(JsonElement::getAsString).toList();
        }
        itemLoreArray = new JsonArray();
        for (String string : this.rentalItemLore) {
            itemLoreArray.add(string);
        }
        rentalItemObject.add("item_lore", (JsonElement)itemLoreArray);
        if (rentalItemObject.has("item_data")) {
            this.rentalItemData = (class_9326)((Pair)class_9326.field_49589.decode((DynamicOps)JsonOps.INSTANCE, (Object)rentalItemObject.get("item_data")).getOrThrow()).getFirst();
        }
        newRoot.add("rentalPokemon_item", (JsonElement)rentalItemObject);
        JsonObject jsonObject = new JsonObject();
        if (root.has("selected_item")) {
            JsonObject jsonObject2 = root.get("selected_item").getAsJsonObject();
        }
        if (var10_14.has("show_above_selected_pokemon")) {
            this.showSelectedItemAbove = var10_14.get("show_above_selected_pokemon").getAsBoolean();
        }
        var10_14.addProperty("show_above_selected_pokemon", Boolean.valueOf(this.showSelectedItemAbove));
        if (var10_14.has("show_below_selected_pokemon")) {
            this.showSelectedItemBelow = var10_14.get("show_below_selected_pokemon").getAsBoolean();
        }
        var10_14.addProperty("show_below_selected_pokemon", Boolean.valueOf(this.showSelectedItemBelow));
        if (var10_14.has("item")) {
            this.selectedItem = var10_14.get("item").getAsString();
        }
        var10_14.addProperty("item", this.selectedItem);
        if (var10_14.has("item_name")) {
            this.selectedItemName = var10_14.get("item_name").getAsString();
        }
        var10_14.addProperty("item_name", this.selectedItemName);
        if (var10_14.has("item_lore")) {
            this.selectedItemLore = var10_14.getAsJsonArray("item_lore").asList().stream().map(JsonElement::getAsString).toList();
        }
        itemLoreArray = new JsonArray();
        for (String string : this.selectedItemLore) {
            itemLoreArray.add(string);
        }
        var10_14.add("item_lore", (JsonElement)itemLoreArray);
        if (var10_14.has("item_data")) {
            this.selectedItemData = (class_9326)((Pair)class_9326.field_49589.decode((DynamicOps)JsonOps.INSTANCE, (Object)var10_14.get("item_data")).getOrThrow()).getFirst();
        }
        newRoot.add("selected_item", (JsonElement)var10_14);
        JsonObject jsonObject3 = new JsonObject();
        if (root.has("cancel_item")) {
            JsonObject jsonObject4 = root.get("cancel_item").getAsJsonObject();
        }
        if (var11_19.has("symbol")) {
            this.cancelItemSymbol = var11_19.get("symbol").getAsString();
        }
        var11_19.addProperty("symbol", this.cancelItemSymbol);
        if (var11_19.has("item")) {
            this.cancelItem = var11_19.get("item").getAsString();
        }
        var11_19.addProperty("item", this.cancelItem);
        if (var11_19.has("item_name")) {
            this.cancelItemName = var11_19.get("item_name").getAsString();
        }
        var11_19.addProperty("item_name", this.cancelItemName);
        if (var11_19.has("item_lore")) {
            this.cancelItemLore = var11_19.getAsJsonArray("item_lore").asList().stream().map(JsonElement::getAsString).toList();
        }
        itemLoreArray = new JsonArray();
        for (String string : this.cancelItemLore) {
            itemLoreArray.add(string);
        }
        var11_19.add("item_lore", (JsonElement)itemLoreArray);
        if (var11_19.has("item_data")) {
            this.cancelItemData = (class_9326)((Pair)class_9326.field_49589.decode((DynamicOps)JsonOps.INSTANCE, (Object)var11_19.get("item_data")).getOrThrow()).getFirst();
        }
        newRoot.add("cancel_item", (JsonElement)var11_19);
        JsonObject jsonObject5 = new JsonObject();
        if (root.has("start_item")) {
            JsonObject jsonObject6 = root.get("start_item").getAsJsonObject();
        }
        if (var12_24.has("symbol")) {
            this.startItemSymbol = var12_24.get("symbol").getAsString();
        }
        var12_24.addProperty("symbol", this.startItemSymbol);
        if (var12_24.has("item")) {
            this.startItem = var12_24.get("item").getAsString();
        }
        var12_24.addProperty("item", this.startItem);
        if (var12_24.has("item_name")) {
            this.startItemName = var12_24.get("item_name").getAsString();
        }
        var12_24.addProperty("item_name", this.startItemName);
        if (var12_24.has("item_lore")) {
            this.startItemLore = var12_24.getAsJsonArray("item_lore").asList().stream().map(JsonElement::getAsString).toList();
        }
        itemLoreArray = new JsonArray();
        for (String lore : this.startItemLore) {
            itemLoreArray.add(lore);
        }
        var12_24.add("item_lore", (JsonElement)itemLoreArray);
        if (var12_24.has("item_data")) {
            this.startItemData = (class_9326)((Pair)class_9326.field_49589.decode((DynamicOps)JsonOps.INSTANCE, (Object)var12_24.get("item_data")).getOrThrow()).getFirst();
        }
        newRoot.add("start_item", (JsonElement)var12_24);
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

