package eu.darkcube.minigame.woolbattle.common.util.schematic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.darkcube.minigame.woolbattle.common.util.math.Vec3i;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.BinaryTagIO;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.BinaryTagTypes;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.CompoundBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.ListBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.TagStringIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchematicReader {
    private static final Logger LOGGER = LoggerFactory.getLogger("SchematicReader");
    private static final BinaryTagIO.Reader READER = BinaryTagIO.unlimitedReader();

    public static Schematic read(InputStream in) {
        try {
            return read(READER.readNamed(in, BinaryTagIO.Compression.GZIP));
        } catch (IOException e) {
            throw new SchematicReadException("Failed to read schematic NBT", e);
        }
    }

    public static Schematic read(Path path) {
        try {
            return read(Files.newInputStream(path));
        } catch (IOException e) {
            throw new SchematicReadException("Failed to read schematic NBT", e);
        }
    }

    public static Schematic read(Map.Entry<String, CompoundBinaryTag> namedTag) {
        return readLitematica(namedTag.getValue());
    }

    private static Schematic readLitematica(CompoundBinaryTag tag) {
        var metadata = readMetaData(tag.getCompound("Metadata"));
        var regions = readRegions(tag.getCompound("Regions"));
        return new Schematic(metadata, regions);
    }

    private static List<SchematicRegion> readRegions(CompoundBinaryTag tag) {
        var regions = new ArrayList<SchematicRegion>(tag.keySet().size());
        for (var regionName : tag.keySet()) {
            regions.add(readRegion(regionName, tag.getCompound(regionName)));
        }
        return List.copyOf(regions);
    }

    private static SchematicRegion readRegion(String name, CompoundBinaryTag tag) {
        var regionPos = readVec3i(tag.getCompound("Position"));
        var regionSize = readVec3i(tag.getCompound("Size"));
        var paletteList = tag.getList("BlockStatePalette");
        var palette = readBlockPalette(paletteList);

        var regionPosEnd = regionSize.relativeEndPos().add(regionPos);
        var posMin = regionPos.minCorner(regionPosEnd);
        var posMax = regionPos.maxCorner(regionPosEnd);
        var size = posMax.sub(posMin).add(1, 1, 1);

        var blockBitArray = readBlockBitArray(paletteList, tag.getLongArray("BlockStates"), size.x(), size.z());

        return new SchematicRegion(name, posMin, size, palette, blockBitArray);
    }

    private static SchematicBlockBitArray readBlockBitArray(ListBinaryTag paletteList, long[] blockStates, int sizeX, int sizeZ) {
        var bits = Math.max(2, Integer.SIZE - Integer.numberOfLeadingZeros(paletteList.size() - 1));
        return new SchematicBlockBitArray(bits, blockStates, sizeX, sizeZ);
    }

    private static SchematicBlockPalette readBlockPalette(ListBinaryTag tag) {
        var list = new ArrayList<SchematicBlockState>();
        for (var t : tag) {
            if (t.type() == BinaryTagTypes.COMPOUND) {
                list.add(readBlockState((CompoundBinaryTag) t));
            }
        }
        var blockStates = list.toArray(SchematicBlockState[]::new);
        return new SchematicBlockPalette(blockStates);
    }

    private static SchematicBlockState readBlockState(CompoundBinaryTag tag) {
        var name = tag.getString("Name");
        tag = tag.remove("Name");
        if (tag.size() != 0) {
            try {
                LOGGER.warn("Failed to read all block state properties: {}", TagStringIO.get().asString(tag));
            } catch (IOException e) {
                LOGGER.warn("Failed to read block state data", e);
            }
        }
        return new SchematicBlockState(name);
    }

    private static SchematicMetadata readMetaData(CompoundBinaryTag tag) {
        var name = tag.getString("Name", "???");
        var totalBlocks = tag.getInt("TotalBlocks", -1);
        var enclosingSize = readVec3i(tag.getCompound("EnclosingSize"));
        return new SchematicMetadata(name, totalBlocks, enclosingSize);
    }

    private static Vec3i readVec3i(CompoundBinaryTag tag) {
        return new Vec3i(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
    }
}
