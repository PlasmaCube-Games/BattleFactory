package me.plascmabue.cobblemonbattlefactory.rct;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.gitlab.srcmc.rctapi.api.RCTApi;
import com.gitlab.srcmc.rctapi.api.ai.RCTBattleAI;
import com.gitlab.srcmc.rctapi.api.battle.BattleFormat;
import com.gitlab.srcmc.rctapi.api.battle.BattleRules;
import com.gitlab.srcmc.rctapi.api.trainer.Trainer;
import com.gitlab.srcmc.rctapi.api.trainer.TrainerBag;
import com.gitlab.srcmc.rctapi.api.trainer.TrainerNPC;
import com.gitlab.srcmc.rctapi.api.trainer.TrainerPlayer;
import com.gitlab.srcmc.rctapi.api.util.Text;
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import me.plascmabue.cobblemonbattlefactory.datatypes.TierSettings;
import me.plascmabue.cobblemonbattlefactory.datatypes.rentalPools.PokemonPool;
import me.plascmabue.cobblemonbattlefactory.datatypes.rentalPools.PokemonPreset;
import me.plascmabue.cobblemonbattlefactory.datatypes.rentalPools.SetPokemonPool;
import net.minecraft.server.level.ServerPlayer;
import com.cobblemon.mod.common.entity.npc.NPCEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Bridges BattleFactory's tier/rental system with rctapi's trainer-based battle engine.
 * Returns the battle UUID on success (null on failure).
 */
public final class RCTBattleHelper {
    private RCTBattleHelper() {}

    /** TrainerPlayer override that returns the player's rental team instead of their real party. */
    public static final class BFTrainerPlayer extends TrainerPlayer {
        private final Pokemon[] rentals;
        public BFTrainerPlayer(ServerPlayer player, Pokemon[] rentals) {
            super(player);
            this.rentals = rentals;
        }
        @Override
        public Pokemon[] getTeam() { return rentals; }
    }

    /**
     * Builds NPC team from the tier's rental pool (same pokemon as player rentals — moves match).
     * Returns null on pool failure.
     */
    public static Pokemon[] buildNpcTeam(TierSettings tier, int desiredCount) {
        PokemonPool pool = tier.getPokemonPool();
        if (!(pool instanceof SetPokemonPool setPool)) return null;
        List<Pokemon> out = new ArrayList<>();
        List<UUID> usedPresets = new ArrayList<>();
        int attempts = 0;
        int maxAttempts = Math.max(desiredCount * 10, setPool.pool.size() * 3);
        while (out.size() < desiredCount && attempts < maxAttempts) {
            attempts++;
            PokemonPreset preset = setPool.getRandomPokemon();
            if (preset == null) break;
            if (usedPresets.contains(preset.uuid())) continue;
            usedPresets.add(preset.uuid());
            Pokemon p = preset.getPokemon();
            if (p == null) continue;
            out.add(p);
        }
        if (out.isEmpty()) return null;
        return out.toArray(new Pokemon[0]);
    }

    /**
     * Start a single 1v1 battle between player (using rentals) and an NPC trainer with RCTBattleAI.
     * Returns the battle UUID captured from the trainer NPC's battle instances, or null on failure.
     */
    public static UUID startRentalBattle(ServerPlayer player, Pokemon[] rentals,
                                         NPCEntity npcEntity, Pokemon[] npcTeam,
                                         String npcDisplayName) {
        try {
            RCTApi rct = BattleFactory.INSTANCE.rct();
            if (rct == null) {
                BattleFactory.LOGGER.error("[BattleFactory] RCTApi not initialized.");
                return null;
            }
            BFTrainerPlayer playerTrainer = new BFTrainerPlayer(player, rentals);
            TrainerNPC npcTrainer = new TrainerNPC(
                    Text.literal(npcDisplayName != null ? npcDisplayName : "Trainer"),
                    npcTeam,
                    new TrainerBag(),
                    new RCTBattleAI(),
                    npcEntity
            );
            UUID battleId = rct.getBattleManager().startBattle(
                    List.of((Trainer) playerTrainer),
                    List.of((Trainer) npcTrainer),
                    BattleFormat.GEN_9_SINGLES,
                    new BattleRules()
            );
            BattleFactory.LOGGER.info("[BattleFactory] RCT battle started: battleId={} player={} npc={}",
                    battleId, player.getScoreboardName(), npcDisplayName);
            return battleId;
        } catch (Throwable t) {
            BattleFactory.LOGGER.error("[BattleFactory] RCT startRentalBattle failed", t);
            return null;
        }
    }
}
