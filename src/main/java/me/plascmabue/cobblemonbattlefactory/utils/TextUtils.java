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
        return text.replaceAll("%prefix%", BattleFactory.INSTANCE.messagesConfig().prefix);
    }

    public static String parse(String text, ServerPlayer player) {
        text = TextUtils.parse(text);
        return text.replaceAll("%player.name%", player.getScoreboardName()).replaceAll("%player.uuid%", player.getStringUUID()).replaceAll("%player.displayName", player.getDisplayName() != null ? player.getDisplayName().getString() : "").replaceAll("%player.cooldown%", TextUtils.hms(BattleFactory.INSTANCE.playerCooldowns.containsKey(player.getUUID()) ? BattleFactory.INSTANCE.playerCooldowns.get(player.getUUID()) / 20L : 0L));
    }

    public static String parse(String text, TierSettings tierSettings) {
        text = TextUtils.parse(text);
        return text.replaceAll("%tier%", tierSettings.tierName()).replaceAll("%tier.id%", tierSettings.tierID()).replaceAll("%tier.total_rounds%", String.valueOf(tierSettings.battlesForNextTier()));
    }

    public static String parse(String text, BattleFactoryInstance battleFactoryInstance) {
        text = TextUtils.parse(text, battleFactoryInstance.challenger);
        text = TextUtils.parse(text, battleFactoryInstance.currentTier);
        return text.replaceAll("%round%", String.valueOf(battleFactoryInstance.round)).replaceAll("%session_timer%", TextUtils.hms((long)battleFactoryInstance.instanceTimer / 20L)).replaceAll("%tier.round%", String.valueOf(battleFactoryInstance.currentTierRound)).replaceAll("%round_timer%", TextUtils.hms((long)battleFactoryInstance.roundTimer / 20L));
    }

    public static String parse(String text, Pokemon pokemon) {
        text = TextUtils.parse(text);
        text = text.replaceAll("%pokemon.name%", pokemon.getDisplayName(false).getString()).replaceAll("%pokemon.species%", pokemon.getSpecies().getTranslatedName().getString()).replaceAll("%pokemon.level%", String.valueOf(pokemon.getLevel())).replaceAll("%pokemon.form%", pokemon.getForm().formOnlyShowdownId().substring(0, 1).toUpperCase() + pokemon.getForm().formOnlyShowdownId().substring(1)).replaceAll("%pokemon.ability%", MiscUtilsKt.asTranslated((String)pokemon.getAbility().getDisplayName()).getString()).replaceAll("%pokemon.nature%", MiscUtilsKt.asTranslated((String)pokemon.getNature().getDisplayName()).getString()).replaceAll("%pokemon.ivs.hp%", String.valueOf(pokemon.getIvs().get((Stat)Stats.HP))).replaceAll("%pokemon.ivs.atk%", String.valueOf(pokemon.getIvs().get((Stat)Stats.ATTACK))).replaceAll("%pokemon.ivs.def%", String.valueOf(pokemon.getIvs().get((Stat)Stats.DEFENCE))).replaceAll("%pokemon.ivs.spatk%", String.valueOf(pokemon.getIvs().get((Stat)Stats.SPECIAL_ATTACK))).replaceAll("%pokemon.ivs.spdef%", String.valueOf(pokemon.getIvs().get((Stat)Stats.SPECIAL_DEFENCE))).replaceAll("%pokemon.ivs.spd%", String.valueOf(pokemon.getIvs().get((Stat)Stats.SPEED))).replaceAll("%pokemon.evs.hp%", String.valueOf(pokemon.getEvs().get((Stat)Stats.HP))).replaceAll("%pokemon.evs.atk%", String.valueOf(pokemon.getEvs().get((Stat)Stats.ATTACK))).replaceAll("%pokemon.evs.def%", String.valueOf(pokemon.getEvs().get((Stat)Stats.DEFENCE))).replaceAll("%pokemon.evs.spatk%", String.valueOf(pokemon.getEvs().get((Stat)Stats.SPECIAL_ATTACK))).replaceAll("%pokemon.evs.spdef%", String.valueOf(pokemon.getEvs().get((Stat)Stats.SPECIAL_DEFENCE))).replaceAll("%pokemon.evs.spd%", String.valueOf(pokemon.getEvs().get((Stat)Stats.SPEED))).replaceAll("%pokemon.shiny%", pokemon.getShiny() ? "<yellow>\u2605" : "").replaceAll("%pokemon.gender%", pokemon.getGender().getShowdownName().equalsIgnoreCase("M") ? "<aqua>\u2642" : (pokemon.getGender().getShowdownName().equalsIgnoreCase("F") ? "<light_purple>\u2640" : "<gray>?"));
        Move firstMove = pokemon.getMoveSet().get(0);
        Move secondMove = pokemon.getMoveSet().get(1);
        Move thirdMove = pokemon.getMoveSet().get(2);
        Move fourthMove = pokemon.getMoveSet().get(3);
        return text.replaceAll("%pokemon.moves.1%", firstMove != null ? firstMove.getDisplayName().getString() : "").replaceAll("%pokemon.moves.2%", secondMove != null ? secondMove.getDisplayName().getString() : "").replaceAll("%pokemon.moves.3%", thirdMove != null ? thirdMove.getDisplayName().getString() : "").replaceAll("%pokemon.moves.4%", fourthMove != null ? fourthMove.getDisplayName().getString() : "");
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

