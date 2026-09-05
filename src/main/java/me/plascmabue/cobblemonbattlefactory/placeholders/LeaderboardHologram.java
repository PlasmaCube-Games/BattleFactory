package me.plascmabue.cobblemonbattlefactory.placeholders;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import me.plascmabue.cobblemonbattlefactory.config.playerdata.LeaderboardManager;
import me.plascmabue.cobblemonbattlefactory.datatypes.LeaderboardSection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Exposes the Battle Factory best-streak leaderboard as Text Placeholder API (eu.pb4) placeholders so an
 * external hologram mod (HoloDisplays) can render a live top-N board with zero extra code.
 *
 * Placeholders (namespace "battlefactory"), for rank i in 1..TOP_N:
 *   %battlefactory:top_&lt;i&gt;%        -> "#i Name - 12"
 *   %battlefactory:top_&lt;i&gt;_name%   -> "Name"
 *   %battlefactory:top_&lt;i&gt;_streak% -> "12"
 * Empty slots render "-".
 *
 * No scheduler/cache is needed: LeaderboardManager.leaderboard is already an in-memory list kept sorted
 * (descending by highestStreak). The eu.pb4 API is a soft dependency (provided at runtime by HoloDisplays);
 * if it is absent, registration fails gracefully and the feature is simply disabled.
 */
public final class LeaderboardHologram {
    private static final int TOP_N = 10;
    private static final String EMPTY = "-";

    public static void register() {
        try {
            registerPlaceholders();
            BattleFactory.LOGGER.info("[BattleFactory] Streak-leaderboard hologram placeholders registered (battlefactory:top_1..{}).", TOP_N);
        } catch (NoClassDefFoundError e) {
            BattleFactory.LOGGER.info("[BattleFactory] Text Placeholder API (eu.pb4) not found, streak hologram placeholders disabled.");
        }
    }

    private static LeaderboardSection at(int rank) {
        List<LeaderboardSection> board = LeaderboardManager.leaderboard;
        if (board == null || rank < 1 || rank > board.size()) {
            return null;
        }
        try {
            return board.get(rank - 1);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private static void registerPlaceholders() {
        for (int i = 1; i <= TOP_N; i++) {
            final int rank = i;
            Placeholders.register(
                    ResourceLocation.fromNamespaceAndPath("battlefactory", "top_" + rank),
                    (ctx, arg) -> {
                        LeaderboardSection s = at(rank);
                        String text = (s == null)
                                ? ("#" + rank + " " + EMPTY)
                                : ("#" + rank + " " + s.name + " - " + s.highestStreak);
                        return PlaceholderResult.value(Component.literal(text));
                    });
            Placeholders.register(
                    ResourceLocation.fromNamespaceAndPath("battlefactory", "top_" + rank + "_name"),
                    (ctx, arg) -> {
                        LeaderboardSection s = at(rank);
                        return PlaceholderResult.value(Component.literal(s == null ? EMPTY : s.name));
                    });
            Placeholders.register(
                    ResourceLocation.fromNamespaceAndPath("battlefactory", "top_" + rank + "_streak"),
                    (ctx, arg) -> {
                        LeaderboardSection s = at(rank);
                        return PlaceholderResult.value(Component.literal(s == null ? EMPTY : Integer.toString(s.highestStreak)));
                    });
        }
    }
}
