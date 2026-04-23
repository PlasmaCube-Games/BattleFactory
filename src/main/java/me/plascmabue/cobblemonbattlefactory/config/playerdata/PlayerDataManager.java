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
 *  net.minecraft.server.level.ServerPlayer
 */
package me.plascmabue.cobblemonbattlefactory.config.playerdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import me.plascmabue.cobblemonbattlefactory.datatypes.PlayerData;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;

public class PlayerDataManager {
    public static Map<UUID, Long> loadCooldowns() {
        File[] playerFiles;
        File playersFolder;
        HashMap<UUID, Long> cooldowns = new HashMap<UUID, Long>();
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }
        if (!(playersFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/players").toFile()).exists()) {
            playersFolder.mkdir();
        }
        if ((playerFiles = playersFolder.listFiles()) == null) {
            return cooldowns;
        }
        for (File file : playerFiles) {
            if (!file.getName().endsWith(".json")) continue;
            try {
                ServerPlayer player;
                JsonObject root = JsonParser.parseReader((Reader)new FileReader(file)).getAsJsonObject();
                if (!root.has("uuid") || !root.has("cooldown_progress")) continue;
                UUID uuid = UUID.fromString(root.get("uuid").getAsString());
                if (!BattleFactory.INSTANCE.config().tickOfflinePlayerCooldowns && (player = BattleFactory.INSTANCE.server().getPlayerList().getPlayer(uuid)) == null) continue;
                long cooldownProgress = root.get("cooldown_progress").getAsLong();
                cooldowns.put(uuid, cooldownProgress);
            }
            catch (IOException e) {
                BattleFactory.INSTANCE.logError("[BattleFactory] Failed To Load Cooldown Information From Player Data File: " + file.getName());
                BattleFactory.INSTANCE.logError(e.getMessage());
            }
        }
        return cooldowns;
    }

    public static Map.Entry<UUID, Long> loadCooldown(UUID uuid) {
        File playerFile;
        File playersFolder;
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }
        if (!(playersFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/players").toFile()).exists()) {
            playersFolder.mkdir();
        }
        if ((playerFile = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/players/" + uuid.toString() + ".json").toFile()).exists()) {
            try {
                JsonObject root = JsonParser.parseReader((Reader)new FileReader(playerFile)).getAsJsonObject();
                if (!root.has("cooldown_progress")) {
                    return null;
                }
                long cooldownProgress = root.get("cooldown_progress").getAsLong();
                return Map.entry(uuid, cooldownProgress);
            }
            catch (IOException e) {
                BattleFactory.INSTANCE.logError("[BattleFactory] Failed To Load Cooldown Information From Player Data File: " + playerFile.getName());
                BattleFactory.INSTANCE.logError(e.getMessage());
            }
        }
        return null;
    }

    public static void saveCooldowns() {
        Map<UUID, Long> cooldowns = BattleFactory.INSTANCE.playerCooldowns;
        for (Map.Entry<UUID, Long> data : cooldowns.entrySet()) {
            UUID uuid = data.getKey();
            long cooldownProgress = data.getValue();
            PlayerDataManager.saveCooldown(uuid, cooldownProgress);
        }
    }

    public static void saveCooldown(UUID uuid, long cooldownProgress) {
        File playerFile;
        File playersFolder;
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }
        if (!(playersFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/players").toFile()).exists()) {
            playersFolder.mkdir();
        }
        if ((playerFile = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/players/" + uuid.toString() + ".json").toFile()).exists()) {
            try {
                JsonObject root = JsonParser.parseReader((Reader)new FileReader(playerFile)).getAsJsonObject();
                root.addProperty("cooldown_progress", (Number)cooldownProgress);
                playerFile.delete();
                playerFile.createNewFile();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                FileWriter writer = new FileWriter(playerFile);
                gson.toJson((JsonElement)root, (Appendable)writer);
                ((Writer)writer).close();
            }
            catch (IOException e) {
                BattleFactory.INSTANCE.logError("[BattleFactory] Failed To Save Player Data File: " + playerFile.getName());
                BattleFactory.INSTANCE.logError(e.getMessage());
            }
        }
    }

    public static PlayerData loadPlayerData(ServerPlayer player) {
        File playerFile;
        File playersFolder;
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }
        if (!(playersFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/players").toFile()).exists()) {
            playersFolder.mkdir();
        }
        if ((playerFile = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/players/" + player.getStringUUID() + ".json").toFile()).exists()) {
            try {
                JsonObject root = JsonParser.parseReader((Reader)new FileReader(playerFile)).getAsJsonObject();
                int highestStreak = 0;
                String highestCompletedTier = "";
                long cooldownProgress = 0L;
                if (root.has("highest_streak")) {
                    highestStreak = root.get("highest_streak").getAsInt();
                }
                if (root.has("highest_completed_tier")) {
                    highestCompletedTier = root.get("highest_completed_tier").getAsString();
                }
                if (root.has("cooldown_progress")) {
                    cooldownProgress = root.get("cooldown_progress").getAsLong();
                }
                BattleFactory.INSTANCE.playerCooldowns.put(player.getUUID(), cooldownProgress);
                return new PlayerData(player.getUUID(), player.getScoreboardName(), highestStreak, highestCompletedTier, cooldownProgress);
            }
            catch (FileNotFoundException e) {
                BattleFactory.INSTANCE.logError("[BattleFactory] Failed To Load Player Data File: " + playerFile.getName());
                BattleFactory.INSTANCE.logError(e.getMessage());
            }
        }
        return PlayerDataManager.savePlayerData(player);
    }

    public static void saveAllPlayerData() {
        for (PlayerData data : BattleFactory.INSTANCE.getPlayerData().values()) {
            PlayerDataManager.savePlayerData(data);
        }
    }

    public static PlayerData savePlayerData(ServerPlayer player) {
        PlayerData data = BattleFactory.INSTANCE.getPlayerData(player);
        UUID uuid = player.getUUID();
        String username = player.getScoreboardName();
        int highestStreak = 0;
        String highestCompletedTier = "";
        long cooldownProgress = 0L;
        if (data != null) {
            uuid = data.uuid;
            username = data.username;
            highestStreak = data.highestStreak;
            highestCompletedTier = data.highestCompletedTier;
            cooldownProgress = data.cooldownProgress;
        }
        return PlayerDataManager.savePlayerData(new PlayerData(uuid, username, highestStreak, highestCompletedTier, cooldownProgress));
    }

    public static PlayerData savePlayerData(PlayerData data) {
        File playersFolder;
        UUID uuid = data.uuid;
        String username = data.username;
        int highestStreak = data.highestStreak;
        String highestCompletedTier = data.highestCompletedTier;
        long cooldownProgress = data.cooldownProgress;
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }
        if (!(playersFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/players").toFile()).exists()) {
            playersFolder.mkdir();
        }
        File playerFile = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/players/" + uuid.toString() + ".json").toFile();
        JsonObject root = new JsonObject();
        root.addProperty("uuid", uuid.toString());
        root.addProperty("username", username);
        root.addProperty("highest_streak", (Number)highestStreak);
        root.addProperty("highest_completed_tier", highestCompletedTier);
        root.addProperty("cooldown_progress", (Number)cooldownProgress);
        try {
            playerFile.delete();
            playerFile.createNewFile();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter(playerFile);
            gson.toJson((JsonElement)root, (Appendable)writer);
            ((Writer)writer).close();
        }
        catch (IOException e) {
            BattleFactory.INSTANCE.logError("[BattleFactory] Failed To Save Player Data File: " + playerFile.getName());
            BattleFactory.INSTANCE.logError(e.getMessage());
        }
        return data;
    }
}

