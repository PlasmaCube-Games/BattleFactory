/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ModInitializer
 *  net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
 *  net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
 *  net.kyori.adventure.platform.fabric.FabricServerAudiences
 *  net.minecraft.class_3222
 *  net.minecraft.server.MinecraftServer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package me.unariginal.cobblemonbattlefactory;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.unariginal.cobblemonbattlefactory.commands.BattleFactoryCommands;
import me.unariginal.cobblemonbattlefactory.config.Config;
import me.unariginal.cobblemonbattlefactory.config.PokemonPresetsConfig;
import me.unariginal.cobblemonbattlefactory.config.RentalPoolsConfig;
import me.unariginal.cobblemonbattlefactory.config.messages.MessagesConfig;
import me.unariginal.cobblemonbattlefactory.config.messages.RentalSelectionGUIConfig;
import me.unariginal.cobblemonbattlefactory.config.messages.SelectNPCPokemonGUIConfig;
import me.unariginal.cobblemonbattlefactory.config.messages.SelectPlayerPokemonGUIConfig;
import me.unariginal.cobblemonbattlefactory.config.messages.TierSelectionGUIConfig;
import me.unariginal.cobblemonbattlefactory.config.playerdata.LeaderboardManager;
import me.unariginal.cobblemonbattlefactory.config.playerdata.PlayerDataManager;
import me.unariginal.cobblemonbattlefactory.config.rewards.RewardPoolPresetsConfig;
import me.unariginal.cobblemonbattlefactory.config.rewards.RewardPresetsConfig;
import me.unariginal.cobblemonbattlefactory.datatypes.LeaderboardSection;
import me.unariginal.cobblemonbattlefactory.datatypes.PlayerData;
import me.unariginal.cobblemonbattlefactory.managers.BattleFactoryInstance;
import me.unariginal.cobblemonbattlefactory.managers.EventManager;
import me.unariginal.cobblemonbattlefactory.managers.TickManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BattleFactory
implements ModInitializer {
    private static final String MOD_ID = "cobblemonbattlefactory";
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"cobblemonbattlefactory");
    public static boolean DEBUG = false;
    public static BattleFactory INSTANCE;
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
    public Map<class_3222, Long> removeAfterTicks = new HashMap<class_3222, Long>();
    private final Map<UUID, PlayerData> playerData = new HashMap<UUID, PlayerData>();
    public final Map<UUID, Long> playerCooldowns = new HashMap<UUID, Long>();

    public void onInitialize() {
        INSTANCE = this;
        new BattleFactoryCommands();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            this.audience = FabricServerAudiences.of((MinecraftServer)server);
            this.reload(false);
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
            class_3222 player = handler.method_32311();
            if (this.playerCooldowns.containsKey(player.method_5667())) {
                PlayerDataManager.saveCooldown(player.method_5667(), this.playerCooldowns.get(player.method_5667()));
            }
            PlayerData data = PlayerDataManager.loadPlayerData(player);
            this.updatePlayerData(player, data);
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            class_3222 player = handler.method_32311();
            this.stopBattleFactoryInstance(player);
            PlayerDataManager.savePlayerData(player);
            this.playerData.remove(player.method_5667());
            if (!this.config.tickOfflinePlayerCooldowns) {
                this.playerCooldowns.remove(player.method_5667());
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
            this.playerData.get((Object)uuid).cooldownProgress = this.playerCooldowns.get(uuid);
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

    public PlayerData getPlayerData(class_3222 player) {
        if (this.playerData.containsKey(player.method_5667())) {
            return this.playerData.get(player.method_5667());
        }
        return null;
    }

    public Map<UUID, PlayerData> getPlayerData() {
        return this.playerData;
    }

    public void updatePlayerData(class_3222 player, PlayerData data) {
        this.playerData.put(player.method_5667(), data);
    }

    public BattleFactoryInstance getBattleFactoryInstance(class_3222 player) {
        for (BattleFactoryInstance battleFactoryInstance : this.battleFactoryInstances) {
            if (!battleFactoryInstance.challenger.method_5667().equals(player.method_5667())) continue;
            return battleFactoryInstance;
        }
        return null;
    }

    public boolean stopBattleFactoryInstance(class_3222 player) {
        boolean removed = false;
        ArrayList<BattleFactoryInstance> toRemove = new ArrayList<BattleFactoryInstance>();
        for (BattleFactoryInstance battleFactoryInstance : this.battleFactoryInstances) {
            if (!battleFactoryInstance.challenger.method_5667().equals(player.method_5667())) continue;
            toRemove.add(battleFactoryInstance);
        }
        for (BattleFactoryInstance battleFactoryInstance : toRemove) {
            battleFactoryInstance.stopInstance();
            this.battleFactoryInstances.remove(battleFactoryInstance);
            removed = true;
        }
        return removed;
    }
}

