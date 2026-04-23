/*
 * Decompiled with CFR 0.152.
 */
package me.unariginal.cobblemonbattlefactory.datatypes.rentalPools;

import java.util.UUID;

public class PokemonPool {
    public UUID uuid;
    public String name;
    public String type;

    public PokemonPool(UUID uuid, String name, String type) {
        this.uuid = uuid;
        this.name = name;
        this.type = type;
    }
}

