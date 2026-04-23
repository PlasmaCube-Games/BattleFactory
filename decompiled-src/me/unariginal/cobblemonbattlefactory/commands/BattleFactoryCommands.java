/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cobblemon.mod.common.api.battles.model.PokemonBattle
 *  com.cobblemon.mod.common.api.battles.model.actor.BattleActor
 *  com.cobblemon.mod.common.battles.BattleRegistry
 *  com.cobblemon.mod.common.battles.actor.PlayerBattleActor
 *  com.cobblemon.mod.common.entity.npc.NPCBattleActor
 *  com.cobblemon.mod.common.entity.npc.NPCEntity
 *  com.cobblemon.mod.common.entity.pokemon.PokemonEntity
 *  com.cobblemon.mod.common.item.PokemonItem
 *  com.cobblemon.mod.common.pokemon.Pokemon
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  eu.pb4.sgui.api.elements.GuiElementBuilder
 *  eu.pb4.sgui.api.elements.GuiElementInterface
 *  eu.pb4.sgui.api.gui.SimpleGui
 *  me.lucko.fabric.api.permissions.v0.Permissions
 *  net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
 *  net.minecraft.class_1293
 *  net.minecraft.class_1294
 *  net.minecraft.class_1297$class_5529
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_2168
 *  net.minecraft.class_2170
 *  net.minecraft.class_2186
 *  net.minecraft.class_2960
 *  net.minecraft.class_3222
 *  net.minecraft.class_7923
 *  net.minecraft.class_9323
 *  net.minecraft.class_9334
 */
package me.unariginal.cobblemonbattlefactory.commands;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.battles.BattleRegistry;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.entity.npc.NPCBattleActor;
import com.cobblemon.mod.common.entity.npc.NPCEntity;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.unariginal.cobblemonbattlefactory.BattleFactory;
import me.unariginal.cobblemonbattlefactory.config.playerdata.LeaderboardManager;
import me.unariginal.cobblemonbattlefactory.config.playerdata.PlayerDataManager;
import me.unariginal.cobblemonbattlefactory.datatypes.LeaderboardSection;
import me.unariginal.cobblemonbattlefactory.datatypes.PlayerData;
import me.unariginal.cobblemonbattlefactory.datatypes.TierSettings;
import me.unariginal.cobblemonbattlefactory.datatypes.rentalPools.PokemonPool;
import me.unariginal.cobblemonbattlefactory.datatypes.rentalPools.PokemonPreset;
import me.unariginal.cobblemonbattlefactory.datatypes.rentalPools.RandomPokemonPool;
import me.unariginal.cobblemonbattlefactory.datatypes.rentalPools.SetPokemonPool;
import me.unariginal.cobblemonbattlefactory.managers.BattleFactoryInstance;
import me.unariginal.cobblemonbattlefactory.utils.TextUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2186;
import net.minecraft.class_2960;
import net.minecraft.class_3222;
import net.minecraft.class_7923;
import net.minecraft.class_9323;
import net.minecraft.class_9334;

public class BattleFactoryCommands {
    private final BattleFactory bf = BattleFactory.INSTANCE;

    public BattleFactoryCommands() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            LiteralCommandNode node = commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247((String)"battlefactory").requires(Permissions.require((String)"battlefactory.base", (boolean)true))).then(((LiteralArgumentBuilder)class_2170.method_9247((String)"start").requires(Permissions.require((String)"battlefactory.start", (boolean)true))).executes(this::preStart))).then(((LiteralArgumentBuilder)class_2170.method_9247((String)"stop").requires(Permissions.require((String)"battlefactory.stop", (boolean)true))).executes(ctx -> {
                class_3222 player = ((class_2168)ctx.getSource()).method_44023();
                if (player != null && this.bf.stopBattleFactoryInstance(player)) {
                    player.method_43496(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("battleFactory_stopped"), player)));
                }
                return 1;
            }))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247((String)"leaderboard").requires(Permissions.require((String)"battlefactory.leaderboard", (boolean)true))).then(class_2170.method_9244((String)"page", (ArgumentType)IntegerArgumentType.integer((int)1, (int)(LeaderboardManager.leaderboard.size() % 10 + 1))).executes(ctx -> {
                int page = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"page");
                int lastIndex = page * 10;
                ((class_2168)ctx.getSource()).method_45068(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("leaderboard_header").replaceAll("%page%", String.valueOf(page)).replaceAll("%max_pages%", String.valueOf(LeaderboardManager.leaderboard.size() % 10)))));
                for (int i = lastIndex - 10; i < lastIndex; ++i) {
                    if (i >= LeaderboardManager.leaderboard.size()) continue;
                    LeaderboardSection section = LeaderboardManager.leaderboard.get(i);
                    ((class_2168)ctx.getSource()).method_45068(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("leaderboard_section").replaceAll("%section.placement%", String.valueOf(section.placement)).replaceAll("%section.player_name%", section.name).replaceAll("%section.highest_streak%", String.valueOf(section.highestStreak)).replaceAll("%section.date_achieved%", section.dateAchieved.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))))));
                }
                return 1;
            }))).executes(ctx -> {
                int page = 1;
                int lastIndex = page * 10;
                ((class_2168)ctx.getSource()).method_45068(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("leaderboard_header").replaceAll("%page%", String.valueOf(page)).replaceAll("%max_pages%", String.valueOf(LeaderboardManager.leaderboard.size() % 10)))));
                for (int i = 0; i < lastIndex; ++i) {
                    if (i >= LeaderboardManager.leaderboard.size()) continue;
                    LeaderboardSection section = LeaderboardManager.leaderboard.get(i);
                    ((class_2168)ctx.getSource()).method_45068(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("leaderboard_section").replaceAll("%section.placement%", String.valueOf(section.placement)).replaceAll("%section.player_name%", section.name).replaceAll("%section.highest_streak%", String.valueOf(section.highestStreak)).replaceAll("%section.date_achieved%", section.dateAchieved.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))))));
                }
                return 1;
            }))).then(((LiteralArgumentBuilder)class_2170.method_9247((String)"status").requires(Permissions.require((String)"battlefactory.status", (boolean)true))).executes(ctx -> {
                BattleFactoryInstance bfInstance;
                class_3222 player = ((class_2168)ctx.getSource()).method_44023();
                if (player != null && (bfInstance = this.bf.getBattleFactoryInstance(player)) != null) {
                    player.method_43496(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_status"), bfInstance)));
                }
                return 1;
            }))).then(((LiteralArgumentBuilder)class_2170.method_9247((String)"reload").requires(Permissions.require((String)"battlefactory.reload", (int)4))).executes(ctx -> {
                this.bf.reload(true);
                ((class_2168)ctx.getSource()).method_45068(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_reload"))));
                return 1;
            }))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247((String)"admin").requires(Permissions.require((String)"battlefactory.admin", (int)4))).then(((LiteralArgumentBuilder)class_2170.method_9247((String)"reset").requires(Permissions.require((String)"battlefactory.admin.reset", (int)4))).then(class_2170.method_9244((String)"player", (ArgumentType)class_2186.method_9305()).executes(ctx -> {
                class_3222 player = class_2186.method_9315((CommandContext)ctx, (String)"player");
                PlayerData data = this.bf.getPlayerData(player);
                data.highestStreak = 0;
                data.highestCompletedTier = "";
                this.bf.updatePlayerData(player, data);
                PlayerDataManager.savePlayerData(player);
                LeaderboardSection section = LeaderboardManager.getLeaderboardSection(player.method_5667());
                if (section != null) {
                    LeaderboardManager.leaderboard.remove(section);
                }
                LeaderboardManager.leaderboard = LeaderboardManager.sortLeaderboard(new ArrayList<LeaderboardSection>(LeaderboardManager.leaderboard));
                LeaderboardManager.saveLeaderboard();
                ((class_2168)ctx.getSource()).method_45068(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_resetPlayerData"), player)));
                return 1;
            })))).then(((LiteralArgumentBuilder)class_2170.method_9247((String)"resetleaderboard").requires(Permissions.require((String)"battlefactory.admin.resetleaderboard", (int)4))).executes(ctx -> {
                LeaderboardManager.leaderboard.clear();
                LeaderboardManager.saveLeaderboard();
                ((class_2168)ctx.getSource()).method_45068(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_resetLeaderboard"))));
                return 1;
            }))).then(((LiteralArgumentBuilder)class_2170.method_9247((String)"forceround").requires(Permissions.require((String)"battlefactory.admin.forceround", (int)4))).then(class_2170.method_9244((String)"player", (ArgumentType)class_2186.method_9305()).executes(ctx -> {
                class_3222 player = class_2186.method_9315((CommandContext)ctx, (String)"player");
                BattleFactoryInstance bfInstance = this.bf.getBattleFactoryInstance(player);
                if (bfInstance != null) {
                    if (!bfInstance.roundTransition) {
                        PokemonBattle battle;
                        if (bfInstance.currentBattleID != null && (battle = BattleRegistry.getBattle((UUID)bfInstance.currentBattleID)) != null) {
                            ArrayList<Pokemon> npcParty = new ArrayList<Pokemon>();
                            for (BattleActor actor : battle.getActors()) {
                                if (actor instanceof NPCBattleActor) {
                                    NPCBattleActor npcBattleActor = (NPCBattleActor)actor;
                                    NPCEntity npc = npcBattleActor.getEntity();
                                    if (!npc.method_5667().equals(bfInstance.currentNPC.method_5667())) continue;
                                    npcBattleActor.getPokemonList().forEach(pokemon -> npcParty.add(pokemon.getEffectedPokemon()));
                                    break;
                                }
                                if (!(actor instanceof PlayerBattleActor)) continue;
                                PlayerBattleActor playerBattleActor = (PlayerBattleActor)actor;
                                playerBattleActor.getPokemonList().forEach(pokemon -> {
                                    PokemonEntity pokemonEntity = pokemon.getEntity();
                                    if (pokemonEntity != null) {
                                        pokemonEntity.method_5650(class_1297.class_5529.field_26999);
                                    }
                                });
                            }
                            battle.stop();
                            bfInstance.nextRound(bfInstance.inBonusEncounter, npcParty);
                        }
                    } else {
                        bfInstance.roundTimer = 1;
                    }
                    ((class_2168)ctx.getSource()).method_45068(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_forceRound_success"), player)));
                } else {
                    ((class_2168)ctx.getSource()).method_45068(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_forceRound_fail"), player)));
                }
                return 1;
            })))).then(((LiteralArgumentBuilder)class_2170.method_9247((String)"forcestop").requires(Permissions.require((String)"battlefactory.admin.forcestop", (int)4))).then(class_2170.method_9244((String)"player", (ArgumentType)class_2186.method_9305()).executes(ctx -> {
                class_3222 player = class_2186.method_9315((CommandContext)ctx, (String)"player");
                if (player != null) {
                    if (this.bf.stopBattleFactoryInstance(player)) {
                        player.method_43496(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("battleFactory_stopped"), player)));
                        ((class_2168)ctx.getSource()).method_45068(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_forceStop_success"), player)));
                        return 1;
                    }
                    ((class_2168)ctx.getSource()).method_45068(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_forceStop_fail"), player)));
                }
                return 0;
            })))).then(((LiteralArgumentBuilder)class_2170.method_9247((String)"forcestart").requires(Permissions.require((String)"battlefactory.admin.forcestart", (int)4))).then(class_2170.method_9244((String)"player", (ArgumentType)class_2186.method_9305()).then(class_2170.method_9244((String)"tier", (ArgumentType)StringArgumentType.string()).suggests((context, builder) -> {
                for (TierSettings tier : this.bf.config().tiers) {
                    builder.suggest(tier.tierID());
                }
                return builder.buildFuture();
            }).executes(ctx -> {
                class_3222 player = class_2186.method_9315((CommandContext)ctx, (String)"player");
                if (this.bf.getBattleFactoryInstance(player) != null) {
                    ((class_2168)ctx.getSource()).method_45068(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_forceStart_fail"), player)));
                    return 0;
                }
                String tierId = StringArgumentType.getString((CommandContext)ctx, (String)"tier");
                TierSettings tier = this.bf.config().getTier(tierId);
                if (tier == null) {
                    return 0;
                }
                this.start(tier, player);
                ((class_2168)ctx.getSource()).method_45068(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_forceStart_success"), player)));
                return 1;
            }))))).then(((LiteralArgumentBuilder)class_2170.method_9247((String)"resetcooldown").requires(Permissions.require((String)"battlefactory.admin.resetcooldown", (int)4))).then(class_2170.method_9244((String)"player", (ArgumentType)class_2186.method_9305()).executes(ctx -> {
                class_3222 player = class_2186.method_9315((CommandContext)ctx, (String)"player");
                PlayerData data = this.bf.getPlayerData(player);
                if (data != null) {
                    data.cooldownProgress = 0L;
                    this.bf.playerCooldowns.remove(player.method_5667());
                    ((class_2168)ctx.getSource()).method_45068(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_resetCooldown_success"), player)));
                } else {
                    ((class_2168)ctx.getSource()).method_45068(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_resetCooldown_fail"), player)));
                }
                return 1;
            })))).then(((LiteralArgumentBuilder)class_2170.method_9247((String)"spectate").requires(Permissions.require((String)"battlefactory.admin.spectate", (int)4))).then(class_2170.method_9244((String)"player", (ArgumentType)class_2186.method_9305()).executes(ctx -> {
                class_3222 player = class_2186.method_9315((CommandContext)ctx, (String)"player");
                return 1;
            })))).then(((LiteralArgumentBuilder)class_2170.method_9247((String)"unspectate").requires(Permissions.require((String)"battlefactory.admin.unspectate", (int)4))).executes(ctx -> 1))));
            commandDispatcher.register((LiteralArgumentBuilder)class_2170.method_9247((String)"bf").redirect((CommandNode)node));
        });
    }

    public int preStart(CommandContext<class_2168> ctx) {
        SimpleGui tierSelectionGUI;
        if (!((class_2168)ctx.getSource()).method_43737()) {
            return 0;
        }
        class_3222 player = ((class_2168)ctx.getSource()).method_44023();
        if (player == null) {
            return 0;
        }
        if (this.bf.config().tiers.isEmpty()) {
            return 0;
        }
        if (this.bf.playerCooldowns.containsKey(player.method_5667()) && this.bf.playerCooldowns.get(player.method_5667()) > 0L) {
            player.method_43496(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("error_waitForCooldown"), player)));
            return 0;
        }
        TierSettings tierSettings = this.bf.config().tiers.getFirst();
        if (this.bf.config().allowTierSelection) {
            class_1799 item;
            ArrayList<TierSettings> availableTiers = new ArrayList<TierSettings>();
            if (this.bf.config().maxTierSelectIsHighestStreak) {
                PlayerData data = this.bf.getPlayerData(player);
                Object maxTierAvailable = tierSettings.tierID();
                if (data != null && !data.highestCompletedTier.isEmpty()) {
                    maxTierAvailable = data.highestCompletedTier;
                }
                for (TierSettings tier : this.bf.config().tiers) {
                    availableTiers.add(tier);
                    if (!tier.tierID().equalsIgnoreCase((String)maxTierAvailable)) continue;
                    break;
                }
            } else {
                availableTiers.addAll(this.bf.config().tiers);
            }
            tierSelectionGUI = new SimpleGui(this.bf.tierSelectionGUIConfig().getScreenSize(), player, false);
            tierSelectionGUI.setTitle(TextUtils.deserialize(TextUtils.parse(this.bf.tierSelectionGUIConfig().title, player)));
            for (Integer slot : this.bf.tierSelectionGUIConfig().getSlotsBySymbol(this.bf.tierSelectionGUIConfig().backgroundItemSymbol)) {
                class_1799 item2 = ((class_1792)class_7923.field_41178.method_10223(class_2960.method_60654((String)this.bf.tierSelectionGUIConfig().backgroundItem))).method_7854();
                item2.method_59692(this.bf.tierSelectionGUIConfig().backgroundItemData);
                tierSelectionGUI.setSlot(slot.intValue(), (GuiElementInterface)new GuiElementBuilder(item2).setName(TextUtils.deserialize(TextUtils.parse(this.bf.tierSelectionGUIConfig().backgroundItemName, player))).setLore(TextUtils.getLoreArray(this.bf.tierSelectionGUIConfig().backgroundItemLore, player)).build());
            }
            List<Integer> tierSlots = this.bf.tierSelectionGUIConfig().getSlotsBySymbol(this.bf.tierSelectionGUIConfig().tierItemSymbol);
            int index = 0;
            for (TierSettings tier : this.bf.config().tiers) {
                if (index >= tierSlots.size()) break;
                if (availableTiers.contains(tier)) {
                    item = ((class_1792)class_7923.field_41178.method_10223(class_2960.method_60654((String)tier.tierItem()))).method_7854();
                    item.method_59692(this.bf.tierSelectionGUIConfig().tierItemData);
                    tierSelectionGUI.setSlot(tierSlots.get(index).intValue(), (GuiElementInterface)((GuiElementBuilder)new GuiElementBuilder(item).setName(TextUtils.deserialize(TextUtils.parse(TextUtils.parse(this.bf.tierSelectionGUIConfig().tierItemName, player), tier))).setLore(TextUtils.getLoreArray(this.bf.tierSelectionGUIConfig().tierItemLore, tier)).setCallback(clickType -> {
                        this.start(tier, player);
                        tierSelectionGUI.close();
                    })).build());
                } else {
                    item = ((class_1792)class_7923.field_41178.method_10223(class_2960.method_60654((String)this.bf.tierSelectionGUIConfig().unavailableTierItem))).method_7854();
                    item.method_59692(this.bf.tierSelectionGUIConfig().unavailableItemData);
                    tierSelectionGUI.setSlot(tierSlots.get(index).intValue(), (GuiElementInterface)new GuiElementBuilder(item).setName(TextUtils.deserialize(TextUtils.parse(TextUtils.parse(this.bf.tierSelectionGUIConfig().tierItemName, player), tier))).setLore(TextUtils.getLoreArray(this.bf.tierSelectionGUIConfig().unavailableTierItemLore, tier)).build());
                }
                ++index;
            }
            for (Integer cancelSlot : this.bf.tierSelectionGUIConfig().getSlotsBySymbol(this.bf.tierSelectionGUIConfig().cancelItemSymbol)) {
                item = ((class_1792)class_7923.field_41178.method_10223(class_2960.method_60654((String)this.bf.tierSelectionGUIConfig().cancelItem))).method_7854();
                item.method_59692(this.bf.tierSelectionGUIConfig().cancelItemData);
                tierSelectionGUI.setSlot(cancelSlot.intValue(), (GuiElementInterface)((GuiElementBuilder)new GuiElementBuilder(item).setName(TextUtils.deserialize(TextUtils.parse(this.bf.tierSelectionGUIConfig().cancelItemName, player))).setLore(TextUtils.getLoreArray(this.bf.tierSelectionGUIConfig().cancelItemLore, player)).setCallback(clickType -> tierSelectionGUI.close())).build());
            }
        } else {
            return this.start(tierSettings, player);
        }
        tierSelectionGUI.open();
        return 1;
    }

    public int start(TierSettings tierSettings, class_3222 player) {
        if (tierSettings == null) {
            this.bf.logError("[BattleFactory] Tier is null");
            return 0;
        }
        PokemonPool pool = tierSettings.getPokemonPool();
        if (pool == null) {
            this.bf.logError("[BattleFactory] Pool is null");
            return 0;
        }
        ArrayList<Object> rentalPresets = new ArrayList<Object>();
        ArrayList<Pokemon> availableRentals = new ArrayList<Pokemon>();
        int count = 0;
        if (pool instanceof SetPokemonPool) {
            SetPokemonPool setPokemonPool = (SetPokemonPool)pool;
            if (setPokemonPool.pool.size() < this.bf.config().numberOfPokemonAvailable) {
                this.bf.logError("[BattleFactory] Pool size is less than number of pokemon available!");
                return 0;
            }
            block0: while (count < this.bf.config().numberOfPokemonAvailable) {
                PokemonPreset preset = setPokemonPool.getRandomPokemon();
                for (PokemonPreset pokemonPreset : rentalPresets) {
                    if (!preset.uuid().equals(pokemonPreset.uuid())) continue;
                    continue block0;
                }
                rentalPresets.add(preset);
                ++count;
            }
            for (PokemonPreset pokemonPreset : rentalPresets) {
                Pokemon pokemon = pokemonPreset.getPokemon();
                if (pokemon == null) continue;
                this.bf.logInfo("Adding " + pokemon.getSpecies().getName() + " to available rentals!");
                availableRentals.add(pokemon);
            }
        } else if (pool instanceof RandomPokemonPool) {
            RandomPokemonPool randomPokemonPool = (RandomPokemonPool)pool;
            for (int i = 0; i < this.bf.config().numberOfPokemonAvailable; ++i) {
                Pokemon pokemon = randomPokemonPool.createRandomPokemon();
                if (pokemon == null) {
                    this.bf.logError("[BattleFactory] Random Pokemon is null");
                    return 0;
                }
                this.bf.logInfo("Adding " + pokemon.getSpecies().getName() + " to available rentals!");
                availableRentals.add(pokemon);
            }
        }
        if (availableRentals.size() < this.bf.config().numberOfPokemonAvailable) {
            player.method_43496(TextUtils.deserialize(TextUtils.parse("%prefix% <red>Not enough pokemon available in the rental pool!")));
            return 0;
        }
        ArrayList<Integer> selectedRentals = new ArrayList<Integer>();
        this.openRentalSelectionGUI(player, availableRentals, selectedRentals, tierSettings);
        return 1;
    }

    public void openRentalSelectionGUI(class_3222 player, List<Pokemon> availableRentals, List<Integer> selectedRentals, TierSettings tierSettings) {
        try {
            class_1799 item;
            if (this.bf.getBattleFactoryInstance(player) != null) {
                player.method_43496(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("error_alreadyInBattleFactory"), player)));
                return;
            }
            SimpleGui rentalSelectionGUI = new SimpleGui(this, this.bf.rentalSelectionGUIConfig().getScreenSize(), player, false){

                public void onClose() {
                    this.player.method_6016(class_1294.field_38092);
                    super.onClose();
                }

                public void onOpen() {
                    this.player.method_6092(new class_1293(class_1294.field_38092, 9999));
                    super.onOpen();
                }
            };
            rentalSelectionGUI.setTitle(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().title, player)));
            for (Integer slot : this.bf.rentalSelectionGUIConfig().getSlotsBySymbol(this.bf.rentalSelectionGUIConfig().backgroundItemSymbol)) {
                class_1799 item2 = ((class_1792)class_7923.field_41178.method_10223(class_2960.method_60654((String)this.bf.rentalSelectionGUIConfig().backgroundItem))).method_7854();
                item2.method_59692(this.bf.rentalSelectionGUIConfig().backgroundItemData);
                rentalSelectionGUI.setSlot(slot.intValue(), (GuiElementInterface)new GuiElementBuilder(item2).setName(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().backgroundItemName, player))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().backgroundItemLore, player)).build());
            }
            List<Integer> rentalSlots = this.bf.rentalSelectionGUIConfig().getSlotsBySymbol(this.bf.rentalSelectionGUIConfig().rentalItemSymbol);
            for (int i = 0; i < rentalSlots.size(); ++i) {
                int finalI = i;
                if (i < availableRentals.size()) {
                    class_1799 rentalItem = PokemonItem.from((Pokemon)availableRentals.get(i));
                    rentalItem.method_57365(class_9323.method_57827().method_57840(class_9334.field_49641, (Object)(this.bf.rentalSelectionGUIConfig().enchantRentalItemOnSelect && !selectedRentals.isEmpty() && selectedRentals.contains(i) ? 1 : 0)).method_57838());
                    rentalItem.method_59692(this.bf.rentalSelectionGUIConfig().rentalItemData);
                    rentalSelectionGUI.setSlot(rentalSlots.get(i).intValue(), (GuiElementInterface)((GuiElementBuilder)new GuiElementBuilder(rentalItem).setName(TextUtils.deserialize(TextUtils.parse(TextUtils.parse(this.bf.rentalSelectionGUIConfig().rentalItemName, availableRentals.get(i)), player))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().rentalItemLore, availableRentals.get(i))).setCallback(clickType -> {
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
                        this.openRentalSelectionGUI(player, availableRentals, selectedRentals, tierSettings);
                    })).build());
                }
                for (Integer selected_rental : selectedRentals) {
                    if (selected_rental != finalI) continue;
                    class_1799 selectedItem = ((class_1792)class_7923.field_41178.method_10223(class_2960.method_60654((String)this.bf.rentalSelectionGUIConfig().selectedItem))).method_7854();
                    selectedItem.method_59692(this.bf.rentalSelectionGUIConfig().selectedItemData);
                    if (this.bf.rentalSelectionGUIConfig().showSelectedItemAbove && rentalSlots.get(i) - 9 >= 0) {
                        rentalSelectionGUI.setSlot(rentalSlots.get(i) - 9, (GuiElementInterface)new GuiElementBuilder(selectedItem).setName(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().selectedItemName, player))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().selectedItemLore, player)).build());
                    }
                    if (!this.bf.rentalSelectionGUIConfig().showSelectedItemBelow || rentalSlots.get(i) + 9 >= this.bf.rentalSelectionGUIConfig().rows * 9) continue;
                    rentalSelectionGUI.setSlot(rentalSlots.get(i) + 9, (GuiElementInterface)new GuiElementBuilder(selectedItem).setName(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().selectedItemName, player))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().selectedItemLore, player)).build());
                }
            }
            for (Integer slot : this.bf.rentalSelectionGUIConfig().getSlotsBySymbol(this.bf.rentalSelectionGUIConfig().cancelItemSymbol)) {
                item = ((class_1792)class_7923.field_41178.method_10223(class_2960.method_60654((String)this.bf.rentalSelectionGUIConfig().cancelItem))).method_7854();
                item.method_59692(this.bf.rentalSelectionGUIConfig().cancelItemData);
                rentalSelectionGUI.setSlot(slot.intValue(), (GuiElementInterface)((GuiElementBuilder)new GuiElementBuilder(item).setName(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().cancelItemName, player))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().cancelItemLore, player)).setCallback(clickType -> rentalSelectionGUI.close())).build());
            }
            for (Integer slot : this.bf.rentalSelectionGUIConfig().getSlotsBySymbol(this.bf.rentalSelectionGUIConfig().startItemSymbol)) {
                item = ((class_1792)class_7923.field_41178.method_10223(class_2960.method_60654((String)this.bf.rentalSelectionGUIConfig().startItem))).method_7854();
                item.method_59692(this.bf.rentalSelectionGUIConfig().startItemData);
                rentalSelectionGUI.setSlot(slot.intValue(), (GuiElementInterface)((GuiElementBuilder)new GuiElementBuilder(item).setName(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().startItemName, player))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().startItemLore, player)).setCallback(clickType -> {
                    if (selectedRentals.size() == this.bf.config().numberOfPokemonRented) {
                        rentalSelectionGUI.close();
                        ArrayList<Pokemon> selected = new ArrayList<Pokemon>();
                        for (int i = 0; i < this.bf.config().numberOfPokemonRented; ++i) {
                            selected.add((Pokemon)availableRentals.get((Integer)selectedRentals.get(i)));
                        }
                        BattleFactory.INSTANCE.battleFactoryInstances.add(new BattleFactoryInstance(player, selected, tierSettings));
                    }
                })).build());
            }
            rentalSelectionGUI.open();
        }
        catch (Exception e) {
            this.bf.logError("[BattleFactory] Error opening GUI: " + e.getMessage());
        }
    }
}

