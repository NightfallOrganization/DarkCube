/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.schematic;

public record SchematicBlockBitArray(long[] longArray, int bitsPerEntry, long maxEntryValue, int sizeLayer, int sizeX) {
    public SchematicBlockBitArray(int bitsPerEntry, long[] longArray, int sizeX, int sizeZ) {
        this(longArray, bitsPerEntry, (1L << bitsPerEntry) - 1L, sizeX * sizeZ, sizeX);
    }

    public int getAt(int x, int y, int z) {
        return getAt(getIndex(x, y, z));
    }

    public void setAt(int x, int y, int z, int value) {
        setAt(getIndex(x, y, z), value);
    }

    public int getIndex(int x, int y, int z) {
        return (y * this.sizeLayer) + z * this.sizeX + x;
    }

    public int getAt(long index) {
        var startOffset = index * (long) bitsPerEntry;
        var startArrIndex = (int) (startOffset >> 6);
        var endArrIndex = (int) (((index + 1L) * (long) this.bitsPerEntry - 1L) >> 6);
        var startBitOffset = (int) (startOffset & 0x3F);

        if (startArrIndex == endArrIndex) {
            return (int) (longArray[startArrIndex] >>> startBitOffset & maxEntryValue);
        }

        var endOffset = 64 - startBitOffset;
        return (int) ((longArray[startArrIndex] >>> startBitOffset | longArray[endArrIndex] << endOffset) & maxEntryValue);
    }

    public void setAt(long index, int value) {
        var startOffset = index * (long) this.bitsPerEntry;
        var startArrIndex = (int) (startOffset >> 6); // startOffset / 64
        var endArrIndex = (int) (((index + 1L) * (long) this.bitsPerEntry - 1L) >> 6);
        var startBitOffset = (int) (startOffset & 0x3F); // startOffset % 64
        this.longArray[startArrIndex] = this.longArray[startArrIndex] & ~(this.maxEntryValue << startBitOffset) | ((long) value & this.maxEntryValue) << startBitOffset;

        if (startArrIndex != endArrIndex) {
            var endOffset = 64 - startBitOffset;
            var j1 = this.bitsPerEntry - endOffset;
            this.longArray[endArrIndex] = this.longArray[endArrIndex] >>> j1 << j1 | ((long) value & this.maxEntryValue) >> endOffset;
        }
    }
}
