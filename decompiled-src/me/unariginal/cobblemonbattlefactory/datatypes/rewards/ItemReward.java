/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1935
 *  net.minecraft.class_2561
 *  net.minecraft.class_3222
 *  net.minecraft.class_9290
 *  net.minecraft.class_9323
 *  net.minecraft.class_9326
 *  net.minecraft.class_9334
 */
package me.unariginal.cobblemonbattlefactory.datatypes.rewards;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import me.unariginal.cobblemonbattlefactory.datatypes.rewards.Reward;
import me.unariginal.cobblemonbattlefactory.utils.TextUtils;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1935;
import net.minecraft.class_2561;
import net.minecraft.class_3222;
import net.minecraft.class_9290;
import net.minecraft.class_9323;
import net.minecraft.class_9326;
import net.minecraft.class_9334;

public class ItemReward
extends Reward {
    public class_1792 item;
    public String itemName;
    public List<String> itemLore;
    public class_9326 itemData;
    public int minCount;
    public int maxCount;

    public ItemReward(UUID uuid, String name, String type, class_1792 item, String itemName, List<String> itemLore, class_9326 itemData, int minCount, int maxCount) {
        super(uuid, name, type);
        this.item = item;
        this.itemName = itemName;
        this.itemLore = itemLore;
        this.itemData = itemData;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    @Override
    public void grant_reward(class_3222 player) {
        class_1799 reward = new class_1799((class_1935)this.item);
        int count = new Random().nextInt(this.minCount, this.maxCount + 1);
        if (count < 1) {
            count = 1;
        }
        reward.method_7939(count);
        if (!this.itemName.isEmpty()) {
            reward.method_57365(class_9323.method_57827().method_57840(class_9334.field_49631, (Object)TextUtils.deserialize(this.itemName)).method_57838());
        }
        if (!this.itemLore.isEmpty()) {
            ArrayList<class_2561> textLore = new ArrayList<class_2561>();
            for (String lore : this.itemLore) {
                textLore.add(TextUtils.deserialize(lore));
            }
            reward.method_57365(class_9323.method_57827().method_57840(class_9334.field_49632, (Object)new class_9290(textLore)).method_57838());
        }
        if (this.itemData != null && !this.itemData.method_57848()) {
            reward.method_59692(this.itemData);
        }
        player.method_31548().method_7398(reward);
    }
}

