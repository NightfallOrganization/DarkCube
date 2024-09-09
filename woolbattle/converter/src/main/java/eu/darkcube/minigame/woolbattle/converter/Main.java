/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.converter;

import static eu.darkcube.system.libs.net.kyori.adventure.nbt.StringBinaryTag.stringBinaryTag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import eu.darkcube.minigame.woolbattle.common.util.schematic.SchematicBlockState;
import eu.darkcube.minigame.woolbattle.common.util.schematic.SchematicBuilder;
import eu.darkcube.minigame.woolbattle.common.util.schematic.SchematicTileEntity;
import eu.darkcube.minigame.woolbattle.common.util.schematic.SchematicWriter;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.JsonArray;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.BinaryTagTypes;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.CompoundBinaryTag;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.ListBinaryTag;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class Main {

    private static final Int2ObjectMap<SchematicBlockState[]> mappings = new Int2ObjectOpenHashMap<>();
    private static final String[] colors = new String[]{"white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"};
    private static final IntList failedIds = new IntArrayList();
    private static int deathHeight = 0;
    private static final Map<String, Integer> deathHeightByMap = new HashMap<>();

    static {
        rc(35, "wool");
        rc(159, "terracotta");
        rc(171, "carpet");
        rc(95, "stained_glass");

        r(1, "stone");
        r(1, 1, "granite");
        r(1, 3, "diorite");
        r(1, 5, "andesite");
        r(1, 6, "polished_andesite");
        r(82, "clay");
        r(173, "coal_block");
        r(43, 8, "smooth_stone");
        r(43, 7, "quartz_block");
        r(43, 5, "stone_bricks");
        r(43, 0, "smooth_stone_slab", "type", "double");
        r(2, "grass_block");
        r(3, "dirt");
        r(17, 14, "birch_wood");
        r(17, 6, "birch_log", "axis", "x");
        r(172, "terracotta");
        r(44, 0, "smooth_stone_slab", "type", "bottom");
        r(44, 5, "stone_brick_slab", "type", "bottom");
        r(44, 7, "quartz_slab", "type", "bottom");
        r(44, 8, "smooth_stone_slab", "type", "top");
        r(44, 13, "stone_brick_slab", "type", "top");
        r(44, 15, "quartz_slab", "type", "top");
        r(98, 3, "chiseled_stone_bricks");
        r(113, "nether_brick_fence");
        r(139, "cobblestone_wall");
        r(68, 4, "oak_wall_sign", "facing", "west");
        r(68, 5, "oak_wall_sign", "facing", "east");

        r(156, 0, "quartz_stairs", "facing", "east");
        r(156, 1, "quartz_stairs", "facing", "west");
        r(156, 2, "quartz_stairs", "facing", "south");
        r(156, 3, "quartz_stairs", "facing", "north");
        r(156, 4, "quartz_stairs", "facing", "east", "half", "top");
        r(156, 5, "quartz_stairs", "facing", "west", "half", "top");
        r(156, 6, "quartz_stairs", "facing", "south", "half", "top");
        r(156, 7, "quartz_stairs", "facing", "north", "half", "top");

        r(125, 1, "spruce_planks");

        r(145, 1, "anvil", "facing", "east");
        r(126, 9, "spruce_slab", "half", "top");
        r(69, 6, "lever", "face", "floor", "facing", "west");
        r(138, "beacon");
        r(155, "quartz_block");
        r(123, "redstone_lamp");
        r(71, 1, "iron_door", "half", "lower", "facing", "south");
        r(71, 3, "iron_door", "half", "lower");
        r(198, "end_rod");
        r(167, 13, "iron_trapdoor", "facing", "south", "open", "true");
        r(167, 3, "iron_trapdoor");
        r(167, 12, "iron_trapdoor", "open", "true");

        r(41, "gold_block");
        r(26, 3, "red_bed", "facing", "east");

        r(5, 0, "oak_planks");
        r(5, 1, "spruce_planks");
        r(5, 2, "birch_planks");
        r(5, 3, "jungle_planks");
        r(5, 4, "acacia_planks");
        r(25, "note_block");
        r(24, 2, "smooth_sandstone");
        r(22, "lapis_block");
        r(98, "stone_bricks");
        r(109, 0, "stone_brick_stairs", "facing", "east");
        r(109, 1, "stone_brick_stairs", "facing", "west");
        r(109, 2, "stone_brick_stairs", "facing", "south");
        r(109, 3, "stone_brick_stairs", "facing", "north");
        r(109, 4, "stone_brick_stairs", "half", "top", "facing", "east");
        r(109, 5, "stone_brick_stairs", "half", "top", "facing", "west");
        r(109, 6, "stone_brick_stairs", "half", "top", "facing", "south");
        r(109, 7, "stone_brick_stairs", "half", "top", "facing", "north");
        r(168, 2, "prismarine_bricks");
        r(155, 1, "chiseled_quartz_block");
        r(166, "barrier");

        r(4, "cobblestone");
        r(18, 4, "oak_leaves");
        r(18, 5, "spruce_leaves");
        r(18, 12, "oak_leaves");
        r(18, 13, "spruce_leaves");
        r(85, "oak_fence"); // TODO check map for block connections
        r(20, "glass");
        r(169, "sea_lantern");
        r(89, "glowstone");
        r(86, 0, "carved_pumpkin", "facing", "south");
        r(86, 1, "carved_pumpkin", "facing", "west");
        r(86, 2, "carved_pumpkin", "facing", "north");
        r(86, 3, "carved_pumpkin", "facing", "east");

    }

    private static void rc(int id, String name) {
        for (var i = 0; i < colors.length; i++) {
            r(id, i, colors[i] + "_" + name);
        }
    }

    private static void r(int id, String name) {
        r(id, 0, name);
    }

    private static void r(int id, int data, String name) {
        r(id, data, name, Map.of());
    }

    private static void r(int id, int data, String name, String n1, String v1) {
        r(id, data, name, Map.of(n1, v1));
    }

    private static void r(int id, int data, String name, String n1, String v1, String n2, String v2) {
        r(id, data, name, Map.of(n1, v1, n2, v2));
    }

    private static void r(int id, int data, String name, @Nullable Map<String, String> properties) {
        if (data >= 16 || data < 0) throw new IllegalArgumentException();
        mappings.computeIfAbsent(id, _ -> new SchematicBlockState[16])[data] = new SchematicBlockState(Key.key(name), properties);
    }

    public static void main() throws IOException {
        var woolbattleDir = Path.of("woolbattle").toAbsolutePath();
        System.out.println("Working in " + woolbattleDir.getParent());
        readMapMetas(woolbattleDir);
        Files.list(woolbattleDir).forEach(sizeDir -> {
            if (!Files.isDirectory(sizeDir)) return;
            try {
                convertSize(sizeDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void readMapMetas(Path dir) throws IOException {
        var gson = new Gson();
        var reader = Files.newBufferedReader(dir.resolve("woolbattle_maps_old.json"), StandardCharsets.UTF_8);
        var array = gson.fromJson(reader, JsonArray.class);
        reader.close();
        for (var element : array) {
            if (element instanceof JsonObject object) {
                if (object.has("data")) {
                    readMapMetas(object.getAsJsonArray("data"));
                }
            }
        }
    }

    public static byte[] hexStringToByteArray(String hex) {
        var l = hex.length();
        var data = new byte[l / 2];
        for (var i = 0; i < l; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    private static void readMapMetas(JsonArray data) {
        for (var mapElement : data) {
            var o = (JsonObject) mapElement;
            var name = o.get("Name").getAsString();
            var hex = o.get("Document").getAsString().substring(2);
            var bytes = hexStringToByteArray(hex);
            var json = new Gson().fromJson(new String(bytes), JsonObject.class);
            var mapHeight = json.get("deathHeight").getAsInt();
            if (name.equals("Baukloetze-2x6") || name.equals("Cubes-2x6") || name.equals("Rush-2x6") || name.equals("Tetris-2x6") || name.equals("Tetris2D-2x6") || name.equals("Spiral-2x6") || name.equals("BlocksMega-2x6")) {
                mapHeight = 70;
            }
            deathHeightByMap.put(name, mapHeight);
        }
    }

    private static void convertSize(Path sizeDir) throws IOException {
        var size = sizeDir.getFileName().toString();
        Files.list(sizeDir).forEach(w -> {
            try {
                convertMap(size, w);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void convertMap(String size, Path worldDir) throws IOException {
        var map = worldDir.getFileName().toString();
        var mapHeightY = deathHeightByMap.remove(map + "-" + size);
        var offsetY = deathHeight - mapHeightY;
        System.out.println(mapHeightY);
        System.out.println("Converting " + map + "-" + size);
        var regionFiles = Files.list(worldDir.resolve("region")).toList();
        var builder = new SchematicBuilder();
        builder.name(map);
        for (var regionFile : regionFiles) {
            convertRegion(builder, offsetY, size, map, regionFile);
        }
        var schematic = builder.build();
        var output = Path.of("schematics").resolve(size).resolve(map + ".litematic");
        Files.createDirectories(output.getParent());
        try (var out = Files.newOutputStream(output)) {
            SchematicWriter.write(out, schematic);
        }
    }

    private static void convertRegion(SchematicBuilder builder, int offsetY, String size, String map, Path regionFile) throws IOException {
        var fileName = regionFile.getFileName().toString();
        fileName = fileName.substring(2);
        fileName = fileName.substring(0, fileName.length() - 4);
        var parts = fileName.split("\\.");
        var regionX = Integer.parseInt(parts[0]);
        var regionZ = Integer.parseInt(parts[1]);
        var region = new RegionFile(regionFile);
        var startX = regionX * 32;
        var startZ = regionZ * 32;
        for (var relX = 0; relX < 32; relX++) {
            for (var relZ = 0; relZ < 32; relZ++) {
                if (!region.hasChunkData(relX, relZ)) continue;

                var chunkX = startX + relX;
                var chunkZ = startZ + relZ;

                var chunkData = Objects.requireNonNull(region.readChunkData(relX, relZ));
                var level = (CompoundBinaryTag) Objects.requireNonNull(chunkData.get("Level"));
                loadChunk(chunkX, chunkZ, builder, level, offsetY);

                // System.out.println(TagStringIO.get().asString(chunkData));
            }
        }
    }

    private static void loadChunk(int chunkX, int chunkZ, SchematicBuilder builder, CompoundBinaryTag level, int offsetY) {
        for (var sectionTag : level.getList("Sections", BinaryTagTypes.COMPOUND)) {
            var sectionData = (CompoundBinaryTag) sectionTag;
            var sectionY = sectionData.getByte("Y");
            var baseY = 16 * sectionY + offsetY;
            var blockBytes = sectionData.getByteArray("Blocks");
            var blockData = sectionData.getByteArray("Data");
            if (sectionData.keySet().contains("Add")) throw new IllegalArgumentException("Add Tag found!");

            for (var relX = 0; relX < 16; relX++) {
                for (var relY = 0; relY < 16; relY++) {
                    for (var relZ = 0; relZ < 16; relZ++) {
                        var i = relY * 256 + relZ * 16 + relX;
                        int blockByte = blockBytes[i];
                        if (blockByte == 0) continue;
                        var data = Nibble4(blockData, i);
                        var mapping = mapping(blockByte, data);
                        if (mapping == null) continue;
                        var x = chunkX * 16 + relX;
                        var y = baseY + relY;
                        var z = chunkZ * 16 + relZ;
                        builder.setBlock(x, y, z, mapping);
                    }
                }
            }
        }
        for (var tileTag : level.getList("TileEntities")) {
            var tag = (CompoundBinaryTag) tileTag;
            var id = tag.getString("id");
            var x = tag.getInt("x");
            var y = tag.getInt("y") + offsetY;
            var z = tag.getInt("z");

            if (id.equals("Sign")) {
                convertSign(builder, x, y, z, tag);
            } else if (id.equals("Beacon")) {
            } else {
                throw new UnsupportedOperationException("Unsupported tile: " + id + " in chunk " + chunkX + ", " + chunkZ);
            }
        }
    }

    private static void convertSign(SchematicBuilder builder, int x, int y, int z, CompoundBinaryTag tag) {
        var id = Key.key("sign");
        var text1 = tag.getString("Text1");
        var text2 = tag.getString("Text2");
        var text3 = tag.getString("Text3");
        var text4 = tag.getString("Text4");
        var data = CompoundBinaryTag.builder();
        data.put("front_text", text(text1, text2, text3, text4));
        data.put("back_text", text("\"\"", "\"\"", "\"\"", "\"\""));

        var tile = new SchematicTileEntity(x, y, z, id, CompoundBinaryTag.empty(), data.build());
        builder.addTile(tile);
    }

    private static CompoundBinaryTag text(String t1, String t2, String t3, String t4) {
        return CompoundBinaryTag.builder().putBoolean("has_glowing_text", false).putString("color", "black").put("messages", ListBinaryTag.builder().add(stringBinaryTag(t1)).add(stringBinaryTag(t2)).add(stringBinaryTag(t3)).add(stringBinaryTag(t4)).build()).build();
    }

    private static final SchematicBlockState[] EMPTY = new SchematicBlockState[16];

    private static @Nullable SchematicBlockState mapping(int id, byte data) {
        if (id < 0) id += 256;

        var mapping = mappings.getOrDefault(id, EMPTY)[data];
        if (mapping == null) {
            if (!failedIds.contains(id)) {
                failedIds.add(id);
                System.out.println("Unknown ID: " + id + ":" + data);
            }
        }
        return mapping;
    }

    public static byte Nibble4(byte[] arr, int index) {
        return (byte) (index % 2 == 0 ? arr[index / 2] & 0x0F : (arr[index / 2] >> 4) & 0x0F);
    }
}
