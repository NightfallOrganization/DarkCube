/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.world;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import net.minestom.server.instance.DynamicChunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.data.LightData;
import org.jetbrains.annotations.NotNull;

public class FullbrightChunk extends DynamicChunk {
    private final LightData lightData;

    public FullbrightChunk(@NotNull Instance instance, int chunkX, int chunkZ) {
        super(instance, chunkX, chunkZ);
        lightData = createLightData0();
    }

    private LightData createLightData0() {
        var skyMask = new BitSet();
        var blockMask = new BitSet();
        var emptySkyMask = new BitSet();
        var emptyBlockMask = new BitSet();
        List<byte[]> skyLights = new ArrayList<>();
        List<byte[]> blockLights = new ArrayList<>();

        var skyLightO = new LitLight();
        // var blockLight0 = new LitLight();

        for (var index = 1; index <= sections.size(); index++) {
            final var skyLight = skyLightO.array();
            // final var blockLight = blockLight0.array();
            if (skyLight.length != 0) {
                skyLights.add(skyLight);
                skyMask.set(index);
            } else {
                emptySkyMask.set(index);
            }
            // if (blockLight.length != 0) {
            //     blockLights.add(blockLight);
            //     blockMask.set(index);
            // } else {
            emptyBlockMask.set(index);
            // }
        }
        return new LightData(skyMask, blockMask, emptySkyMask, emptyBlockMask, skyLights, blockLights);
    }

    // private UpdateLightPacket createLightPacket() {
    //     return new UpdateLightPacket(chunkX, chunkZ, createLightData());
    // }
    //
    @Override
    protected LightData createLightData() {
        return lightData;
    }
}
