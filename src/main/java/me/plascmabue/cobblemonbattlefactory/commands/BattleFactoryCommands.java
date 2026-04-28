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
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.EntityArgument
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.component.DataComponentMap
 *  net.minecraft.core.component.DataComponents
 */
package me.plascmabue.cobblemonbattlefactory.commands;

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
import java.util.concurrent.atomic.AtomicBoolean;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import me.plascmabue.cobblemonbattlefactory.config.playerdata.LeaderboardManager;
import me.plascmabue.cobblemonbattlefactory.config.playerdata.PlayerDataManager;
import me.plascmabue.cobblemonbattlefactory.datatypes.LeaderboardSection;
import me.plascmabue.cobblemonbattlefactory.datatypes.PlayerData;
import me.plascmabue.cobblemonbattlefactory.datatypes.TierSettings;
import me.plascmabue.cobblemonbattlefactory.datatypes.rentalPools.PokemonPool;
import me.plascmabue.cobblemonbattlefactory.datatypes.rentalPools.PokemonPreset;
import me.plascmabue.cobblemonbattlefactory.datatypes.rentalPools.RandomPokemonPool;
import me.plascmabue.cobblemonbattlefactory.datatypes.rentalPools.SetPokemonPool;
import me.plascmabue.cobblemonbattlefactory.managers.BattleFactoryInstance;
import me.plascmabue.cobblemonbattlefactory.utils.TextUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomModelData;

public class BattleFactoryCommands {
    private final BattleFactory bf = BattleFactory.INSTANCE;

    public BattleFactoryCommands() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            LiteralCommandNode node = commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"battlefactory").requires(Permissions.require((String)"battlefactory.base", (boolean)true))).then(((LiteralArgumentBuilder)Commands.literal((String)"start").requires(Permissions.require((String)"battlefactory.start", (boolean)true))).executes(this::preStart))).then(((LiteralArgumentBuilder)Commands.literal((String)"stop").requires(Permissions.require((String)"battlefactory.stop", (boolean)true))).executes(ctx -> {
                ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayer();
                if (player != null && this.bf.stopBattleFactoryInstance(player)) {
                    player.sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("battleFactory_stopped"), player)));
                }
                return 1;
            }))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"leaderboard").requires(Permissions.require((String)"battlefactory.leaderboard", (boolean)true))).then(Commands.argument((String)"page", (ArgumentType)IntegerArgumentType.integer((int)1, (int)(LeaderboardManager.leaderboard.size() % 10 + 1))).executes(ctx -> {
                int page = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"page");
                int lastIndex = page * 10;
                ((CommandSourceStack)ctx.getSource()).sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("leaderboard_header").replaceAll("%page%", String.valueOf(page)).replaceAll("%max_pages%", String.valueOf(LeaderboardManager.leaderboard.size() % 10)))));
                for (int i = lastIndex - 10; i < lastIndex; ++i) {
                    if (i >= LeaderboardManager.leaderboard.size()) continue;
                    LeaderboardSection section = LeaderboardManager.leaderboard.get(i);
                    ((CommandSourceStack)ctx.getSource()).sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("leaderboard_section").replaceAll("%section.placement%", String.valueOf(section.placement)).replaceAll("%section.player_name%", section.name).replaceAll("%section.highest_streak%", String.valueOf(section.highestStreak)).replaceAll("%section.date_achieved%", section.dateAchieved.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))))));
                }
                return 1;
            }))).executes(ctx -> {
                int page = 1;
                int lastIndex = page * 10;
                ((CommandSourceStack)ctx.getSource()).sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("leaderboard_header").replaceAll("%page%", String.valueOf(page)).replaceAll("%max_pages%", String.valueOf(LeaderboardManager.leaderboard.size() % 10)))));
                for (int i = 0; i < lastIndex; ++i) {
                    if (i >= LeaderboardManager.leaderboard.size()) continue;
                    LeaderboardSection section = LeaderboardManager.leaderboard.get(i);
                    ((CommandSourceStack)ctx.getSource()).sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("leaderboard_section").replaceAll("%section.placement%", String.valueOf(section.placement)).replaceAll("%section.player_name%", section.name).replaceAll("%section.highest_streak%", String.valueOf(section.highestStreak)).replaceAll("%section.date_achieved%", section.dateAchieved.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))))));
                }
                return 1;
            }))).then(((LiteralArgumentBuilder)Commands.literal((String)"status").requires(Permissions.require((String)"battlefactory.status", (boolean)true))).executes(ctx -> {
                BattleFactoryInstance bfInstance;
                ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayer();
                if (player != null && (bfInstance = this.bf.getBattleFactoryInstance(player)) != null) {
                    player.sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_status"), bfInstance)));
                }
                return 1;
            }))).then(((LiteralArgumentBuilder)Commands.literal((String)"reload").requires(Permissions.require((String)"battlefactory.reload", (int)4))).executes(ctx -> {
                this.bf.reload(true);
                ((CommandSourceStack)ctx.getSource()).sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_reload"))));
                return 1;
            }))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"admin").requires(Permissions.require((String)"battlefactory.admin", (int)4))).then(((LiteralArgumentBuilder)Commands.literal((String)"reset").requires(Permissions.require((String)"battlefactory.admin.reset", (int)4))).then(Commands.argument((String)"player", (ArgumentType)EntityArgument.player()).executes(ctx -> {
                ServerPlayer player = EntityArgument.getPlayer((CommandContext)ctx, (String)"player");
                PlayerData data = this.bf.getPlayerData(player);
                data.highestStreak = 0;
                data.highestCompletedTier = "";
                this.bf.updatePlayerData(player, data);
                PlayerDataManager.savePlayerData(player);
                LeaderboardSection section = LeaderboardManager.getLeaderboardSection(player.getUUID());
                if (section != null) {
                    LeaderboardManager.leaderboard.remove(section);
                }
                LeaderboardManager.leaderboard = LeaderboardManager.sortLeaderboard(new ArrayList<LeaderboardSection>(LeaderboardManager.leaderboard));
                LeaderboardManager.saveLeaderboard();
                ((CommandSourceStack)ctx.getSource()).sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_resetPlayerData"), player)));
                return 1;
            })))).then(((LiteralArgumentBuilder)Commands.literal((String)"resetleaderboard").requires(Permissions.require((String)"battlefactory.admin.resetleaderboard", (int)4))).executes(ctx -> {
                LeaderboardManager.leaderboard.clear();
                LeaderboardManager.saveLeaderboard();
                ((CommandSourceStack)ctx.getSource()).sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_resetLeaderboard"))));
                return 1;
            }))).then(((LiteralArgumentBuilder)Commands.literal((String)"forceround").requires(Permissions.require((String)"battlefactory.admin.forceround", (int)4))).then(Commands.argument((String)"player", (ArgumentType)EntityArgument.player()).executes(ctx -> {
                ServerPlayer player = EntityArgument.getPlayer((CommandContext)ctx, (String)"player");
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
                                    if (!npc.getUUID().equals(bfInstance.currentNPC.getUUID())) continue;
                                    npcBattleActor.getPokemonList().forEach(pokemon -> npcParty.add(pokemon.getEffectedPokemon()));
                                    break;
                                }
                                if (!(actor instanceof PlayerBattleActor)) continue;
                                PlayerBattleActor playerBattleActor = (PlayerBattleActor)actor;
                                playerBattleActor.getPokemonList().forEach(pokemon -> {
                                    PokemonEntity pokemonEntity = pokemon.getEntity();
                                    if (pokemonEntity != null) {
                                        pokemonEntity.remove(Entity.RemovalReason.DISCARDED);
                                    }
                                });
                            }
                            battle.stop();
                            bfInstance.nextRound(bfInstance.inBonusEncounter, npcParty);
                        }
                    } else {
                        bfInstance.roundTimer = 1;
                    }
                    ((CommandSourceStack)ctx.getSource()).sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_forceRound_success"), player)));
                } else {
                    ((CommandSourceStack)ctx.getSource()).sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_forceRound_fail"), player)));
                }
                return 1;
            })))).then(((LiteralArgumentBuilder)Commands.literal((String)"forcestop").requires(Permissions.require((String)"battlefactory.admin.forcestop", (int)4))).then(Commands.argument((String)"player", (ArgumentType)EntityArgument.player()).executes(ctx -> {
                ServerPlayer player = EntityArgument.getPlayer((CommandContext)ctx, (String)"player");
                if (player != null) {
                    if (this.bf.stopBattleFactoryInstance(player)) {
                        player.sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("battleFactory_stopped"), player)));
                        ((CommandSourceStack)ctx.getSource()).sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_forceStop_success"), player)));
                        return 1;
                    }
                    ((CommandSourceStack)ctx.getSource()).sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_forceStop_fail"), player)));
                }
                return 0;
            })))).then(((LiteralArgumentBuilder)Commands.literal((String)"forcestart").requires(Permissions.require((String)"battlefactory.admin.forcestart", (int)4))).then(Commands.argument((String)"player", (ArgumentType)EntityArgument.player()).then(Commands.argument((String)"tier", (ArgumentType)StringArgumentType.string()).suggests((context, builder) -> {
                for (TierSettings tier : this.bf.config().tiers) {
                    builder.suggest(tier.tierID());
                }
                return builder.buildFuture();
            }).executes(ctx -> {
                ServerPlayer player = EntityArgument.getPlayer((CommandContext)ctx, (String)"player");
                if (this.bf.getBattleFactoryInstance(player) != null) {
                    ((CommandSourceStack)ctx.getSource()).sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_forceStart_fail"), player)));
                    return 0;
                }
                String tierId = StringArgumentType.getString((CommandContext)ctx, (String)"tier");
                TierSettings tier = this.bf.config().getTier(tierId);
                if (tier == null) {
                    return 0;
                }
                this.start(tier, player);
                ((CommandSourceStack)ctx.getSource()).sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_forceStart_success"), player)));
                return 1;
            }))))).then(((LiteralArgumentBuilder)Commands.literal((String)"resetcooldown").requires(Permissions.require((String)"battlefactory.admin.resetcooldown", (int)4))).then(Commands.argument((String)"player", (ArgumentType)EntityArgument.player()).executes(ctx -> {
                ServerPlayer player = EntityArgument.getPlayer((CommandContext)ctx, (String)"player");
                PlayerData data = this.bf.getPlayerData(player);
                if (data != null) {
                    data.cooldownProgress = 0L;
                    this.bf.playerCooldowns.remove(player.getUUID());
                    ((CommandSourceStack)ctx.getSource()).sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_resetCooldown_success"), player)));
                } else {
                    ((CommandSourceStack)ctx.getSource()).sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("command_resetCooldown_fail"), player)));
                }
                return 1;
            })))).then(((LiteralArgumentBuilder)Commands.literal((String)"spectate").requires(Permissions.require((String)"battlefactory.admin.spectate", (int)4))).then(Commands.argument((String)"player", (ArgumentType)EntityArgument.player()).executes(ctx -> {
                ServerPlayer player = EntityArgument.getPlayer((CommandContext)ctx, (String)"player");
                return 1;
            })))).then(((LiteralArgumentBuilder)Commands.literal((String)"unspectate").requires(Permissions.require((String)"battlefactory.admin.unspectate", (int)4))).executes(ctx -> 1))));
            commandDispatcher.register((LiteralArgumentBuilder)Commands.literal((String)"bf").redirect((CommandNode)node));
            commandDispatcher.register(Commands.literal("bftestreward")
                    .requires(src -> src.hasPermission(4))
                    .then(Commands.argument("player", EntityArgument.player())
                            .executes(ctx -> {
                                ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
                                BattleFactoryInstance inst = this.bf.getBattleFactoryInstance(player);
                                BattleFactory.LOGGER.info("[BattleFactory] /bftestreward {} — inDungeon={} tier={} tierRound={} perRound={}",
                                        player.getScoreboardName(),
                                        inst != null,
                                        inst != null ? inst.currentTier.tierID() : "n/a",
                                        inst != null ? inst.currentTierRound : -1,
                                        inst != null && inst.currentTier.perRoundRewards() != null ? "size=" + inst.currentTier.perRoundRewards().size() : "null");
                                me.plascmabue.cobblemonbattlefactory.datatypes.rewards.CommandReward test = new me.plascmabue.cobblemonbattlefactory.datatypes.rewards.CommandReward(
                                        java.util.UUID.randomUUID(), "test_bp", "command",
                                        java.util.List.of("bp give %player% 1"));
                                test.grant_reward(player);
                                ctx.getSource().sendSystemMessage(net.minecraft.network.chat.Component.literal("§a[BF] Test reward fired — check if " + player.getScoreboardName() + " got 1 BP."));
                                return 1;
                            })));
            commandDispatcher.register(Commands.literal("bfdiag")
                    .requires(src -> src.hasPermission(4))
                    .then(Commands.argument("player", EntityArgument.player())
                            .executes(ctx -> {
                                ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
                                BattleFactoryInstance inst = this.bf.getBattleFactoryInstance(player);
                                if (inst == null) {
                                    ctx.getSource().sendSystemMessage(net.minecraft.network.chat.Component.literal("§c[BF] " + player.getScoreboardName() + " n'est pas en session BF."));
                                    return 0;
                                }
                                StringBuilder sb = new StringBuilder();
                                sb.append("§e[BF diag] tier=").append(inst.currentTier.tierID())
                                  .append(" round=").append(inst.round)
                                  .append(" tierRound=").append(inst.currentTierRound)
                                  .append("\n§7perBattleRewards=").append(inst.currentTier.perBattleRewards() != null ? "present" : "NULL")
                                  .append("\n§7perRoundRewards=").append(inst.currentTier.perRoundRewards() != null ? "size " + inst.currentTier.perRoundRewards().size() : "NULL");
                                if (inst.currentTier.perRoundRewards() != null) {
                                    sb.append("\n§7keys: ").append(inst.currentTier.perRoundRewards().keySet());
                                    sb.append("\n§7get(").append(inst.currentTierRound).append(")=").append(inst.currentTier.perRoundRewards().get(inst.currentTierRound) != null ? "present" : "NULL");
                                }
                                ctx.getSource().sendSystemMessage(net.minecraft.network.chat.Component.literal(sb.toString()));
                                BattleFactory.LOGGER.info("[BattleFactory] /bfdiag → {}", sb.toString().replace("\n", " | "));
                                return 1;
                            })));
        });
    }

    public int preStart(CommandContext<CommandSourceStack> ctx) {
        SimpleGui tierSelectionGUI;
        if (!((CommandSourceStack)ctx.getSource()).isPlayer()) {
            return 0;
        }
        ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayer();
        if (player == null) {
            return 0;
        }
        if (this.bf.config().tiers.isEmpty()) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "§c[BattleFactory] Aucun tier configuré. Vérifie config/BattleFactory/config.json — il faut au moins un tier avec 'battles_for_next_tier' et 'possible_npcs'."));
            return 0;
        }
        if (this.bf.playerCooldowns.containsKey(player.getUUID()) && this.bf.playerCooldowns.get(player.getUUID()) > 0L) {
            player.sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("error_waitForCooldown"), player)));
            return 0;
        }
        TierSettings tierSettings = this.bf.config().tiers.getFirst();
        if (this.bf.config().allowTierSelection) {
            ItemStack item;
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
                ItemStack item2 = ((Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)this.bf.tierSelectionGUIConfig().backgroundItem))).getDefaultInstance();
                item2.applyComponents(this.bf.tierSelectionGUIConfig().backgroundItemData);
                tierSelectionGUI.setSlot(slot.intValue(), (GuiElementInterface)new GuiElementBuilder(item2).setName(TextUtils.deserialize(TextUtils.parse(this.bf.tierSelectionGUIConfig().backgroundItemName, player))).setLore(TextUtils.getLoreArray(this.bf.tierSelectionGUIConfig().backgroundItemLore, player)).build());
            }
            List<Integer> tierSlots = this.bf.tierSelectionGUIConfig().getSlotsBySymbol(this.bf.tierSelectionGUIConfig().tierItemSymbol);
            int index = 0;
            for (TierSettings tier : this.bf.config().tiers) {
                if (index >= tierSlots.size()) break;
                if (availableTiers.contains(tier)) {
                    item = ((Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)tier.tierItem()))).getDefaultInstance();
                    item.applyComponents(this.bf.tierSelectionGUIConfig().tierItemData);
                    tierSelectionGUI.setSlot(tierSlots.get(index).intValue(), (GuiElementInterface)((GuiElementBuilder)new GuiElementBuilder(item).setName(TextUtils.deserialize(TextUtils.parse(TextUtils.parse(this.bf.tierSelectionGUIConfig().tierItemName, player), tier))).setLore(TextUtils.getLoreArray(this.bf.tierSelectionGUIConfig().tierItemLore, tier)).setCallback(clickType -> {
                        this.start(tier, player);
                        tierSelectionGUI.close();
                    })).build());
                } else {
                    item = ((Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)this.bf.tierSelectionGUIConfig().unavailableTierItem))).getDefaultInstance();
                    item.applyComponents(this.bf.tierSelectionGUIConfig().unavailableItemData);
                    tierSelectionGUI.setSlot(tierSlots.get(index).intValue(), (GuiElementInterface)new GuiElementBuilder(item).setName(TextUtils.deserialize(TextUtils.parse(TextUtils.parse(this.bf.tierSelectionGUIConfig().tierItemName, player), tier))).setLore(TextUtils.getLoreArray(this.bf.tierSelectionGUIConfig().unavailableTierItemLore, tier)).build());
                }
                ++index;
            }
            for (Integer cancelSlot : this.bf.tierSelectionGUIConfig().getSlotsBySymbol(this.bf.tierSelectionGUIConfig().cancelItemSymbol)) {
                item = ((Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)this.bf.tierSelectionGUIConfig().cancelItem))).getDefaultInstance();
                item.applyComponents(this.bf.tierSelectionGUIConfig().cancelItemData);
                tierSelectionGUI.setSlot(cancelSlot.intValue(), (GuiElementInterface)((GuiElementBuilder)new GuiElementBuilder(item).setName(TextUtils.deserialize(TextUtils.parse(this.bf.tierSelectionGUIConfig().cancelItemName, player))).setLore(TextUtils.getLoreArray(this.bf.tierSelectionGUIConfig().cancelItemLore, player)).setCallback(clickType -> tierSelectionGUI.close())).build());
            }
        } else {
            return this.start(tierSettings, player);
        }
        tierSelectionGUI.open();
        return 1;
    }

    public int start(TierSettings tierSettings, ServerPlayer player) {
        if (tierSettings == null) {
            this.bf.logError("[BattleFactory] Tier is null");
            return 0;
        }
        PokemonPool pool = tierSettings.getPokemonPool();
        if (pool == null) {
            this.bf.logError("[BattleFactory] Pool is null");
            return 0;
        }
        ArrayList<PokemonPreset> rentalPresets = new ArrayList<PokemonPreset>();
        ArrayList<Pokemon> availableRentals = new ArrayList<Pokemon>();
        int needed = this.bf.config().numberOfPokemonAvailable;
        if (pool instanceof SetPokemonPool) {
            SetPokemonPool setPokemonPool = (SetPokemonPool)pool;
            if (setPokemonPool.pool.size() < needed) {
                this.bf.logError("[BattleFactory] Pool '" + setPokemonPool.name + "' has "
                        + setPokemonPool.pool.size() + " presets but " + needed + " are required (numberOfPokemonAvailable).");
                player.sendSystemMessage(TextUtils.deserialize(TextUtils.parse(
                        "%prefix% <red>Pool trop petite : " + setPokemonPool.pool.size() + "/" + needed + " Pokémon.")));
                return 0;
            }
            int attempts = 0;
            int failedBuilds = 0;
            int maxAttempts = Math.max(needed * 10, setPokemonPool.pool.size() * 3);
            while (availableRentals.size() < needed && attempts < maxAttempts) {
                ++attempts;
                PokemonPreset preset = setPokemonPool.getRandomPokemon();
                if (preset == null) break;
                boolean duplicate = false;
                for (PokemonPreset p : rentalPresets) {
                    if (p.uuid().equals(preset.uuid())) { duplicate = true; break; }
                }
                if (duplicate) continue;
                rentalPresets.add(preset);
                Pokemon pokemon = preset.getPokemon();
                if (pokemon == null) {
                    ++failedBuilds;
                    this.bf.logError("[BattleFactory] Preset '" + preset.name() + "' failed to build a Pokemon (unknown species or invalid data). Skipping.");
                    continue;
                }
                me.plascmabue.cobblemonbattlefactory.rct.RCTBattleHelper.stripGimmicks(pokemon);
                this.bf.logInfo("Adding " + pokemon.getSpecies().getName() + " to available rentals!");
                availableRentals.add(pokemon);
            }
            if (availableRentals.size() < needed) {
                this.bf.logError("[BattleFactory] Only " + availableRentals.size() + "/" + needed
                        + " Pokémon could be built from pool '" + setPokemonPool.name + "' after " + attempts
                        + " attempts (" + failedBuilds + " failed builds, pool size " + setPokemonPool.pool.size() + ").");
            }
        } else if (pool instanceof RandomPokemonPool) {
            RandomPokemonPool randomPokemonPool = (RandomPokemonPool)pool;
            for (int i = 0; i < this.bf.config().numberOfPokemonAvailable; ++i) {
                Pokemon pokemon = randomPokemonPool.createRandomPokemon();
                if (pokemon == null) {
                    this.bf.logError("[BattleFactory] Random Pokemon is null");
                    return 0;
                }
                me.plascmabue.cobblemonbattlefactory.rct.RCTBattleHelper.stripGimmicks(pokemon);
                this.bf.logInfo("Adding " + pokemon.getSpecies().getName() + " to available rentals!");
                availableRentals.add(pokemon);
            }
        }
        if (availableRentals.size() < this.bf.config().numberOfPokemonAvailable) {
            player.sendSystemMessage(TextUtils.deserialize(TextUtils.parse(
                    "%prefix% <red>Pool insuffisant : seulement " + availableRentals.size() + "/"
                            + this.bf.config().numberOfPokemonAvailable + " Pokémon disponibles (voir logs serveur).")));
            return 0;
        }
        ArrayList<Integer> selectedRentals = new ArrayList<Integer>();
        this.openRentalSelectionGUI(player, availableRentals, selectedRentals, tierSettings);
        return 1;
    }

    public void openRentalSelectionGUI(ServerPlayer player, List<Pokemon> availableRentals, List<Integer> selectedRentals, TierSettings tierSettings) {
        try {
            ItemStack item;
            if (this.bf.getBattleFactoryInstance(player) != null) {
                player.sendSystemMessage(TextUtils.deserialize(TextUtils.parse(this.bf.messagesConfig().getMessage("error_alreadyInBattleFactory"), player)));
                return;
            }
            AtomicBoolean selectionCompleted = new AtomicBoolean(false);
            SimpleGui rentalSelectionGUI = new SimpleGui(this.bf.rentalSelectionGUIConfig().getScreenSize(), player, false){

                public void onClose() {
                    this.player.removeEffect(MobEffects.DARKNESS);
                    if (!selectionCompleted.get()) {
                        int seconds = BattleFactoryCommands.this.bf.config().cancelCooldownSeconds;
                        if (seconds > 0) {
                            long ticks = (long)seconds * 20L;
                            BattleFactoryCommands.this.bf.playerCooldowns.put(this.player.getUUID(), ticks);
                            PlayerData data = BattleFactoryCommands.this.bf.getPlayerData(this.player);
                            if (data != null) {
                                data.cooldownProgress = ticks;
                                PlayerDataManager.savePlayerData(this.player);
                            }
                            int mins = seconds / 60;
                            int secs = seconds % 60;
                            String label = mins > 0 ? (mins + " min" + (secs > 0 ? " " + secs + " s" : "")) : (secs + " s");
                            this.player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                    "§c[BattleFactory] Sélection annulée — cooldown de §e" + label
                                            + " §cavant de pouvoir relancer §e/bf start§c."));
                        }
                    }
                    super.onClose();
                }

                public void onOpen() {
                    this.player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 9999));
                    super.onOpen();
                }
            };
            rentalSelectionGUI.setTitle(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().title, player)));
            for (Integer slot : this.bf.rentalSelectionGUIConfig().getSlotsBySymbol(this.bf.rentalSelectionGUIConfig().backgroundItemSymbol)) {
                ItemStack item2 = ((Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)this.bf.rentalSelectionGUIConfig().backgroundItem))).getDefaultInstance();
                item2.applyComponents(this.bf.rentalSelectionGUIConfig().backgroundItemData);
                rentalSelectionGUI.setSlot(slot.intValue(), (GuiElementInterface)new GuiElementBuilder(item2).setName(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().backgroundItemName, player))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().backgroundItemLore, player)).build());
            }
            List<Integer> rentalSlots = this.bf.rentalSelectionGUIConfig().getSlotsBySymbol(this.bf.rentalSelectionGUIConfig().rentalItemSymbol);
            for (int i = 0; i < rentalSlots.size(); ++i) {
                int finalI = i;
                if (i < availableRentals.size()) {
                    ItemStack rentalItem = PokemonItem.from((Pokemon)availableRentals.get(i));
                    rentalItem.applyComponents(DataComponentMap.builder().set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(this.bf.rentalSelectionGUIConfig().enchantRentalItemOnSelect && !selectedRentals.isEmpty() && selectedRentals.contains(i) ? 1 : 0)).build());
                    rentalItem.applyComponents(this.bf.rentalSelectionGUIConfig().rentalItemData);
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
                    ItemStack selectedItem = ((Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)this.bf.rentalSelectionGUIConfig().selectedItem))).getDefaultInstance();
                    selectedItem.applyComponents(this.bf.rentalSelectionGUIConfig().selectedItemData);
                    if (this.bf.rentalSelectionGUIConfig().showSelectedItemAbove && rentalSlots.get(i) - 9 >= 0) {
                        rentalSelectionGUI.setSlot(rentalSlots.get(i) - 9, (GuiElementInterface)new GuiElementBuilder(selectedItem).setName(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().selectedItemName, player))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().selectedItemLore, player)).build());
                    }
                    if (!this.bf.rentalSelectionGUIConfig().showSelectedItemBelow || rentalSlots.get(i) + 9 >= this.bf.rentalSelectionGUIConfig().rows * 9) continue;
                    rentalSelectionGUI.setSlot(rentalSlots.get(i) + 9, (GuiElementInterface)new GuiElementBuilder(selectedItem).setName(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().selectedItemName, player))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().selectedItemLore, player)).build());
                }
            }
            for (Integer slot : this.bf.rentalSelectionGUIConfig().getSlotsBySymbol(this.bf.rentalSelectionGUIConfig().cancelItemSymbol)) {
                item = ((Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)this.bf.rentalSelectionGUIConfig().cancelItem))).getDefaultInstance();
                item.applyComponents(this.bf.rentalSelectionGUIConfig().cancelItemData);
                rentalSelectionGUI.setSlot(slot.intValue(), (GuiElementInterface)((GuiElementBuilder)new GuiElementBuilder(item).setName(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().cancelItemName, player))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().cancelItemLore, player)).setCallback(clickType -> rentalSelectionGUI.close())).build());
            }
            for (Integer slot : this.bf.rentalSelectionGUIConfig().getSlotsBySymbol(this.bf.rentalSelectionGUIConfig().startItemSymbol)) {
                item = ((Item)BuiltInRegistries.ITEM.get(ResourceLocation.parse((String)this.bf.rentalSelectionGUIConfig().startItem))).getDefaultInstance();
                item.applyComponents(this.bf.rentalSelectionGUIConfig().startItemData);
                rentalSelectionGUI.setSlot(slot.intValue(), (GuiElementInterface)((GuiElementBuilder)new GuiElementBuilder(item).setName(TextUtils.deserialize(TextUtils.parse(this.bf.rentalSelectionGUIConfig().startItemName, player))).setLore(TextUtils.getLoreArray(this.bf.rentalSelectionGUIConfig().startItemLore, player)).setCallback(clickType -> {
                    if (selectedRentals.size() == this.bf.config().numberOfPokemonRented) {
                        selectionCompleted.set(true);
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

