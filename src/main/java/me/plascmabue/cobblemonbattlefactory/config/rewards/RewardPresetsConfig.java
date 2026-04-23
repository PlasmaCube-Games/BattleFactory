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
package me.plascmabue.cobblemonbattlefactory.config.rewards;

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
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import me.plascmabue.cobblemonbattlefactory.datatypes.rewards.Reward;
import me.plascmabue.cobblemonbattlefactory.utils.ConfigUtils;
import net.fabricmc.loader.api.FabricLoader;

public class RewardPresetsConfig {
    List<Reward> rewardPresets = new ArrayList<Reward>();

    public RewardPresetsConfig() {
        try {
            this.loadConfig();
        }
        catch (IOException e) {
            BattleFactory.INSTANCE.logError("[BattleFactory] Failed to load reward presets config file. Error: " + e.getMessage());
        }
    }

    public void loadConfig() throws IOException {
        File rootFolder = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory").toFile();
        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }
        File configFile = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory/reward_presets.json").toFile();
        JsonObject root = new JsonObject();
        if (configFile.exists()) {
            root = JsonParser.parseReader((Reader)new FileReader(configFile)).getAsJsonObject();
            this.rewardPresets.clear();
            for (String key : root.keySet()) {
                JsonObject rewardPresetObject = root.getAsJsonObject(key);
                Reward rewardPreset = ConfigUtils.getReward(rewardPresetObject, key);
                if (rewardPreset == null) continue;
                this.rewardPresets.add(rewardPreset);
            }
        } else {
            configFile.createNewFile();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter(configFile);
            gson.toJson((JsonElement)root, (Appendable)writer);
            ((Writer)writer).close();
        }
    }

    public Reward getRewardPreset(String name) {
        for (Reward rewardPreset : this.rewardPresets) {
            if (!rewardPreset.name.equalsIgnoreCase(name)) continue;
            return rewardPreset;
        }
        return null;
    }
}

