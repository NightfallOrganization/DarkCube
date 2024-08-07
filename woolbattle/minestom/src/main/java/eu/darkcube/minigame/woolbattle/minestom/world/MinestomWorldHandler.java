/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.world;

import static eu.darkcube.minigame.woolbattle.api.util.LogUtil.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.zip.ZipInputStream;

import eu.darkcube.minigame.woolbattle.api.world.ColoredWool;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.ingame.world.CommonGameWorld;
import eu.darkcube.minigame.woolbattle.common.util.GsonUtil;
import eu.darkcube.minigame.woolbattle.common.util.schematic.Schematic;
import eu.darkcube.minigame.woolbattle.common.world.CommonIngameWorld;
import eu.darkcube.minigame.woolbattle.common.world.CommonWorld;
import eu.darkcube.minigame.woolbattle.common.world.PlatformWorldHandler;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattleApi;
import eu.darkcube.minigame.woolbattle.minestom.world.impl.MinestomGameWorldImpl;
import eu.darkcube.minigame.woolbattle.minestom.world.impl.MinestomIngameWorldImpl;
import eu.darkcube.minigame.woolbattle.minestom.world.impl.MinestomWorldImpl;
import eu.darkcube.server.minestom.instance.DoNotSave;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.minestom.item.material.MinestomMaterial;
import eu.darkcube.system.server.item.material.Material;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.item.ItemEntityMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.biome.Biome;

public class MinestomWorldHandler implements PlatformWorldHandler {
    private final @NotNull MinestomWoolBattleApi woolbattle;
    private final @NotNull InstanceManager instanceManager;
    private final @NotNull DynamicRegistry<Biome> biomeRegistry;
    private final @NotNull Path worldsDirectory = Path.of("worlds");

    public MinestomWorldHandler(@NotNull MinestomWoolBattleApi woolbattle, @NotNull InstanceManager instanceManager, @NotNull DynamicRegistry<Biome> biomeRegistry) {
        this.woolbattle = woolbattle;
        this.instanceManager = instanceManager;
        this.biomeRegistry = biomeRegistry;
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
            var worldHandler = woolbattle.worldHandler();
            var instance = new NoSaveInstanceContainer(UUID.randomUUID(), DimensionType.OVERWORLD);
            var biome = biomeRegistry.get(Biome.PLAINS);
            if (biome == null) throw new IllegalStateException("Biome plains not registered");
            var zip = woolbattle.woolbattle().worldDataProvider().loadLobbyZip().join();
            var path = extractWorld(null, "setup", zip, null);
            instance.setChunkLoader(new AnvilLoader(path));
            instance.setChunkSupplier(FullbrightChunk::new);
            instance.setTimeRate(0);
            instance.setTime(6000);
            instanceManager.registerInstance(instance);
            var world = new MinestomWorldImpl(worldHandler, instance, path);
            woolbattle.woolbattle().worlds().put(instance, world);
            logLoadWorld(world);
            return world;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull CommonGameWorld loadLobbyWorld(@NotNull CommonGame game) {
        try {
            var instance = new NoSaveInstanceContainer(UUID.randomUUID(), DimensionType.OVERWORLD);
            var biome = biomeRegistry.get(Biome.PLAINS);
            if (biome == null) throw new IllegalStateException("Biome plains not registered");
            var zip = woolbattle.woolbattle().worldDataProvider().loadLobbyZip().join();
            var path = extractWorld(game, "lobby", zip, null);
            instance.setChunkLoader(new AnvilLoader(path));
            instance.setChunkSupplier(FullbrightChunk::new);
            instance.setTimeRate(0);
            instance.setTime(6000);
            instanceManager.registerInstance(instance);
            var world = new MinestomGameWorldImpl(game, instance, path);
            woolbattle.woolbattle().worlds().put(instance, world);
            logLoadWorld(world);
            return world;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull CommonIngameWorld loadIngameWorld(@NotNull CommonGame game, @Nullable Schematic schematic) {
        try {
            var instance = new InstanceContainer(UUID.randomUUID(), DimensionType.OVERWORLD);
            var biome = biomeRegistry.get(Biome.PLAINS);
            if (biome == null) throw new IllegalStateException("Biome plains not registered");
            var path = createDirectory(game, "ingame");
            instance.setChunkLoader(new AnvilLoader(path));
            if (schematic != null) {
                instance.setGenerator(new VoidSchematicGenerator(schematic));
            }
            instance.setChunkSupplier(FullbrightChunk::new);
            instance.setTimeRate(0);
            instance.setTime(6000);
            instanceManager.registerInstance(instance);
            var world = new MinestomIngameWorldImpl(game, instance, path);
            woolbattle.woolbattle().worlds().put(instance, world);
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
            woolbattle.woolbattle().logger().warn("Forcefully disconnecting {}", p.getUsername());
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

    @Override
    public void dropAt(@NotNull CommonWorld world, double x, double y, double z, @NotNull ColoredWool wool, int amt) {
        var instance = instance(world);
        var entity = new Entity(EntityType.ITEM);
        entity.editEntityMeta(ItemEntityMeta.class, meta -> meta.setItem(wool.createSingleItem().amount(amt).build()));
        entity.setInstance(instance, new Pos(x, y, z));
    }

    private @NotNull Path createDirectory(@Nullable CommonGame game, @NotNull String suffix) throws IOException {
        Files.createDirectories(worldsDirectory);
        return Files.createTempDirectory(worldsDirectory, game == null ? suffix : game.id() + "-" + suffix);
    }

    private @NotNull Path extractWorld(@Nullable CommonGame game, @NotNull String suffix, @NotNull ZipInputStream zip, @Nullable Consumer<@NotNull JsonObject> dataConsumer) throws IOException {
        var directory = createDirectory(game, suffix);
        while (true) {
            var entry = zip.getNextEntry();
            if (entry == null) {
                zip.closeEntry();
                zip.close();
                return directory;
            }
            try {
                var name = entry.getName();
                if (name.equals("data.json")) {
                    if (dataConsumer != null) {
                        var bytes = zip.readAllBytes();
                        var string = new String(bytes, StandardCharsets.UTF_8);
                        var jsonObject = GsonUtil.gson().fromJson(string, JsonObject.class);
                        dataConsumer.accept(jsonObject);
                        continue;
                    }
                }
                var path = directory.resolve(name);
                if (entry.isDirectory()) {
                    Files.createDirectories(path);
                } else {
                    Files.createDirectories(path.getParent());
                    Files.copy(zip, path);
                }
            } finally {
                zip.closeEntry();
            }
        }
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
