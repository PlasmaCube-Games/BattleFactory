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

public class RentalSelectionGUIConfig {
    public String title = "<gold>Sélection des Pokémon";
    public int rows = 5;
    public List<String> slots = new ArrayList<String>(List.of("#########", "#RRR#RRR#", "#########", "#########", "###C#S###"));
    public String backgroundItemSymbol = "#";
    public String backgroundItem = "minecraft:air";
    public String backgroundItemName = "";
    public List<String> backgroundItemLore = new ArrayList<String>();
    public DataComponentPatch backgroundItemData = DataComponentPatch.EMPTY;
    public boolean enchantRentalItemOnSelect = true;
    public String rentalItemSymbol = "R";
    public String rentalItemName = "<gray>%pokemon.name% %pokemon.gender% %pokemon.shiny%";
    public List<String> rentalItemLore = new ArrayList<String>(List.of("<gray>Niveau : %pokemon.level%", "<gray>Forme : %pokemon.form%", "<gray>Talent : %pokemon.ability%", "<gray>Nature : %pokemon.nature%", "<gray>IVs : PV %pokemon.ivs.hp% Att %pokemon.ivs.atk% Déf %pokemon.ivs.def% AttSp %pokemon.ivs.spatk% DéfSp %pokemon.ivs.spdef% Vit %pokemon.ivs.spd%", "<gray>EVs : PV %pokemon.evs.hp% Att %pokemon.evs.atk% Déf %pokemon.evs.def% AttSp %pokemon.evs.spatk% DéfSp %pokemon.evs.spdef% Vit %pokemon.evs.spd%", "<gray>Attaques : %pokemon.moves.1% | %pokemon.moves.2% | %pokemon.moves.3% | %pokemon.moves.4%"));
    public DataComponentPatch rentalItemData = DataComponentPatch.EMPTY;
    public boolean showSelectedItemAbove = true;
    public boolean showSelectedItemBelow = true;
    public String selectedItem = "minecraft:lime_stained_glass_pane";
    public String selectedItemName = "<green>Sélectionné !";
    public List<String> selectedItemLore = new ArrayList<String>();
    public DataComponentPatch selectedItemData = DataComponentPatch.EMPTY;
    public String cancelItemSymbol = "C";
    public String cancelItem = "minecraft:red_concrete";
    public String cancelItemName = "<red>Annuler";
    public List<String> cancelItemLore = new ArrayList<String>();
    public DataComponentPatch cancelItemData = DataComponentPatch.EMPTY;
    public String startItemSymbol = "S";
    public String startItem = "minecraft:green_concrete";
    public String startItemName = "<green>Commencer";
    public List<String> startItemLore = new ArrayList<String>();
    public DataComponentPatch startItemData = DataComponentPatch.EMPTY;

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
            this.backgroundItemData = (DataComponentPatch)((Pair)DataComponentPatch.CODEC.decode((DynamicOps)JsonOps.INSTANCE, backgroundItemObject.get("item_data")).getOrThrow()).getFirst();
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
            this.rentalItemData = (DataComponentPatch)((Pair)DataComponentPatch.CODEC.decode((DynamicOps)JsonOps.INSTANCE, rentalItemObject.get("item_data")).getOrThrow()).getFirst();
        }
        newRoot.add("rentalPokemon_item", (JsonElement)rentalItemObject);
        JsonObject selectedItemObject = new JsonObject();
        if (root.has("selected_item")) {
            selectedItemObject = root.get("selected_item").getAsJsonObject();
        }
        if (selectedItemObject.has("show_above_selected_pokemon")) {
            this.showSelectedItemAbove = selectedItemObject.get("show_above_selected_pokemon").getAsBoolean();
        }
        selectedItemObject.addProperty("show_above_selected_pokemon", Boolean.valueOf(this.showSelectedItemAbove));
        if (selectedItemObject.has("show_below_selected_pokemon")) {
            this.showSelectedItemBelow = selectedItemObject.get("show_below_selected_pokemon").getAsBoolean();
        }
        selectedItemObject.addProperty("show_below_selected_pokemon", Boolean.valueOf(this.showSelectedItemBelow));
        if (selectedItemObject.has("item")) {
            this.selectedItem = selectedItemObject.get("item").getAsString();
        }
        selectedItemObject.addProperty("item", this.selectedItem);
        if (selectedItemObject.has("item_name")) {
            this.selectedItemName = selectedItemObject.get("item_name").getAsString();
        }
        selectedItemObject.addProperty("item_name", this.selectedItemName);
        if (selectedItemObject.has("item_lore")) {
            this.selectedItemLore = selectedItemObject.getAsJsonArray("item_lore").asList().stream().map(JsonElement::getAsString).toList();
        }
        itemLoreArray = new JsonArray();
        for (String string : this.selectedItemLore) {
            itemLoreArray.add(string);
        }
        selectedItemObject.add("item_lore", (JsonElement)itemLoreArray);
        if (selectedItemObject.has("item_data")) {
            this.selectedItemData = (DataComponentPatch)((Pair)DataComponentPatch.CODEC.decode((DynamicOps)JsonOps.INSTANCE, selectedItemObject.get("item_data")).getOrThrow()).getFirst();
        }
        newRoot.add("selected_item", (JsonElement)selectedItemObject);
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
        for (String string : this.cancelItemLore) {
            itemLoreArray.add(string);
        }
        cancelItemObject.add("item_lore", (JsonElement)itemLoreArray);
        if (cancelItemObject.has("item_data")) {
            this.cancelItemData = (DataComponentPatch)((Pair)DataComponentPatch.CODEC.decode((DynamicOps)JsonOps.INSTANCE, cancelItemObject.get("item_data")).getOrThrow()).getFirst();
        }
        newRoot.add("cancel_item", (JsonElement)cancelItemObject);
        JsonObject startItemObject = new JsonObject();
        if (root.has("start_item")) {
            startItemObject = root.get("start_item").getAsJsonObject();
        }
        if (startItemObject.has("symbol")) {
            this.startItemSymbol = startItemObject.get("symbol").getAsString();
        }
        startItemObject.addProperty("symbol", this.startItemSymbol);
        if (startItemObject.has("item")) {
            this.startItem = startItemObject.get("item").getAsString();
        }
        startItemObject.addProperty("item", this.startItem);
        if (startItemObject.has("item_name")) {
            this.startItemName = startItemObject.get("item_name").getAsString();
        }
        startItemObject.addProperty("item_name", this.startItemName);
        if (startItemObject.has("item_lore")) {
            this.startItemLore = startItemObject.getAsJsonArray("item_lore").asList().stream().map(JsonElement::getAsString).toList();
        }
        itemLoreArray = new JsonArray();
        for (String lore : this.startItemLore) {
            itemLoreArray.add(lore);
        }
        startItemObject.add("item_lore", (JsonElement)itemLoreArray);
        if (startItemObject.has("item_data")) {
            this.startItemData = (DataComponentPatch)((Pair)DataComponentPatch.CODEC.decode((DynamicOps)JsonOps.INSTANCE, startItemObject.get("item_data")).getOrThrow()).getFirst();
        }
        newRoot.add("start_item", (JsonElement)startItemObject);
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

