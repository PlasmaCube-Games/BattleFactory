/*
 * Decompiled with CFR 0.152.
 */
package me.unariginal.cobblemonbattlefactory.utils;

import java.util.Map;
import java.util.Random;

public class RandomUtils {
    public static Map.Entry<?, Double> getRandomEntry(Map<?, Double> map) {
        double total_weight = 0.0;
        if (!map.isEmpty()) {
            for (Map.Entry<?, Double> entry : map.entrySet()) {
                total_weight += entry.getValue().doubleValue();
            }
            if (total_weight > 0.0) {
                double random_weight = new Random().nextDouble(total_weight);
                total_weight = 0.0;
                for (Map.Entry<?, Double> entry : map.entrySet()) {
                    if (!(random_weight < (total_weight += entry.getValue().doubleValue()))) continue;
                    return entry;
                }
            }
            return map.entrySet().stream().findFirst().orElse(null);
        }
        return null;
    }
}

