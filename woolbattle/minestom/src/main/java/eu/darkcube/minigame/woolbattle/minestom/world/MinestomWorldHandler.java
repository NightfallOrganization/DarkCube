/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.world;

import static eu.darkcube.minigame.woolbattle.api.util.LogUtil.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.ingame.world.CommonGameWorld;
import eu.darkcube.minigame.woolbattle.common.util.schematic.Schematic;
import eu.darkcube.minigame.woolbattle.common.world.CommonIngameWorld;
import eu.darkcube.minigame.woolbattle.common.world.CommonWorld;
import eu.darkcube.minigame.woolbattle.common.world.PlatformWorldHandler;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattleApi;
import eu.darkcube.minigame.woolbattle.minestom.listener.MinestomQuitListener;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import eu.darkcube.minigame.woolbattle.minestom.world.impl.MinestomGameWorldImpl;
import eu.darkcube.minigame.woolbattle.minestom.world.impl.MinestomIngameWorldImpl;
import eu.darkcube.minigame.woolbattle.minestom.world.impl.MinestomWorldImpl;
import eu.darkcube.server.minestom.instance.DoNotSave;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.minestom.item.material.MinestomMaterial;
import eu.darkcube.system.server.item.material.Material;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.biome.Biome;

public class MinestomWorldHandler implements PlatformWorldHandler {
    private final @NotNull MinestomWoolBattleApi api;
    private final @NotNull InstanceManager instanceManager;
    private final @NotNull DynamicRegistry<Biome> biomeRegistry;
    private final @NotNull Path worldsDirectory = Path.of("worlds");
    private final @NotNull DynamicRegistry.Key<DimensionType> dimensionType;
    private final @NotNull DynamicRegistry.Key<Biome> biome;

    public MinestomWorldHandler(@NotNull MinestomWoolBattleApi api, @NotNull InstanceManager instanceManager, @NotNull DynamicRegistry<Biome> biomeRegistry) {
        this.api = api;
        this.instanceManager = instanceManager;
        this.biomeRegistry = biomeRegistry;
        // this.dimensionType = MinecraftServer.getDimensionTypeRegistry().register("woolbattle:error_404", DimensionType.builder().minY(-1024).height(2048).build());
        this.dimensionType = DimensionType.OVERWORLD;
        // this.biome = MinecraftServer.getBiomeRegistry().register("woolbattle:error_405", Biome.builder().effects(BiomeEffects.builder().fogColor(new Color(255, 255, 255, 0).getRGB()).skyColor(new Color(255, 255, 255, 0).getRGB()).build()).build());
        this.biome = Biome.PLAINS;
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
        try {
            var worldHandler = api.worldHandler();
            var instance = new NoSaveInstanceContainer(UUID.randomUUID(), dimensionType);
            var biome = biomeRegistry.get(this.biome);
            if (biome == null) throw new IllegalStateException("Biome plains not registered");
            // var zip = woolbattle.woolbattle().worldDataProvider().loadLobbyZip().join();
            // var path = extractWorld(null, "setup", zip, null);
            // instance.setChunkLoader(new AnvilLoader(path));
            var path = createDirectory(null, "setup");
            instance.setGenerator(new VoidSchematicGenerator(api.woolbattle().lobbySchematic(), this.biome, (int) this.api.lobbyData().spawn().y()));
            instance.setChunkSupplier(FullbrightChunk::new);
            instance.setTimeRate(0);
            instance.setTime(6000);
            instanceManager.registerInstance(instance);
            var world = new MinestomWorldImpl(api.woolbattle(), worldHandler, instance, path);
            api.woolbattle().worlds().put(instance, world);
            logLoadWorld(world);
            return world;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull CommonGameWorld loadLobbyWorld(@NotNull CommonGame game) {
        try {
            var instance = new NoSaveInstanceContainer(UUID.randomUUID(), dimensionType);
            var biome = biomeRegistry.get(this.biome);
            if (biome == null) throw new IllegalStateException("Biome plains not registered");
            // var zip = woolbattle.woolbattle().worldDataProvider().loadLobbyZip().join();
            // var path = extractWorld(game, "lobby", zip, null);
            // instance.setChunkLoader(new AnvilLoader(path));
            var path = createDirectory(game, "lobby");
            instance.setGenerator(new VoidSchematicGenerator(api.woolbattle().lobbySchematic(), this.biome, (int) this.api.lobbyData().spawn().y()));
            instance.setChunkSupplier(FullbrightChunk::new);
            instance.setTimeRate(0);
            instance.setTime(6000);
            instanceManager.registerInstance(instance);
            var world = new MinestomGameWorldImpl(game, instance, path);
            api.woolbattle().worlds().put(instance, world);
            logLoadWorld(world);
            return world;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull CommonIngameWorld loadIngameWorld(@NotNull CommonGame game, @Nullable Schematic schematic) {
        try {
            var instance = new InstanceContainer(UUID.randomUUID(), dimensionType);
            var biome = biomeRegistry.get(this.biome);
            if (biome == null) throw new IllegalStateException("Biome plains not registered");
            var path = createDirectory(game, "ingame");
            instance.setChunkLoader(new AnvilLoader(path));
            if (schematic != null) {
                instance.setGenerator(new VoidSchematicGenerator(schematic, this.biome, this.api.mapManager().deathHeight()));
            }
            instance.setChunkSupplier(FullbrightChunk::new);
            instance.setTimeRate(0);
            instance.setTime(6000);
            instanceManager.registerInstance(instance);
            var world = new MinestomIngameWorldImpl(game, instance, path);
            api.woolbattle().worlds().put(instance, world);
            logLoadWorld(world);
            return world;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void logLoadWorld(MinestomWorld world) {
        LOGGER.info("World {} was loaded", world.instance().getUniqueId());
    }

    private void logUnloadWorld(MinestomWorld world) {
        LOGGER.info("World {} was unloaded", world.instance().getUniqueId());
    }

    @Override
    public void unloadWorld(@NotNull CommonWorld world) {
        var minestomWorld = (MinestomWorld) world;
        var instance = minestomWorld.instance();
        List.copyOf(instance.getPlayers()).forEach(p -> {
            if (!MinestomQuitListener.WORKING.get().contains((MinestomPlayer) p)) { // Ignore message if player is already disconnecting
                api.woolbattle().logger().warn("Forcefully disconnecting {}", p.getUsername());
            }
            p.remove();
        });
        instanceManager.unregisterInstance(instance);
        logUnloadWorld(minestomWorld);
        var worldDirectory = minestomWorld.worldDirectory();
        if (worldDirectory != null) {
            try (var walk = Files.walk(worldDirectory)) {
                walk.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private @NotNull Path createDirectory(@Nullable CommonGame game, @NotNull String suffix) throws IOException {
        Files.createDirectories(worldsDirectory);
        return Files.createTempDirectory(worldsDirectory, game == null ? suffix : game.id() + "-" + suffix);
    }

    private Instance instance(CommonWorld world) {
        return ((MinestomWorld) world).instance();
    }

    private static class NoSaveInstanceContainer extends InstanceContainer implements DoNotSave {
        public NoSaveInstanceContainer(@NotNull UUID uniqueId, @NotNull DynamicRegistry.Key<DimensionType> dimensionType) {
            super(uniqueId, dimensionType);
        }
    }
}
