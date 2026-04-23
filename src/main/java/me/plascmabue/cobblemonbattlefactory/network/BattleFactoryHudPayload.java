package me.plascmabue.cobblemonbattlefactory.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Sent from the BattleFactory server to the running player whenever the
 * Battle Factory state changes (new round, streak updated, instance stopped).
 * Consumed by PokeSkiesClient to render a HUD overlay.
 */
public record BattleFactoryHudPayload(
        boolean active,
        String tier,
        int round,
        int streak,
        int timerTicks
) implements CustomPacketPayload {
    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath("cobblemonbattlefactory", "hud");
    public static final Type<BattleFactoryHudPayload> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, BattleFactoryHudPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, BattleFactoryHudPayload::active,
                    ByteBufCodecs.STRING_UTF8, BattleFactoryHudPayload::tier,
                    ByteBufCodecs.VAR_INT, BattleFactoryHudPayload::round,
                    ByteBufCodecs.VAR_INT, BattleFactoryHudPayload::streak,
                    ByteBufCodecs.VAR_INT, BattleFactoryHudPayload::timerTicks,
                    BattleFactoryHudPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
