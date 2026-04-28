package me.plascmabue.cobblemonbattlefactory.mixin;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.battles.ShowdownActionRequest;
import com.cobblemon.mod.common.battles.ShowdownMoveset;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import me.plascmabue.cobblemonbattlefactory.BattleFactory;
import me.plascmabue.cobblemonbattlefactory.managers.BattleFactoryInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Forces off all Cobblemon gimmicks (mega / dynamax / z-move / ultra burst / tera) for every
 * actor in a BattleFactory battle. Runs AFTER mega_showdown's own mixin (higher priority number
 * = applied later, so our TAIL injection runs LAST), so our clears override anything mega_showdown
 * may have re-enabled.
 */
@Mixin(value = ShowdownActionRequest.class, priority = 2000, remap = false)
public abstract class ShowdownActionRequestMixin {

    @Shadow public abstract List<ShowdownMoveset> getActive();

    @Inject(method = "sanitize", at = @At("TAIL"), remap = false)
    private void bf_disableGimmicks(PokemonBattle battle, BattleActor actor, CallbackInfo ci) {
        if (battle == null) return;
        boolean isBf = false;
        for (BattleFactoryInstance inst : BattleFactory.INSTANCE.battleFactoryInstances) {
            if (inst.currentBattleID != null && inst.currentBattleID.equals(battle.getBattleId())) {
                isBf = true;
                break;
            }
        }
        if (!isBf) return;
        List<ShowdownMoveset> active = getActive();
        if (active == null) {
            BattleFactory.LOGGER.info("[BattleFactory] mixin sanitize: active=null for battle={} actor={}",
                    battle.getBattleId(), actor != null ? actor.getUuid() : "null");
            return;
        }
        int idx = 0;
        for (ShowdownMoveset ms : active) {
            if (ms == null) { idx++; continue; }
            try {
                String before = String.format("mega=%s dyna=%s ub=%s z=%s tera=%s",
                        ms.getCanMegaEvo(), ms.getCanDynamax(), ms.getCanUltraBurst(),
                        ms.getCanZMove() != null ? "list" : "null",
                        ms.getCanTerastallize());
                ms.blockGimmick(ShowdownMoveset.Gimmick.MEGA_EVOLUTION);
                ms.blockGimmick(ShowdownMoveset.Gimmick.ULTRA_BURST);
                ms.blockGimmick(ShowdownMoveset.Gimmick.Z_POWER);
                ms.blockGimmick(ShowdownMoveset.Gimmick.DYNAMAX);
                ms.blockGimmick(ShowdownMoveset.Gimmick.TERASTALLIZATION);
                ms.setMaxMoves(null);
                ms.setCanZMove(null);
                ms.setCanTerastallize(null);
                ms.setCanDynamax(false);
                ms.setCanMegaEvo(false);
                ms.setCanUltraBurst(false);
                String actorTag;
                if (actor instanceof PlayerBattleActor pba) {
                    actorTag = "PLAYER:" + pba.getPlayerUUIDs() + " uuid=" + pba.getUuid();
                } else {
                    actorTag = (actor != null ? actor.getClass().getSimpleName() + ":" + actor.getUuid() : "null");
                }
                BattleFactory.LOGGER.info("[BattleFactory] mixin sanitize: cleared gimmicks for actor={} ms[{}] (was {})",
                        actorTag, idx, before);
            } catch (Throwable t) {
                BattleFactory.LOGGER.warn("[BattleFactory] Mixin bf_disableGimmicks: {}", t.getMessage());
            }
            idx++;
        }
    }
}