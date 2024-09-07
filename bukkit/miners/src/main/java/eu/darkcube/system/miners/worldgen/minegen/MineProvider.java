package eu.darkcube.system.miners.worldgen.minegen;

import java.util.Collections;
import java.util.List;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

public class MineProvider extends BiomeProvider {

    public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {

        return Biome.JUNGLE;
    }

    @Override
    public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
        return Collections.singletonList(Biome.JUNGLE);
    }

}
