/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.converter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

import eu.darkcube.system.libs.net.kyori.adventure.nbt.BinaryTagIO;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.CompoundBinaryTag;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;

public class RegionFile {
    private static final int MAX_ENTRY_COUNT = 1024;
    private static final int SECTOR_SIZE = 4096;
    private static final int HEADER_LENGTH = MAX_ENTRY_COUNT * 2 * 4; // 2 4-byte fields per entry

    private static final int COMPRESSION_ZLIB = 2;
    private static final BinaryTagIO.Reader TAG_READER = BinaryTagIO.unlimitedReader();

    private final int[] locations = new int[MAX_ENTRY_COUNT];
    private final int[] timestamps = new int[MAX_ENTRY_COUNT];
    private final BooleanList freeSectors = new BooleanArrayList(2);
    private final RandomAccessFile file;

    public RegionFile(Path path) throws IOException {
        this.file = new RandomAccessFile(path.toFile(), "r");
        readHeader();
    }

    public boolean hasChunkData(int chunkX, int chunkZ) {
        return locations[getChunkIndex(chunkX, chunkZ)] != 0;
    }

    public @Nullable CompoundBinaryTag readChunkData(int chunkX, int chunkZ) throws IOException {
        if (!hasChunkData(chunkX, chunkZ)) return null;

        var location = locations[getChunkIndex(chunkX, chunkZ)];
        file.seek((long) (location >> 8) * SECTOR_SIZE); // Move to start of first sector
        var length = file.readInt();
        int compressionType = file.readByte();
        var compression = switch (compressionType) {
            case 1 -> BinaryTagIO.Compression.GZIP;
            case COMPRESSION_ZLIB -> BinaryTagIO.Compression.ZLIB;
            case 3 -> BinaryTagIO.Compression.NONE;
            default -> throw new IOException("Unsupported compression type: " + compressionType);
        };

        // Read the raw content
        var data = new byte[length - 1];
        file.read(data);

        // Parse it as a compound tag
        return TAG_READER.read(new ByteArrayInputStream(data), compression);
    }

    public void close() throws IOException {
        file.close();
    }

    private int getChunkIndex(int chunkX, int chunkZ) {
        return (toRegionLocal(chunkZ) << 5) | toRegionLocal(chunkX);
    }

    private void readHeader() throws IOException {
        file.seek(0);
        if (file.length() < HEADER_LENGTH) {
            // new file, fill in data
            file.write(new byte[HEADER_LENGTH]);
        }

        // todo: addPadding()

        final var totalSectors = ((file.length() - 1) / SECTOR_SIZE) + 1; // Round up, last sector does not need to be full size
        for (var i = 0; i < totalSectors; i++) freeSectors.add(true);
        freeSectors.set(0, false); // First sector is locations
        freeSectors.set(1, false); // Second sector is timestamps

        // Read locations
        file.seek(0);
        for (var i = 0; i < MAX_ENTRY_COUNT; i++) {
            var location = locations[i] = file.readInt();
            if (location != 0) {
                markLocation(location, false);
            }
        }

        // Read timestamps
        for (var i = 0; i < MAX_ENTRY_COUNT; i++) {
            timestamps[i] = file.readInt();
        }
    }

    private void markLocation(int location, boolean free) {
        var sectorCount = location & 0xFF;
        var sectorStart = location >> 8;
        // Check.stateCondition(sectorStart + sectorCount > freeSectors.size(), "Invalid sector count");
        for (var i = sectorStart; i < sectorStart + sectorCount; i++) {
            freeSectors.set(i, free);
        }
    }

    public static int toRegionCoordinate(int chunkCoordinate) {
        return chunkCoordinate >> 5;
    }

    public static int toRegionLocal(int chunkCoordinate) {
        return chunkCoordinate & 0x1F;
    }
}
