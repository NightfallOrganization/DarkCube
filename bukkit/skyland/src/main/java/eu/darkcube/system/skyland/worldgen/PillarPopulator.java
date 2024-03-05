/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland.worldgen;

import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class PillarPopulator  extends BlockPopulator {

    SimplexOctaveGenerator islandGen = new SimplexOctaveGenerator(new Random(1), 8);
    SimplexOctaveGenerator pillarDirection = new SimplexOctaveGenerator(new Random(12), 8);

    public PillarPopulator() {
        islandGen.setScale(1D);
        pillarDirection.setScale(1D);
    }
    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX,
                         int chunkZ, @NotNull LimitedRegion limitedRegion) {
        double roll = (islandGen.noise(chunkX, chunkZ, 0.5D, 0.5D, true) + 1);
        int pillarFreq = 6; // every pillarFreq chunks a pillar is placed

        int startX = chunkX * 16;
        int startZ = chunkZ * 16;

        if (((int) (roll * pillarFreq)) % pillarFreq == 0){
            var directionRoll = pillarDirection.noise(chunkX, chunkZ, 0,5D, 0,5D, true) + 1;
            PillarDirection pillarDirection = PillarDirection.values()[(int) (directionRoll%PillarDirection.values().length)];

            //todo tile pillar towards direction


            for (int x = startX; x < startX + 8; x++) {
                for (int z = startZ; z < startZ + 8; z++) {

                    int y = worldInfo.getMaxHeight()-10;

                    while(limitedRegion.getType(x,y,  z).isAir() && y > limitedRegion.getWorld()
                            .getMinHeight()){
                        y--;
                    }

                    for (int i = y; i < 10; i++) {
                        limitedRegion.setType(x, i, z, Material.PRISMARINE_BRICKS);
                    }

                }
            }


        }


    }

    private enum PillarDirection{
        NORTH,EAST,SOUTH,WEST
    }

}
