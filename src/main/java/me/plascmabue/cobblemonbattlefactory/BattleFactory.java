
/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ModInitializer
 *  net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
 *  net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
 *  net.kyori.adventure.platform.fabric.FabricServerAudiences
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.MinecraftServer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package me.plascmabue.cobblemonbattlefactory;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.plascmabue.cobblemonbattlefactory.commands.BattleFactoryCommands;
import me.plascmabue.cobblemonbattlefactory.config.Config;
import me.plascmabue.cobblemonbattlefactory.config.PokemonPresetsConfig;
import me.plascmabue.cobblemonbattlefactory.config.RentalPoolsConfig;
import me.plascmabue.cobblemonbattlefactory.config.messages.MessagesConfig;
import me.plascmabue.cobblemonbattlefactory.config.messages.RentalSelectionGUIConfig;
import me.plascmabue.cobblemonbattlefactory.config.messages.SelectNPCPokemonGUIConfig;
import me.plascmabue.cobblemonbattlefactory.config.messages.SelectPlayerPokemonGUIConfig;
import me.plascmabue.cobblemonbattlefactory.config.messages.TierSelectionGUIConfig;
import me.plascmabue.cobblemonbattlefactory.config.playerdata.LeaderboardManager;
import me.plascmabue.cobblemonbattlefactory.config.playerdata.PlayerDataManager;
import me.plascmabue.cobblemonbattlefactory.config.rewards.RewardPoolPresetsConfig;
import me.plascmabue.cobblemonbattlefactory.config.rewards.RewardPresetsConfig;
import me.plascmabue.cobblemonbattlefactory.datatypes.LeaderboardSection;
import me.plascmabue.cobblemonbattlefactory.datatypes.PlayerData;
import me.plascmabue.cobblemonbattlefactory.managers.BattleFactoryInstance;
import me.plascmabue.cobblemonbattlefactory.managers.EventManager;
import me.plascmabue.cobblemonbattlefactory.managers.TickManager;
import me.plascmabue.cobblemonbattlefactory.network.BattleFactoryHudPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BattleFactory
implements ModInitializer {
    public static final String MOD_ID = "cobblemonbattlefactory";
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"cobblemonbattlefactory");
    public static boolean DEBUG = false;
    public static BattleFactory INSTANCE;
    private com.gitlab.srcmc.rctapi.api.RCTApi rct;
    private MinecraftServer server;
    private FabricServerAudiences audience;
    private Config config;
    private RewardPresetsConfig rewardPresetsConfig;
    private RewardPoolPresetsConfig rewardPoolPresetsConfig;
    private PokemonPresetsConfig pokemonPresetsConfig;
    private RentalPoolsConfig rentalPoolsConfig;
    private MessagesConfig messagesConfig;
    private RentalSelectionGUIConfig rentalSelectionGUIConfig;
    private TierSelectionGUIConfig tierSelectionGUIConfig;
    private SelectNPCPokemonGUIConfig selectNPCPokemonGUIConfig;
    private SelectPlayerPokemonGUIConfig selectPlayerPokemonGUIConfig;
    public List<BattleFactoryInstance> battleFactoryInstances = new ArrayList<BattleFactoryInstance>();
    public Map<ServerPlayer, Long> removeAfterTicks = new HashMap<ServerPlayer, Long>();
    private final Map<UUID, PlayerData> playerData = new HashMap<UUID, PlayerData>();
    public final Map<UUID, Long> playerCooldowns = new HashMap<UUID, Long>();

    public void onInitialize() {
        INSTANCE = this;
        this.rct = com.gitlab.srcmc.rctapi.api.RCTApi.initInstance(MOD_ID);
        LOGGER.info("[BattleFactory] RCTApi instance initialized for mod id {}", MOD_ID);
        PayloadTypeRegistry.playS2C().register(BattleFactoryHudPayload.TYPE, BattleFactoryHudPayload.STREAM_CODEC);
        new BattleFactoryCommands();
        new me.plascmabue.cobblemonbattlefactory.commands.SetLocationCommand();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            this.audience = FabricServerAudiences.of((MinecraftServer)server);
            this.rct.getTrainerRegistry().init(server);
            LOGGER.info("[BattleFactory] RCT TrainerRegistry initialized.");
            me.plascmabue.cobblemonbattlefactory.utils.ConfigBootstrap.ensureDefaults();
            this.reload(false);
            LOGGER.info("[BattleFactory] Loaded {} tier(s) from config", this.config != null ? this.config.tiers.size() : 0);
            EventManager.registerBattleEvents();
            EventManager.registerRightClickEvents();
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            try {
                TickManager.tickTimers();
                TickManager.preventPlayerTeleport();
                TickManager.tickCooldowns();
            }
            catch (ConcurrentModificationException e) {
                this.logError("[BattleFactory] Suppressing ConcurrentModificationException!");
            }
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            if (this.playerCooldowns.containsKey(player.getUUID())) {
                PlayerDataManager.saveCooldown(player.getUUID(), this.playerCooldowns.get(player.getUUID()));
            }
            PlayerData data = PlayerDataManager.loadPlayerData(player);
            this.updatePlayerData(player, data);
            try {
                this.rct.getTrainerRegistry().registerPlayer(player.getUUID().toString(), player);
            } catch (Exception ex) {
                LOGGER.warn("[BattleFactory] Failed to register RCT player trainer for {}: {}", player.getScoreboardName(), ex.getMessage());
            }
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayer player = handler.getPlayer();
            this.stopBattleFactoryInstance(player);
            PlayerDataManager.savePlayerData(player);
            this.playerData.remove(player.getUUID());
            if (!this.config.tickOfflinePlayerCooldowns) {
                this.playerCooldowns.remove(player.getUUID());
            }
            try {
                this.rct.getTrainerRegistry().unregisterById(player.getUUID().toString());
            } catch (Exception ex) {
                LOGGER.warn("[BattleFactory] Failed to unregister RCT player trainer for {}: {}", player.getScoreboardName(), ex.getMessage());
            }
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            for (BattleFactoryInstance battleFactoryInstance : this.battleFactoryInstances) {
                battleFactoryInstance.stopInstance();
            }
            PlayerDataManager.saveCooldowns();
            PlayerDataManager.saveAllPlayerData();
            LeaderboardManager.leaderboard = LeaderboardManager.sortLeaderboard(new ArrayList<LeaderboardSection>(LeaderboardManager.leaderboard));
            LeaderboardManager.saveLeaderboard();
        });
    }

    public void reload(boolean fromCommand) {
        this.messagesConfig = new MessagesConfig();
        this.rentalSelectionGUIConfig = new RentalSelectionGUIConfig();
        this.tierSelectionGUIConfig = new TierSelectionGUIConfig();
        this.selectNPCPokemonGUIConfig = new SelectNPCPokemonGUIConfig();
        this.selectPlayerPokemonGUIConfig = new SelectPlayerPokemonGUIConfig();
        this.rewardPresetsConfig = new RewardPresetsConfig();
        this.rewardPoolPresetsConfig = new RewardPoolPresetsConfig();
        this.pokemonPresetsConfig = new PokemonPresetsConfig();
        this.rentalPoolsConfig = new RentalPoolsConfig();
        this.config = new Config();
        if (fromCommand) {
            PlayerDataManager.saveCooldowns();
            PlayerDataManager.saveAllPlayerData();
            LeaderboardManager.saveLeaderboard();
        }
        this.playerCooldowns.clear();
        this.playerCooldowns.putAll(PlayerDataManager.loadCooldowns());
        for (PlayerData data : this.playerData.values()) {
            if (data.cooldownProgress <= 0L) continue;
            this.playerCooldowns.put(data.uuid, data.cooldownProgress);
        }
        LeaderboardManager.loadLeaderboard();
        LeaderboardManager.leaderboard = LeaderboardManager.sortLeaderboard(new ArrayList<LeaderboardSection>(LeaderboardManager.leaderboard));
    }

    public Config config() {
        return this.config;
    }

    public RewardPresetsConfig rewardPresetsConfig() {
        return this.rewardPresetsConfig;
    }

    public RewardPoolPresetsConfig rewardPoolPresetsConfig() {
        return this.rewardPoolPresetsConfig;
    }

    public PokemonPresetsConfig pokemonPresetsConfig() {
        return this.pokemonPresetsConfig;
    }

    public RentalPoolsConfig rentalPoolsConfig() {
        return this.rentalPoolsConfig;
    }

    public MessagesConfig messagesConfig() {
        return this.messagesConfig;
    }

    public RentalSelectionGUIConfig rentalSelectionGUIConfig() {
        return this.rentalSelectionGUIConfig;
    }

    public TierSelectionGUIConfig tierSelectionGUIConfig() {
        return this.tierSelectionGUIConfig;
    }

    public SelectNPCPokemonGUIConfig selectNPCPokemonGUIConfig() {
        return this.selectNPCPokemonGUIConfig;
    }

    public SelectPlayerPokemonGUIConfig selectPlayerPokemonGUIConfig() {
        return this.selectPlayerPokemonGUIConfig;
    }

    public void tickCooldown(UUID uuid) {
        this.playerCooldowns.put(uuid, Math.max(this.playerCooldowns.get(uuid) - 1L, 0L));
        if (this.playerData.containsKey(uuid)) {
            this.playerData.get(uuid).cooldownProgress = this.playerCooldowns.get(uuid);
        }
    }

    public void clearCooldowns() {
        ArrayList<UUID> toRemove = new ArrayList<UUID>();
        for (Map.Entry<UUID, Long> entry : this.playerCooldowns.entrySet()) {
            if (entry.getValue() > 0L) continue;
            toRemove.add(entry.getKey());
        }
        for (UUID uuid : toRemove) {
            PlayerDataManager.saveCooldown(uuid, 0L);
            this.playerCooldowns.remove(uuid);
        }
    }

    public MinecraftServer server() {
        return this.server;
    }

    public com.gitlab.srcmc.rctapi.api.RCTApi rct() {
        return this.rct;
    }

    public FabricServerAudiences audience() {
        return this.audience;
    }

    public void logInfo(String msg) {
        if (DEBUG) {
            LOGGER.info(msg);
        }
    }

    public void logError(String msg) {
        LOGGER.error(msg);
    }

    public PlayerData getPlayerData(ServerPlayer player) {
        if (this.playerData.containsKey(player.getUUID())) {
            return this.playerData.get(player.getUUID());
        }
        return null;
    }

    public Map<UUID, PlayerData> getPlayerData() {
        return this.playerData;
    }

    public void updatePlayerData(ServerPlayer player, PlayerData data) {
        this.playerData.put(player.getUUID(), data);
    }

    public BattleFactoryInstance getBattleFactoryInstance(ServerPlayer player) {
        for (BattleFactoryInstance battleFactoryInstance : this.battleFactoryInstances) {
            if (!battleFactoryInstance.challenger.getUUID().equals(player.getUUID())) continue;
            return battleFactoryInstance;
        }
        return null;
    }

    public boolean stopBattleFactoryInstance(ServerPlayer player) {
        boolean removed = false;
        ArrayList<BattleFactoryInstance> toRemove = new ArrayList<BattleFactoryInstance>();
        for (BattleFactoryInstance battleFactoryInstance : this.battleFactoryInstances) {
            if (!battleFactoryInstance.challenger.getUUID().equals(player.getUUID())) continue;
            toRemove.add(battleFactoryInstance);
        }
        for (BattleFactoryInstance battleFactoryInstance : toRemove) {
            battleFactoryInstance.stopInstance();
            this.battleFactoryInstances.remove(battleFactoryInstance);
            removed = true;
        }
        if (removed) {
            sendHudUpdate(player, new BattleFactoryHudPayload(false, "", 0, 0, 0));
        }
        return removed;
    }

    public void sendHudUpdate(ServerPlayer player, BattleFactoryHudPayload payload) {
        if (player == null || player.connection == null) return;
        try {
            ServerPlayNetworking.send(player, payload);
        } catch (Throwable t) {
            // Client does not have the payload registered — silently ignore.
        }
    }
}

