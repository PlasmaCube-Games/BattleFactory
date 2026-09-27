package me.plascmabue.cobblemonbattlefactory.rct;

import com.cobblemon.mod.common.battles.ActiveBattlePokemon;
import com.cobblemon.mod.common.battles.BattleSide;
import com.cobblemon.mod.common.battles.DefaultActionResponse;
import com.cobblemon.mod.common.battles.PassActionResponse;
import com.cobblemon.mod.common.battles.ShowdownActionResponse;
import com.cobblemon.mod.common.battles.ShowdownMoveset;
import com.cobblemon.mod.common.battles.SwitchActionResponse;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
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
     * RCTBattleAI subclass that force-clears all gimmick fields on the moveset before rctapi's logic
     * runs. RCTBattleAI's {@code choose} reads canDynamax / canTerastallize / etc and may toggle them
     * via setters; clearing here guarantees the AI never selects a gimmick action regardless of what
     * Showdown sent or what the GimmicksMap says.
     */
    public static final class BfNoGimmickAI extends RCTBattleAI {
        // NB: le 5e paramètre est forceSwitch (Pokémon K.O. à remplacer), PAS isMega.
        @Override
        public ShowdownActionResponse choose(ActiveBattlePokemon active, PokemonBattle battle,
                                             BattleSide side, ShowdownMoveset moveset, boolean forceSwitch) {
            if (moveset != null) {
                try {
                    moveset.setCanDynamax(false);
                    moveset.setCanMegaEvo(false);
                    moveset.setCanUltraBurst(false);
                    moveset.setCanZMove(null);
                    moveset.setCanTerastallize(null);
                    moveset.setMaxMoves(null);
                } catch (Throwable t) {
                    BattleFactory.LOGGER.warn("[BattleFactory] BfNoGimmickAI clear failed: {}", t.getMessage());
                }
            }
            if (forceSwitch) {
                BattleFactory.LOGGER.info("[BattleFactory] IA : switch forcé demandé (Pokémon K.O. à remplacer) — choix en cours.");
            }
            try {
                ShowdownActionResponse resp = super.choose(active, battle, side, moveset, forceSwitch);
                if (forceSwitch) {
                    BattleFactory.LOGGER.info("[BattleFactory] IA : remplaçant choisi = {}.", resp);
                }
                // Filet « NPC qui n'attaque pas » : rctapi renvoie PassActionResponse quand aucun coup n'est
                // jugé jouable (moveset.moves filtré par canBeUsed vide). Sur un tour normal avec un Pokémon
                // vivant, passer = rester planté. On force DefaultActionResponse (Showdown choisit un coup) +
                // diagnostic sur l'état des coups.
                if (!forceSwitch && resp instanceof PassActionResponse && active != null && active.hasPokemon()) {
                    BattleFactory.LOGGER.warn("[BattleFactory] IA allait PASSER avec un Pokémon vivant (ne pas attaquer) — moves=[{}] → DefaultActionResponse forcé.",
                            describeMoves(moveset));
                    return new DefaultActionResponse();
                }
                return resp;
            } catch (Throwable t) {
                // Si l'IA plante en choisissant (typique d'un switch forcé après double K.O.), Cobblemon reste
                // bloqué : AIBattleActor.onChoiceRequested() n'attrape QUE IllegalActionChoiceException, donc
                // toute autre exception laisse l'acteur sans réponse → softlock. Réponse valide de repli.
                BattleFactory.LOGGER.error("[BattleFactory] BfNoGimmickAI.choose a échoué (forceSwitch=" + forceSwitch + ") — réponse de repli.", t);
                return fallbackResponse(active, moveset, forceSwitch);
            }
        }
    }

    /** Diagnostic : liste les coups du moveset avec canBeUsed/mustBeUsed (pour comprendre pourquoi rctapi
     *  ne propose aucun coup → NPC qui passe). */
    private static String describeMoves(ShowdownMoveset moveset) {
        if (moveset == null) return "moveset=null";
        try {
            StringBuilder sb = new StringBuilder();
            for (var m : moveset.moves) {
                if (m == null) continue;
                if (sb.length() > 0) sb.append(", ");
                sb.append(m.id).append("(usable=").append(m.canBeUsed()).append(",must=").append(m.mustBeUsed()).append(")");
            }
            return sb.length() == 0 ? "aucun coup" : sb.toString();
        } catch (Throwable t) {
            return "err:" + t.getMessage();
        }
    }

    /** Réponse de repli quand l'IA plante : switch vers le 1er Pokémon envoyable si un remplacement est
     *  exigé, sinon on passe. Garantit une réponse VALIDE pour que Cobblemon ne se bloque pas. */
    private static ShowdownActionResponse fallbackResponse(ActiveBattlePokemon active, ShowdownMoveset moveset,
                                                           boolean forceSwitch) {
        try {
            if (forceSwitch && active != null) {
                for (BattlePokemon bp : active.getActor().getPokemonList()) {
                    if (bp == null || !bp.canBeSentOut()) continue;
                    SwitchActionResponse sw = new SwitchActionResponse(bp.getUuid());
                    if (sw.isValid(active, moveset, true)) {
                        BattleFactory.LOGGER.warn("[BattleFactory] Repli IA : switch forcé vers {}.", bp.getName().getString());
                        return sw;
                    }
                }
            }
        } catch (Throwable ignored) {}
        return PassActionResponse.INSTANCE;
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
            stripGimmicks(p);
            out.add(p);
        }
        if (out.isEmpty()) return null;
        return out.toArray(new Pokemon[0]);
    }

    /**
     * Disable every gimmick at the data level: held item (mega stone / Z-crystal / ultra-burst item),
     * dmaxLevel + gmaxFactor (dynamax / gigantamax), and teraType reset to the Pokemon's primary type
     * so terastalization is a no-op even if the player still has a Tera Orb in keyItems.
     * Applied to both NPC pokemon (buildNpcTeam) and player rental pokemon (BattleFactoryCommands +
     * BattleFactoryInstance.openNewRentalGUI).
     */
    public static void stripGimmicks(Pokemon p) {
        try {
            p.setDmaxLevel(0);
            p.setGmaxFactor(false);
            p.setHeldItem$common(net.minecraft.world.item.ItemStack.EMPTY);
            try {
                var primaryType = p.getPrimaryType();
                var teraTypes = com.cobblemon.mod.common.api.types.tera.TeraTypes.INSTANCE;
                var teraForType = teraTypes.forElementalType(primaryType);
                if (teraForType != null) p.setTeraType(teraForType);
            } catch (Throwable teraEx) {
                BattleFactory.LOGGER.debug("[BattleFactory] stripGimmicks teraType reset failed: {}", teraEx.getMessage());
            }
        } catch (Throwable t) {
            BattleFactory.LOGGER.debug("[BattleFactory] stripGimmicks failed (non-fatal): {}", t.getMessage());
        }
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
            // Build a GimmicksMap that forbids tera/dynamax/gmax for every NPC pokemon.
            // GimmicksMap has no public setter — inject via reflection on its private 'map' field.
            com.gitlab.srcmc.rctapi.api.trainer.TrainerNPC.GimmicksMap noGimmicks =
                    new com.gitlab.srcmc.rctapi.api.trainer.TrainerNPC.GimmicksMap();
            try {
                java.lang.reflect.Field mapField = com.gitlab.srcmc.rctapi.api.trainer.TrainerNPC.GimmicksMap.class
                        .getDeclaredField("map");
                mapField.setAccessible(true);
                @SuppressWarnings("unchecked")
                java.util.Map<java.util.UUID, com.gitlab.srcmc.rctapi.api.models.Gimmicks> internalMap =
                        (java.util.Map<java.util.UUID, com.gitlab.srcmc.rctapi.api.models.Gimmicks>) mapField.get(noGimmicks);
                com.gitlab.srcmc.rctapi.api.models.Gimmicks disabled =
                        new com.gitlab.srcmc.rctapi.api.models.Gimmicks(null, false, false);
                for (Pokemon p : npcTeam) {
                    if (p == null) continue;
                    internalMap.put(p.getUuid(), disabled);
                }
            } catch (Throwable reflectEx) {
                BattleFactory.LOGGER.warn("[BattleFactory] Could not inject GimmicksMap (fallback = default gimmicks): {}",
                        reflectEx.getMessage());
            }
            TrainerNPC npcTrainer = new TrainerNPC(
                    Text.literal(npcDisplayName != null ? npcDisplayName : "Trainer"),
                    npcTeam,
                    noGimmicks,
                    new TrainerBag(),
                    new BfNoGimmickAI(),
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
            me.plascmabue.cobblemonbattlefactory.debug.BattleLog.log("========================================");
            me.plascmabue.cobblemonbattlefactory.debug.BattleLog.log(
                    "COMBAT START battleId={} joueur={} npc={} taille={}",
                    battleId, player.getScoreboardName(),
                    (npcDisplayName != null ? npcDisplayName : "Trainer"),
                    (npcTeam != null ? npcTeam.length : 0));
            return battleId;
        } catch (Throwable t) {
            BattleFactory.LOGGER.error("[BattleFactory] RCT startRentalBattle failed", t);
            return null;
        }
    }
}
