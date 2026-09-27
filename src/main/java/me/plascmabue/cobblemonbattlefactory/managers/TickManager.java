/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.server.level.ServerPlayer
 */
package me.plascmabue.cobblemonbattlefactory.managers;

import com.cobblemon.mod.common.battles.BattleRegistry;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.UUID;
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import me.plascmabue.cobblemonbattlefactory.managers.BattleFactoryInstance;
import me.plascmabue.cobblemonbattlefactory.utils.TextUtils;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerPlayer;

public class
TickManager {
    // Maintenance counter for the periodic instance-count log / offline purge (see tickTimers).
    private static int maintenanceTick = 0;

    public static void tickTimers() throws ConcurrentModificationException {
        // Périodique (toutes les ~30 s) : on log le nombre d'instances actives et on purge celles
        // dont le joueur est hors-ligne. L'overlay tourne à chaque tick PAR instance ; une instance
        // « morte » qui traîne = coût CPU permanent (cf. crash watchdog max-tick-time).
        if (++maintenanceTick >= 600) {
            maintenanceTick = 0;
            int count = BattleFactory.INSTANCE.battleFactoryInstances.size();
            if (count > 0) {
                BattleFactory.LOGGER.info("[BattleFactory] Instances actives: {}", count);
            }
            if (count > 25) {
                BattleFactory.LOGGER.warn("[BattleFactory] Beaucoup d'instances BF actives ({}) — fuite possible ?", count);
            }
            ArrayList<ServerPlayer> offline = new ArrayList<ServerPlayer>();
            for (BattleFactoryInstance instance : BattleFactory.INSTANCE.battleFactoryInstances) {
                ServerPlayer challenger = instance.challenger;
                if (challenger == null || BattleFactory.INSTANCE.server().getPlayerList().getPlayer(challenger.getUUID()) == null) {
                    if (challenger != null) offline.add(challenger);
                }
            }
            for (ServerPlayer p : offline) {
                BattleFactory.LOGGER.warn("[BattleFactory] Purge instance orpheline (joueur hors-ligne): {}", p.getScoreboardName());
                BattleFactory.INSTANCE.stopBattleFactoryInstance(p);
            }
        }
        for (BattleFactoryInstance battleFactoryInstance : BattleFactory.INSTANCE.battleFactoryInstances) {
            ++battleFactoryInstance.instanceTimer;
            // Watchdog anti-softlock. Deux cas :
            //   (1) VRAIE égalité : Cobblemon n'a pas de handler pour |tie|, donc quand les DERNIERS Pokémon
            //       des deux camps tombent ensemble le combat finit sans vainqueur (disparu du registre OU
            //       getEnded()) et BATTLE_VICTORY ne fire jamais → on nettoie en défaite.
            //   (2) Double K.O. EN COURS de combat : l'IA doit renvoyer un Pokémon mais Cobblemon ne la relance
            //       pas toujours (onChoiceRequested pas appelé / choose qui plante) → l'acteur IA reste bloqué
            //       en mustChoose et le combat fige. On NE termine PAS le run : on relance l'IA puis on pousse
            //       le tour. checkForInputDispatch() est inerte tant qu'un acteur (ex. joueur qui réfléchit)
            //       doit encore choisir → aucun risque de couper un combat sain.
            if (!battleFactoryInstance.roundTransition && battleFactoryInstance.currentBattleID != null) {
                var b = BattleRegistry.getBattle(battleFactoryInstance.currentBattleID);
                if (b == null || b.getEnded()) {
                    battleFactoryInstance.aiStuckTicks = 0;
                    battleFactoryInstance.lastSeenTurn = null;
                    if (++battleFactoryInstance.missingBattleTicks >= 3) {
                        forceLossCleanup(battleFactoryInstance, "terminé sans vainqueur (égalité/orphelin)");
                        continue;
                    }
                } else {
                    battleFactoryInstance.missingBattleTicks = 0;
                    // DÉTECTION PAR N° DE TOUR (pas mustChoose) : le vrai softlock double-K.O. a souvent les
                    // DEUX camps mustChoose=false, responses=0 → le tour ne se dispatch plus. Seul signe fiable
                    // = le tour n'avance plus. tick() = 20/s → aiStuckTicks compte des ticks (5 s = 100).
                    int turn = safeTurn(b);
                    Integer prev = battleFactoryInstance.lastSeenTurn;
                    if (prev == null || prev != turn) {
                        battleFactoryInstance.lastSeenTurn = turn;
                        battleFactoryInstance.aiStuckTicks = 0;      // le tour a avancé → tout va bien
                    } else {
                        battleFactoryInstance.aiStuckTicks++;
                        if (battleFactoryInstance.aiStuckTicks == 80) {
                            dumpStuckState(b);   // diagnostic (tour figé depuis 4 s)
                        }
                        // À partir de 5 s de tour figé (100 ticks), toutes les 2 s (40 ticks) : on pousse.
                        if (battleFactoryInstance.aiStuckTicks == 100
                                || (battleFactoryInstance.aiStuckTicks > 100 && battleFactoryInstance.aiStuckTicks % 40 == 0)) {
                            me.plascmabue.cobblemonbattlefactory.debug.BattleLog.log(
                                    "WATCHDOG tour figé {}t battleId={} — poussée du combat",
                                    battleFactoryInstance.aiStuckTicks, battleFactoryInstance.currentBattleID);
                            resolveStuck(battleFactoryInstance, b);
                        }
                    }
                }
            } else {
                battleFactoryInstance.missingBattleTicks = 0;
                battleFactoryInstance.aiStuckTicks = 0;
                battleFactoryInstance.lastSeenTurn = null;
            }
            if (battleFactoryInstance.roundTransition) {
                --battleFactoryInstance.roundTimer;
                battleFactoryInstance.challenger.displayClientMessage(TextUtils.deserialize(TextUtils.parse(BattleFactory.INSTANCE.messagesConfig().getMessage("overlay_nextRoundTimer"), battleFactoryInstance)), true);
                if (battleFactoryInstance.roundTimer > 0) continue;
                battleFactoryInstance.roundTransition = false;
                battleFactoryInstance.setupRound();
                continue;
            }
            if (!battleFactoryInstance.inBonusEncounter) {
                battleFactoryInstance.challenger.displayClientMessage(TextUtils.deserialize(TextUtils.parse(BattleFactory.INSTANCE.messagesConfig().getMessage("overlay_currentStatus"), battleFactoryInstance)), true);
                continue;
            }
            battleFactoryInstance.challenger.displayClientMessage(TextUtils.deserialize(TextUtils.parse(BattleFactory.INSTANCE.messagesConfig().getMessage("overlay_bonusEncounter"), battleFactoryInstance)), true);
        }
        ArrayList<ServerPlayer> toRemove = new ArrayList<ServerPlayer>();
        for (Map.Entry<ServerPlayer, Long> entry : BattleFactory.INSTANCE.removeAfterTicks.entrySet()) {
            entry.setValue(entry.getValue() - 1L);
            if (entry.getValue() > 0L) continue;
            BattleFactory.INSTANCE.stopBattleFactoryInstance(entry.getKey());
            toRemove.add(entry.getKey());
        }
        for (ServerPlayer p : toRemove) {
            BattleFactory.INSTANCE.removeAfterTicks.remove(p);
        }
    }

    /** Débloque un combat non résolu (égalité / orphelin / figé) : retire le PNJ, casse la série de BP
     *  progressive (fin = défaite ; sinon un double K.O. provoqué esquiverait la remise à 0) et programme
     *  l'arrêt de l'instance. */
    /** Lecture sûre du n° de tour courant (-1 si indispo). */
    private static int safeTurn(com.cobblemon.mod.common.api.battles.model.PokemonBattle b) {
        try { return b.getTurn(); } catch (Throwable t) { return -1; }
    }

    /** true si l'acteur a un switch forcé en attente (requête forceSwitch contient true). */
    private static boolean hasForceSwitch(com.cobblemon.mod.common.api.battles.model.actor.BattleActor a) {
        try {
            var req = a.getRequest();
            if (req != null && req.getForceSwitch() != null) {
                for (Boolean x : req.getForceSwitch()) if (Boolean.TRUE.equals(x)) return true;
            }
        } catch (Throwable ignored) {}
        return false;
    }

    /** 1er Pokémon du banc encore envoyable pour cet acteur (null si aucun). */
    private static com.cobblemon.mod.common.battles.pokemon.BattlePokemon firstSendable(
            com.cobblemon.mod.common.api.battles.model.actor.BattleActor a) {
        try {
            for (var bp : a.getPokemonList()) {
                if (bp != null && bp.canBeSentOut()) return bp;
            }
        } catch (Throwable ignored) {}
        return null;
    }

    /** Débloque un combat dont le tour ne progresse plus, en imposant UNIQUEMENT les réponses manquantes
     *  de l'IA. Le joueur n'est JAMAIS forcé : il garde le contrôle de son switch/coup.
     *
     *  <p>Vérifié au bytecode Cobblemon 1.8.1 : {@code setActionResponses(..)} valide chaque réponse, passe
     *  {@code mustChoose=false} puis appelle {@code checkForInputDispatch()} (no-op si {@code request==null}) ;
     *  {@code checkForInputDispatch()} dispatche dès que (un acteur a répondu) ET (aucun acteur n'a mustChoose),
     *  puis vide responses + met request=null sur TOUS les acteurs.
     *
     *  <p>GATE : l'IA (rctapi) répond instantanément ; si aucune IA n'est en attente, c'est que seul le JOUEUR
     *  doit choisir (KO normal / tour normal) → on ne touche à RIEN. On n'agit que sur un vrai double-K.O.
     *  On reconstruit {@code setMustChoose(true)} sur les acteurs en attente (sinon forcer l'IA seule
     *  dispatcherait aussitôt et annulerait la requête du joueur), puis on force SEULEMENT l'IA. */
    private static void resolveStuck(BattleFactoryInstance inst, com.cobblemon.mod.common.api.battles.model.PokemonBattle b) {
        try {
            java.util.List<com.cobblemon.mod.common.api.battles.model.actor.BattleActor> pending = new ArrayList<>();
            boolean anyAiPending = false;
            for (var a : b.getActors()) {
                boolean hasResp = a.getResponses() != null && !a.getResponses().isEmpty();
                if (hasResp) continue;
                boolean mustChoose = false;
                try { mustChoose = a.getMustChoose(); } catch (Throwable ignored) {}
                if (!mustChoose && !hasForceSwitch(a)) continue;
                pending.add(a);
                if (a instanceof com.cobblemon.mod.common.api.battles.model.actor.AIBattleActor) anyAiPending = true;
            }
            if (!anyAiPending) return;  // seul le joueur est en attente → on le laisse choisir

            // VRAI DOUBLE-K.O. = le joueur AUSSI a un switch forcé en attente → Cobblemon n'ouvre PAS son
            // écran de switch (gel infini). On force alors AUSSI son switch. Sur un KO normal (seul le joueur
            // forceSwitch, l'IA en wait) on ne force JAMAIS le joueur : son écran s'ouvre bien.
            boolean playerForceSwitch = false;
            for (var a : pending) {
                if (!(a instanceof com.cobblemon.mod.common.api.battles.model.actor.AIBattleActor) && hasForceSwitch(a)) {
                    playerForceSwitch = true; break;
                }
            }

            for (var a : pending) { try { a.setMustChoose(true); } catch (Throwable ignored) {} }

            for (var a : pending) {
                boolean isAI = a instanceof com.cobblemon.mod.common.api.battles.model.actor.AIBattleActor;
                boolean forceSwitch = hasForceSwitch(a);
                if (isAI) {
                    com.cobblemon.mod.common.battles.ShowdownActionResponse resp = null;
                    if (forceSwitch) {
                        var pick = firstSendable(a);
                        if (pick != null) resp = new com.cobblemon.mod.common.battles.SwitchActionResponse(pick.getUuid());
                    }
                    if (resp == null) resp = new com.cobblemon.mod.common.battles.DefaultActionResponse();
                    a.setActionResponses(java.util.List.of(resp));
                    BattleFactory.LOGGER.warn("[BattleFactory] FORCE IA (tour figé) joueur={} battleId={} forceSwitch={}",
                            inst.challenger.getScoreboardName(), inst.currentBattleID, forceSwitch);
                    me.plascmabue.cobblemonbattlefactory.debug.BattleLog.log(
                            "FORCE IA actor='{}' forceSwitch={}", a.getName().getString(), forceSwitch);
                } else if (playerForceSwitch && forceSwitch) {
                    var pick = firstSendable(a);
                    if (pick != null) {
                        a.setActionResponses(java.util.List.of(
                                new com.cobblemon.mod.common.battles.SwitchActionResponse(pick.getUuid())));
                        me.plascmabue.cobblemonbattlefactory.debug.BattleLog.log(
                                "FORCE JOUEUR switch actor='{}' → {} (vrai double-K.O., écran non ouvert)",
                                a.getName().getString(), pick.getName().getString());
                    }
                }
            }
            b.checkForInputDispatch();
        } catch (Throwable t) {
            BattleFactory.LOGGER.error("[BattleFactory] resolveStuck a échoué : {}", t.getMessage());
            me.plascmabue.cobblemonbattlefactory.debug.BattleLog.log("resolveStuck EXCEPTION : {}", String.valueOf(t));
        }
    }

    /** Dump l'état de CHAQUE acteur (joueur + IA) dans le battle-debug.log : mustChoose, nb de réponses, et
     *  la requête Showdown (wait / forceSwitch / active) — pour voir qui bloque le combat. */
    private static void dumpStuckState(com.cobblemon.mod.common.api.battles.model.PokemonBattle b) {
        try {
            for (var a : b.getActors()) {
                var req = a.getRequest();
                String wait = "?", fs = "?", active = "?";
                if (req != null) {
                    try { wait = String.valueOf(req.getWait()); } catch (Throwable ignored) {}
                    try { fs = String.valueOf(req.getForceSwitch()); } catch (Throwable ignored) {}
                    try { active = req.getActive() == null ? "null" : String.valueOf(req.getActive().size()); } catch (Throwable ignored) {}
                }
                me.plascmabue.cobblemonbattlefactory.debug.BattleLog.log(
                        "DUMP actor='{}' isAI={} mustChoose={} responses={} req(wait={} forceSwitch={} active={})",
                        a.getName().getString(),
                        (a instanceof com.cobblemon.mod.common.api.battles.model.actor.AIBattleActor),
                        a.getMustChoose(),
                        (a.getResponses() == null ? "null" : a.getResponses().size()),
                        wait, fs, active);
            }
        } catch (Throwable ignored) {}
    }

    /** Dump le journal Showdown complet du combat (fin / diagnostic). */
    public static void dumpBattleLog(com.cobblemon.mod.common.api.battles.model.PokemonBattle battle, String issue) {
        try {
            me.plascmabue.cobblemonbattlefactory.debug.BattleLog.log("COMBAT {} battleId={} — journal Showdown :", issue, battle.getBattleId());
            var log = battle.getBattleLog();
            if (log == null || log.isEmpty()) {
                me.plascmabue.cobblemonbattlefactory.debug.BattleLog.log("  (journal vide)");
                return;
            }
            for (var line : log) {
                me.plascmabue.cobblemonbattlefactory.debug.BattleLog.log("  | {}", String.valueOf(line));
            }
        } catch (Throwable t) {
            me.plascmabue.cobblemonbattlefactory.debug.BattleLog.log("  (échec lecture journal : {})", String.valueOf(t));
        }
    }

    private static void forceLossCleanup(BattleFactoryInstance inst, String reason) {
        BattleFactory.LOGGER.warn("[BattleFactory] Combat {} pour {} (battleId={}) — nettoyage forcé.",
                reason, inst.challenger.getScoreboardName(), inst.currentBattleID);
        inst.currentBattleID = null;
        inst.removeNpc();
        ServerPlayer chal = inst.challenger;
        me.plascmabue.cobblemonbattlefactory.datatypes.PlayerData d = BattleFactory.INSTANCE.getPlayerData(chal);
        if (d != null && d.winStreak != 0L) {
            d.winStreak = 0L;
            BattleFactory.INSTANCE.updatePlayerData(chal, d);
            me.plascmabue.cobblemonbattlefactory.config.playerdata.PlayerDataManager.savePlayerData(chal);
        }
        BattleFactory.INSTANCE.removeAfterTicks.put(chal, 5L);
    }

    public static void preventPlayerTeleport() throws ConcurrentModificationException {
        for (BattleFactoryInstance battleFactoryInstance : BattleFactory.INSTANCE.battleFactoryInstances) {
            if (!battleFactoryInstance.preventTeleportation) continue;
            Vec3 pos = new Vec3(battleFactoryInstance.roundLocation.playerX(), battleFactoryInstance.roundLocation.playerY(), battleFactoryInstance.roundLocation.playerZ());
            if (battleFactoryInstance.challenger.serverLevel() == battleFactoryInstance.roundLocation.world() && !(battleFactoryInstance.challenger.position().distanceTo(pos) > (double)BattleFactory.INSTANCE.config().maxDistanceFromBattle)) continue;
            battleFactoryInstance.challenger.teleportTo(battleFactoryInstance.roundLocation.world(), battleFactoryInstance.roundLocation.playerX(), battleFactoryInstance.roundLocation.playerY(), battleFactoryInstance.roundLocation.playerZ(), battleFactoryInstance.roundLocation.playerYRot(), battleFactoryInstance.roundLocation.playerXRot());
        }
    }

    public static void tickCooldowns() throws ConcurrentModificationException {
        for (UUID uuid : BattleFactory.INSTANCE.playerCooldowns.keySet()) {
            ServerPlayer player;
            if (!BattleFactory.INSTANCE.config().tickOfflinePlayerCooldowns && (player = BattleFactory.INSTANCE.server().getPlayerList().getPlayer(uuid)) == null) continue;
            BattleFactory.INSTANCE.tickCooldown(uuid);
        }
        BattleFactory.INSTANCE.clearCooldowns();
    }
}

