/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1282
 *  net.minecraft.class_3222
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.unariginal.cobblemonbattlefactory.mixin;

import me.unariginal.cobblemonbattlefactory.BattleFactory;
import net.minecraft.class_1282;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_3222.class})
public class PlayerDeathMixin {
    @Inject(method={"onDeath"}, at={@At(value="HEAD")})
    public void onPlayerDeath(class_1282 damageSource, CallbackInfo ci) {
        class_3222 player = (class_3222)this;
        BattleFactory.INSTANCE.stopBattleFactoryInstance(player);
    }
}

