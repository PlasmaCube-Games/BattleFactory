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
package me.unariginal.cobblemonbattlefactory.config.rewards;

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
import java.util.ArrayList;
import java.util.List;
import me.unariginal.cobblemonbattlefactory.BattleFactory;
import me.unariginal.cobblemonbattlefactory.datatypes.rewards.RewardPool;
import me.unariginal.cobblemonbattlefactory.utils.ConfigUtils;
import net.fabricmc.loader.api.FabricLoader;

public class RewardPoolPresetsConfig {
    List<RewardPool> rewardPoolPresets = new ArrayList<RewardPool>();

    public RewardPoolPresetsConfig() {
        try {
            this.loadConfig();
        }
        catch (IOException e) {
            BattleFactory.INSTANCE.logError("[BattleFactory] Failed to load reward pool presets config file. Error: " + e.getMessage());
        }
    }

    public void loadConfig() throws IOException {
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }
        File configFile = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/reward_pool_presets.json").toFile();
        JsonObject root = new JsonObject();
        if (configFile.exists()) {
            root = JsonParser.parseReader((Reader)new FileReader(configFile)).getAsJsonObject();
            this.rewardPoolPresets.clear();
            for (String key : root.keySet()) {
                JsonObject rewardPoolPresetObject = root.getAsJsonObject(key);
                RewardPool rewardPoolPreset = ConfigUtils.getRewardPool(rewardPoolPresetObject, key);
                if (rewardPoolPreset == null) continue;
                this.rewardPoolPresets.add(rewardPoolPreset);
            }
        } else {
            configFile.createNewFile();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter(configFile);
            gson.toJson((JsonElement)root, (Appendable)writer);
            ((Writer)writer).close();
        }
    }

    public RewardPool getRewardPoolPreset(String name) {
        for (RewardPool rewardPoolPreset : this.rewardPoolPresets) {
            if (!rewardPoolPreset.name().equalsIgnoreCase(name)) continue;
            return rewardPoolPreset;
        }
        return null;
    }
}

