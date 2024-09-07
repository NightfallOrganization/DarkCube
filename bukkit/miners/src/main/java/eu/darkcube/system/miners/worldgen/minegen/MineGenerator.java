package eu.darkcube.system.miners.worldgen.minegen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MineGenerator extends ChunkGenerator {

    @Override
    public @NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int chunkX, int chunkZ, @NotNull BiomeGrid biome) {
        ChunkData chunkData = createChunkData(world);

        Material[] blocks = {
                Material.STONE,
                Material.GOLD_ORE,
                Material.IRON_ORE,
                Material.DIAMOND_ORE,
                Material.EMERALD_ORE,
                Material.REDSTONE_ORE,
                Material.TNT,
                Material.OAK_PLANKS,
                Material.COAL_ORE,
        };

        // Wahrscheinlichkeiten in Promille (10000 für 100%, 1 für 0,1%, etc.)
        int[] probabilities = {
                9525,  // STONE
                50,    // GOLD_ORE
                50,    // IRON_ORE
                15,    // DIAMOND_ORE
                40,    // EMERALD_ORE
                100,    // REDSTONE_ORE
                20,    // TNT
                50,    // OAK_PLANKS
                50,    // COAL_ORE
        };

        int[] cumulativeProbabilities = new int[probabilities.length];
        cumulativeProbabilities[0] = probabilities[0];
        for (int i = 1; i < probabilities.length; i++) {
            cumulativeProbabilities[i] = cumulativeProbabilities[i - 1] + probabilities[i];
        }

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 150; y++) {
                    int randomValue = random.nextInt(10000);
                    Material block = Material.STONE;
                    for (int i = 0; i < cumulativeProbabilities.length; i++) {
                        if (randomValue < cumulativeProbabilities[i]) {
                            block = blocks[i];
                            break;
                        }
                    }

                    chunkData.setBlock(x, y, z, block);
                }
            }
        }

        return chunkData;
    }

    @Override
    public @Nullable BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
        return new MineProvider();
    }

    @Override
    public @NotNull List<BlockPopulator> getDefaultPopulators(@NotNull World world) {
        ArrayList<BlockPopulator> out = new ArrayList<>();
        out.add(new MinePopulator());
        return out;
    }

}
