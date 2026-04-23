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
package me.unariginal.cobblemonbattlefactory.config.messages;

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
import java.util.HashMap;
import java.util.Map;
import me.unariginal.cobblemonbattlefactory.BattleFactory;
import net.fabricmc.loader.api.FabricLoader;

public class MessagesConfig {
    public String prefix = "[BattleFactory]";
    public Map<String, String> messages = new HashMap<String, String>();

    public MessagesConfig() {
        try {
            this.loadConfig();
        }
        catch (IOException e) {
            BattleFactory.INSTANCE.logError("[BattleFactory] Failed to load messages config file. Error: " + e.getMessage());
        }
    }

    private void loadConfig() throws IOException {
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }
        File configFile = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/messages.json").toFile();
        JsonObject newRoot = new JsonObject();
        JsonObject root = new JsonObject();
        if (configFile.exists()) {
            root = JsonParser.parseReader((Reader)new FileReader(configFile)).getAsJsonObject();
        }
        this.fillMessages();
        if (root.has("prefix")) {
            this.prefix = root.get("prefix").getAsString();
        }
        newRoot.addProperty("prefix", this.prefix);
        JsonObject messagesObject = new JsonObject();
        if (root.has("messages")) {
            messagesObject = root.get("messages").getAsJsonObject();
        }
        for (String string : messagesObject.keySet()) {
            this.messages.put(string, messagesObject.get(string).getAsString());
        }
        for (Map.Entry entry : this.messages.entrySet()) {
            messagesObject.addProperty((String)entry.getKey(), (String)entry.getValue());
        }
        newRoot.add("messages", (JsonElement)messagesObject);
        configFile.delete();
        configFile.createNewFile();
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        FileWriter fileWriter = new FileWriter(configFile);
        gson.toJson((JsonElement)newRoot, (Appendable)fileWriter);
        ((Writer)fileWriter).close();
    }

    private void fillMessages() {
        this.messages.clear();
        this.messages.put("command_reload", "%prefix% <green>Reloaded!");
        this.messages.put("command_status", "%prefix% <gray>Current Streak: <yellow>%round%<gray>. Current Tier: <yellow>%tier%");
        this.messages.put("battleFactory_stopped", "%prefix% <green>Battle Factory Stopped!");
        this.messages.put("command_resetPlayerData", "%prefix% <green>Successfully reset %player.name%'s data!");
        this.messages.put("command_resetLeaderboard", "%prefix% <green>Successfully reset the leaderboard!");
        this.messages.put("command_forceRound_success", "%prefix% <green>Forced %player.name% to the next round!");
        this.messages.put("command_forceRound_fail", "%prefix% <red>%player.name% is not in a battle factory instance!");
        this.messages.put("command_forceStop_success", "%prefix% <green>Stopped %player.name%'s battle factory instance!");
        this.messages.put("command_forceStop_fail", "%prefix% <red>%player.name% is not in a battle factory instance!");
        this.messages.put("command_forceStart_success", "%prefix% <green>Started a battle factory instance for %player.name%!");
        this.messages.put("command_forceStart_fail", "%prefix% <red>%player.name% is already in a battle factory instance!");
        this.messages.put("command_resetCooldown_success", "%prefix% <green>Reset %player.name%'s cooldown!");
        this.messages.put("command_resetCooldown_fail", "%prefix% <red>Failed to reset %player.name%'s cooldown!");
        this.messages.put("error_alreadyInBattleFactory", "%prefix% <red>You're already in the battle factory!");
        this.messages.put("error_failedToStartBattle", "%prefix% <red>Failed to start battle!");
        this.messages.put("error_waitForCooldown", "%prefix% <red>You're still on cooldown! %player.cooldown%");
        this.messages.put("error_bannedBagItem", "%prefix% <red>You can't use this item in the battle factory!");
        this.messages.put("overlay_nextRoundTimer", "<gray>Next Round In: <yellow>%round_timer%");
        this.messages.put("overlay_currentStatus", "<gray>Current Round: <yellow>%round% <gray>| Current Tier: <yellow>%tier% (%tier.round%/%tier.total_rounds%)");
        this.messages.put("overlay_bonusEncounter", "<rainbow><b>BONUS ENCOUNTER");
        this.messages.put("leaderboard_header", "<gray>--------- Page %page% / %max_pages% ---------");
        this.messages.put("leaderboard_section", "<gray>[%section.placement%] %section.player_name% | Streak: %section.highest_streak% (%section.date_achieved%)");
    }

    public String getMessage(String id) {
        if (this.messages.containsKey(id)) {
            return this.messages.get(id);
        }
        return "null";
    }
}

