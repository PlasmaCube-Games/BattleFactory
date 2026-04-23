/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cobblemon.mod.common.api.battles.model.PokemonBattle
 *  com.cobblemon.mod.common.api.battles.model.actor.BattleActor
 *  com.cobblemon.mod.common.api.npc.NPCClass
 *  com.cobblemon.mod.common.api.npc.NPCClasses
 *  com.cobblemon.mod.common.api.storage.party.PartyStore
 *  com.cobblemon.mod.common.battles.BattleBuilder
 *  com.cobblemon.mod.common.battles.BattleFormat
 *  com.cobblemon.mod.common.battles.BattleRegistry
 *  com.cobblemon.mod.common.battles.BattleStartError
 *  com.cobblemon.mod.common.battles.BattleStartResult
 *  com.cobblemon.mod.common.battles.actor.PlayerBattleActor
 *  com.cobblemon.mod.common.entity.npc.NPCEntity
 *  com.cobblemon.mod.common.entity.pokemon.PokemonEntity
 *  com.cobblemon.mod.common.item.PokemonItem
 *  com.cobblemon.mod.common.pokemon.Pokemon
 *  eu.pb4.sgui.api.elements.GuiElementBuilder
 *  eu.pb4.sgui.api.elements.GuiElementInterface
 *  eu.pb4.sgui.api.gui.SimpleGui
 *  kotlin.Unit
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.component.DataComponentMap
 *  net.minecraft.core.component.DataComponents
 */
package me.plascmabue.cobblemonbattlefactory.managers;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.npc.NPCClass;
import com.cobblemon.mod.common.api.npc.NPCClasses;
import com.cobblemon.mod.common.api.storage.party.NPCPartyStore;
import com.cobblemon.mod.common.api.storage.party.PartyStore;
import com.cobblemon.mod.common.battles.BattleBuilder;
import com.cobblemon.mod.common.battles.BattleFormat;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.battles.BattleStartError;
import com.cobblemon.mod.common.battles.BattleStartResult;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.entity.npc.NPCEntity;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.Unit;
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import me.plascmabue.cobblemonbattlefactory.config.playerdata.LeaderboardManager;
import me.plascmabue.cobblemonbattlefactory.config.playerdata.PlayerDataManager;
import me.plascmabue.cobblemonbattlefactory.datatypes.LeaderboardSection;
import me.plascmabue.cobblemonbattlefactory.datatypes.Location;
import me.plascmabue.cobblemonbattlefactory.datatypes.PlayerData;
import me.plascmabue.cobblemonbattlefactory.datatypes.TierSettings;
import me.plascmabue.cobblemonbattlefactory.datatypes.rentalPools.PokemonPool;
import me.plascmabue.cobblemonbattlefactory.datatypes.rentalPools.PokemonPreset;
import me.plascmabue.cobblemonbattlefactory.datatypes.rentalPools.SetPokemonPool;
import me.plascmabue.cobblemonbattlefactory.datatypes.rewards.CommandReward;
import me.plascmabue.cobblemonbattlefactory.datatypes.rewards.Reward;
import me.plascmabue.cobblemonbattlefactory.utils.PermissionsHelper;
import me.plascmabue.cobblemonbattlefactory.utils.TextUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomModelData;

public class BattleFactoryInstance {
    private final BattleFactory bf = BattleFactory.INSTANCE;
    public int instanceTimer = 0;
    public ServerPlayer challenger;
    public Location challengerLocation;
    public boolean preventTeleportation = true;
    public Location roundLocation;
    public PartyStore rentalParty = new PartyStore(UUID.randomUUID());
    public List<Pokemon> rentedPokemon;
    public NPCEntity currentNPC;
    public NPCEntity bonusNPC;
    public UUID currentBattleID;
    public TierSettings currentTier;
    public int currentTierRound = 0;
    public String highestCompletedTier = "";
    public int round;
    public int roundTimer;
    public boolean roundTransition = false;
    public boolean inBonusEncounter = false;
    public List<Reward> collectedRewards = new ArrayList<Reward>();

    public BattleFactoryInstance(ServerPlayer challenger, List<Pokemon> rentedPokemon, TierSettings currentTier) {
        this.challenger = challenger;
        this.roundLocation = this.challengerLocation = new Location(challenger.serverLevel(), challenger.position().x, challenger.position().y, challenger.position().z, challenger.getYRot(), challenger.getXRot(), 0.0, 0.0, 0.0);
        this.rentedPokemon = rentedPokemon;
        this.round = 0;
        this.currentTier = currentTier;
        for (Pokemon pokemon : rentedPokemon) {
            CompoundTag data = pokemon.getPersistentData();
            data.putBoolean("bf_rental", true);
            pokemon.setPersistentData$common(data);
            this.rentalParty.add(pokemon);
        }
        this.nextRound(false, null);
    }

    public void stopInstance() {
        PokemonBattle battle;
        if (!this.roundTransition) {
            --this.round;
        }
        this.roundTransition = false;
        PlayerData data = BattleFactory.INSTANCE.getPlayerData(this.challenger);
        if (data != null) {
            if (this.round > data.highestStreak) {
                data.highestStreak = this.round;
                data.highestCompletedTier = this.highestCompletedTier;
                LeaderboardSection section = LeaderboardManager.getLeaderboardSection(this.challenger.getUUID());
                if (section == null) {
                    section = new LeaderboardSection(LeaderboardManager.leaderboard.size() + 1, this.challenger.getUUID(), this.challenger.getScoreboardName(), data.highestStreak, LocalDateTime.now());
                }
                section.highestStreak = data.highestStreak;
                section.dateAchieved = LocalDateTime.now();
                LeaderboardManager.updateLeaderboardSection(section);
                LeaderboardManager.leaderboard = LeaderboardManager.sortLeaderboard(new ArrayList<LeaderboardSection>(LeaderboardManager.leaderboard));
                LeaderboardManager.saveLeaderboard();
            }
            data.cooldownProgress = PermissionsHelper.getBaseCooldown(this.challenger);
            this.bf.logInfo("[BattleFactory] New Cooldown: " + data.cooldownProgress);
            if (data.cooldownProgress > 0L) {
                this.bf.playerCooldowns.put(data.uuid, data.cooldownProgress);
            }
            BattleFactory.INSTANCE.updatePlayerData(this.challenger, data);
            PlayerDataManager.savePlayerData(this.challenger);
        }
        this.challenger.removeEffect(MobEffects.DARKNESS);
        if (this.currentBattleID != null && (battle = BattleRegistry.getBattle((UUID)this.currentBattleID)) != null) {
            battle.stop();
            for (BattleActor actor : battle.getActors()) {
                if (!(actor instanceof PlayerBattleActor)) continue;
                PlayerBattleActor playerBattleActor = (PlayerBattleActor)actor;
                playerBattleActor.getPokemonList().forEach(pokemon -> {
                    PokemonEntity pokemonEntity = pokemon.getEntity();
                    if (pokemonEntity != null) {
                        pokemonEntity.remove(Entity.RemovalReason.DISCARDED);
                    }
                });
            }
        }
        this.removeNpc();
        for (Reward reward : this.collectedRewards) {
            reward.grant_reward(this.challenger);
        }
        this.preventTeleportation = false;
        this.challenger.teleportTo(this.challengerLocation.world(), this.challengerLocation.playerX(), this.challengerLocation.playerY(), this.challengerLocation.playerZ(), this.challengerLocation.playerYRot(), this.challengerLocation.playerXRot());
    }

    public NPCEntity generateNextNpc() {
        try {
            String npcId;
            Location location = null;
            for (int i = this.currentTierRound; i > 0; --i) {
                if (!this.currentTier.battleLocations().containsKey(i)) continue;
                location = this.currentTier.battleLocations().get(i);
                break;
            }
            ServerLevel world = this.challenger.serverLevel();
            if (location != null) {
                world = location.world();
            }
            Vec3 pos = this.challenger.blockPosition().getCenter().add(5.0, 0.0, 0.0);
            if (location != null) {
                pos = new Vec3(location.npcX(), location.npcY(), location.npcZ());
            }
            if ((npcId = this.currentTier.getNPCId()) == null) {
                return null;
            }
            NPCClass npcClass = NPCClasses.getByIdentifier((ResourceLocation)ResourceLocation.parse((String)npcId));
            if (npcClass == null) {
                return null;
            }
            NPCEntity npc = new NPCEntity((Level)world);
            npc.moveTo(pos.x(), pos.y(), pos.z(), npc.getYRot(), npc.getXRot());
            npc.setNpc(npcClass);
            npc.initialize(1);
            this.applyCustomNpcParty(npc);
            return npc;
        }
        catch (Exception e) {
            this.bf.logError("[BattleFactory] Error generating NPC: " + e.getMessage());
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                this.bf.logError("  " + stackTraceElement.toString());
            }
            return null;
        }
    }

    public NPCEntity getBonusEncounterNpc(String id, Location location) {
        try {
            ServerLevel world = this.challenger.serverLevel();
            if (location != null) {
                world = location.world();
            }
            Vec3 pos = this.challenger.blockPosition().getCenter().add(5.0, 0.0, 0.0);
            if (location != null) {
                pos = new Vec3(location.npcX(), location.npcY(), location.npcZ());
            }
            if (id == null) {
                return null;
            }
            NPCClass npcClass = NPCClasses.getByIdentifier((ResourceLocation)ResourceLocation.parse((String)id));
            if (npcClass == null) {
                return null;
            }
            NPCEntity npc = new NPCEntity((Level)world);
            npc.moveTo(pos.x(), pos.y(), pos.z(), npc.getYRot(), npc.getXRot());
            npc.setNpc(npcClass);
            npc.initialize(1);
            return npc;
        }
        catch (Exception e) {
            this.bf.logError("[BattleFactory] Error generating bonus NPC: " + e.getMessage());
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                this.bf.logError("  " + stackTraceElement.toString());
            }
            return null;
        }
    }

    public void spawnNpc() {
        if (this.currentNPC != null) {
            this.currentNPC.level().addFreshEntity((Entity)(Object)this.currentNPC);
        }
    }

    private void applyCustomNpcParty(NPCEntity npc) {
        try {
            PokemonPool pool = this.currentTier.getPokemonPool();
            if (!(pool instanceof SetPokemonPool setPool)) return;
            int desiredCount = Math.max(1, this.bf.config().numberOfPokemonRented);
            NPCPartyStore customParty = new NPCPartyStore(npc);
            List<UUID> usedPresetUUIDs = new ArrayList<>();
            int added = 0, attempts = 0;
            while (added < desiredCount && attempts < 100) {
                attempts++;
                PokemonPreset preset = setPool.getRandomPokemon();
                if (preset == null) break;
                if (usedPresetUUIDs.contains(preset.uuid())) continue;
                Pokemon pokemon = preset.getPokemon();
                if (pokemon == null) continue;
                usedPresetUUIDs.add(preset.uuid());
                customParty.add(pokemon);
                added++;
            }
            if (added > 0) {
                npc.setParty(customParty);
            }
        } catch (Exception ex) {
            this.bf.logError("[BattleFactory] applyCustomNpcParty failed: " + ex.getMessage());
        }
    }

    private void handleRewards(List<Reward> earnedRewards) {
        if (earnedRewards == null) {
            BattleFactory.LOGGER.info("[BattleFactory] handleRewards: null list (no rewards distributed).");
            return;
        }
        BattleFactory.LOGGER.info("[BattleFactory] handleRewards: distributing {} reward(s) — giveAtEnd={}",
                earnedRewards.size(), this.bf.config().giveRewardsAtEnd);
        for (Reward reward : earnedRewards) {
            if (reward instanceof CommandReward || !this.bf.config().giveRewardsAtEnd) {
                reward.grant_reward(this.challenger);
            } else {
                this.collectedRewards.add(reward);
            }
        }
    }

    public void removeNpc() {
        if (this.currentNPC != null) {
            this.currentNPC.remove(Entity.RemovalReason.DISCARDED);
            this.currentNPC = null;
        }
    }

    public void startBattle() {
        int desiredCount = Math.max(1, this.bf.config().numberOfPokemonRented);
        Pokemon[] npcTeam = me.plascmabue.cobblemonbattlefactory.rct.RCTBattleHelper.buildNpcTeam(this.currentTier, desiredCount);
        if (npcTeam == null || npcTeam.length == 0) {
            BattleFactory.LOGGER.warn("[BattleFactory] RCT: failed to build NPC team — falling back to vanilla pvn.");
            fallbackStartBattle();
            return;
        }
        Pokemon[] rentals = new Pokemon[(int) this.rentalParty.size()];
        int i = 0;
        for (Pokemon p : this.rentalParty) {
            if (p == null) continue;
            if (i >= rentals.length) break;
            rentals[i++] = p;
        }
        if (i == 0) {
            BattleFactory.LOGGER.warn("[BattleFactory] RCT: rentalParty is empty — falling back to vanilla pvn.");
            fallbackStartBattle();
            return;
        }
        Pokemon[] rentalsTrimmed = i == rentals.length ? rentals : java.util.Arrays.copyOf(rentals, i);
        java.util.UUID battleId = me.plascmabue.cobblemonbattlefactory.rct.RCTBattleHelper.startRentalBattle(
                this.challenger, rentalsTrimmed, this.currentNPC, npcTeam,
                this.currentTier.tierName());
        if (battleId == null) {
            BattleFactory.LOGGER.warn("[BattleFactory] RCT startBattle returned null — falling back to vanilla pvn.");
            fallbackStartBattle();
            return;
        }
        this.currentBattleID = battleId;
    }

    private void fallbackStartBattle() {
        BattleStartResult result = BattleBuilder.INSTANCE.pvn(this.challenger, this.currentNPC, null, BattleFormat.Companion.getGEN_9_SINGLES(), false, true, this.rentalParty);
        result.ifSuccessful(battle -> {
            this.currentBattleID = battle.getBattleId();
            return Unit.INSTANCE;
        });
        result.ifErrored(battle -> {
            this.currentBattleID = null;
            this.bf.logError("[BattleFactory] Battle failed to start:");
            for (BattleStartError error : battle.getErrors()) {
                this.bf.logError(" - " + error.toString());
            }
            return Unit.INSTANCE;
        });
    }

    public void nextRound(boolean fromBonusEncounter, List<Pokemon> npcParty) {
        if (npcParty != null) {
            for (Pokemon pokemon : npcParty) {
                if (!pokemon.heldItem().isEmpty() || !pokemon.getPersistentData().contains("bf_heldItem")) continue;
                String heldItemID = pokemon.getPersistentData().getString("bf_heldItem");
                if (BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse((String)heldItemID))) {
                    pokemon.setHeldItem$common(((Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)heldItemID))).getDefaultInstance());
                }
                pokemon.getPersistentData().remove("bf_heldItem");
            }
        }
        if (this.bf.config().newTeamAtTierIncrease && this.currentTierRound >= this.currentTier.battlesForNextTier() && (fromBonusEncounter || !this.currentTier.hasBonusEncounter())) {
            TierSettings nextTier = null; // each tier is a standalone run — no auto-advance
            if (nextTier != null) {
                this.openNewRentalGUI(fromBonusEncounter, new ArrayList<Pokemon>(), new ArrayList<Integer>(), nextTier);
            } else {
                this.continueNextRound(fromBonusEncounter);
            }
        } else if (this.round > 0 && this.bf.config().swapPokemonAfterEachBattle) {
            this.openSwapGUI(fromBonusEncounter, npcParty, -1, -1);
        } else {
            this.continueNextRound(fromBonusEncounter);
        }
    }

    public void openNewRentalGUI(final boolean fromBonusEncounter, List<Pokemon> availableRentals, final List<Integer> selectedRentals, final TierSettings tierSettings) {
        ItemStack item;
        PokemonPool pool = tierSettings.getPokemonPool();
        if (pool == null) {
            return;
        }
        ArrayList<PokemonPreset> rentalPresets = new ArrayList<PokemonPreset>();
        int count = 0;
        if (pool instanceof SetPokemonPool) {
            SetPokemonPool setPokemonPool = (SetPokemonPool)pool;
            if (setPokemonPool.pool.size() < this.bf.config().numberOfPokemonAvailable) {
                return;
            }
            block0: while (count < this.bf.config().numberOfPokemonAvailable) {
                PokemonPreset preset = setPokemonPool.getRandomPokemon();
                for (PokemonPreset p : rentalPresets) {
                    if (!preset.uuid().equals(p.uuid())) continue;
                    continue block0;
                }
                rentalPresets.add(preset);
                ++count;
            }
        }
        if (availableRentals == null || availableRentals.isEmpty()) {
            availableRentals = new ArrayList<Pokemon>();
            for (PokemonPreset p : rentalPresets) {
                Pokemon pokemon = p.getPokemon();
                if (pokemon == null) continue;
                BattleFactory.INSTANCE.logInfo("Adding " + pokemon.getSpecies().getName() + " to available rentals!");
                availableRentals.add(pokemon);
            }
            if (availableRentals.size() < this.bf.config().numberOfPokemonAvailable) {
                return;
            }
        }
        final List<Pokemon> finalAvailableRentals1 = availableRentals;
        SimpleGui rentalSelectionGUI = new SimpleGui(this.bf.rentalSelectionGUIConfig().getScreenSize(), this.challenger, false){

            public void onClose() {
                if (BattleFactoryInstance.this.bf.config().forceTeamSwap && selectedRentals.size() != BattleFactoryInstance.this.bf.config().numberOfPokemonRented) {
                    BattleFactoryInstance.this.openNewRentalGUI(fromBonusEncounter, finalAvailableRentals1, selectedRentals, tierSettings);
                } else {
                    BattleFactoryInstance.this.challenger.removeEffect(MobEffects.DARKNESS);
                    BattleFactoryInstance.this.continueNextRound(fromBonusEncounter);
                }
                super.onClose();
            }

            public void onOpen() {
                BattleFactoryInstance.this.challenger.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 9999));
                super.onOpen();
            }
        };
        rentalSelectionGUI.setTitle(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().title, this)));
        for (Integer slot : this.bf.rentalSelectionGUIConfig().getSlotsBySymbol(this.bf.rentalSelectionGUIConfig().backgroundItemSymbol)) {
            ItemStack item2 = ((Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)this.bf.rentalSelectionGUIConfig().backgroundItem))).getDefaultInstance();
            item2.applyComponents(this.bf.rentalSelectionGUIConfig().backgroundItemData);
            rentalSelectionGUI.setSlot(slot.intValue(), (GuiElementInterface)new GuiElementBuilder(item2).setName(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().backgroundItemName, this))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().backgroundItemLore, this)).build());
        }
        final List<Pokemon> finalAvailableRentals = availableRentals;
        List<Integer> rentalSlots = this.bf.rentalSelectionGUIConfig().getSlotsBySymbol(this.bf.rentalSelectionGUIConfig().rentalItemSymbol);
        for (int i = 0; i < rentalSlots.size(); ++i) {
            int finalI = i;
            if (i < availableRentals.size()) {
                ItemStack rentalItem = PokemonItem.from((Pokemon)availableRentals.get(i));
                rentalItem.applyComponents(DataComponentMap.builder().set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(this.bf.rentalSelectionGUIConfig().enchantRentalItemOnSelect && !selectedRentals.isEmpty() && selectedRentals.contains(i) ? 1 : 0)).build());
                rentalItem.applyComponents(this.bf.rentalSelectionGUIConfig().rentalItemData);
                rentalSelectionGUI.setSlot(rentalSlots.get(i).intValue(), (GuiElementInterface)((GuiElementBuilder)new GuiElementBuilder(rentalItem).setName(TextUtils.deserialize(TextUtils.parse(TextUtils.parse(this.bf.rentalSelectionGUIConfig().rentalItemName, availableRentals.get(i)), this))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().rentalItemLore, availableRentals.get(i))).setCallback(clickType -> {
                    boolean alreadySelected = false;
                    for (Integer selected_rental : selectedRentals) {
                        if (selected_rental != finalI) continue;
                        alreadySelected = true;
                        break;
                    }
                    if (!alreadySelected) {
                        if (selectedRentals.size() >= this.bf.config().numberOfPokemonRented) {
                            selectedRentals.removeFirst();
                        }
                        selectedRentals.add(finalI);
                    }
                    this.openNewRentalGUI(fromBonusEncounter, finalAvailableRentals, selectedRentals, tierSettings);
                })).build());
            }
            for (Integer selected_rental : selectedRentals) {
                if (selected_rental != finalI) continue;
                ItemStack selectedItem = ((Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)this.bf.rentalSelectionGUIConfig().selectedItem))).getDefaultInstance();
                selectedItem.applyComponents(this.bf.rentalSelectionGUIConfig().selectedItemData);
                if (this.bf.rentalSelectionGUIConfig().showSelectedItemAbove && rentalSlots.get(i) - 9 >= 0) {
                    rentalSelectionGUI.setSlot(rentalSlots.get(i) - 9, (GuiElementInterface)new GuiElementBuilder(selectedItem).setName(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().selectedItemName, this))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().selectedItemLore, this)).build());
                }
                if (!this.bf.rentalSelectionGUIConfig().showSelectedItemBelow || rentalSlots.get(i) + 9 >= this.bf.rentalSelectionGUIConfig().rows * 9) continue;
                rentalSelectionGUI.setSlot(rentalSlots.get(i) + 9, (GuiElementInterface)new GuiElementBuilder(selectedItem).setName(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().selectedItemName, this))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().selectedItemLore, this)).build());
            }
        }
        for (Integer slot : this.bf.rentalSelectionGUIConfig().getSlotsBySymbol(this.bf.rentalSelectionGUIConfig().cancelItemSymbol)) {
            item = ((Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)this.bf.rentalSelectionGUIConfig().cancelItem))).getDefaultInstance();
            item.applyComponents(this.bf.rentalSelectionGUIConfig().cancelItemData);
            rentalSelectionGUI.setSlot(slot.intValue(), (GuiElementInterface)((GuiElementBuilder)new GuiElementBuilder(item).setName(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().cancelItemName, this))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().cancelItemLore, this)).setCallback(clickType -> {
                if (!this.bf.config().forceTeamSwap) {
                    rentalSelectionGUI.close();
                }
            })).build());
        }
        for (Integer slot : this.bf.rentalSelectionGUIConfig().getSlotsBySymbol(this.bf.rentalSelectionGUIConfig().startItemSymbol)) {
            item = ((Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)this.bf.rentalSelectionGUIConfig().startItem))).getDefaultInstance();
            item.applyComponents(this.bf.rentalSelectionGUIConfig().startItemData);
            rentalSelectionGUI.setSlot(slot.intValue(), (GuiElementInterface)((GuiElementBuilder)new GuiElementBuilder(item).setName(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().startItemName, this))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().startItemLore, this)).setCallback(clickType -> {
                if (selectedRentals.size() == this.bf.config().numberOfPokemonRented) {
                    rentalSelectionGUI.close();
                    this.rentalParty.clearParty();
                    for (int i = 0; i < this.bf.config().numberOfPokemonRented; ++i) {
                        Pokemon pokemon = (Pokemon)finalAvailableRentals.get((Integer)selectedRentals.get(i));
                        CompoundTag data = pokemon.getPersistentData();
                        data.putBoolean("bf_rental", true);
                        pokemon.setPersistentData$common(data);
                        this.rentalParty.set(i, pokemon);
                    }
                }
            })).build());
        }
        rentalSelectionGUI.open();
    }

    public void openSwapGUI(final boolean fromBonusEncounter, final List<Pokemon> npcParty, final int selectedPlayerIndex, final int selectedNPCIndex) {
        final AtomicBoolean hasSwapped = new AtomicBoolean(false);
        if (selectedNPCIndex == -1) {
            SimpleGui swapGUI = new SimpleGui(this.bf.selectNPCPokemonGUIConfig().getScreenSize(), this.challenger, false){

                public void onClose() {
                    if (BattleFactoryInstance.this.bf.config().forcePokemonSwap && !hasSwapped.get()) {
                        BattleFactoryInstance.this.openSwapGUI(fromBonusEncounter, npcParty, selectedPlayerIndex, selectedNPCIndex);
                    } else {
                        BattleFactoryInstance.this.continueNextRound(fromBonusEncounter);
                    }
                    super.onClose();
                }
            };
            if (hasSwapped.get()) {
                swapGUI.close();
                this.continueNextRound(fromBonusEncounter);
                return;
            }
            if (npcParty == null) {
                this.continueNextRound(fromBonusEncounter);
                return;
            }
            swapGUI.setTitle(TextUtils.deserialize(TextUtils.parse(this.bf.selectNPCPokemonGUIConfig().title, this)));
            for (Integer slot : this.bf.selectNPCPokemonGUIConfig().getSlotsBySymbol(this.bf.selectNPCPokemonGUIConfig().backgroundItemSymbol)) {
                ItemStack item = ((Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)this.bf.selectNPCPokemonGUIConfig().backgroundItem))).getDefaultInstance();
                item.applyComponents(this.bf.selectNPCPokemonGUIConfig().backgroundItemData);
                swapGUI.setSlot(slot.intValue(), (GuiElementInterface)new GuiElementBuilder(item).setName(TextUtils.deserialize(TextUtils.parse(this.bf.selectNPCPokemonGUIConfig().backgroundItemName, this))).setLore(TextUtils.getLoreArray(this.bf.selectNPCPokemonGUIConfig().backgroundItemLore, this)).build());
            }
            int index = 0;
            List<Integer> pokemonSlots = this.bf.selectNPCPokemonGUIConfig().getSlotsBySymbol(this.bf.selectNPCPokemonGUIConfig().pokemonItemSymbol);
            for (Pokemon pokemon : npcParty) {
                if (index >= pokemonSlots.size()) break;
                if (pokemon != null) {
                    int finalIndex = index;
                    ItemStack pokemonItem = PokemonItem.from((Pokemon)pokemon);
                    pokemonItem.applyComponents(this.bf.selectNPCPokemonGUIConfig().pokemonItemData);
                    swapGUI.setSlot(pokemonSlots.get(index).intValue(), (GuiElementInterface)((GuiElementBuilder)new GuiElementBuilder(pokemonItem).setName(TextUtils.deserialize(TextUtils.parse(TextUtils.parse(this.bf.selectNPCPokemonGUIConfig().pokemonItemName, pokemon), this))).setLore(TextUtils.getLoreArray(this.bf.selectNPCPokemonGUIConfig().pokemonItemLore, pokemon, this)).setCallback(clickType -> {
                        hasSwapped.set(true);
                        this.openSwapGUI(fromBonusEncounter, npcParty, selectedPlayerIndex, finalIndex);
                    })).build());
                }
                ++index;
            }
            ItemStack skipItem = net.minecraft.world.item.Items.LIME_CONCRETE.getDefaultInstance();
            final SimpleGui npcSwapGUIRef = swapGUI;
            swapGUI.setSlot(0, (GuiElementInterface) new GuiElementBuilder(skipItem)
                    .setName(net.minecraft.network.chat.Component.literal("§aPasser"))
                    .setCallback(clickType -> {
                        hasSwapped.set(true);
                        npcSwapGUIRef.close();
                        // onClose triggered by close() above already calls continueNextRound
                    }).build());
            swapGUI.open();
        } else {
            SimpleGui swapGUI = new SimpleGui(this.bf.selectPlayerPokemonGUIConfig().getScreenSize(), this.challenger, false){

                public void onClose() {
                    if (BattleFactoryInstance.this.bf.config().forcePokemonSwap && !hasSwapped.get()) {
                        BattleFactoryInstance.this.openSwapGUI(fromBonusEncounter, npcParty, selectedPlayerIndex, selectedNPCIndex);
                    } else {
                        BattleFactoryInstance.this.continueNextRound(fromBonusEncounter);
                    }
                    super.onClose();
                }
            };
            if (selectedPlayerIndex != -1 || hasSwapped.get()) {
                swapGUI.close();
                this.continueNextRound(fromBonusEncounter);
                return;
            }
            if (npcParty == null) {
                this.continueNextRound(fromBonusEncounter);
                return;
            }
            swapGUI.setTitle(TextUtils.deserialize(TextUtils.parse(this.bf.selectPlayerPokemonGUIConfig().title, this)));
            for (Integer slot : this.bf.selectPlayerPokemonGUIConfig().getSlotsBySymbol(this.bf.selectPlayerPokemonGUIConfig().backgroundItemSymbol)) {
                ItemStack item = ((Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)this.bf.selectPlayerPokemonGUIConfig().backgroundItem))).getDefaultInstance();
                item.applyComponents(this.bf.selectPlayerPokemonGUIConfig().backgroundItemData);
                swapGUI.setSlot(slot.intValue(), (GuiElementInterface)new GuiElementBuilder(item).setName(TextUtils.deserialize(TextUtils.parse(this.bf.selectPlayerPokemonGUIConfig().backgroundItemName, this))).setLore(TextUtils.getLoreArray(this.bf.selectPlayerPokemonGUIConfig().backgroundItemLore, this)).build());
            }
            int index = 0;
            List<Integer> pokemonSlots = this.bf.selectPlayerPokemonGUIConfig().getSlotsBySymbol(this.bf.selectPlayerPokemonGUIConfig().pokemonItemSymbol);
            for (Pokemon pokemon : this.rentalParty) {
                if (index >= pokemonSlots.size()) break;
                if (pokemon != null) {
                    int finalIndex = index;
                    ItemStack pokemonItem = PokemonItem.from((Pokemon)pokemon);
                    pokemonItem.applyComponents(this.bf.selectPlayerPokemonGUIConfig().pokemonItemData);
                    swapGUI.setSlot(pokemonSlots.get(index).intValue(), new GuiElementBuilder(pokemonItem).setName(TextUtils.deserialize(TextUtils.parse(TextUtils.parse(this.bf.selectPlayerPokemonGUIConfig().pokemonItemName, pokemon), this))).setLore(TextUtils.getLoreArray(this.bf.selectPlayerPokemonGUIConfig().pokemonItemLore, pokemon, this)).setCallback(clickType -> {
                        Pokemon npcPokemon = (Pokemon)npcParty.get(selectedNPCIndex);
                        if (npcPokemon != null) {
                            Pokemon swapPokemon = npcPokemon.clone(true, null);
                            CompoundTag data = swapPokemon.getPersistentData();
                            data.putBoolean("bf_rental", true);
                            swapPokemon.setPersistentData$common(data);
                            this.rentalParty.set(finalIndex, swapPokemon);
                        }
                        hasSwapped.set(true);
                        swapGUI.close();
                    }));
                }
                ++index;
            }
            ItemStack skipItem2 = net.minecraft.world.item.Items.LIME_CONCRETE.getDefaultInstance();
            final SimpleGui playerSwapGUIRef = swapGUI;
            swapGUI.setSlot(0, (GuiElementInterface) new GuiElementBuilder(skipItem2)
                    .setName(net.minecraft.network.chat.Component.literal("§aPasser"))
                    .setCallback(clickType -> {
                        hasSwapped.set(true);
                        playerSwapGUIRef.close();
                        // onClose triggered by close() above already calls continueNextRound
                    }).build());
            swapGUI.open();
        }
    }

    public void continueNextRound(boolean fromBonusEncounter) {
        BattleFactory.LOGGER.info("[BattleFactory] continueNextRound: player={} tier={} round={} tierRound={} fromBonus={}",
                this.challenger.getScoreboardName(), this.currentTier.tierID(), this.round, this.currentTierRound, fromBonusEncounter);
        for (Pokemon pokemon : this.rentalParty) {
            if (pokemon == null || !pokemon.heldItem().isEmpty() || !pokemon.getPersistentData().contains("bf_heldItem")) continue;
            String heldItemID = pokemon.getPersistentData().getString("bf_heldItem");
            if (BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse((String)heldItemID))) {
                pokemon.setHeldItem$common(((Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)heldItemID))).getDefaultInstance());
            }
            pokemon.getPersistentData().remove("bf_heldItem");
        }
        boolean noMoreTiers = false;
        if (this.currentTierRound >= this.currentTier.battlesForNextTier()) {
            List<Reward> earnedRewards;
            TierSettings nextTier = null; // each tier is a standalone run — no auto-advance
            if ((fromBonusEncounter || !this.currentTier.hasBonusEncounter()) && this.currentTier.tierCompletionRewards() != null) {
                earnedRewards = this.currentTier.tierCompletionRewards().distributeRewards(this.challenger);
                this.handleRewards(earnedRewards);
            }
            if (nextTier != null) {
                if (!fromBonusEncounter && this.currentTier.hasBonusEncounter()) {
                    NPCEntity bonusNPC = this.getBonusEncounterNpc(this.currentTier.bonusEncounterNPC(), this.currentTier.bonusEncounterLocation());
                    if (bonusNPC != null) {
                        this.bonusNPC = bonusNPC;
                        this.inBonusEncounter = true;
                    } else {
                        this.highestCompletedTier = this.currentTier.tierID();
                        this.currentTier = nextTier;
                        this.currentTierRound = 0;
                        this.bf.logInfo("[BattleFactory] Reset current tier round. " + this.currentTierRound);
                    }
                }
                if (!this.currentTier.hasBonusEncounter() || fromBonusEncounter) {
                    this.highestCompletedTier = this.currentTier.tierID();
                    this.currentTier = nextTier;
                    this.currentTierRound = 0;
                    this.bf.logInfo("[BattleFactory] Reset current tier round. " + this.currentTierRound);
                }
                if (fromBonusEncounter) {
                    this.inBonusEncounter = false;
                    if (this.currentTier.bonusEncounterRewards() != null) {
                        earnedRewards = this.currentTier.bonusEncounterRewards().distributeRewards(this.challenger);
                        this.handleRewards(earnedRewards);
                    }
                }
            } else {
                noMoreTiers = true;
            }
        }
        BattleFactory.LOGGER.info("[BattleFactory] reward check: perBattleRewards={} perRoundRewards={} round={} tierRound={}",
                this.currentTier.perBattleRewards() != null ? "present" : "NULL",
                this.currentTier.perRoundRewards() != null ? "present(size=" + this.currentTier.perRoundRewards().size() + ")" : "NULL",
                this.round, this.currentTierRound);
        if (this.currentTier.perBattleRewards() != null && this.round > 0) {
            List<Reward> earnedRewards = this.currentTier.perBattleRewards().distributeRewards(this.challenger);
            this.handleRewards(earnedRewards);
        } else {
            BattleFactory.LOGGER.info("[BattleFactory] SKIP perBattleRewards (perBattleRewards={}, round={})",
                    this.currentTier.perBattleRewards() != null, this.round);
        }
        if (this.currentTier.perRoundRewards() != null && this.round > 0) {
            me.plascmabue.cobblemonbattlefactory.datatypes.rewards.DistributionSection section =
                    this.currentTier.perRoundRewards().get(this.currentTierRound);
            BattleFactory.LOGGER.info("[BattleFactory] perRoundRewards.get({}) → {}", this.currentTierRound, section != null ? "found" : "NULL (no entry for this round)");
            if (section != null) {
                List<Reward> earnedRewards = section.distributeRewards(this.challenger);
                this.handleRewards(earnedRewards);
            }
        } else {
            BattleFactory.LOGGER.info("[BattleFactory] SKIP perRoundRewards (perRoundRewards={}, round={})",
                    this.currentTier.perRoundRewards() != null, this.round);
        }
        if (!noMoreTiers) {
            if (!this.inBonusEncounter) {
                ++this.currentTierRound;
                BattleFactory.LOGGER.info("[BattleFactory] Increasing current tier round. {}", this.currentTierRound);
            }
            ++this.round;
            this.roundTimer = Math.max(3, this.bf.config().secondsBetweenBattles) * 20;
            this.roundTransition = true;
            this.bf.sendHudUpdate(this.challenger, new me.plascmabue.cobblemonbattlefactory.network.BattleFactoryHudPayload(
                    true,
                    this.currentTier != null ? this.currentTier.tierName() : "",
                    this.round,
                    this.round,
                    this.roundTimer
            ));
            Location location = null;
            if (!this.inBonusEncounter) {
                for (int i = this.currentTierRound; i > 0; --i) {
                    if (!this.currentTier.battleLocations().containsKey(i)) continue;
                    location = this.currentTier.battleLocations().get(i);
                    break;
                }
            } else {
                location = this.currentTier.bonusEncounterLocation();
            }
            ServerLevel world = this.challenger.serverLevel();
            if (location != null) {
                world = location.world();
            }
            Vec3 pos = this.challenger.blockPosition().getCenter();
            if (location != null) {
                pos = new Vec3(location.playerX(), location.playerY(), location.playerZ());
            }
            float yRot = this.challenger.getYRot();
            float xRot = this.challenger.getXRot();
            if (location != null) {
                yRot = location.playerYRot();
                xRot = location.playerXRot();
            }
            this.preventTeleportation = false;
            boolean sameArena = this.roundLocation != null
                    && this.roundLocation.world() == world
                    && Math.abs(this.roundLocation.playerX() - pos.x) < 0.01
                    && Math.abs(this.roundLocation.playerY() - pos.y) < 0.01
                    && Math.abs(this.roundLocation.playerZ() - pos.z) < 0.01;
            if (!sameArena) {
                this.challenger.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 9999));
                this.challenger.teleportTo(world, pos.x, pos.y, pos.z, yRot, xRot);
            } else {
                this.roundTimer = 3 * 20;
            }
            this.roundLocation = new Location(world, pos.x, pos.y, pos.z, yRot, xRot, 0.0, 0.0, 0.0);
            this.preventTeleportation = true;
        } else {
            this.bf.removeAfterTicks.put(this.challenger, 20L);
        }
    }

    public void setupRound() {
        this.challenger.removeEffect(MobEffects.DARKNESS);
        this.removeNpc();
        if (!this.inBonusEncounter) {
            this.currentNPC = this.generateNextNpc();
        } else {
            this.currentNPC = this.bonusNPC;
            this.bonusNPC = null;
        }
        if (this.currentNPC == null) {
            this.challenger.sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("error_failedToStartBattle"), this)));
            --this.round;
            this.bf.removeAfterTicks.put(this.challenger, 20L);
            return;
        }
        this.spawnNpc();
        this.startBattle();
        if (this.currentBattleID == null) {
            this.removeNpc();
            this.challenger.sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("error_failedToStartBattle"), this)));
            --this.round;
            this.bf.removeAfterTicks.put(this.challenger, 20L);
        }
    }
}

