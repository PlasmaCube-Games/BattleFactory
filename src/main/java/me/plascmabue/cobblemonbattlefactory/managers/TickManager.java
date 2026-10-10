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
                        // Dès 2 s (40 ticks) puis chaque 1 s (20 ticks) : on pousse. resolveStuck force l'IA tôt
                        // (elle devrait répondre en instantané → 5 s de latence/tour sinon) et reprompte les
                        // joueurs en limbo (écran fermé, sûr) ; les actions sensibles (switch forcé / joueur
                        // mustChoose=true) n'arrivent qu'à partir de 100 ticks (5 s).
                        int st = battleFactoryInstance.aiStuckTicks;
                        if (st >= 40 && st % 20 == 0) {
                            me.plascmabue.cobblemonbattlefactory.debug.BattleLog.log(
                                    "WATCHDOG tour figé {}t battleId={} — poussée du combat",
                                    st, battleFactoryInstance.currentBattleID);
                            resolveStuck(battleFactoryInstance, b, st);
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

    /** Construit les réponses forcées d'un acteur, DOUBLES-AWARE : une réponse PAR slot actif (sinon
     *  setActionResponses plante en IndexOutOfBounds avec 2 actifs et 1 réponse). Switch forcé → switch vers
     *  un Pokémon de banc distinct (sinon Pass) ; autre slot d'une requête de switch → Pass ; move → Default. */
    private static java.util.List<com.cobblemon.mod.common.battles.ShowdownActionResponse> buildForced(
            com.cobblemon.mod.common.api.battles.model.actor.BattleActor a) {
        java.util.List<Boolean> fs = null;
        java.util.List<?> active = null;
        try { var req = a.getRequest(); if (req != null) { fs = req.getForceSwitch(); active = req.getActive(); } } catch (Throwable ignored) {}
int activeReq = (active != null) ? active.size() : 0;
        int fsSize = (fs != null) ? fs.size() : 0;
        int activePk = 0;
        try { var ap = a.getActivePokemon(); if (ap != null) activePk = ap.size(); } catch (Throwable ignored) {}
        // iterate() dispatche max(active.size, forceSwitch.size) fois en indexant responses[] : il FAUT au
        // moins autant de reponses, sinon IndexOutOfBounds -> Showdown desync -> gel definitif. On cale sur le
        // nb de slots physiques (activePokemon) qui majore les deux et satisfait la validation de setActionResponses.
        int slots = Math.max(Math.max(activePk, activeReq), fsSize);
        if (slots < 1) slots = 1;
        java.util.List<com.cobblemon.mod.common.battles.pokemon.BattlePokemon> bench = new ArrayList<>();
        try { for (var bp : a.getPokemonList()) if (bp != null && bp.canBeSentOut()) bench.add(bp); } catch (Throwable ignored) {}
        boolean anyForce = false;
        if (fs != null) for (Boolean x : fs) if (Boolean.TRUE.equals(x)) { anyForce = true; break; }
        int bi = 0;
        java.util.List<com.cobblemon.mod.common.battles.ShowdownActionResponse> out = new ArrayList<>(slots);
        for (int i = 0; i < slots; i++) {
            boolean slotForce = fs != null && i < fs.size() && Boolean.TRUE.equals(fs.get(i));
            if (slotForce) {
                if (bi < bench.size()) out.add(new com.cobblemon.mod.common.battles.SwitchActionResponse(bench.get(bi++).getUuid()));
                else out.add(com.cobblemon.mod.common.battles.PassActionResponse.INSTANCE);
            } else if (anyForce) {
                out.add(com.cobblemon.mod.common.battles.PassActionResponse.INSTANCE);
            } else {
                out.add(new com.cobblemon.mod.common.battles.DefaultActionResponse());
            }
        }
        return out;
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
    private static void resolveStuck(BattleFactoryInstance inst, com.cobblemon.mod.common.api.battles.model.PokemonBattle b, int n) {
        try {
            // « Bloqueur » = acteur avec une requête VIVANTE (non-wait) et aucune réponse posée. On détecte par la
            // REQUÊTE, PAS par mustChoose : le deadlock « menu fight mais impossible d'attaquer » a les DEUX camps
            // mustChoose=false responses=0 active!=null (Cobblemon n'a lancé le choix de personne). wait=true =
            // attend l'autre camp → ignoré.
            java.util.List<com.cobblemon.mod.common.api.battles.model.actor.BattleActor> pending = new ArrayList<>();
            // Joueurs en LIMBO = requête vivante mais mustChoose=false (Cobblemon ne leur a pas ouvert le choix).
            // Un joueur mustChoose=true a son écran OUVERT et clique normalement : il ne faut SURTOUT PAS le
            // reprompter (ça renvoie BattleQueueRequestPacket et écrase son clic en cours → livelock « plus d'attaques »).
            java.util.List<com.cobblemon.mod.common.api.battles.model.actor.BattleActor> limboPlayers = new ArrayList<>();
            boolean anyAiBlocker = false, anyPlayerLimbo = false;
            boolean aiForceSwitch = false, playerForceSwitch = false;
            for (var a : b.getActors()) {
                var resp = a.getResponses();
                if (resp != null && !resp.isEmpty()) continue;      // a déjà répondu
                var req = a.getRequest();
                if (req == null) continue;
                boolean wait = false;
                try { wait = req.getWait(); } catch (Throwable ignored) {}
                if (wait) continue;                                 // attend l'autre camp → OK
                pending.add(a);
                boolean isAI = a instanceof com.cobblemon.mod.common.api.battles.model.actor.AIBattleActor;
                boolean mustChoose = false;
                try { mustChoose = a.getMustChoose(); } catch (Throwable ignored) {}
                boolean fsw = hasForceSwitch(a);
                if (isAI) { anyAiBlocker = true; if (fsw) aiForceSwitch = true; }
                else { if (!mustChoose) { anyPlayerLimbo = true; limboPlayers.add(a); } if (fsw) playerForceSwitch = true; }
            }
            // Si le SEUL bloqueur est un joueur qui choisit normalement (mustChoose=true), il réfléchit → on ne
            // touche à RIEN. On n'agit que si une IA est coincée (elle répond normalement en instantané) OU si un
            // joueur est en limbo (mustChoose=false mais requête vivante = le bug du menu fight figé).
            // touchPlayers = on autorise les actions SENSIBLES sur le joueur (switch forcé auto / reprompt d'un
            // joueur mustChoose=true) seulement à 5 s (100 ticks). Avant (dès 2 s) on ne fait que : forcer l'IA
            // (elle doit répondre en instantané) et reprompter un joueur en LIMBO (écran fermé → sûr).
            boolean touchPlayers = n >= 100;
            boolean actionable = anyAiBlocker || anyPlayerLimbo || (touchPlayers && playerForceSwitch);
            if (pending.isEmpty() || !actionable) return;

            // VRAI DOUBLE-K.O. = joueur ET IA ont un switch forcé → Cobblemon n'ouvre pas l'écran du joueur →
            // on force aussi son switch. KO SIMPLE (joueur forceSwitch seul, IA en wait) → JAMAIS forcer : reprompt.
            boolean trueDoubleKO = playerForceSwitch && aiForceSwitch;

            // setMustChoose(true) sur tous AVANT de forcer l'IA (sinon le dispatch IA-seul annule la requête joueur).
            for (var a : pending) { try { a.setMustChoose(true); } catch (Throwable ignored) {} }

            for (var a : pending) {
                boolean isAI = a instanceof com.cobblemon.mod.common.api.battles.model.actor.AIBattleActor;
                if (isAI) {
                    a.setActionResponses(buildForced(a));
                    me.plascmabue.cobblemonbattlefactory.debug.BattleLog.log(
                            "FORCE IA actor='{}' forceSwitch={}", a.getName().getString(), hasForceSwitch(a));
                } else if (touchPlayers && trueDoubleKO && hasForceSwitch(a)) {
                    a.setActionResponses(buildForced(a));
                    me.plascmabue.cobblemonbattlefactory.debug.BattleLog.log(
                            "FORCE JOUEUR actor='{}' (vrai double-K.O., écran non ouvert)", a.getName().getString());
                } else if (limboPlayers.contains(a) || (touchPlayers && hasForceSwitch(a))) {
                    // LIMBO (mustChoose=false, écran fermé) → reprompt dès 2 s ; joueur mustChoose=true en switch
                    // forcé (KO, écran cassé) → reprompt seulement à 5 s. Jamais forcer ses coups.
                    reprompt(a);
                    me.plascmabue.cobblemonbattlefactory.debug.BattleLog.log(
                            "REPROMPT JOUEUR actor='{}' (menu fige, re-ouverture du choix)", a.getName().getString());
                }
                // else : joueur mustChoose=true sans KO = écran ouvert, il clique → on ne le touche PAS.
            }
            b.checkForInputDispatch();
        } catch (Throwable t) {
            BattleFactory.LOGGER.error("[BattleFactory] resolveStuck a échoué : {}", t.getMessage());
            me.plascmabue.cobblemonbattlefactory.debug.BattleLog.log("resolveStuck EXCEPTION : {}", String.valueOf(t));
        }
    }

    /** Re-prompte un acteur bloqué SANS forcer son choix : re-pousse sa requête (rafraîchit les coups affichés)
     *  puis le paquet « fais ton choix » → ré-ouvre l'écran côté joueur / re-déclenche onChoiceRequested côté IA. */
    private static void reprompt(com.cobblemon.mod.common.api.battles.model.actor.BattleActor a) {
        try {
            var req = a.getRequest();
            if (req != null) a.sendUpdate(new com.cobblemon.mod.common.net.messages.client.battle.BattleQueueRequestPacket(req));
            a.sendUpdate(new com.cobblemon.mod.common.net.messages.client.battle.BattleMakeChoicePacket());
        } catch (Throwable ignored) {}
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

