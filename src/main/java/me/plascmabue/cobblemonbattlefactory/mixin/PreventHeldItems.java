/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cobblemon.mod.common.entity.npc.NPCEntity
 *  com.cobblemon.mod.common.entity.pokemon.PokemonEntity
 *  com.cobblemon.mod.common.pokemon.Pokemon
 *  net.minecraft.core.registries.BuiltInRegistries
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.plascmabue.cobblemonbattlefactory.mixin;

import com.cobblemon.mod.common.entity.npc.NPCEntity;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import me.plascmabue.cobblemonbattlefactory.managers.BattleFactoryInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PokemonEntity.class})
public class PreventHeldItems {
    @Inject(at={@At(value="HEAD")}, method={"tickDeath"})
    public void updatePostDeath(CallbackInfo ci) {
        block5: {
            Pokemon pokemon;
            block6: {
                PokemonEntity entity = (PokemonEntity)(Object)this;
                pokemon = entity.getPokemon();
                if (!pokemon.isNPCOwned()) break block6;
                NPCEntity npcEntity = pokemon.getOwnerNPC();
                if (npcEntity == null) break block5;
                for (BattleFactoryInstance battleFactoryInstance : BattleFactory.INSTANCE.battleFactoryInstances) {
                    if (battleFactoryInstance.currentNPC == null || !npcEntity.getUUID().equals(battleFactoryInstance.currentNPC.getUUID())) continue;
                    if (!pokemon.heldItem().isEmpty()) {
                        pokemon.getPersistentData().putString("bf_heldItem", BuiltInRegistries.ITEM.getKey(pokemon.heldItem().getItem()).toString());
                    }
                    pokemon.removeHeldItem();
                    break block5;
                }
                break block5;
            }
            block1: for (BattleFactoryInstance battleFactoryInstance : BattleFactory.INSTANCE.battleFactoryInstances) {
                for (Pokemon p : battleFactoryInstance.rentalParty) {
                    if (p == null || !p.getUuid().equals(pokemon.getUuid())) continue;
                    if (!pokemon.heldItem().isEmpty()) {
                        pokemon.getPersistentData().putString("bf_heldItem", BuiltInRegistries.ITEM.getKey(pokemon.heldItem().getItem()).toString());
                    }
                    pokemon.removeHeldItem();
                    continue block1;
                }
            }
        }
    }
}

