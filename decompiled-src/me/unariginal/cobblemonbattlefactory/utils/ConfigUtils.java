/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cobblemon.mod.common.api.abilities.Abilities
 *  com.cobblemon.mod.common.api.moves.MoveTemplate
 *  com.cobblemon.mod.common.api.moves.Moves
 *  com.cobblemon.mod.common.api.pokemon.Natures
 *  com.cobblemon.mod.common.api.pokemon.PokemonProperties
 *  com.cobblemon.mod.common.api.pokemon.PokemonSpecies
 *  com.cobblemon.mod.common.api.pokemon.stats.Stat
 *  com.cobblemon.mod.common.api.pokemon.stats.Stats
 *  com.cobblemon.mod.common.pokemon.EVs
 *  com.cobblemon.mod.common.pokemon.Gender
 *  com.cobblemon.mod.common.pokemon.IVs
 *  com.cobblemon.mod.common.pokemon.Pokemon
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1935
 *  net.minecraft.class_2561
 *  net.minecraft.class_2960
 *  net.minecraft.class_7923
 *  net.minecraft.class_9290
 *  net.minecraft.class_9323
 *  net.minecraft.class_9326
 *  net.minecraft.class_9334
 */
package me.unariginal.cobblemonbattlefactory.utils;

import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.EVs;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import me.unariginal.cobblemonbattlefactory.BattleFactory;
import me.unariginal.cobblemonbattlefactory.datatypes.rewards.CommandReward;
import me.unariginal.cobblemonbattlefactory.datatypes.rewards.DistributionSection;
import me.unariginal.cobblemonbattlefactory.datatypes.rewards.ItemReward;
import me.unariginal.cobblemonbattlefactory.datatypes.rewards.Reward;
import me.unariginal.cobblemonbattlefactory.datatypes.rewards.RewardPool;
import me.unariginal.cobblemonbattlefactory.utils.RandomUtils;
import me.unariginal.cobblemonbattlefactory.utils.TextUtils;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1935;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_7923;
import net.minecraft.class_9290;
import net.minecraft.class_9323;
import net.minecraft.class_9326;
import net.minecraft.class_9334;

public class ConfigUtils {
    public static DistributionSection getDistributionSection(JsonObject distributionObject) {
        boolean allowDuplicates = true;
        if (distributionObject.has("allow_duplicates")) {
            allowDuplicates = distributionObject.get("allow_duplicates").getAsBoolean();
        }
        int minRolls = 1;
        int maxRolls = 1;
        JsonObject rollsObject = new JsonObject();
        if (distributionObject.has("rolls")) {
            rollsObject = distributionObject.get("rolls").getAsJsonObject();
        }
        if (rollsObject.has("min")) {
            minRolls = rollsObject.get("min").getAsInt();
        }
        if (rollsObject.has("max")) {
            maxRolls = rollsObject.get("max").getAsInt();
        }
        if (maxRolls < minRolls) {
            maxRolls = minRolls;
        }
        HashMap<RewardPool, Double> rewardPoolsMap = new HashMap<RewardPool, Double>();
        if (!distributionObject.has("reward_pools")) {
            return null;
        }
        JsonArray rewardPoolsArray = distributionObject.get("reward_pools").getAsJsonArray();
        for (JsonElement rewardPoolElement : rewardPoolsArray) {
            JsonObject poolObject;
            RewardPool pool;
            JsonObject rewardPoolObject = rewardPoolElement.getAsJsonObject();
            if (!rewardPoolObject.has("weight")) continue;
            double weight = rewardPoolObject.get("weight").getAsDouble();
            if (rewardPoolObject.has("pool_preset")) {
                String rewardPoolPresetName = rewardPoolObject.get("pool_preset").getAsString();
                RewardPool rewardPoolPreset = BattleFactory.INSTANCE.rewardPoolPresetsConfig().getRewardPoolPreset(rewardPoolPresetName);
                if (rewardPoolPreset == null) continue;
                rewardPoolsMap.put(rewardPoolPreset, weight);
                continue;
            }
            if (!rewardPoolObject.has("pool") || (pool = ConfigUtils.getRewardPool(poolObject = rewardPoolObject.getAsJsonObject(), null)) == null) continue;
            rewardPoolsMap.put(pool, weight);
        }
        if (rewardPoolsMap.isEmpty()) {
            return null;
        }
        return new DistributionSection(allowDuplicates, minRolls, maxRolls, rewardPoolsMap);
    }

    public static RewardPool getRewardPool(JsonObject rewardPoolObject, String rewardPoolName) {
        boolean allowDuplicates = true;
        if (rewardPoolObject.has("allow_duplicates")) {
            allowDuplicates = rewardPoolObject.get("allow_duplicates").getAsBoolean();
        }
        int minRolls = 1;
        int maxRolls = 1;
        JsonObject rollsObject = new JsonObject();
        if (rewardPoolObject.has("rolls")) {
            rollsObject = rewardPoolObject.get("rolls").getAsJsonObject();
        }
        if (rollsObject.has("min")) {
            minRolls = rollsObject.get("min").getAsInt();
        }
        if (rollsObject.has("max")) {
            maxRolls = rollsObject.get("max").getAsInt();
        }
        if (maxRolls < minRolls) {
            maxRolls = minRolls;
        }
        HashMap<Reward, Double> rewardsMap = new HashMap<Reward, Double>();
        if (!rewardPoolObject.has("rewards")) {
            return null;
        }
        JsonArray rewardsArray = rewardPoolObject.get("rewards").getAsJsonArray();
        for (JsonElement rewardElement : rewardsArray) {
            JsonObject reward;
            Reward innerReward;
            JsonObject rewardObject = rewardElement.getAsJsonObject();
            if (!rewardObject.has("weight")) continue;
            double rewardWeight = rewardObject.get("weight").getAsDouble();
            if (rewardObject.has("reward_preset")) {
                String rewardPresetName = rewardObject.get("reward_preset").getAsString();
                Reward rewardPreset = BattleFactory.INSTANCE.rewardPresetsConfig().getRewardPreset(rewardPresetName);
                if (rewardPreset == null) continue;
                rewardsMap.put(rewardPreset, rewardWeight);
                continue;
            }
            if (!rewardObject.has("reward") || (innerReward = ConfigUtils.getReward(reward = rewardObject.get("reward").getAsJsonObject(), null)) == null) continue;
            rewardsMap.put(innerReward, rewardWeight);
        }
        if (rewardsMap.isEmpty()) {
            return null;
        }
        return new RewardPool(UUID.randomUUID(), rewardPoolName, allowDuplicates, minRolls, maxRolls, rewardsMap);
    }

    public static Reward getReward(JsonObject rewardObject, String rewardName) {
        String type;
        if (!rewardObject.has("type")) {
            return null;
        }
        switch (type = rewardObject.get("type").getAsString()) {
            case "item": {
                if (!rewardObject.has("item")) {
                    return null;
                }
                String itemId = rewardObject.get("item").getAsString();
                class_1792 rewardItem = (class_1792)class_7923.field_41178.method_10223(class_2960.method_60654((String)itemId));
                String itemName = "";
                if (rewardObject.has("item_name")) {
                    itemName = rewardObject.get("item_name").getAsString();
                }
                ArrayList<String> itemLore = new ArrayList();
                if (rewardObject.has("item_lore")) {
                    itemLore = rewardObject.get("item_lore").getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList();
                }
                class_9326 itemData = class_9326.field_49588;
                if (rewardObject.has("item_data")) {
                    itemData = (class_9326)((Pair)class_9326.field_49589.decode((DynamicOps)JsonOps.INSTANCE, (Object)rewardObject.get("item_data")).getOrThrow()).getFirst();
                }
                int minCount = 1;
                int maxCount = 1;
                JsonObject countObject = new JsonObject();
                if (rewardObject.has("count")) {
                    countObject = rewardObject.get("count").getAsJsonObject();
                }
                if (countObject.has("min")) {
                    minCount = countObject.get("min").getAsInt();
                }
                if (countObject.has("max")) {
                    maxCount = countObject.get("max").getAsInt();
                }
                if (maxCount < minCount) {
                    maxCount = minCount;
                }
                return new ItemReward(UUID.randomUUID(), rewardName, type, rewardItem, itemName, itemLore, itemData, minCount, maxCount);
            }
            case "command": {
                List<String> commands = new ArrayList<String>();
                if (rewardObject.has("commands")) {
                    commands = rewardObject.get("commands").getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList();
                }
                return new CommandReward(UUID.randomUUID(), rewardName, type, commands);
            }
        }
        return null;
    }

    public static Pokemon getPokemon(JsonObject pokemonObject) {
        Map.Entry<?, Double> entry;
        String chosenNature;
        Map.Entry<?, Double> entry2;
        String chosenAbility;
        Map.Entry<?, Double> entry3;
        Map.Entry<?, Double> entry4;
        PokemonProperties pokemonBuilder = new PokemonProperties();
        if (pokemonObject.has("species_features")) {
            pokemonBuilder = PokemonProperties.Companion.parse(pokemonObject.get("species_features").getAsString());
        }
        if (!pokemonObject.has("species")) {
            return null;
        }
        String speciesName = pokemonObject.get("species").getAsString();
        if (PokemonSpecies.getByName((String)speciesName) == null) {
            return null;
        }
        pokemonBuilder.setSpecies(speciesName);
        int minLevel = 1;
        int maxLevel = 1;
        JsonObject levelRangeObject = new JsonObject();
        if (pokemonObject.has("level_range")) {
            levelRangeObject = pokemonObject.get("level_range").getAsJsonObject();
        }
        if (levelRangeObject.has("min")) {
            minLevel = levelRangeObject.get("min").getAsInt();
        }
        if (levelRangeObject.has("max")) {
            maxLevel = levelRangeObject.get("max").getAsInt();
        }
        if (maxLevel < minLevel) {
            maxLevel = minLevel;
        }
        int level = new Random().nextInt(minLevel, maxLevel + 1);
        pokemonBuilder.setLevel(Integer.valueOf(level));
        HashMap<String, Double> possibleGendersMap = new HashMap<String, Double>();
        if (pokemonObject.has("possible_genders")) {
            JsonArray possibleGenders = pokemonObject.get("possible_genders").getAsJsonArray();
            for (Object genderElement : possibleGenders) {
                JsonObject genderObject = genderElement.getAsJsonObject();
                if (!genderObject.has("weight")) continue;
                double weight = genderObject.get("weight").getAsDouble();
                if (!genderObject.has("gender")) continue;
                String gender = genderObject.get("gender").getAsString();
                possibleGendersMap.put(gender, weight);
            }
        }
        if (!possibleGendersMap.isEmpty() && (entry4 = RandomUtils.getRandomEntry(possibleGendersMap)) != null) {
            String chosenGender = (String)entry4.getKey();
            switch (chosenGender) {
                case "male": {
                    pokemonBuilder.setGender(Gender.MALE);
                    break;
                }
                case "female": {
                    pokemonBuilder.setGender(Gender.FEMALE);
                    break;
                }
                case "genderless": {
                    pokemonBuilder.setGender(Gender.GENDERLESS);
                }
            }
        }
        HashMap<String, Double> possibleAbilitiesMap = new HashMap<String, Double>();
        if (pokemonObject.has("possible_abilities")) {
            JsonArray possibleAbilities = pokemonObject.get("possible_abilities").getAsJsonArray();
            for (JsonElement abilityElement : possibleAbilities) {
                JsonObject abilityObject = abilityElement.getAsJsonObject();
                if (!abilityObject.has("weight")) continue;
                double weight = abilityObject.get("weight").getAsDouble();
                if (!abilityObject.has("ability")) continue;
                String ability = abilityObject.get("ability").getAsString();
                possibleAbilitiesMap.put(ability, weight);
            }
        }
        if (!possibleAbilitiesMap.isEmpty() && (entry3 = RandomUtils.getRandomEntry(possibleAbilitiesMap)) != null && Abilities.get((String)(chosenAbility = (String)entry3.getKey())) != null) {
            pokemonBuilder.setAbility(chosenAbility);
        }
        HashMap<String, Double> possibleNaturesMap = new HashMap<String, Double>();
        if (pokemonObject.has("possible_natures")) {
            JsonArray possibleNatures = pokemonObject.get("possible_natures").getAsJsonArray();
            for (JsonElement natureElement : possibleNatures) {
                JsonObject natureObject = natureElement.getAsJsonObject();
                if (!natureObject.has("weight")) continue;
                double weight = natureObject.get("weight").getAsDouble();
                if (!natureObject.has("nature")) continue;
                String nature = natureObject.get("nature").getAsString();
                possibleNaturesMap.put(nature, weight);
            }
        }
        if (!possibleNaturesMap.isEmpty() && (entry2 = RandomUtils.getRandomEntry(possibleNaturesMap)) != null && Natures.getNature((String)(chosenNature = (String)entry2.getKey())) != null) {
            pokemonBuilder.setNature(chosenNature);
        }
        if (pokemonObject.has("ivs")) {
            String ivsString = pokemonObject.get("ivs").getAsString();
            String[] ivsArray = ivsString.split(",");
            IVs ivs = IVs.createRandomIVs((int)0);
            int count = 0;
            for (String iv : ivsArray) {
                try {
                    int ivInt = Integer.parseInt(iv);
                    switch (count) {
                        case 0: {
                            ivs.set((Stat)Stats.HP, ivInt);
                            break;
                        }
                        case 1: {
                            ivs.set((Stat)Stats.ATTACK, ivInt);
                            break;
                        }
                        case 2: {
                            ivs.set((Stat)Stats.DEFENCE, ivInt);
                            break;
                        }
                        case 3: {
                            ivs.set((Stat)Stats.SPECIAL_ATTACK, ivInt);
                            break;
                        }
                        case 4: {
                            ivs.set((Stat)Stats.SPECIAL_DEFENCE, ivInt);
                            break;
                        }
                        case 5: {
                            ivs.set((Stat)Stats.SPEED, ivInt);
                        }
                    }
                }
                catch (NumberFormatException e) {
                    continue;
                }
                ++count;
            }
            pokemonBuilder.setIvs(ivs);
        }
        if (pokemonObject.has("evs")) {
            String evsString = pokemonObject.get("evs").getAsString();
            String[] evsArray = evsString.split(",");
            EVs evs = EVs.createEmpty();
            int count = 0;
            for (String ev : evsArray) {
                try {
                    int evInt = Integer.parseInt(ev);
                    switch (count) {
                        case 0: {
                            evs.set((Stat)Stats.HP, evInt);
                            break;
                        }
                        case 1: {
                            evs.set((Stat)Stats.ATTACK, evInt);
                            break;
                        }
                        case 2: {
                            evs.set((Stat)Stats.DEFENCE, evInt);
                            break;
                        }
                        case 3: {
                            evs.set((Stat)Stats.SPECIAL_ATTACK, evInt);
                            break;
                        }
                        case 4: {
                            evs.set((Stat)Stats.SPECIAL_DEFENCE, evInt);
                            break;
                        }
                        case 5: {
                            evs.set((Stat)Stats.SPEED, evInt);
                        }
                    }
                }
                catch (NumberFormatException e) {
                    continue;
                }
                ++count;
            }
            pokemonBuilder.setEvs(evs);
        }
        Pokemon pokemon = pokemonBuilder.create();
        pokemon.getBenchedMoves().clear();
        if (pokemonObject.has("known_moves")) {
            List<String> knownMoves = pokemonObject.getAsJsonArray("known_moves").asList().stream().map(JsonElement::getAsString).toList();
            int count = 0;
            pokemon.getMoveSet().clear();
            ArrayList<String> validMoves = new ArrayList<String>();
            for (String move : knownMoves) {
                if (validMoves.contains(move)) continue;
                MoveTemplate moveTemplate = Moves.getByName((String)move);
                if (moveTemplate == null) {
                    BattleFactory.INSTANCE.logError("[BattleFactory] Unknown move: " + move);
                    continue;
                }
                validMoves.add(move);
            }
            Collections.shuffle(validMoves);
            for (String move : validMoves) {
                MoveTemplate moveTemplate = Moves.getByName((String)move);
                if (moveTemplate == null) continue;
                pokemon.getMoveSet().setMove(count, moveTemplate.create(moveTemplate.getMaxPp()));
                if (++count < 4) continue;
                break;
            }
        }
        HashMap<class_1799, Double> possibleHeldItemsMap = new HashMap<class_1799, Double>();
        if (pokemonObject.has("possible_held_items")) {
            JsonArray possibleHeldItems = pokemonObject.get("possible_held_items").getAsJsonArray();
            for (JsonElement heldItemElement : possibleHeldItems) {
                class_9326 itemData;
                String itemID;
                JsonObject heldItemObject = heldItemElement.getAsJsonObject();
                if (!heldItemObject.has("weight")) continue;
                double weight = heldItemObject.get("weight").getAsDouble();
                if (!heldItemObject.has("item") || !class_7923.field_41178.method_10250(class_2960.method_60654((String)(itemID = heldItemObject.get("item").getAsString())))) continue;
                class_1792 item = (class_1792)class_7923.field_41178.method_10223(class_2960.method_60654((String)itemID));
                class_1799 itemStack = new class_1799((class_1935)item, 1);
                if (heldItemObject.has("item_name")) {
                    itemStack.method_57365(class_9323.method_57827().method_57840(class_9334.field_49631, (Object)TextUtils.deserialize(heldItemObject.get("item_name").getAsString())).method_57838());
                }
                if (heldItemObject.has("item_lore")) {
                    ArrayList<class_2561> lore = new ArrayList<class_2561>();
                    for (JsonElement loreElement : heldItemObject.get("item_lore").getAsJsonArray()) {
                        lore.add(TextUtils.deserialize(loreElement.getAsString()));
                    }
                    itemStack.method_57365(class_9323.method_57827().method_57840(class_9334.field_49632, (Object)new class_9290(lore)).method_57838());
                }
                if (heldItemObject.has("item_data") && (itemData = (class_9326)((Pair)class_9326.field_49589.decode((DynamicOps)JsonOps.INSTANCE, (Object)heldItemObject.get("item_data")).getOrThrow()).getFirst()) != null) {
                    itemStack.method_59692(itemData);
                }
                possibleHeldItemsMap.put(itemStack, weight);
            }
        }
        if (!possibleHeldItemsMap.isEmpty() && (entry = RandomUtils.getRandomEntry(possibleHeldItemsMap)) != null) {
            class_1799 itemStack = (class_1799)entry.getKey();
            pokemon.setHeldItem$common(itemStack);
        }
        return pokemon;
    }
}

