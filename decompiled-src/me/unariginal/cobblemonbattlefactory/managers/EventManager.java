/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cobblemon.mod.common.Cobblemon
 *  com.cobblemon.mod.common.api.Priority
 *  com.cobblemon.mod.common.api.battles.model.PokemonBattle
 *  com.cobblemon.mod.common.api.battles.model.actor.BattleActor
 *  com.cobblemon.mod.common.api.events.CobblemonEvents
 *  com.cobblemon.mod.common.api.storage.player.GeneralPlayerData
 *  com.cobblemon.mod.common.battles.actor.PlayerBattleActor
 *  com.cobblemon.mod.common.entity.npc.NPCBattleActor
 *  com.cobblemon.mod.common.entity.npc.NPCEntity
 *  com.cobblemon.mod.common.entity.pokemon.PokemonEntity
 *  com.cobblemon.mod.common.pokemon.Pokemon
 *  kotlin.Unit
 *  net.fabricmc.fabric.api.event.player.UseItemCallback
 *  net.minecraft.class_1271
 *  net.minecraft.class_1297$class_5529
 *  net.minecraft.class_1309
 *  net.minecraft.class_1799
 *  net.minecraft.class_3222
 */
package me.unariginal.cobblemonbattlefactory.managers;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.storage.player.GeneralPlayerData;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.entity.npc.NPCBattleActor;
import com.cobblemon.mod.common.entity.npc.NPCEntity;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import java.util.ArrayList;
import kotlin.Unit;
import me.unariginal.cobblemonbattlefactory.BattleFactory;
import me.unariginal.cobblemonbattlefactory.managers.BattleFactoryInstance;
import me.unariginal.cobblemonbattlefactory.utils.TextUtils;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.class_1271;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_3222;

public class EventManager {
    public static void registerRightClickEvents() {
        UseItemCallback.EVENT.register((playerEntity, world, hand) -> {
            class_3222 player;
            BattleFactoryInstance battleFactoryInstance;
            class_1799 itemStack = playerEntity.method_5998(hand);
            if (playerEntity instanceof class_3222 && (battleFactoryInstance = BattleFactory.INSTANCE.getBattleFactoryInstance(player = (class_3222)playerEntity)) != null && BattleFactory.INSTANCE.config().bannedBagItems.contains(itemStack.method_7909())) {
                player.method_43496(TextUtils.deserialize(TextUtils.parse(BattleFactory.INSTANCE.messagesConfig().getMessage("error_bannedBagItem"), player)));
                return class_1271.method_22431((Object)itemStack);
            }
            return class_1271.method_22430((Object)itemStack);
        });
    }

    public static void registerBattleEvents() {
        CobblemonEvents.BATTLE_STARTED_PRE.subscribe(Priority.LOWEST, event -> {
            PokemonBattle battle = event.getBattle();
            for (class_3222 player : battle.getPlayers()) {
                if (BattleFactory.INSTANCE.getBattleFactoryInstance(player) == null) continue;
                GeneralPlayerData playerData = Cobblemon.playerDataManager.getGenericData(player);
                playerData.getKeyItems().removeIf(item -> BattleFactory.INSTANCE.config().bannedKeyItems.contains(item.toString()));
            }
            return Unit.INSTANCE;
        });
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.HIGHEST, battleVictoryEvent -> {
            PokemonBattle battle = battleVictoryEvent.getBattle();
            for (BattleFactoryInstance battleFactoryInstance : BattleFactory.INSTANCE.battleFactoryInstances) {
                class_3222 player;
                PlayerBattleActor playerBattleActor;
                if (battleFactoryInstance.currentBattleID == null || !battle.getBattleId().equals(battleFactoryInstance.currentBattleID)) continue;
                for (BattleActor winner : battleVictoryEvent.getWinners()) {
                    if (!(winner instanceof PlayerBattleActor)) continue;
                    playerBattleActor = (PlayerBattleActor)winner;
                    playerBattleActor.getPokemonList().forEach(pokemon -> {
                        PokemonEntity pokemonEntity = pokemon.getEntity();
                        if (pokemonEntity != null) {
                            pokemonEntity.method_5650(class_1297.class_5529.field_26999);
                        }
                    });
                    player = playerBattleActor.getEntity();
                    if (player == null || !battleFactoryInstance.challenger.method_5667().equals(player.method_5667())) continue;
                    battleFactoryInstance.currentBattleID = null;
                    ArrayList<Pokemon> npcParty = new ArrayList<Pokemon>();
                    for (BattleActor loser : battleVictoryEvent.getLosers()) {
                        NPCBattleActor npcBattleActor;
                        NPCEntity npcEntity;
                        if (!(loser instanceof NPCBattleActor) || !(npcEntity = (npcBattleActor = (NPCBattleActor)loser).getEntity()).method_5667().equals(battleFactoryInstance.currentNPC.method_5667())) continue;
                        npcBattleActor.getPokemonList().forEach(pokemon -> npcParty.add(pokemon.getEffectedPokemon()));
                        break;
                    }
                    battleFactoryInstance.nextRound(battleFactoryInstance.inBonusEncounter, npcParty);
                }
                for (BattleActor loser : battleVictoryEvent.getLosers()) {
                    if (!(loser instanceof PlayerBattleActor)) continue;
                    playerBattleActor = (PlayerBattleActor)loser;
                    playerBattleActor.getPokemonList().forEach(pokemon -> {
                        PokemonEntity pokemonEntity = pokemon.getEntity();
                        if (pokemonEntity != null) {
                            pokemonEntity.method_5650(class_1297.class_5529.field_26999);
                        }
                    });
                    player = playerBattleActor.getEntity();
                    if (player == null || !battleFactoryInstance.challenger.method_5667().equals(player.method_5667())) continue;
                    battleFactoryInstance.currentBattleID = null;
                    BattleFactory.INSTANCE.removeAfterTicks.put(player, 5L);
                }
            }
            return Unit.INSTANCE;
        });
        CobblemonEvents.LOOT_DROPPED.subscribe(Priority.NORMAL, lootDroppedEvent -> {
            block3: {
                Pokemon pokemon;
                block4: {
                    class_1309 patt0$temp = lootDroppedEvent.getEntity();
                    if (!(patt0$temp instanceof PokemonEntity)) break block3;
                    PokemonEntity pokemonEntity = (PokemonEntity)patt0$temp;
                    pokemon = pokemonEntity.getPokemon();
                    if (!pokemon.isNPCOwned()) break block4;
                    NPCEntity npcEntity = pokemon.getOwnerNPC();
                    if (npcEntity == null) break block3;
                    for (BattleFactoryInstance battleFactoryInstance : BattleFactory.INSTANCE.battleFactoryInstances) {
                        if (!npcEntity.method_5667().equals(battleFactoryInstance.currentNPC.method_5667())) continue;
                        lootDroppedEvent.cancel();
                    }
                    break block3;
                }
                block1: for (BattleFactoryInstance battleFactoryInstance : BattleFactory.INSTANCE.battleFactoryInstances) {
                    for (Pokemon p : battleFactoryInstance.rentalParty) {
                        if (p == null || !pokemon.getUuid().equals(p.getUuid())) continue;
                        lootDroppedEvent.cancel();
                        continue block1;
                    }
                }
            }
            return Unit.INSTANCE;
        });
        CobblemonEvents.EXPERIENCE_GAINED_EVENT_PRE.subscribe(Priority.NORMAL, event -> {
            Pokemon pokemon = event.getPokemon();
            if (pokemon.getPersistentData().method_10545("bf_rental")) {
                event.cancel();
            }
            return Unit.INSTANCE;
        });
        CobblemonEvents.THROWN_POKEBALL_HIT.subscribe(Priority.NORMAL, event -> {
            PokemonEntity pokemonEntity = event.getPokemon();
            Pokemon pokemon = pokemonEntity.getPokemon();
            if (pokemon.getPersistentData().method_10545("bf_rental")) {
                pokemonEntity.method_5650(class_1297.class_5529.field_26999);
                event.cancel();
            }
            return Unit.INSTANCE;
        });
        CobblemonEvents.EV_GAINED_EVENT_PRE.subscribe(Priority.NORMAL, event -> {
            Pokemon pokemon = event.getPokemon();
            if (pokemon.getPersistentData().method_10545("bf_rental")) {
                event.cancel();
            }
            return Unit.INSTANCE;
        });
    }
}

