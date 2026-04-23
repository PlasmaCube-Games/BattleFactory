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
 *  net.minecraft.class_1792
 *  net.minecraft.class_2960
 *  net.minecraft.class_3218
 *  net.minecraft.class_7923
 */
package me.unariginal.cobblemonbattlefactory.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
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
import java.util.HashMap;
import java.util.List;
import me.unariginal.cobblemonbattlefactory.BattleFactory;
import me.unariginal.cobblemonbattlefactory.datatypes.Location;
import me.unariginal.cobblemonbattlefactory.datatypes.TierSettings;
import me.unariginal.cobblemonbattlefactory.datatypes.rewards.DistributionSection;
import me.unariginal.cobblemonbattlefactory.utils.ConfigUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_1792;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_7923;

public class Config {
    public boolean hideIrrelevantEntities = true;
    public int secondsBetweenBattles = 10;
    public int maxDistanceFromBattle = 15;
    public int maxLeaderboardPlayers = 100;
    public boolean swapPokemonAfterEachBattle = true;
    public boolean forcePokemonSwap = true;
    public boolean newTeamAtTierIncrease = true;
    public boolean forceTeamSwap = true;
    public int numberOfPokemonRented = 3;
    public int numberOfPokemonAvailable = 6;
    public boolean allowTierSelection = true;
    public boolean maxTierSelectIsHighestStreak = true;
    public boolean giveRewardsAtEnd = true;
    public boolean repeatFinalTier = true;
    public boolean tickOfflinePlayerCooldowns = true;
    public List<class_1792> bannedBagItems = new ArrayList<class_1792>();
    public List<String> bannedKeyItems = new ArrayList<String>(List.of("cobblemon:key_stone", "cobblemon:tera_orb", "cobblemon:z_ring", "cobblemon:dynamax_band"));
    public List<TierSettings> tiers = new ArrayList<TierSettings>();

    public Config() {
        try {
            this.loadConfig();
        }
        catch (IOException e) {
            BattleFactory.INSTANCE.logError("[BattleFactory] Failed to load config file. Error: " + e.getMessage());
        }
    }

    /*
     * WARNING - void declaration
     */
    public void loadConfig() throws IOException {
        void var7_13;
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }
        File configFile = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/config.json").toFile();
        JsonObject newRoot = new JsonObject();
        JsonObject root = new JsonObject();
        if (configFile.exists()) {
            root = JsonParser.parseReader((Reader)new FileReader(configFile)).getAsJsonObject();
        }
        if (root.has("debug")) {
            BattleFactory.DEBUG = root.get("debug").getAsBoolean();
        }
        newRoot.addProperty("debug", Boolean.valueOf(BattleFactory.DEBUG));
        if (root.has("hide_irrelevant_entities")) {
            this.hideIrrelevantEntities = root.get("hide_irrelevant_entities").getAsBoolean();
        }
        newRoot.addProperty("hide_irrelevant_entities", Boolean.valueOf(this.hideIrrelevantEntities));
        if (root.has("seconds_between_battles")) {
            this.secondsBetweenBattles = root.get("seconds_between_battles").getAsInt();
        }
        newRoot.addProperty("seconds_between_battles", (Number)this.secondsBetweenBattles);
        if (root.has("max_distance_from_battle")) {
            this.maxDistanceFromBattle = root.get("max_distance_from_battle").getAsInt();
        }
        newRoot.addProperty("max_distance_from_battle", (Number)this.maxDistanceFromBattle);
        if (root.has("max_leaderboard_players")) {
            this.maxLeaderboardPlayers = root.get("max_leaderboard_players").getAsInt();
        }
        newRoot.addProperty("max_leaderboard_players", (Number)this.maxLeaderboardPlayers);
        if (root.has("swap_pokemon_after_each_battle")) {
            this.swapPokemonAfterEachBattle = root.get("swap_pokemon_after_each_battle").getAsBoolean();
        }
        newRoot.addProperty("swap_pokemon_after_each_battle", Boolean.valueOf(this.swapPokemonAfterEachBattle));
        if (root.has("force_pokemon_swap")) {
            this.forcePokemonSwap = root.get("force_pokemon_swap").getAsBoolean();
        }
        newRoot.addProperty("force_pokemon_swap", Boolean.valueOf(this.forcePokemonSwap));
        if (root.has("new_team_at_tier_increase")) {
            this.newTeamAtTierIncrease = root.get("new_team_at_tier_increase").getAsBoolean();
        }
        newRoot.addProperty("new_team_at_tier_increase", Boolean.valueOf(this.newTeamAtTierIncrease));
        if (root.has("force_team_swap")) {
            this.forceTeamSwap = root.get("force_team_swap").getAsBoolean();
        }
        newRoot.addProperty("force_team_swap", Boolean.valueOf(this.forceTeamSwap));
        if (root.has("number_of_pokemon_rented")) {
            this.numberOfPokemonRented = root.get("number_of_pokemon_rented").getAsInt();
        }
        if (this.numberOfPokemonRented < 1) {
            this.numberOfPokemonRented = 1;
        }
        if (this.numberOfPokemonRented > 6) {
            this.numberOfPokemonRented = 6;
        }
        newRoot.addProperty("number_of_pokemon_rented", (Number)this.numberOfPokemonRented);
        if (root.has("number_of_pokemon_available")) {
            this.numberOfPokemonAvailable = root.get("number_of_pokemon_available").getAsInt();
        }
        newRoot.addProperty("number_of_pokemon_available", (Number)this.numberOfPokemonAvailable);
        if (root.has("allow_tier_selection")) {
            this.allowTierSelection = root.get("allow_tier_selection").getAsBoolean();
        }
        newRoot.addProperty("allow_tier_selection", Boolean.valueOf(this.allowTierSelection));
        if (root.has("max_tier_select_is_highest_streak")) {
            this.maxTierSelectIsHighestStreak = root.get("max_tier_select_is_highest_streak").getAsBoolean();
        }
        newRoot.addProperty("max_tier_select_is_highest_streak", Boolean.valueOf(this.maxTierSelectIsHighestStreak));
        if (root.has("give_rewards_at_end")) {
            this.giveRewardsAtEnd = root.get("give_rewards_at_end").getAsBoolean();
        }
        newRoot.addProperty("give_rewards_at_end", Boolean.valueOf(this.giveRewardsAtEnd));
        if (root.has("repeat_final_tier")) {
            this.repeatFinalTier = root.get("repeat_final_tier").getAsBoolean();
        }
        newRoot.addProperty("repeat_final_tier", Boolean.valueOf(this.repeatFinalTier));
        if (root.has("tick_offline_player_cooldowns")) {
            this.tickOfflinePlayerCooldowns = root.get("tick_offline_player_cooldowns").getAsBoolean();
        }
        newRoot.addProperty("tick_offline_player_cooldowns", Boolean.valueOf(this.tickOfflinePlayerCooldowns));
        JsonArray bannedBagItemsArray = new JsonArray();
        if (root.has("banned_bag_items")) {
            bannedBagItemsArray = root.get("banned_bag_items").getAsJsonArray();
        }
        for (JsonElement jsonElement : bannedBagItemsArray) {
            String string = jsonElement.getAsString();
            if (!class_7923.field_41178.method_10250(class_2960.method_60654((String)string))) continue;
            this.bannedBagItems.add((class_1792)class_7923.field_41178.method_10223(class_2960.method_60654((String)string)));
        }
        bannedBagItemsArray = new JsonArray();
        for (class_1792 class_17922 : this.bannedBagItems) {
            bannedBagItemsArray.add(class_7923.field_41178.method_10221((Object)class_17922).toString());
        }
        newRoot.add("banned_bag_items", (JsonElement)bannedBagItemsArray);
        if (root.has("banned_key_items")) {
            this.bannedKeyItems = root.getAsJsonArray("banned_key_items").asList().stream().map(JsonElement::getAsString).toList();
        }
        JsonArray bannedKeyItemsArray = new JsonArray();
        for (String string : this.bannedKeyItems) {
            bannedKeyItemsArray.add(string);
        }
        newRoot.add("banned_key_items", (JsonElement)bannedKeyItemsArray);
        JsonObject jsonObject = new JsonObject();
        if (root.has("tiers")) {
            JsonObject jsonObject2 = root.get("tiers").getAsJsonObject();
        }
        for (String key : var7_13.keySet()) {
            JsonArray possibleRentalPoolsArray;
            JsonObject tierObject = var7_13.get(key).getAsJsonObject();
            if (!tierObject.has("battles_for_next_tier") || !tierObject.has("possible_npcs")) continue;
            String displayName = key;
            if (tierObject.has("display_name")) {
                displayName = tierObject.get("display_name").getAsString();
            }
            String displayItem = "minecraft:emerald";
            if (tierObject.has("display_item")) {
                displayItem = tierObject.get("display_item").getAsString();
            }
            int battlesForNextTier = tierObject.get("battles_for_next_tier").getAsInt();
            HashMap<Integer, Location> battleLocations = new HashMap<Integer, Location>();
            JsonObject battleLocationsObject = new JsonObject();
            if (tierObject.has("battle_locations")) {
                battleLocationsObject = tierObject.get("battle_locations").getAsJsonObject();
            }
            for (int i = 1; i <= battlesForNextTier; ++i) {
                if (!battleLocationsObject.has("" + i)) continue;
                JsonObject battleLocationObject = battleLocationsObject.get("" + i).getAsJsonObject();
                Object world = BattleFactory.INSTANCE.server().method_30002();
                if (battleLocationObject.has("world")) {
                    String worldPath = battleLocationObject.get("world").getAsString();
                    for (class_3218 w : BattleFactory.INSTANCE.server().method_3738()) {
                        String id = w.method_27983().method_29177().toString();
                        String path = w.method_27983().method_29177().method_12832();
                        if (!id.equals(worldPath) && !path.equals(worldPath)) continue;
                        world = w;
                        break;
                    }
                }
                double playerX = 0.0;
                double playerY = 0.0;
                double playerZ = 0.0;
                float yRot = 90.0f;
                float xRot = 0.0f;
                if (battleLocationObject.has("player_location")) {
                    JsonObject playerLocationObject = battleLocationObject.get("player_location").getAsJsonObject();
                    if (playerLocationObject.has("x")) {
                        playerX = playerLocationObject.get("x").getAsDouble();
                    }
                    if (playerLocationObject.has("y")) {
                        playerY = playerLocationObject.get("y").getAsDouble();
                    }
                    if (playerLocationObject.has("z")) {
                        playerZ = playerLocationObject.get("z").getAsDouble();
                    }
                    if (playerLocationObject.has("yRot")) {
                        yRot = playerLocationObject.get("yRot").getAsFloat();
                    }
                    if (playerLocationObject.has("xRot")) {
                        xRot = playerLocationObject.get("xRot").getAsFloat();
                    }
                }
                double npcX = playerX + 5.0;
                double npcY = playerY;
                double npcZ = playerZ;
                if (battleLocationObject.has("npc_location")) {
                    JsonObject npcLocationObject = battleLocationObject.get("npc_location").getAsJsonObject();
                    if (npcLocationObject.has("x")) {
                        npcX = npcLocationObject.get("x").getAsDouble();
                    }
                    if (npcLocationObject.has("y")) {
                        npcY = npcLocationObject.get("y").getAsDouble();
                    }
                    if (npcLocationObject.has("z")) {
                        npcZ = npcLocationObject.get("z").getAsDouble();
                    }
                }
                battleLocations.put(i, new Location((class_3218)world, playerX, playerY, playerZ, yRot, xRot, npcX, npcY, npcZ));
            }
            HashMap<String, Double> possibleNPCs = new HashMap<String, Double>();
            JsonArray possibleNPCsArray = tierObject.get("possible_npcs").getAsJsonArray();
            if (possibleNPCsArray.isEmpty()) continue;
            for (JsonElement possibleNPC : possibleNPCsArray) {
                JsonObject possibleNPCObject = possibleNPC.getAsJsonObject();
                if (!possibleNPCObject.has("weight") || !possibleNPCObject.has("npc_id")) continue;
                double weight = possibleNPCObject.get("weight").getAsDouble();
                String npcId = possibleNPCObject.get("npc_id").getAsString();
                possibleNPCs.put(npcId, weight);
            }
            if (possibleNPCs.isEmpty()) continue;
            HashMap<String, Double> possibleRentalPools = new HashMap<String, Double>();
            if (!tierObject.has("possible_rental_pools") || (possibleRentalPoolsArray = tierObject.getAsJsonArray("possible_rental_pools")).isEmpty()) continue;
            for (JsonElement possibleRentalPool : possibleRentalPoolsArray) {
                JsonObject possibleRentalPoolObject = possibleRentalPool.getAsJsonObject();
                if (!possibleRentalPoolObject.has("weight") || !possibleRentalPoolObject.has("rental_pool")) continue;
                double weight = possibleRentalPoolObject.get("weight").getAsDouble();
                String rentalPoolId = possibleRentalPoolObject.get("rental_pool").getAsString();
                possibleRentalPools.put(rentalPoolId, weight);
            }
            if (possibleRentalPools.isEmpty()) continue;
            DistributionSection perBattleRewards = null;
            if (tierObject.has("per_battle_rewards")) {
                perBattleRewards = ConfigUtils.getDistributionSection(tierObject.get("per_battle_rewards").getAsJsonObject());
            }
            DistributionSection tierCompletionRewards = null;
            if (tierObject.has("tier_completion_rewards")) {
                tierCompletionRewards = ConfigUtils.getDistributionSection(tierObject.get("tier_completion_rewards").getAsJsonObject());
            }
            boolean hasBonusEncounter = false;
            if (tierObject.has("has_bonus_encounter")) {
                hasBonusEncounter = tierObject.get("has_bonus_encounter").getAsBoolean();
            }
            String bonusEncounterNPC = null;
            Location bonusEncounterLocation = null;
            DistributionSection bonusEncounterRewards = null;
            JsonObject bonusEncounterObject = new JsonObject();
            if (hasBonusEncounter) {
                if (tierObject.has("bonus_encounter_settings")) {
                    bonusEncounterObject = tierObject.get("bonus_encounter_settings").getAsJsonObject();
                }
                if (bonusEncounterObject.has("bonus_encounter_npc")) {
                    bonusEncounterNPC = bonusEncounterObject.get("bonus_encounter_npc").getAsString();
                }
                if (bonusEncounterObject.has("bonus_encounter_location")) {
                    JsonObject bonusEncounterLocationObject = bonusEncounterObject.get("bonus_encounter_location").getAsJsonObject();
                    class_3218 world = BattleFactory.INSTANCE.server().method_30002();
                    if (bonusEncounterLocationObject.has("world")) {
                        String worldPath = bonusEncounterLocationObject.get("world").getAsString();
                        for (class_3218 w : BattleFactory.INSTANCE.server().method_3738()) {
                            String id = w.method_27983().method_29177().toString();
                            String path = w.method_27983().method_29177().method_12832();
                            if (!id.equals(worldPath) && !path.equals(worldPath)) continue;
                            world = w;
                            break;
                        }
                    }
                    double playerX = 0.0;
                    double playerY = 0.0;
                    double playerZ = 0.0;
                    float yRot = 90.0f;
                    float xRot = 0.0f;
                    if (bonusEncounterLocationObject.has("player_location")) {
                        JsonObject playerLocationObject = bonusEncounterLocationObject.get("player_location").getAsJsonObject();
                        if (playerLocationObject.has("x")) {
                            playerX = playerLocationObject.get("x").getAsDouble();
                        }
                        if (playerLocationObject.has("y")) {
                            playerY = playerLocationObject.get("y").getAsDouble();
                        }
                        if (playerLocationObject.has("z")) {
                            playerZ = playerLocationObject.get("z").getAsDouble();
                        }
                        if (playerLocationObject.has("yRot")) {
                            yRot = playerLocationObject.get("yRot").getAsFloat();
                        }
                        if (playerLocationObject.has("xRot")) {
                            xRot = playerLocationObject.get("xRot").getAsFloat();
                        }
                    }
                    double npcX = playerX + 5.0;
                    double npcY = playerY;
                    double npcZ = playerZ;
                    if (bonusEncounterLocationObject.has("npc_location")) {
                        JsonObject npcLocationObject = bonusEncounterLocationObject.get("npc_location").getAsJsonObject();
                        if (npcLocationObject.has("x")) {
                            npcX = npcLocationObject.get("x").getAsDouble();
                        }
                        if (npcLocationObject.has("y")) {
                            npcY = npcLocationObject.get("y").getAsDouble();
                        }
                        if (npcLocationObject.has("z")) {
                            npcZ = npcLocationObject.get("z").getAsDouble();
                        }
                    }
                    bonusEncounterLocation = new Location(world, playerX, playerY, playerZ, yRot, xRot, npcX, npcY, npcZ);
                }
                if (bonusEncounterObject.has("bonus_encounter_rewards")) {
                    bonusEncounterRewards = ConfigUtils.getDistributionSection(bonusEncounterObject.get("bonus_encounter_rewards").getAsJsonObject());
                }
            }
            this.tiers.add(new TierSettings(key, displayName, displayItem, battlesForNextTier, battleLocations, possibleNPCs, possibleRentalPools, perBattleRewards, tierCompletionRewards, hasBonusEncounter, bonusEncounterNPC, bonusEncounterLocation, bonusEncounterRewards));
        }
        newRoot.add("tiers", (JsonElement)var7_13);
        configFile.delete();
        configFile.createNewFile();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileWriter writer = new FileWriter(configFile);
        gson.toJson((JsonElement)newRoot, (Appendable)writer);
        ((Writer)writer).close();
    }

    public TierSettings getTier(String ID) {
        for (TierSettings tier : this.tiers) {
            if (!tier.tierID().equalsIgnoreCase(ID)) continue;
            return tier;
        }
        return null;
    }

    public TierSettings getNextTier(String ID) {
        boolean nextTeir = false;
        TierSettings backupTier = null;
        for (TierSettings tier : this.tiers) {
            if (nextTeir) {
                return tier;
            }
            if (!tier.tierID().equalsIgnoreCase(ID)) continue;
            nextTeir = true;
            backupTier = tier;
        }
        if (this.repeatFinalTier) {
            return backupTier;
        }
        return null;
    }
}

