/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.world;

import java.util.UUID;

import eu.darkcube.minigame.woolbattle.api.world.ColoredWool;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.ingame.world.CommonGameWorld;
import eu.darkcube.minigame.woolbattle.common.world.CommonIngameWorld;
import eu.darkcube.minigame.woolbattle.common.world.CommonWorld;
import eu.darkcube.minigame.woolbattle.common.world.PlatformWorldHandler;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattleApi;
import eu.darkcube.minigame.woolbattle.minestom.world.impl.MinestomGameWorldImpl;
import eu.darkcube.minigame.woolbattle.minestom.world.impl.MinestomWorldImpl;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.item.material.MinestomMaterial;
import eu.darkcube.system.server.item.material.Material;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.item.ItemEntityMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.biomes.BiomeManager;

public class MinestomWorldHandler implements PlatformWorldHandler {
    private final @NotNull MinestomWoolBattleApi woolbattle;
    private final @NotNull InstanceManager instanceManager;
    private final @NotNull BiomeManager biomeManager;

    public MinestomWorldHandler(@NotNull MinestomWoolBattleApi woolbattle, @NotNull InstanceManager instanceManager, @NotNull BiomeManager biomeManager) {
        this.woolbattle = woolbattle;
        this.instanceManager = instanceManager;
        this.biomeManager = biomeManager;
    }

    @Override
    public @NotNull Material material(@NotNull CommonWorld world, int x, int y, int z) {
        return Material.ofNullable(instance(world).getBlock(x, y, z).registry().material());
    }

    @Override
    public void material(@NotNull CommonWorld world, int x, int y, int z, @NotNull Material material) {
        var minestomType = ((MinestomMaterial) material).minestomType();
        instance(world).setBlock(x, y, z, minestomType.block());
    }

    @Override
    public @NotNull CommonWorld loadSetupWorld() {
        var worldHandler = woolbattle.worldHandler();
        var instance = new InstanceContainer(UUID.randomUUID(), DimensionType.OVERWORLD);
        var biome = biomeManager.getByName("minecraft:plains");
        if (biome == null) throw new IllegalStateException("Biome plains not registered");
        instance.setGenerator(unit -> {
            unit.modifier().fillHeight(unit.absoluteStart().blockY(), 99, Block.STONE);
            unit.modifier().fillHeight(99, 100, Block.GRASS_BLOCK);
            unit.modifier().fillBiome(biome);
        });
        instance.setChunkSupplier(FullbrightChunk::new);
        // instance.setTimeRate(0);
        // instance.setTime(6000);
        instanceManager.registerInstance(instance);
        var world = new MinestomWorldImpl(worldHandler, instance);
        woolbattle.woolbattle().worlds().put(instance, world);
        return world;
    }

    @Override
    public @NotNull CommonGameWorld loadLobbyWorld(@NotNull CommonGame game) {
        var instance = new InstanceContainer(UUID.randomUUID(), DimensionType.OVERWORLD);
        var biome = biomeManager.getByName("minecraft:plains");
        if (biome == null) throw new IllegalStateException("Biome plains not registered");
        instance.setGenerator(unit -> {
            unit.modifier().fillHeight(unit.absoluteStart().blockY(), 60, Block.STONE);
            unit.modifier().fillHeight(60, 61, Block.GRASS_BLOCK);
            unit.modifier().fillBiome(biome);
        });
        instance.setChunkSupplier(FullbrightChunk::new);
        instanceManager.registerInstance(instance);
        var world = new MinestomGameWorldImpl(game, instance);
        woolbattle.woolbattle().worlds().put(instance, world);
        return world;
    }

    @Override
    public @NotNull CommonIngameWorld loadIngameWorld(@NotNull CommonGame game) {
        return null;
    }

    @Override
    public void dropAt(@NotNull CommonWorld world, double x, double y, double z, @NotNull ColoredWool wool, int amt) {
        var instance = instance(world);
        var entity = new Entity(EntityType.ITEM);
        entity.editEntityMeta(ItemEntityMeta.class, meta -> meta.setItem(wool.createSingleItem().amount(amt).build()));
        entity.setInstance(instance, new Pos(x, y, z));
    }

    private Instance instance(CommonWorld world) {
        return ((MinestomWorld) world).instance();
    }
}
