/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.voidworld;

import java.util.List;
import java.util.Random;

import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

public class VoidWorld extends DarkCubePlugin {
    public VoidWorld() {
        super("voidworld");
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, String id) {
        return new ChunkGenerator() {
            @Override
            public boolean canSpawn(@NotNull World world, int x, int z) {
                return true;
            }

            @Override
            public @NotNull BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
                return new BiomeProvider() {
                    @Override
                    public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
                        return Biome.PLAINS;
                    }

                    @Override
                    public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
                        return List.of(Biome.PLAINS);
                    }
                };
            }

            @Override
            public Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
                return new Location(world, 0.5, 128, 0.5);
            }
        };
    }
}
