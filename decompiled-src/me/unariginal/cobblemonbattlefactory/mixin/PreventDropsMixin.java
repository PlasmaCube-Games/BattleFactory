/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cobblemon.mod.common.entity.npc.NPCEntity
 *  com.cobblemon.mod.common.entity.pokemon.PokemonEntity
 *  com.cobblemon.mod.common.pokemon.Pokemon
 *  net.minecraft.class_1282
 *  net.minecraft.class_1309
 *  net.minecraft.class_7923
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.unariginal.cobblemonbattlefactory.mixin;

import com.cobblemon.mod.common.entity.npc.NPCEntity;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import me.unariginal.cobblemonbattlefactory.BattleFactory;
import me.unariginal.cobblemonbattlefactory.managers.BattleFactoryInstance;
import net.minecraft.class_1282;
import net.minecraft.class_1309;
import net.minecraft.class_7923;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={class_1309.class})
public class PreventDropsMixin {
    @Inject(method={"onDeath"}, at={@At(value="HEAD")})
    public void onDeath(class_1282 source, CallbackInfo ci) {
        block5: {
            Pokemon pokemon;
            PokemonEntity pokemonEntity;
            block6: {
                class_1309 livingEntity = (class_1309)this;
                if (!(livingEntity instanceof PokemonEntity)) break block5;
                pokemonEntity = (PokemonEntity)livingEntity;
                pokemon = pokemonEntity.getPokemon();
                if (!pokemon.isNPCOwned()) break block6;
                NPCEntity npcEntity = pokemon.getOwnerNPC();
                if (npcEntity == null) break block5;
                for (BattleFactoryInstance battleFactoryInstance : BattleFactory.INSTANCE.battleFactoryInstances) {
                    if (!npcEntity.method_5667().equals(battleFactoryInstance.currentNPC.method_5667())) continue;
                    if (!pokemon.heldItem().method_7960()) {
                        pokemon.getPersistentData().method_10582("bf_heldItem", class_7923.field_41178.method_10221((Object)pokemon.heldItem().method_7909()).toString());
                    }
                    pokemon.removeHeldItem();
                    pokemonEntity.method_6082(pokemonEntity.method_23317(), -1000.0, pokemonEntity.method_23321(), false);
                    break block5;
                }
                break block5;
            }
            block1: for (BattleFactoryInstance battleFactoryInstance : BattleFactory.INSTANCE.battleFactoryInstances) {
                for (Pokemon p : battleFactoryInstance.rentalParty) {
                    if (p == null || !p.getUuid().equals(pokemon.getUuid())) continue;
                    if (!pokemon.heldItem().method_7960()) {
                        pokemon.getPersistentData().method_10582("bf_heldItem", class_7923.field_41178.method_10221((Object)pokemon.heldItem().method_7909()).toString());
                    }
                    pokemon.removeHeldItem();
                    pokemonEntity.method_6082(pokemonEntity.method_23317(), -1000.0, pokemonEntity.method_23321(), false);
                    continue block1;
                }
            }
        }
    }
}

