/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cobblemon.mod.common.entity.npc.NPCEntity
 *  com.cobblemon.mod.common.entity.pokemon.PokemonEntity
 *  com.cobblemon.mod.common.pokemon.Pokemon
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.server.level.ServerPlayer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package me.plascmabue.cobblemonbattlefactory.mixin;

import com.cobblemon.mod.common.entity.npc.NPCEntity;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import me.plascmabue.cobblemonbattlefactory.managers.BattleFactoryInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Entity.class})
public class HideEntitiesMixin {
    @Inject(at={@At(value="HEAD")}, method={"broadcastToPlayer"}, cancellable=true)
    public void canBeSpectated(ServerPlayer spectator, CallbackInfoReturnable<Boolean> cir) {
        if (!BattleFactory.INSTANCE.config().hideIrrelevantEntities) {
            return;
        }
        Entity self = (Entity)(Object)this;
        boolean inBattleFactory = false;
        BattleFactoryInstance bfInstance = null;
        for (BattleFactoryInstance instance : BattleFactory.INSTANCE.battleFactoryInstances) {
            if (!instance.challenger.getUUID().equals(spectator.getUUID())) continue;
            inBattleFactory = true;
            bfInstance = instance;
        }
        if (bfInstance == null || !inBattleFactory) {
            return;
        }
        if (self instanceof ServerPlayer) {
            cir.cancel();
        } else if (self instanceof NPCEntity) {
            NPCEntity npcEntity = (NPCEntity)self;
            if (bfInstance.currentNPC != null) {
                if (!npcEntity.getUUID().equals(bfInstance.currentNPC.getUUID())) {
                    cir.cancel();
                }
            } else {
                cir.cancel();
            }
        } else if (self instanceof PokemonEntity) {
            PokemonEntity pokemonEntity = (PokemonEntity)self;
            Pokemon pokemon = pokemonEntity.getPokemon();
            if (pokemon.isPlayerOwned()) {
                ServerPlayer player = pokemon.getOwnerPlayer();
                if (player != null) {
                    if (!player.getUUID().equals(spectator.getUUID())) {
                        cir.cancel();
                    }
                } else {
                    cir.cancel();
                }
            } else if (pokemon.isNPCOwned()) {
                NPCEntity npcEntity = pokemon.getOwnerNPC();
                if (npcEntity != null && bfInstance.currentNPC != null) {
                    if (!npcEntity.getUUID().equals(bfInstance.currentNPC.getUUID())) {
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

