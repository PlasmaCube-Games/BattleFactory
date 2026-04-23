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
 *  net.fabricmc.loader.api.FabricLoader
 */
package me.unariginal.cobblemonbattlefactory.config.playerdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.unariginal.cobblemonbattlefactory.BattleFactory;
import me.unariginal.cobblemonbattlefactory.datatypes.LeaderboardSection;
import net.fabricmc.loader.api.FabricLoader;

public class LeaderboardManager {
    public static List<LeaderboardSection> leaderboard = new ArrayList<LeaderboardSection>();

    public static LeaderboardSection getLeaderboardSection(UUID uuid) {
        for (LeaderboardSection leaderboardSection : leaderboard) {
            if (!leaderboardSection.uuid.equals(uuid)) continue;
            return leaderboardSection;
        }
        return null;
    }

    public static void updateLeaderboardSection(LeaderboardSection leaderboardSection) {
        boolean exists = false;
        for (int i = 0; i < leaderboard.size(); ++i) {
            if (!leaderboardSection.uuid.equals(LeaderboardManager.leaderboard.get((int)i).uuid)) continue;
            leaderboard.set(i, leaderboardSection);
            exists = true;
        }
        if (!exists && leaderboard.size() < BattleFactory.INSTANCE.config().maxLeaderboardPlayers) {
            leaderboard.add(leaderboardSection);
        }
        leaderboard = LeaderboardManager.sortLeaderboard(new ArrayList<LeaderboardSection>(leaderboard));
    }

    public static void loadLeaderboard() {
        File leaderboardFile;
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }
        if ((leaderboardFile = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/leaderboard.json").toFile()).exists()) {
            try {
                JsonObject root = JsonParser.parseReader((Reader)new FileReader(leaderboardFile)).getAsJsonObject();
                JsonArray leaderboardArray = new JsonArray();
                if (root.has("leaderboard")) {
                    leaderboardArray = root.getAsJsonArray("leaderboard");
                }
                ArrayList<LeaderboardSection> leaderboardSections = new ArrayList<LeaderboardSection>();
                for (JsonElement leaderboardElement : leaderboardArray) {
                    JsonObject leaderboardObject = leaderboardElement.getAsJsonObject();
                    if (!leaderboardObject.has("placement") || !leaderboardObject.has("uuid") || !leaderboardObject.has("username") || !leaderboardObject.has("highest_streak") || !leaderboardObject.has("date_achieved")) continue;
                    int placement = leaderboardObject.get("placement").getAsInt();
                    UUID uuid = UUID.fromString(leaderboardObject.get("uuid").getAsString());
                    String username = leaderboardObject.get("username").getAsString();
                    int highestStreak = leaderboardObject.get("highest_streak").getAsInt();
                    LocalDateTime dateAchieved = LocalDateTime.parse(leaderboardObject.get("date_achieved").getAsString());
                    leaderboardSections.add(new LeaderboardSection(placement, uuid, username, highestStreak, dateAchieved));
                    if (leaderboardSections.size() < BattleFactory.INSTANCE.config().maxLeaderboardPlayers) continue;
                    break;
                }
                leaderboard = LeaderboardManager.sortLeaderboard(new ArrayList<LeaderboardSection>(leaderboardSections));
            }
            catch (FileNotFoundException e) {
                BattleFactory.INSTANCE.logError("[BattleFactory] Failed To Load Leaderboard File: " + e.getMessage());
            }
        }
    }

    public static void saveLeaderboard() {
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }
        File leaderboardFile = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/leaderboard.json").toFile();
        JsonObject root = new JsonObject();
        JsonArray leaderboardArray = new JsonArray();
        if (!leaderboard.isEmpty()) {
            for (int i = 0; i < Math.min(leaderboard.size(), BattleFactory.INSTANCE.config().maxLeaderboardPlayers); ++i) {
                LeaderboardSection leaderboardSection = leaderboard.get(i);
                JsonObject leaderboardObject = new JsonObject();
                leaderboardObject.addProperty("placement", (Number)(i + 1));
                leaderboardObject.addProperty("uuid", leaderboardSection.uuid.toString());
                leaderboardObject.addProperty("username", leaderboardSection.name);
                leaderboardObject.addProperty("highest_streak", (Number)leaderboardSection.highestStreak);
                leaderboardObject.addProperty("date_achieved", leaderboardSection.dateAchieved.toString());
                leaderboardArray.add((JsonElement)leaderboardObject);
            }
        }
        root.add("leaderboard", (JsonElement)leaderboardArray);
        try {
            leaderboardFile.delete();
            leaderboardFile.createNewFile();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter(leaderboardFile);
            gson.toJson((JsonElement)root, (Appendable)writer);
            ((Writer)writer).close();
        }
        catch (IOException e) {
            BattleFactory.INSTANCE.logError("[BattleFactory] Failed To Save Leaderboard File: " + e.getMessage());
        }
    }

    public static List<LeaderboardSection> sortLeaderboard(List<LeaderboardSection> unsortedLeaderboard) {
        LeaderboardSection temp;
        int j;
        int i;
        for (i = 0; i < unsortedLeaderboard.size(); ++i) {
            for (j = i + 1; j < unsortedLeaderboard.size(); ++j) {
                if (unsortedLeaderboard.get((int)i).highestStreak >= unsortedLeaderboard.get((int)j).highestStreak) continue;
                temp = unsortedLeaderboard.get(j);
                unsortedLeaderboard.set(j, unsortedLeaderboard.get(i));
                unsortedLeaderboard.get((int)j).placement = j + 1;
                unsortedLeaderboard.set(i, temp);
                unsortedLeaderboard.get((int)i).placement = i + 1;
            }
        }
        for (i = 0; i < unsortedLeaderboard.size(); ++i) {
            for (j = i + 1; j < unsortedLeaderboard.size(); ++j) {
                if (unsortedLeaderboard.get((int)i).highestStreak != unsortedLeaderboard.get((int)j).highestStreak || !unsortedLeaderboard.get((int)i).dateAchieved.isAfter(unsortedLeaderboard.get((int)j).dateAchieved)) continue;
                temp = unsortedLeaderboard.get(j);
                unsortedLeaderboard.set(j, unsortedLeaderboard.get(i));
                unsortedLeaderboard.get((int)j).placement = j + 1;
                unsortedLeaderboard.set(i, temp);
                unsortedLeaderboard.get((int)i).placement = i + 1;
            }
        }
        return unsortedLeaderboard;
    }
}

