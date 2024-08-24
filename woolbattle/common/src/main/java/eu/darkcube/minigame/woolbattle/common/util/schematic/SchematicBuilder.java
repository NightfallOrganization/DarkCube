/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.schematic;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.List;

import eu.darkcube.minigame.woolbattle.common.util.math.Vec3i;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

public class SchematicBuilder {
    private final Object2IntMap<SchematicBlockState> reverseBlockStates = new Object2IntOpenHashMap<>();
    private final ObjectList<SchematicBlockState> blockStates = new ObjectArrayList<>();
    private final Object2IntMap<Pos> blocks = new Object2IntOpenHashMap<>();
    private final List<SchematicTileEntity> tileEntities = new ArrayList<>();
    private String name = "Unknown";
    private int bits = 2;
    private int posX = Integer.MAX_VALUE;
    private int posY = Integer.MAX_VALUE;
    private int posZ = Integer.MAX_VALUE;
    private int maxX = 0;
    private int maxY = 0;
    private int maxZ = 0;

    public SchematicBuilder() {
        checkAir();
    }

    public void name(String name) {
        this.name = name;
    }

    private void checkAir() {
        if (blockStates.isEmpty()) {
            blockStates.add(SchematicBlockState.AIR);
            reverseBlockStates.put(SchematicBlockState.AIR, 0);
        }
    }

    public void addTile(SchematicTileEntity tile) {
        this.tileEntities.add(tile);
    }

    public void setBlock(int x, int y, int z, SchematicBlockState block) {
        var pos = new Pos(x, y, z);
        if (blocks.containsKey(pos)) throw new IllegalStateException("Can't add block at position " + pos + " twice");
        expand(x, y, z);
        int key;
        if (this.reverseBlockStates.containsKey(block)) {
            key = this.reverseBlockStates.getInt(block);
        } else {
            key = this.blockStates.size();
            this.blockStates.add(block);
            this.reverseBlockStates.put(block, key);
        }
        this.blocks.put(pos, key);

        if (key >= (1 << this.bits)) {
            this.bits++;
        }
    }

    public Schematic build() {
        var totalBlocks = this.blocks.size();
        var region = buildRegion();
        var s = region.size();
        var metadata = new SchematicMetadata(this.name, totalBlocks, s.x() * s.y() * s.z(), 1, region.size());
        return new Schematic(metadata, List.of(region));
    }

    private SchematicRegion buildRegion() {
        if (this.posX == Integer.MAX_VALUE) this.posX = this.maxX;
        if (this.posY == Integer.MAX_VALUE) this.posY = this.maxY;
        if (this.posZ == Integer.MAX_VALUE) this.posZ = this.maxZ;
        var sizeX = this.maxX - this.posX;
        var sizeY = this.maxY - this.posY;
        var sizeZ = this.maxZ - this.posZ;
        var size = new Vec3i(sizeX, sizeY, sizeZ);
        var position = new Vec3i(this.posX, this.posY, this.posZ);
        var blockPalette = buildPalette();
        var blockBitArray = buildBitArray(size);
        var tileEntities = buildTileEntities();
        return new SchematicRegion(this.name, position, size, blockPalette, blockBitArray, tileEntities);
    }

    private SchematicTileEntities buildTileEntities() {
        return new SchematicTileEntities(this.tileEntities.stream().map(e -> e.withPos(e.x() - posX, e.y() - posY, e.z() - posZ)).toList());
    }

    private SchematicBlockPalette buildPalette() {
        var states = blockStates.toArray(SchematicBlockState[]::new);
        return new SchematicBlockPalette(states);
    }

    private SchematicBlockBitArray buildBitArray(Vec3i size) {
        var bitsPerEntry = this.bits;
        var volume = size.x() * size.y() * size.z();
        var longArray = new long[(int) (roundUp((long) volume * (long) bitsPerEntry, 64L) / 64L)];
        var bitArray = new SchematicBlockBitArray(bitsPerEntry, longArray, size.x(), size.z());
        for (var entry : this.blocks.object2IntEntrySet()) {
            var pos = entry.getKey();
            var value = entry.getIntValue();
            var x = pos.x - posX;
            var y = pos.y - posY;
            var z = pos.z - posZ;
            bitArray.setAt(x, y, z, value);
        }
        return bitArray;
    }

    private void expand(int x, int y, int z) {
        this.posX = min(this.posX, x);
        this.maxX = max(this.maxX, x + 1);
        this.posY = min(this.posY, y);
        this.maxY = max(this.maxY, y + 1);
        this.posZ = min(this.posZ, z);
        this.maxZ = max(this.maxZ, z + 1);
    }

    private record Pos(int x, int y, int z) {
    }

    public static long roundUp(long number, long interval) {
        if (interval == 0) {
            return 0;
        } else if (number == 0) {
            return interval;
        } else {
            if (number < 0) {
                interval *= -1;
            }
            var i = number % interval;
            return i == 0 ? number : number + interval - i;
        }
    }
}
