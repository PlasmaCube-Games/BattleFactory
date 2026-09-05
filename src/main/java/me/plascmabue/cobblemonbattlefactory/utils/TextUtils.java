/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cobblemon.mod.common.api.moves.Move
 *  com.cobblemon.mod.common.api.pokemon.stats.Stat
 *  com.cobblemon.mod.common.api.pokemon.stats.Stats
 *  com.cobblemon.mod.common.pokemon.Pokemon
 *  com.cobblemon.mod.common.util.MiscUtilsKt
 *  net.kyori.adventure.text.minimessage.MiniMessage
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 */
package me.plascmabue.cobblemonbattlefactory.utils;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import java.util.ArrayList;
import java.util.List;
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import me.plascmabue.cobblemonbattlefactory.datatypes.TierSettings;
import me.plascmabue.cobblemonbattlefactory.managers.BattleFactoryInstance;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class TextUtils {
    public static Component deserialize(String text) {
        return BattleFactory.INSTANCE.audience().toNative(MiniMessage.miniMessage().deserialize(("<!i>" + text)));
    }

    public static List<Component> getLoreArray(List<String> lore) {
        ArrayList<Component> loreList = new ArrayList<Component>();
        for (String line : lore) {
            loreList.add(TextUtils.deserialize(TextUtils.parse(line)));
        }
        return loreList;
    }

    public static List<Component> getLoreArray(List<String> lore, ServerPlayer player) {
        ArrayList<Component> loreList = new ArrayList<Component>();
        for (String line : lore) {
            loreList.add(TextUtils.deserialize(TextUtils.parse(line, player)));
        }
        return loreList;
    }

    public static List<Component> getLoreArray(List<String> lore, TierSettings tierSettings) {
        ArrayList<Component> loreList = new ArrayList<Component>();
        for (String line : lore) {
            loreList.add(TextUtils.deserialize(TextUtils.parse(line, tierSettings)));
        }
        return loreList;
    }

    public static List<Component> getLoreArray(List<String> lore, BattleFactoryInstance instance) {
        ArrayList<Component> loreList = new ArrayList<Component>();
        for (String line : lore) {
            loreList.add(TextUtils.deserialize(TextUtils.parse(line, instance)));
        }
        return loreList;
    }

    public static List<Component> getLoreArray(List<String> lore, Pokemon pokemon, BattleFactoryInstance instance) {
        ArrayList<Component> loreList = new ArrayList<Component>();
        for (String line : lore) {
            loreList.add(TextUtils.deserialize(TextUtils.parse(TextUtils.parse(line, pokemon), instance)));
        }
        return loreList;
    }

    public static List<Component> getLoreArray(List<String> lore, Pokemon pokemon) {
        ArrayList<Component> loreList = new ArrayList<Component>();
        for (String line : lore) {
            loreList.add(TextUtils.deserialize(TextUtils.parse(line, pokemon)));
        }
        return loreList;
    }

    public static String parse(String text) {
        // NOTE: use String.replace (literal), NOT replaceAll (regex) — parse() runs every tick per
        // BF instance for the overlay; recompiling regexes here made a heavy tick loop (watchdog crash).
        return text.replace("%prefix%", BattleFactory.INSTANCE.messagesConfig().prefix);
    }

    public static String parse(String text, ServerPlayer player) {
        text = TextUtils.parse(text);
        return text.replace("%player.name%", player.getScoreboardName()).replace("%player.uuid%", player.getStringUUID()).replace("%player.displayName", player.getDisplayName() != null ? player.getDisplayName().getString() : "").replace("%player.cooldown%", TextUtils.hms(BattleFactory.INSTANCE.playerCooldowns.containsKey(player.getUUID()) ? BattleFactory.INSTANCE.playerCooldowns.get(player.getUUID()) / 20L : 0L));
    }

    public static String parse(String text, TierSettings tierSettings) {
        text = TextUtils.parse(text);
        return text.replace("%tier%", tierSettings.tierName()).replace("%tier.id%", tierSettings.tierID()).replace("%tier.total_rounds%", String.valueOf(tierSettings.battlesForNextTier()));
    }

    public static String parse(String text, BattleFactoryInstance battleFactoryInstance) {
        text = TextUtils.parse(text, battleFactoryInstance.challenger);
        text = TextUtils.parse(text, battleFactoryInstance.currentTier);
        // %streak% = persistent win streak (carries across runs, resets on a loss) — the "real" combo,
        // unlike %round% which is capped at the tier size and resets every run.
        long persistentStreak = 0L;
        me.plascmabue.cobblemonbattlefactory.datatypes.PlayerData streakData =
                BattleFactory.INSTANCE.getPlayerData(battleFactoryInstance.challenger);
        if (streakData != null) {
            persistentStreak = streakData.winStreak;
        }
        return text.replace("%round%", String.valueOf(battleFactoryInstance.round)).replace("%streak%", String.valueOf(persistentStreak)).replace("%session_timer%", TextUtils.hms((long)battleFactoryInstance.instanceTimer / 20L)).replace("%tier.round%", String.valueOf(battleFactoryInstance.currentTierRound)).replace("%round_timer%", TextUtils.hms((long)battleFactoryInstance.roundTimer / 20L));
    }

    public static String parse(String text, Pokemon pokemon) {
        text = TextUtils.parse(text);
        text = text.replace("%pokemon.name%", pokemon.getDisplayName(false).getString()).replace("%pokemon.species%", pokemon.getSpecies().getTranslatedName().getString()).replace("%pokemon.level%", String.valueOf(pokemon.getLevel())).replace("%pokemon.form%", pokemon.getForm().formOnlyShowdownId().substring(0, 1).toUpperCase() + pokemon.getForm().formOnlyShowdownId().substring(1)).replace("%pokemon.ability%", MiscUtilsKt.asTranslated((String)pokemon.getAbility().getDisplayName()).getString()).replace("%pokemon.nature%", MiscUtilsKt.asTranslated((String)pokemon.getNature().getDisplayName()).getString()).replace("%pokemon.ivs.hp%", String.valueOf(pokemon.getIvs().get((Stat)Stats.HP))).replace("%pokemon.ivs.atk%", String.valueOf(pokemon.getIvs().get((Stat)Stats.ATTACK))).replace("%pokemon.ivs.def%", String.valueOf(pokemon.getIvs().get((Stat)Stats.DEFENCE))).replace("%pokemon.ivs.spatk%", String.valueOf(pokemon.getIvs().get((Stat)Stats.SPECIAL_ATTACK))).replace("%pokemon.ivs.spdef%", String.valueOf(pokemon.getIvs().get((Stat)Stats.SPECIAL_DEFENCE))).replace("%pokemon.ivs.spd%", String.valueOf(pokemon.getIvs().get((Stat)Stats.SPEED))).replace("%pokemon.evs.hp%", String.valueOf(pokemon.getEvs().get((Stat)Stats.HP))).replace("%pokemon.evs.atk%", String.valueOf(pokemon.getEvs().get((Stat)Stats.ATTACK))).replace("%pokemon.evs.def%", String.valueOf(pokemon.getEvs().get((Stat)Stats.DEFENCE))).replace("%pokemon.evs.spatk%", String.valueOf(pokemon.getEvs().get((Stat)Stats.SPECIAL_ATTACK))).replace("%pokemon.evs.spdef%", String.valueOf(pokemon.getEvs().get((Stat)Stats.SPECIAL_DEFENCE))).replace("%pokemon.evs.spd%", String.valueOf(pokemon.getEvs().get((Stat)Stats.SPEED))).replace("%pokemon.shiny%", pokemon.getShiny() ? "<yellow>\u2605" : "").replace("%pokemon.gender%", pokemon.getGender().getShowdownName().equalsIgnoreCase("M") ? "<aqua>\u2642" : (pokemon.getGender().getShowdownName().equalsIgnoreCase("F") ? "<light_purple>\u2640" : "<gray>?"));
        Move firstMove = pokemon.getMoveSet().get(0);
        Move secondMove = pokemon.getMoveSet().get(1);
        Move thirdMove = pokemon.getMoveSet().get(2);
        Move fourthMove = pokemon.getMoveSet().get(3);
        return text.replace("%pokemon.moves.1%", firstMove != null ? firstMove.getDisplayName().getString() : "").replace("%pokemon.moves.2%", secondMove != null ? secondMove.getDisplayName().getString() : "").replace("%pokemon.moves.3%", thirdMove != null ? thirdMove.getDisplayName().getString() : "").replace("%pokemon.moves.4%", fourthMove != null ? fourthMove.getDisplayName().getString() : "");
    }

    public static String hms(long raw_time) {
        if (raw_time < 0L) {
            raw_time = 0L;
        }
        long seconds = raw_time;
        String output = "";
        if (raw_time >= 3600L) {
            seconds = raw_time % 3600L;
            long hours = (raw_time - seconds) / 3600L;
            output = output.concat(hours + "h ");
        }
        long temp = seconds;
        long minutes = (temp -= (seconds %= 60L)) / 60L;
        if (minutes > 0L) {
            output = output.concat(minutes + "m ");
        }
        output = output.concat(seconds + "s");
        return output;
    }
}

