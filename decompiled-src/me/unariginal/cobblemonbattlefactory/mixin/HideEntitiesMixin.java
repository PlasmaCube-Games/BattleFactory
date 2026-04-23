/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cobblemon.mod.common.entity.npc.NPCEntity
 *  com.cobblemon.mod.common.entity.pokemon.PokemonEntity
 *  com.cobblemon.mod.common.pokemon.Pokemon
 *  net.minecraft.class_1297
 *  net.minecraft.class_3222
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package me.unariginal.cobblemonbattlefactory.mixin;

import com.cobblemon.mod.common.entity.npc.NPCEntity;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.unariginal.cobblemonbattlefactory.BattleFactory;
import me.unariginal.cobblemonbattlefactory.managers.BattleFactoryInstance;
import net.minecraft.class_1297;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={class_1297.class})
public class HideEntitiesMixin {
    @Inject(at={@At(value="HEAD")}, method={"canBeSpectated"}, cancellable=true)
    public void canBeSpectated(class_3222 spectator, CallbackInfoReturnable<Boolean> cir) {
        if (!BattleFactory.INSTANCE.config().hideIrrelevantEntities) {
            return;
        }
        class_1297 self = (class_1297)this;
        boolean inBattleFactory = false;
        BattleFactoryInstance bfInstance = null;
        for (BattleFactoryInstance instance : BattleFactory.INSTANCE.battleFactoryInstances) {
            if (!instance.challenger.method_5667().equals(spectator.method_5667())) continue;
            inBattleFactory = true;
            bfInstance = instance;
        }
        if (bfInstance == null || !inBattleFactory) {
            return;
        }
        if (self instanceof class_3222) {
            cir.cancel();
        } else if (self instanceof NPCEntity) {
            NPCEntity npcEntity = (NPCEntity)self;
            if (bfInstance.currentNPC != null) {
                if (!npcEntity.method_5667().equals(bfInstance.currentNPC.method_5667())) {
                    cir.cancel();
                }
            } else {
                cir.cancel();
            }
        } else if (self instanceof PokemonEntity) {
            PokemonEntity pokemonEntity = (PokemonEntity)self;
            Pokemon pokemon = pokemonEntity.getPokemon();
            if (pokemon.isPlayerOwned()) {
                class_3222 player = pokemon.getOwnerPlayer();
                if (player != null) {
                    if (!player.method_5667().equals(spectator.method_5667())) {
                        cir.cancel();
                    }
                } else {
                    cir.cancel();
                }
            } else if (pokemon.isNPCOwned()) {
                NPCEntity npcEntity = pokemon.getOwnerNPC();
                if (npcEntity != null && bfInstance.currentNPC != null) {
                    if (!npcEntity.method_5667().equals(bfInstance.currentNPC.method_5667())) {
                        cir.cancel();
                    }
                } else {
                    cir.cancel();
                }
            } else {
                boolean isRented = false;
                for (Pokemon p : bfInstance.rentalParty) {
                    if (p == null || !p.getUuid().equals(pokemon.getUuid())) continue;
                    isRented = true;
                    break;
                }
                if (!isRented) {
                    cir.cancel();
                }
            }
        }
    }
}

