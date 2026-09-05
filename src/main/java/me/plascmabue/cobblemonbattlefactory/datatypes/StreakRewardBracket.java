package me.plascmabue.cobblemonbattlefactory.datatypes;

/**
 * A progressive-points bracket: win-streak values in [fromStreak..toStreak] (inclusive) award {@code bp}
 * Battle Points. A toStreak of -1 means open-ended (from fromStreak upwards).
 */
public class StreakRewardBracket {
    public int fromStreak;
    public int toStreak;
    public int bp;

    public StreakRewardBracket(int fromStreak, int toStreak, int bp) {
        this.fromStreak = fromStreak;
        this.toStreak = toStreak;
        this.bp = bp;
    }
}
