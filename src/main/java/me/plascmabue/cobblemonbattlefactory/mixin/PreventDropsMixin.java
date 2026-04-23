/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cobblemon.mod.common.entity.npc.NPCEntity
 *  com.cobblemon.mod.common.entity.pokemon.PokemonEntity
 *  com.cobblemon.mod.common.pokemon.Pokemon
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.LivingEntity
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LivingEntity.class})
public class PreventDropsMixin {
    @Inject(method={"die"}, at={@At(value="HEAD")})
    public void onDeath(DamageSource source, CallbackInfo ci) {
        block5: {
            Pokemon pokemon;
            PokemonEntity pokemonEntity;
            block6: {
                LivingEntity livingEntity = (LivingEntity)(Object)this;
                if (!(livingEntity instanceof PokemonEntity)) break block5;
                pokemonEntity = (PokemonEntity)livingEntity;
                pokemon = pokemonEntity.getPokemon();
                if (!pokemon.isNPCOwned()) break block6;
                NPCEntity npcEntity = pokemon.getOwnerNPC();
                if (npcEntity == null) break block5;
                for (BattleFactoryInstance battleFactoryInstance : BattleFactory.INSTANCE.battleFactoryInstances) {
                    if (!npcEntity.getUUID().equals(battleFactoryInstance.currentNPC.getUUID())) continue;
                    if (!pokemon.heldItem().isEmpty()) {
                        pokemon.getPersistentData().putString("bf_heldItem", BuiltInRegistries.ITEM.getKey(pokemon.heldItem().getItem()).toString());
                    }
                    pokemon.removeHeldItem();
                    pokemonEntity.randomTeleport(pokemonEntity.getX(), -1000.0, pokemonEntity.getZ(), false);
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
                    pokemonEntity.randomTeleport(pokemonEntity.getX(), -1000.0, pokemonEntity.getZ(), false);
                    continue block1;
                }
            }
        }
    }
}

