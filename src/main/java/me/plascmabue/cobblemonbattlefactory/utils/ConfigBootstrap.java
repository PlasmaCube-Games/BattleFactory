package me.plascmabue.cobblemonbattlefactory.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import net.fabricmc.loader.api.FabricLoader;

import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Copies the default configs bundled inside the mod jar (under
 * {@code /BattleFactoryConfigs/}) into the runtime config directory
 * ({@code config/BattleFactory/}) the first time the server starts,
 * so admins get a working tier/pool/messages setup out of the box.
 *
 * A file is copied only if it does not already exist — existing player
 * edits are never overwritten.
 */
public final class ConfigBootstrap {

    private static final List<String> ROOT_FILES = List.of(
            "config.json",
            "messages.json",
            "pokemon_presets.json",
            "rental_pools.json",
            "reward_pool_presets.json",
            "reward_presets.json"
    );

    private static final List<String> GUI_FILES = List.of(
            "rental_selection_gui.json",
            "select_npc_pokemon_gui.json",
            "select_player_pokemon_gui.json",
            "tier_selection_gui.json"
    );

    /** Bumped when the bundled config.json structure changes and old runtime configs must be regenerated. */
    public static final int BUNDLED_CONFIG_VERSION = 3;

    private ConfigBootstrap() {}

    public static void ensureDefaults() {
        Path root = FabricLoader.getInstance().getConfigDir().resolve("BattleFactory");
        try {
            Files.createDirectories(root);
            Files.createDirectories(root.resolve("guis"));
        } catch (Exception e) {
            BattleFactory.LOGGER.error("[BattleFactory] Failed to create config dir: " + e.getMessage());
            return;
        }

        // Migrate config.json specifically: if runtime's config_version < bundled, backup + overwrite.
        migrateConfigJsonIfStale(root.resolve("config.json"));

        for (String name : ROOT_FILES) {
            copyIfMissing("/BattleFactoryConfigs/" + name, root.resolve(name));
        }
        for (String name : GUI_FILES) {
            copyIfMissing("/BattleFactoryConfigs/guis/" + name, root.resolve("guis").resolve(name));
        }
    }

    private static void migrateConfigJsonIfStale(Path target) {
        if (!Files.exists(target)) return;
        int runtimeVersion = 0;
        try (Reader r = Files.newBufferedReader(target)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
            if (root.has("config_version") && root.get("config_version").isJsonPrimitive()) {
                runtimeVersion = root.get("config_version").getAsInt();
            }
        } catch (Exception e) {
            BattleFactory.LOGGER.warn("[BattleFactory] Could not read config_version from existing config.json ({}). Assuming 0 → migrating.", e.getMessage());
        }
        if (runtimeVersion >= BUNDLED_CONFIG_VERSION) return;
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        Path backup = target.resolveSibling("config.json.bak-v" + runtimeVersion + "-" + stamp);
        try {
            Files.copy(target, backup, StandardCopyOption.REPLACE_EXISTING);
            BattleFactory.LOGGER.warn("[BattleFactory] Migrating config.json from v{} to v{}. Old config backed up to {}",
                    runtimeVersion, BUNDLED_CONFIG_VERSION, backup.getFileName());
        } catch (Exception e) {
            BattleFactory.LOGGER.error("[BattleFactory] Failed to backup config.json before migration: " + e.getMessage());
            return;
        }
        try {
            Files.delete(target);
        } catch (Exception e) {
            BattleFactory.LOGGER.error("[BattleFactory] Failed to delete old config.json for migration: " + e.getMessage());
            return;
        }
        // copyIfMissing below will now repopulate it from the bundled resource.
        BattleFactory.LOGGER.warn("[BattleFactory] config.json deleted — will be regenerated from bundled defaults. Restore battle_locations and any custom tuning from {}", backup.getFileName());
    }

    private static void copyIfMissing(String resourcePath, Path target) {
        if (Files.exists(target)) {
            return;
        }
        try (InputStream in = ConfigBootstrap.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                return;
            }
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            BattleFactory.LOGGER.info("[BattleFactory] Copied default {}", target.getFileName());
        } catch (Exception e) {
            BattleFactory.LOGGER.error("[BattleFactory] Failed to copy default {}: {}", resourcePath, e.getMessage());
        }
    }
}
