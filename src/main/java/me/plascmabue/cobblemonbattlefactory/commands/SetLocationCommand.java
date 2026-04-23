package me.plascmabue.cobblemonbattlefactory.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import me.plascmabue.cobblemonbattlefactory.datatypes.TierSettings;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

/**
 * Admin helper — capture battle locations by standing where you want things.
 *
 *   /bfsetpos <tier> <round> player   → saves player spawn (x/y/z + yaw/pitch)
 *   /bfsetpos <tier> <round> npc      → saves NPC spawn (x/y/z at current pos)
 *   /bfsetpos <tier> <round>          → both: player at current pos, NPC 3 blocks ahead
 *
 * All variants update config.json, create the tier/round entry if missing,
 * preserve the other half of the location (player if you ran "npc" and vice
 * versa), and trigger a config reload.
 */
public class SetLocationCommand {

    private enum Mode { BOTH, PLAYER, NPC }

    public SetLocationCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, access, env) -> {
            SuggestionProvider<CommandSourceStack> tierSuggest = (ctx, builder) -> {
                for (TierSettings t : BattleFactory.INSTANCE.config().tiers) {
                    builder.suggest(t.tierID());
                }
                return builder.buildFuture();
            };

            dispatcher.register(
                Commands.literal("bfsetpos")
                    .requires(src -> src.hasPermission(4))
                    .then(Commands.argument("tier", StringArgumentType.word())
                        .suggests(tierSuggest)
                        .then(Commands.argument("round", IntegerArgumentType.integer(1))
                            .executes(ctx -> execute(ctx, Mode.BOTH))
                            .then(Commands.literal("player").executes(ctx -> execute(ctx, Mode.PLAYER)))
                            .then(Commands.literal("npc").executes(ctx -> execute(ctx, Mode.NPC)))
                        ))
            );
        });
    }

    private int execute(CommandContext<CommandSourceStack> ctx, Mode mode) {
        CommandSourceStack src = ctx.getSource();
        ServerPlayer player = src.getPlayer();
        if (player == null) {
            src.sendFailure(Component.literal("Doit être exécuté par un joueur."));
            return 0;
        }

        String tierId = StringArgumentType.getString(ctx, "tier");
        int round = IntegerArgumentType.getInteger(ctx, "round");

        TierSettings tier = BattleFactory.INSTANCE.config().getTier(tierId);
        if (tier == null) {
            src.sendFailure(Component.literal("Tier inconnu : " + tierId));
            return 0;
        }

        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();
        float yaw = player.getYRot();
        float pitch = player.getXRot();
        ServerLevel level = player.serverLevel();
        String worldId = level.dimension().location().toString();

        Path configPath = FabricLoader.getInstance().getConfigDir()
                .resolve("BattleFactory").resolve("config.json");
        JsonObject root;
        try (FileReader reader = new FileReader(configPath.toFile())) {
            root = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            src.sendFailure(Component.literal("Lecture de config.json échouée : " + e.getMessage()));
            return 0;
        }

        JsonObject tiers = root.has("tiers") ? root.getAsJsonObject("tiers") : new JsonObject();
        if (!tiers.has(tierId)) {
            src.sendFailure(Component.literal("Tier '" + tierId + "' absent dans config.json"));
            return 0;
        }
        JsonObject tierObj = tiers.getAsJsonObject(tierId);
        JsonObject battleLocs = tierObj.has("battle_locations")
                ? tierObj.getAsJsonObject("battle_locations")
                : new JsonObject();

        String roundKey = String.valueOf(round);
        JsonObject loc = battleLocs.has(roundKey)
                ? battleLocs.getAsJsonObject(roundKey)
                : new JsonObject();
        loc.addProperty("world", worldId);

        if (mode == Mode.BOTH || mode == Mode.PLAYER) {
            JsonObject playerLoc = new JsonObject();
            playerLoc.addProperty("x", round1(px));
            playerLoc.addProperty("y", round1(py));
            playerLoc.addProperty("z", round1(pz));
            playerLoc.addProperty("yRot", yaw);
            playerLoc.addProperty("xRot", pitch);
            loc.add("player_location", playerLoc);
        }

        if (mode == Mode.BOTH || mode == Mode.NPC) {
            double nx, ny, nz;
            if (mode == Mode.BOTH) {
                double yawRad = Math.toRadians(yaw);
                nx = px - Math.sin(yawRad) * 3.0;
                ny = py;
                nz = pz + Math.cos(yawRad) * 3.0;
            } else {
                nx = px; ny = py; nz = pz;
            }
            JsonObject npcLoc = new JsonObject();
            npcLoc.addProperty("x", round1(nx));
            npcLoc.addProperty("y", round1(ny));
            npcLoc.addProperty("z", round1(nz));
            loc.add("npc_location", npcLoc);
        }

        battleLocs.add(roundKey, loc);
        tierObj.add("battle_locations", battleLocs);
        tiers.add(tierId, tierObj);
        root.add("tiers", tiers);

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        try (FileWriter writer = new FileWriter(configPath.toFile())) {
            writer.write(gson.toJson(root));
        } catch (Exception e) {
            src.sendFailure(Component.literal("Écriture de config.json échouée : " + e.getMessage()));
            return 0;
        }

        BattleFactory.INSTANCE.reload(true);

        String what = switch (mode) {
            case PLAYER -> "Position joueur";
            case NPC -> "Position NPC";
            case BOTH -> "Positions joueur + NPC";
        };
        src.sendSuccess(() -> Component.literal(
            what + " enregistrée pour " + tierId + " round " + round + " à ("
            + String.format("%.1f, %.1f, %.1f", px, py, pz) + "). Config rechargée."
        ), true);
        return 1;
    }

    private static double round1(double v) {
        return Math.floor(v * 10) / 10;
    }
}