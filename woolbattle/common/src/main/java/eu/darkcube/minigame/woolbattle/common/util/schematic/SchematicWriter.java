/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.schematic;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import eu.darkcube.minigame.woolbattle.common.util.math.Vec3i;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.BinaryTagIO;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.BinaryTagTypes;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.CompoundBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.ListBinaryTag;

public class SchematicWriter {
    // private static final Logger LOGGER = LoggerFactory.getLogger(SchematicWriter.class);
    private static final BinaryTagIO.Writer WRITER = BinaryTagIO.writer();

    public static void write(OutputStream out, Schematic schematic) {
        try {
            WRITER.writeNamed(Map.entry("", writeLitematica(schematic)), out, BinaryTagIO.Compression.GZIP);
        } catch (IOException e) {
            e.printStackTrace();
            // LOGGER.error("Failed to write schematic", e);
        }
    }

    private static CompoundBinaryTag writeLitematica(Schematic schematic) {
        var builder = CompoundBinaryTag.builder();
        builder.putInt("Version", 7);
        builder.putInt("MinecraftDataVersion", 3953);
        builder.put("Metadata", writeMetadata(schematic.metadata()));
        builder.put("Regions", writeRegions(schematic.regions()));
        return builder.build();
    }

    private static CompoundBinaryTag writeRegions(List<SchematicRegion> regions) {
        var builder = CompoundBinaryTag.builder();
        for (var region : regions) {
            builder.put(region.name(), writeRegion(region));
        }
        return builder.build();
    }

    private static CompoundBinaryTag writeRegion(SchematicRegion region) {
        var builder = CompoundBinaryTag.builder();
        builder.put("Position", writeVec3i(region.position()));
        builder.put("Size", writeVec3i(region.size()));
        builder.put("BlockStatePalette", writeBlockPalette(region.blockPalette()));
        builder.put("TileEntities", writeTileEntities(region.tileEntities()));
        builder.putLongArray("BlockStates", region.blockBitArray().longArray());
        return builder.build();
    }

    private static ListBinaryTag writeTileEntities(SchematicTileEntities tileEntities) {
        var builder = ListBinaryTag.builder(BinaryTagTypes.COMPOUND);
        for (var tile : tileEntities.entities()) {
            builder.add(writeTile(tile));
        }
        return builder.build();
    }

    private static CompoundBinaryTag writeTile(SchematicTileEntity entity) {
        var builder = CompoundBinaryTag.builder();
        builder.put(entity.additionalData());
        builder.putInt("x", entity.x());
        builder.putInt("y", entity.y());
        builder.putInt("z", entity.z());
        builder.putString("id", entity.id().asString());
        if (entity.components().size() > 0) {
            builder.put("components", entity.components());
        }
        return builder.build();
    }

    private static ListBinaryTag writeBlockPalette(SchematicBlockPalette palette) {
        var builder = ListBinaryTag.builder(BinaryTagTypes.COMPOUND);
        for (var state : palette.states()) {
            builder.add(writeBlockState(state));
        }
        return builder.build();
    }

    private static CompoundBinaryTag writeBlockState(SchematicBlockState state) {
        var builder = CompoundBinaryTag.builder();
        builder.putString("Name", state.name().asString());
        if (state.properties() != null) {
            var propertiesBuilder = CompoundBinaryTag.builder();
            for (var entry : state.properties().entrySet()) {
                propertiesBuilder.putString(entry.getKey(), entry.getValue());
            }
            builder.put("Properties", propertiesBuilder.build());
        }
        return builder.build();
    }

    private static CompoundBinaryTag writeMetadata(SchematicMetadata metadata) {
        var builder = CompoundBinaryTag.builder();
        builder.putString("Name", metadata.name());
        builder.putInt("TotalBlocks", metadata.totalBlocks());
        builder.putInt("TotalVolume", metadata.totalVolume());
        builder.putInt("RegionCount", metadata.regionCount());
        builder.put("EnclosingSize", writeVec3i(metadata.enclosingSize()));
        return builder.build();
    }

    private static CompoundBinaryTag writeVec3i(Vec3i vec) {
        return CompoundBinaryTag.builder().putInt("x", vec.x()).putInt("y", vec.y()).putInt("z", vec.z()).build();
    }
}
