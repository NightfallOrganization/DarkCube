/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.world;

import eu.darkcube.minigame.woolbattle.common.util.math.AABBi;
import eu.darkcube.minigame.woolbattle.common.util.schematic.Schematic;
import eu.darkcube.minigame.woolbattle.common.util.schematic.SchematicRegion;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.world.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoidSchematicGenerator implements Generator {
    private static final Logger LOGGER = LoggerFactory.getLogger("SchematicGenerator");
    private final ParsedSchematic schematic;
    private final DynamicRegistry.Key<Biome> biome;
    private final int deathHeight;

    public VoidSchematicGenerator(Schematic schematic, DynamicRegistry.Key<Biome> biome, int deathHeight) {
        this.schematic = new ParsedSchematic(schematic);
        this.biome = biome;
        this.deathHeight = deathHeight;
    }

    @Override
    public void generate(@NotNull GenerationUnit unit) {
        unit.modifier().fillBiome(biome);
        var start = System.nanoTime();
        var unitAABB = aabb(unit);
        var intersects = false;
        for (var region : schematic.regions) {
            if (unitAABB.intersects(region.aabb)) {
                intersects = true;
                break;
            }
        }
        if (!intersects) return; // No schematic in unit, generate void world

        // Fill unit with schematic
        var regions = schematic.schematic.regions();
        for (var i = 0; i < regions.size(); i++) {
            var region = regions.get(i);

            placeRegion(unit, region, unitAABB);
        }
        var took = System.nanoTime() - start;
    }

    private void placeRegion(GenerationUnit unit, SchematicRegion region, AABBi unitAABB) {
        var startPos = region.position();
        var endPos = region.position().add(region.size());
        var modifier = unit.modifier();

        var startX = startPos.x();
        var startY = startPos.y();
        var startZ = startPos.z();
        for (var x = startX; x < endPos.x(); x++) {
            if (!unitAABB.containsX(x)) continue;
            var relX = x - startX;
            for (var z = startZ; z < endPos.z(); z++) {
                if (!unitAABB.containsZ(z)) continue;
                var relZ = z - startZ;
                for (var y = startY; y < endPos.y(); y++) {
                    if (!unitAABB.containsY(y)) continue;
                    var relY = y - startY;
                    var index = region.blockBitArray().getAt(relX, relY, relZ);
                    var state = region.blockPalette().states()[index];
                    var block = Block.fromNamespaceId(state.name().asString());
                    if (block == null) {
                        LOGGER.error("Unknown Block: {}", state);
                        continue;
                    }
                    if (state.properties() != null) {
                        for (var entry : state.properties().entrySet()) {
                            block = block.withProperty(entry.getKey(), entry.getValue());
                        }
                    }
                    modifier.setBlock(x, y + deathHeight, z, block);
                }
            }
        }
    }

    private AABBi aabb(GenerationUnit unit) {
        var start = unit.absoluteStart().sub(0, deathHeight, 0);
        var end = unit.absoluteEnd().sub(1, 1 + deathHeight, 1);
        return new AABBi(start.blockX(), end.blockX(), start.blockY(), end.blockY(), start.blockZ(), end.blockZ());
    }

    private record ParsedSchematic(Schematic schematic, ParsedRegion[] regions) {
        public ParsedSchematic(Schematic schematic) {
            this(schematic, parseRegions(schematic));
        }

        private static ParsedRegion[] parseRegions(Schematic schematic) {
            var regions = new ParsedRegion[schematic.regions().size()];
            for (var i = 0; i < regions.length; i++) {
                var region = schematic.regions().get(i);
                var parsed = new ParsedRegion(region);
                regions[i] = parsed;
            }
            return regions;
        }
    }

    private record ParsedRegion(AABBi aabb) {
        public ParsedRegion(SchematicRegion region) {
            this(new AABBi(region.position(), region.size()));
        }
    }
}
