/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.item.component.ItemLore
 *  net.minecraft.core.component.DataComponentMap
 *  net.minecraft.core.component.DataComponentPatch
 *  net.minecraft.core.component.DataComponents
 */
package me.plascmabue.cobblemonbattlefactory.datatypes.rewards;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import me.plascmabue.cobblemonbattlefactory.datatypes.rewards.Reward;
import me.plascmabue.cobblemonbattlefactory.utils.TextUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;

public class ItemReward
extends Reward {
    public Item item;
    public String itemName;
    public List<String> itemLore;
    public DataComponentPatch itemData;
    public int minCount;
    public int maxCount;

    public ItemReward(UUID uuid, String name, String type, Item item, String itemName, List<String> itemLore, DataComponentPatch itemData, int minCount, int maxCount) {
        super(uuid, name, type);
        this.item = item;
        this.itemName = itemName;
        this.itemLore = itemLore;
        this.itemData = itemData;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }

    @Override
    public void grant_reward(ServerPlayer player) {
        ItemStack reward = new ItemStack((ItemLike)this.item);
        int count = new Random().nextInt(this.minCount, this.maxCount + 1);
        if (count < 1) {
            count = 1;
        }
        reward.setCount(count);
        if (!this.itemName.isEmpty()) {
            reward.applyComponents(DataComponentMap.builder().set(DataComponents.CUSTOM_NAME, TextUtils.deserialize(this.itemName)).build());
        }
        if (!this.itemLore.isEmpty()) {
            ArrayList<Component> textLore = new ArrayList<Component>();
            for (String lore : this.itemLore) {
                textLore.add(TextUtils.deserialize(lore));
            }
            reward.applyComponents(DataComponentMap.builder().set(DataComponents.LORE, new ItemLore(textLore)).build());
        }
        if (this.itemData != null && !this.itemData.isEmpty()) {
            reward.applyComponents(this.itemData);
        }
        player.getInventory().add(reward);
    }
}

