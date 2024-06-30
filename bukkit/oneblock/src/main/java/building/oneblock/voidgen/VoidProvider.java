package building.oneblock.voidgen;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class VoidProvider extends BiomeProvider {

    public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {

        return Biome.PLAINS;
    }

    @Override
    public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
        return Collections.singletonList(Biome.PLAINS);
    }

}