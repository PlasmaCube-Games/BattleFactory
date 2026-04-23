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
 *  net.minecraft.class_1293
 *  net.minecraft.class_1294
 *  net.minecraft.class_1297
 *  net.minecraft.class_1297$class_5529
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1937
 *  net.minecraft.class_243
 *  net.minecraft.class_2487
 *  net.minecraft.class_2960
 *  net.minecraft.class_3218
 *  net.minecraft.class_3222
 *  net.minecraft.class_7923
 *  net.minecraft.class_9323
 *  net.minecraft.class_9334
 */
package me.unariginal.cobblemonbattlefactory.managers;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.npc.NPCClass;
import com.cobblemon.mod.common.api.npc.NPCClasses;
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
import me.unariginal.cobblemonbattlefactory.BattleFactory;
import me.unariginal.cobblemonbattlefactory.config.playerdata.LeaderboardManager;
import me.unariginal.cobblemonbattlefactory.config.playerdata.PlayerDataManager;
import me.unariginal.cobblemonbattlefactory.datatypes.LeaderboardSection;
import me.unariginal.cobblemonbattlefactory.datatypes.Location;
import me.unariginal.cobblemonbattlefactory.datatypes.PlayerData;
import me.unariginal.cobblemonbattlefactory.datatypes.TierSettings;
import me.unariginal.cobblemonbattlefactory.datatypes.rentalPools.PokemonPool;
import me.unariginal.cobblemonbattlefactory.datatypes.rentalPools.PokemonPreset;
import me.unariginal.cobblemonbattlefactory.datatypes.rentalPools.SetPokemonPool;
import me.unariginal.cobblemonbattlefactory.datatypes.rewards.Reward;
import me.unariginal.cobblemonbattlefactory.utils.PermissionsHelper;
import me.unariginal.cobblemonbattlefactory.utils.TextUtils;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_2487;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_7923;
import net.minecraft.class_9323;
import net.minecraft.class_9334;

public class BattleFactoryInstance {
    private final BattleFactory bf = BattleFactory.INSTANCE;
    public int instanceTimer = 0;
    public class_3222 challenger;
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

    public BattleFactoryInstance(class_3222 challenger, List<Pokemon> rentedPokemon, TierSettings currentTier) {
        this.challenger = challenger;
        this.roundLocation = this.challengerLocation = new Location(challenger.method_51469(), challenger.method_19538().field_1352, challenger.method_19538().field_1351, challenger.method_19538().field_1350, challenger.method_36454(), challenger.method_36455(), 0.0, 0.0, 0.0);
        this.rentedPokemon = rentedPokemon;
        this.round = 0;
        this.currentTier = currentTier;
        for (Pokemon pokemon : rentedPokemon) {
            class_2487 data = pokemon.getPersistentData();
            data.method_10556("bf_rental", true);
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
                LeaderboardSection section = LeaderboardManager.getLeaderboardSection(this.challenger.method_5667());
                if (section == null) {
                    section = new LeaderboardSection(LeaderboardManager.leaderboard.size() + 1, this.challenger.method_5667(), this.challenger.method_5820(), data.highestStreak, LocalDateTime.now());
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
        this.challenger.method_6016(class_1294.field_38092);
        if (this.currentBattleID != null && (battle = BattleRegistry.getBattle((UUID)this.currentBattleID)) != null) {
            battle.stop();
            for (BattleActor actor : battle.getActors()) {
                if (!(actor instanceof PlayerBattleActor)) continue;
                PlayerBattleActor playerBattleActor = (PlayerBattleActor)actor;
                playerBattleActor.getPokemonList().forEach(pokemon -> {
                    PokemonEntity pokemonEntity = pokemon.getEntity();
                    if (pokemonEntity != null) {
                        pokemonEntity.method_5650(class_1297.class_5529.field_26999);
                    }
                });
            }
        }
        this.removeNpc();
        for (Reward reward : this.collectedRewards) {
            reward.grant_reward(this.challenger);
        }
        this.preventTeleportation = false;
        this.challenger.method_14251(this.challengerLocation.world(), this.challengerLocation.playerX(), this.challengerLocation.playerY(), this.challengerLocation.playerZ(), this.challengerLocation.playerYRot(), this.challengerLocation.playerXRot());
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
            class_3218 world = this.challenger.method_51469();
            if (location != null) {
                world = location.world();
            }
            class_243 pos = this.challenger.method_24515().method_46558().method_1031(5.0, 0.0, 0.0);
            if (location != null) {
                pos = new class_243(location.npcX(), location.npcY(), location.npcZ());
            }
            if ((npcId = this.currentTier.getNPCId()) == null) {
                return null;
            }
            NPCClass npcClass = NPCClasses.getByIdentifier((class_2960)class_2960.method_60654((String)npcId));
            if (npcClass == null) {
                return null;
            }
            NPCEntity npc = new NPCEntity((class_1937)world);
            npc.method_5808(pos.method_10216(), pos.method_10214(), pos.method_10215(), npc.method_36454(), npc.method_36455());
            npc.setNpc(npcClass);
            npc.initialize(1);
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
            class_3218 world = this.challenger.method_51469();
            if (location != null) {
                world = location.world();
            }
            class_243 pos = this.challenger.method_24515().method_46558().method_1031(5.0, 0.0, 0.0);
            if (location != null) {
                pos = new class_243(location.npcX(), location.npcY(), location.npcZ());
            }
            if (id == null) {
                return null;
            }
            NPCClass npcClass = NPCClasses.getByIdentifier((class_2960)class_2960.method_60654((String)id));
            if (npcClass == null) {
                return null;
            }
            NPCEntity npc = new NPCEntity((class_1937)world);
            npc.method_5808(pos.method_10216(), pos.method_10214(), pos.method_10215(), npc.method_36454(), npc.method_36455());
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
            this.currentNPC.method_37908().method_8649((class_1297)this.currentNPC);
        }
    }

    public void removeNpc() {
        if (this.currentNPC != null) {
            this.currentNPC.method_5650(class_1297.class_5529.field_26999);
            this.currentNPC = null;
        }
    }

    public void startBattle() {
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
                if (!pokemon.heldItem().method_7960() || !pokemon.getPersistentData().method_10545("bf_heldItem")) continue;
                String heldItemID = pokemon.getPersistentData().method_10558("bf_heldItem");
                if (class_7923.field_41178.method_10250(class_2960.method_60654((String)heldItemID))) {
                    pokemon.setHeldItem$common(((class_1792)class_7923.field_41178.method_10223(class_2960.method_60654((String)heldItemID))).method_7854());
                }
                pokemon.getPersistentData().method_10551("bf_heldItem");
            }
        }
        if (this.bf.config().newTeamAtTierIncrease && this.currentTierRound >= this.currentTier.battlesForNextTier() && (fromBonusEncounter || !this.currentTier.hasBonusEncounter())) {
            TierSettings nextTier = this.bf.config().getNextTier(this.currentTier.tierID());
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
        class_1799 item;
        List<Pokemon> finalAvailableRentals;
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
                    BattleFactoryInstance.this.challenger.method_6016(class_1294.field_38092);
                    BattleFactoryInstance.this.continueNextRound(fromBonusEncounter);
                }
                super.onClose();
            }

            public void onOpen() {
                BattleFactoryInstance.this.challenger.method_6092(new class_1293(class_1294.field_38092, 9999));
                super.onOpen();
            }
        };
        rentalSelectionGUI.setTitle(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().title, this)));
        for (Integer slot : this.bf.rentalSelectionGUIConfig().getSlotsBySymbol(this.bf.rentalSelectionGUIConfig().backgroundItemSymbol)) {
            class_1799 item2 = ((class_1792)class_7923.field_41178.method_10223(class_2960.method_60654((String)this.bf.rentalSelectionGUIConfig().backgroundItem))).method_7854();
            item2.method_59692(this.bf.rentalSelectionGUIConfig().backgroundItemData);
            rentalSelectionGUI.setSlot(slot.intValue(), (GuiElementInterface)new GuiElementBuilder(item2).setName(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().backgroundItemName, this))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().backgroundItemLore, this)).build());
        }
        List<Integer> rentalSlots = this.bf.rentalSelectionGUIConfig().getSlotsBySymbol(this.bf.rentalSelectionGUIConfig().rentalItemSymbol);
        for (int i = 0; i < rentalSlots.size(); ++i) {
            int finalI = i;
            if (i < availableRentals.size()) {
                class_1799 rentalItem = PokemonItem.from((Pokemon)availableRentals.get(i));
                rentalItem.method_57365(class_9323.method_57827().method_57840(class_9334.field_49641, (Object)(this.bf.rentalSelectionGUIConfig().enchantRentalItemOnSelect && !selectedRentals.isEmpty() && selectedRentals.contains(i) ? 1 : 0)).method_57838());
                rentalItem.method_59692(this.bf.rentalSelectionGUIConfig().rentalItemData);
                finalAvailableRentals = availableRentals;
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
                class_1799 selectedItem = ((class_1792)class_7923.field_41178.method_10223(class_2960.method_60654((String)this.bf.rentalSelectionGUIConfig().selectedItem))).method_7854();
                selectedItem.method_59692(this.bf.rentalSelectionGUIConfig().selectedItemData);
                if (this.bf.rentalSelectionGUIConfig().showSelectedItemAbove && rentalSlots.get(i) - 9 >= 0) {
                    rentalSelectionGUI.setSlot(rentalSlots.get(i) - 9, (GuiElementInterface)new GuiElementBuilder(selectedItem).setName(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().selectedItemName, this))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().selectedItemLore, this)).build());
                }
                if (!this.bf.rentalSelectionGUIConfig().showSelectedItemBelow || rentalSlots.get(i) + 9 >= this.bf.rentalSelectionGUIConfig().rows * 9) continue;
                rentalSelectionGUI.setSlot(rentalSlots.get(i) + 9, (GuiElementInterface)new GuiElementBuilder(selectedItem).setName(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().selectedItemName, this))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().selectedItemLore, this)).build());
            }
        }
        for (Integer slot : this.bf.rentalSelectionGUIConfig().getSlotsBySymbol(this.bf.rentalSelectionGUIConfig().cancelItemSymbol)) {
            item = ((class_1792)class_7923.field_41178.method_10223(class_2960.method_60654((String)this.bf.rentalSelectionGUIConfig().cancelItem))).method_7854();
            item.method_59692(this.bf.rentalSelectionGUIConfig().cancelItemData);
            rentalSelectionGUI.setSlot(slot.intValue(), (GuiElementInterface)((GuiElementBuilder)new GuiElementBuilder(item).setName(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().cancelItemName, this))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().cancelItemLore, this)).setCallback(clickType -> {
                if (!this.bf.config().forceTeamSwap) {
                    rentalSelectionGUI.close();
                }
            })).build());
        }
        for (Integer slot : this.bf.rentalSelectionGUIConfig().getSlotsBySymbol(this.bf.rentalSelectionGUIConfig().startItemSymbol)) {
            item = ((class_1792)class_7923.field_41178.method_10223(class_2960.method_60654((String)this.bf.rentalSelectionGUIConfig().startItem))).method_7854();
            item.method_59692(this.bf.rentalSelectionGUIConfig().startItemData);
            finalAvailableRentals = availableRentals;
            rentalSelectionGUI.setSlot(slot.intValue(), (GuiElementInterface)((GuiElementBuilder)new GuiElementBuilder(item).setName(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().startItemName, this))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().startItemLore, this)).setCallback(clickType -> {
                if (selectedRentals.size() == this.bf.config().numberOfPokemonRented) {
                    rentalSelectionGUI.close();
                    this.rentalParty.clearParty();
                    for (int i = 0; i < this.bf.config().numberOfPokemonRented; ++i) {
                        Pokemon pokemon = (Pokemon)finalAvailableRentals.get((Integer)selectedRentals.get(i));
                        class_2487 data = pokemon.getPersistentData();
                        data.method_10556("bf_rental", true);
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
                class_1799 item = ((class_1792)class_7923.field_41178.method_10223(class_2960.method_60654((String)this.bf.selectNPCPokemonGUIConfig().backgroundItem))).method_7854();
                item.method_59692(this.bf.selectNPCPokemonGUIConfig().backgroundItemData);
                swapGUI.setSlot(slot.intValue(), (GuiElementInterface)new GuiElementBuilder(item).setName(TextUtils.deserialize(TextUtils.parse(this.bf.selectNPCPokemonGUIConfig().backgroundItemName, this))).setLore(TextUtils.getLoreArray(this.bf.selectNPCPokemonGUIConfig().backgroundItemLore, this)).build());
            }
            int index = 0;
            List<Integer> pokemonSlots = this.bf.selectNPCPokemonGUIConfig().getSlotsBySymbol(this.bf.selectNPCPokemonGUIConfig().pokemonItemSymbol);
            for (Pokemon pokemon : npcParty) {
                if (index >= pokemonSlots.size()) break;
                if (pokemon != null) {
                    int finalIndex = index;
                    class_1799 pokemonItem = PokemonItem.from((Pokemon)pokemon);
                    pokemonItem.method_59692(this.bf.selectNPCPokemonGUIConfig().pokemonItemData);
                    swapGUI.setSlot(pokemonSlots.get(index).intValue(), (GuiElementInterface)((GuiElementBuilder)new GuiElementBuilder(pokemonItem).setName(TextUtils.deserialize(TextUtils.parse(TextUtils.parse(this.bf.selectNPCPokemonGUIConfig().pokemonItemName, pokemon), this))).setLore(TextUtils.getLoreArray(this.bf.selectNPCPokemonGUIConfig().pokemonItemLore, pokemon, this)).setCallback(clickType -> {
                        hasSwapped.set(true);
                        this.openSwapGUI(fromBonusEncounter, npcParty, selectedPlayerIndex, finalIndex);
                    })).build());
                }
                ++index;
            }
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
                class_1799 item = ((class_1792)class_7923.field_41178.method_10223(class_2960.method_60654((String)this.bf.selectPlayerPokemonGUIConfig().backgroundItem))).method_7854();
                item.method_59692(this.bf.selectPlayerPokemonGUIConfig().backgroundItemData);
                swapGUI.setSlot(slot.intValue(), (GuiElementInterface)new GuiElementBuilder(item).setName(TextUtils.deserialize(TextUtils.parse(this.bf.selectPlayerPokemonGUIConfig().backgroundItemName, this))).setLore(TextUtils.getLoreArray(this.bf.selectPlayerPokemonGUIConfig().backgroundItemLore, this)).build());
            }
            int index = 0;
            List<Integer> pokemonSlots = this.bf.selectPlayerPokemonGUIConfig().getSlotsBySymbol(this.bf.selectPlayerPokemonGUIConfig().pokemonItemSymbol);
            for (Pokemon pokemon : this.rentalParty) {
                if (index >= pokemonSlots.size()) break;
                if (pokemon != null) {
                    int finalIndex = index;
                    class_1799 pokemonItem = PokemonItem.from((Pokemon)pokemon);
                    pokemonItem.method_59692(this.bf.selectPlayerPokemonGUIConfig().pokemonItemData);
                    swapGUI.setSlot(pokemonSlots.get(index).intValue(), new GuiElementBuilder(pokemonItem).setName(TextUtils.deserialize(TextUtils.parse(TextUtils.parse(this.bf.selectPlayerPokemonGUIConfig().pokemonItemName, pokemon), this))).setLore(TextUtils.getLoreArray(this.bf.selectPlayerPokemonGUIConfig().pokemonItemLore, pokemon, this)).setCallback(clickType -> {
                        Pokemon npcPokemon = (Pokemon)npcParty.get(selectedNPCIndex);
                        if (npcPokemon != null) {
                            Pokemon swapPokemon = npcPokemon.clone(true, null);
                            class_2487 data = swapPokemon.getPersistentData();
                            data.method_10556("bf_rental", true);
                            swapPokemon.setPersistentData$common(data);
                            this.rentalParty.set(finalIndex, swapPokemon);
                        }
                        hasSwapped.set(true);
                        swapGUI.close();
                    }));
                }
                ++index;
            }
            swapGUI.open();
        }
    }

    public void continueNextRound(boolean fromBonusEncounter) {
        for (Pokemon pokemon : this.rentalParty) {
            if (pokemon == null || !pokemon.heldItem().method_7960() || !pokemon.getPersistentData().method_10545("bf_heldItem")) continue;
            String heldItemID = pokemon.getPersistentData().method_10558("bf_heldItem");
            if (class_7923.field_41178.method_10250(class_2960.method_60654((String)heldItemID))) {
                pokemon.setHeldItem$common(((class_1792)class_7923.field_41178.method_10223(class_2960.method_60654((String)heldItemID))).method_7854());
            }
            pokemon.getPersistentData().method_10551("bf_heldItem");
        }
        boolean noMoreTiers = false;
        if (this.currentTierRound >= this.currentTier.battlesForNextTier()) {
            List<Reward> earnedRewards;
            TierSettings nextTier = this.bf.config().getNextTier(this.currentTier.tierID());
            if ((fromBonusEncounter || !this.currentTier.hasBonusEncounter()) && this.currentTier.tierCompletionRewards() != null) {
                earnedRewards = this.currentTier.tierCompletionRewards().distributeRewards(this.challenger);
                if (this.bf.config().giveRewardsAtEnd) {
                    this.collectedRewards.addAll(earnedRewards);
                } else {
                    earnedRewards.forEach(reward -> reward.grant_reward(this.challenger));
                }
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
                        if (this.bf.config().giveRewardsAtEnd) {
                            this.collectedRewards.addAll(earnedRewards);
                        } else {
                            earnedRewards.forEach(reward -> reward.grant_reward(this.challenger));
                        }
                    }
                }
            } else {
                noMoreTiers = true;
            }
        }
        if (this.currentTier.perBattleRewards() != null && this.round > 0) {
            List<Reward> earnedRewards = this.currentTier.perBattleRewards().distributeRewards(this.challenger);
            if (this.bf.config().giveRewardsAtEnd) {
                this.collectedRewards.addAll(earnedRewards);
            } else {
                earnedRewards.forEach(reward -> reward.grant_reward(this.challenger));
            }
        }
        if (!noMoreTiers) {
            if (!this.inBonusEncounter) {
                ++this.currentTierRound;
                this.bf.logInfo("[BattleFactory] Increasing current tier round. " + this.currentTierRound);
            }
            ++this.round;
            this.roundTimer = Math.max(3, this.bf.config().secondsBetweenBattles) * 20;
            this.roundTransition = true;
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
            class_3218 world = this.challenger.method_51469();
            if (location != null) {
                world = location.world();
            }
            class_243 pos = this.challenger.method_24515().method_46558();
            if (location != null) {
                pos = new class_243(location.playerX(), location.playerY(), location.playerZ());
            }
            float yRot = this.challenger.method_36454();
            float xRot = this.challenger.method_36455();
            if (location != null) {
                yRot = location.playerYRot();
                xRot = location.playerXRot();
            }
            this.preventTeleportation = false;
            this.challenger.method_6092(new class_1293(class_1294.field_38092, 9999));
            this.roundLocation = new Location(world, pos.field_1352, pos.field_1351, pos.field_1350, yRot, xRot, 0.0, 0.0, 0.0);
            this.challenger.method_14251(world, pos.field_1352, pos.field_1351, pos.field_1350, yRot, xRot);
            this.preventTeleportation = true;
        } else {
            this.bf.removeAfterTicks.put(this.challenger, 20L);
        }
    }

    public void setupRound() {
        this.challenger.method_6016(class_1294.field_38092);
        this.removeNpc();
        if (!this.inBonusEncounter) {
            this.currentNPC = this.generateNextNpc();
        } else {
            this.currentNPC = this.bonusNPC;
            this.bonusNPC = null;
        }
        if (this.currentNPC == null) {
            this.challenger.method_43496(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("error_failedToStartBattle"), this)));
            --this.round;
            this.bf.removeAfterTicks.put(this.challenger, 20L);
            return;
        }
        this.spawnNpc();
        this.startBattle();
        if (this.currentBattleID == null) {
            this.removeNpc();
            this.challenger.method_43496(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("error_failedToStartBattle"), this)));
            --this.round;
            this.bf.removeAfterTicks.put(this.challenger, 20L);
        }
    }
}

