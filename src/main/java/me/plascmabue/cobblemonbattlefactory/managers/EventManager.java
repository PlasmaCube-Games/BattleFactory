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
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.server.level.ServerPlayer
 */
package me.plascmabue.cobblemonbattlefactory.managers;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.storage.player.GeneralPlayerData;
import com.cobblemon.mod.common.api.item.HealingSource;
import com.cobblemon.mod.common.api.item.PokemonSelectingItem;
import com.cobblemon.mod.common.battles.BagItems;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.entity.npc.NPCBattleActor;
import com.cobblemon.mod.common.entity.npc.NPCEntity;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import java.util.ArrayList;
import kotlin.Unit;
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import me.plascmabue.cobblemonbattlefactory.managers.BattleFactoryInstance;
import me.plascmabue.cobblemonbattlefactory.utils.TextUtils;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;

public class EventManager {
    /**
     * Un objet interdit pendant un run BF ? Cobblemon a DEUX familles distinctes, d'ou les trois tests :
     *  - HealingSource        : potions, rappels, herbes, baies de soin, racine energie, remede.
     *                           Ils s'utilisent en clic droit DANS LE VIDE (Item.use), pas sur l'entite.
     *  - PokemonSelectingItem : ouvre un selecteur d'equipe (menthes, bonbons, elixirs, soins de statut...).
     *  - BagItemLike          : X-Attaque, Muscle+, Lait Meumeu... ceux-la s'utilisent en clic droit SUR
     *                           le Pokemon (PokemonEntity.attemptItemInteraction).
     * Attention : PotionItem et ReviveItem n'implementent PAS BagItemLike, donc getConvertibleForStack()
     * seul ne les voit pas. C'est le piege qui laissait passer les potions/rappels.
     */
    private static boolean isRestrictedPokemonItem(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return item instanceof HealingSource
                || item instanceof PokemonSelectingItem
                || BagItems.getConvertibleForStack(itemStack) != null;
    }

    private static boolean isBlockedInBattleFactory(ServerPlayer player, ItemStack itemStack) {
        if (itemStack.isEmpty() || BattleFactory.INSTANCE.getBattleFactoryInstance(player) == null) {
            return false;
        }
        if (BattleFactory.INSTANCE.config().bannedBagItems.contains(itemStack.getItem())) {
            return true;
        }
        return BattleFactory.INSTANCE.config().blockBagItems && EventManager.isRestrictedPokemonItem(itemStack);
    }

    private static void warnBlocked(ServerPlayer player) {
        player.sendSystemMessage(TextUtils.deserialize(TextUtils.parse(BattleFactory.INSTANCE.messagesConfig().getMessage("error_bannedBagItem"), player)));
    }

    public static void registerRightClickEvents() {
        // Clic droit dans le vide : couvre potions, rappels, baies, et tout PokemonSelectingItem.
        UseItemCallback.EVENT.register((playerEntity, world, hand) -> {
            ItemStack itemStack = playerEntity.getItemInHand(hand);
            if (playerEntity instanceof ServerPlayer player && EventManager.isBlockedInBattleFactory(player, itemStack)) {
                EventManager.warnBlocked(player);
                return InteractionResultHolder.fail(itemStack);
            }
            return InteractionResultHolder.pass(itemStack);
        });
        // Clic droit sur le Pokemon : couvre les BagItemLike (X-Attaque, Lait Meumeu...), qui passent par
        // PokemonEntity.attemptItemInteraction et ne declenchent jamais UseItemCallback.
        UseEntityCallback.EVENT.register((playerEntity, world, hand, entity, hitResult) -> {
            if (!(playerEntity instanceof ServerPlayer player) || !(entity instanceof PokemonEntity)) {
                return InteractionResult.PASS;
            }
            ItemStack itemStack = playerEntity.getItemInHand(hand);
            if (!EventManager.isBlockedInBattleFactory(player, itemStack)) {
                return InteractionResult.PASS;
            }
            EventManager.warnBlocked(player);
            return InteractionResult.FAIL;
        });
    }

    public static void registerBattleEvents() {
        CobblemonEvents.BATTLE_STARTED_PRE.subscribe(Priority.LOWEST, event -> {
            PokemonBattle battle = event.getBattle();
            for (ServerPlayer player : battle.getPlayers()) {
                if (BattleFactory.INSTANCE.getBattleFactoryInstance(player) == null) continue;
                GeneralPlayerData playerData = Cobblemon.playerDataManager.getGenericData(player);
                int beforeCount = playerData.getKeyItems().size();
                java.util.List<String> removed = new java.util.ArrayList<>();
                playerData.getKeyItems().removeIf(item -> {
                    String id = item.toString();
                    if (BattleFactory.INSTANCE.config().bannedKeyItems.contains(id)) {
                        removed.add(id);
                        return true;
                    }
                    return false;
                });
                BattleFactory.LOGGER.info("[BattleFactory] BATTLE_STARTED_PRE keyItems: player={} before={} removed={} ({})",
                        player.getScoreboardName(), beforeCount, removed.size(), removed);
            }
            return Unit.INSTANCE;
        });
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.HIGHEST, battleVictoryEvent -> {
            PokemonBattle battle = battleVictoryEvent.getBattle();
            for (BattleFactoryInstance battleFactoryInstance : BattleFactory.INSTANCE.battleFactoryInstances) {
                ServerPlayer player;
                PlayerBattleActor playerBattleActor;
                if (battleFactoryInstance.currentBattleID == null || !battle.getBattleId().equals(battleFactoryInstance.currentBattleID)) continue;
                boolean playerWon = battleVictoryEvent.getWinners().stream().anyMatch(w -> w instanceof PlayerBattleActor);
                me.plascmabue.cobblemonbattlefactory.managers.TickManager.dumpBattleLog(battle, playerWon ? "VICTOIRE" : "DÉFAITE");
                for (BattleActor winner : battleVictoryEvent.getWinners()) {
                    if (!(winner instanceof PlayerBattleActor)) continue;
                    playerBattleActor = (PlayerBattleActor)winner;
                    playerBattleActor.getPokemonList().forEach(pokemon -> {
                        PokemonEntity pokemonEntity = pokemon.getEntity();
                        if (pokemonEntity != null) {
                            pokemonEntity.remove(Entity.RemovalReason.DISCARDED);
                        }
                    });
                    player = playerBattleActor.getEntity();
                    if (player == null || !battleFactoryInstance.challenger.getUUID().equals(player.getUUID())) continue;
                    battleFactoryInstance.currentBattleID = null;
                    ArrayList<Pokemon> npcParty = new ArrayList<Pokemon>();
                    for (BattleActor loser : battleVictoryEvent.getLosers()) {
                        if (loser instanceof NPCBattleActor nba
                                && nba.getEntity() != null
                                && nba.getEntity().getUUID().equals(battleFactoryInstance.currentNPC.getUUID())) {
                            nba.getPokemonList().forEach(pokemon -> npcParty.add(pokemon.getEffectedPokemon()));
                            break;
                        }
                        // rctapi path: TrainerEntityBattleActor implements EntityBackedBattleActor
                        if (loser instanceof com.cobblemon.mod.common.api.battles.model.actor.EntityBackedBattleActor ebba) {
                            var ent = ebba.getEntity();
                            if (ent != null && ent.getUUID().equals(battleFactoryInstance.currentNPC.getUUID())) {
                                loser.getPokemonList().forEach(pokemon -> npcParty.add(pokemon.getEffectedPokemon()));
                                break;
                            }
                        }
                    }
                    battleFactoryInstance.nextRound(battleFactoryInstance.inBonusEncounter, npcParty);
                }
                for (BattleActor loser : battleVictoryEvent.getLosers()) {
                    if (!(loser instanceof PlayerBattleActor)) continue;
                    playerBattleActor = (PlayerBattleActor)loser;
                    playerBattleActor.getPokemonList().forEach(pokemon -> {
                        PokemonEntity pokemonEntity = pokemon.getEntity();
                        if (pokemonEntity != null) {
                            pokemonEntity.remove(Entity.RemovalReason.DISCARDED);
                        }
                    });
                    player = playerBattleActor.getEntity();
                    if (player == null || !battleFactoryInstance.challenger.getUUID().equals(player.getUUID())) continue;
                    battleFactoryInstance.currentBattleID = null;
                    // A loss breaks the persistent win streak used for progressive BP.
                    me.plascmabue.cobblemonbattlefactory.datatypes.PlayerData lossData = BattleFactory.INSTANCE.getPlayerData(player);
                    if (lossData != null && lossData.winStreak != 0L) {
                        BattleFactory.LOGGER.info("[BattleFactory] Win streak reset (loss): {} had streak {}", player.getScoreboardName(), lossData.winStreak);
                        lossData.winStreak = 0L;
                        BattleFactory.INSTANCE.updatePlayerData(player, lossData);
                        me.plascmabue.cobblemonbattlefactory.config.playerdata.PlayerDataManager.savePlayerData(player);
                    }
                    BattleFactory.INSTANCE.removeAfterTicks.put(player, 5L);
                }
            }
            return Unit.INSTANCE;
        });
        CobblemonEvents.LOOT_DROPPED.subscribe(Priority.NORMAL, lootDroppedEvent -> {
            block3: {
                Pokemon pokemon;
                block4: {
                    LivingEntity patt0$temp = lootDroppedEvent.getEntity();
                    if (!(patt0$temp instanceof PokemonEntity)) break block3;
                    PokemonEntity pokemonEntity = (PokemonEntity)patt0$temp;
                    pokemon = pokemonEntity.getPokemon();
                    if (!pokemon.isNPCOwned()) break block4;
                    NPCEntity npcEntity = pokemon.getOwnerNPC();
                    if (npcEntity == null) break block3;
                    for (BattleFactoryInstance battleFactoryInstance : BattleFactory.INSTANCE.battleFactoryInstances) {
                        if (!npcEntity.getUUID().equals(battleFactoryInstance.currentNPC.getUUID())) continue;
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
            if (pokemon.getPersistentData().contains("bf_rental")) {
                event.cancel();
            }
            return Unit.INSTANCE;
        });
        CobblemonEvents.THROWN_POKEBALL_HIT.subscribe(Priority.NORMAL, event -> {
            PokemonEntity pokemonEntity = event.getPokemon();
            Pokemon pokemon = pokemonEntity.getPokemon();
            if (pokemon.getPersistentData().contains("bf_rental")) {
                pokemonEntity.remove(Entity.RemovalReason.DISCARDED);
                event.cancel();
            }
            return Unit.INSTANCE;
        });
        CobblemonEvents.EV_GAINED_EVENT_PRE.subscribe(Priority.NORMAL, event -> {
            Pokemon pokemon = event.getPokemon();
            if (pokemon.getPersistentData().contains("bf_rental")) {
                event.cancel();
            }
            return Unit.INSTANCE;
        });
    }
}

