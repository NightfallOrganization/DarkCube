package eu.darkcube.minigame.woolbattle.minestom.world;

import java.util.concurrent.atomic.AtomicLong;

import eu.darkcube.minigame.woolbattle.common.util.math.AABBi;
import eu.darkcube.minigame.woolbattle.common.util.schematic.Schematic;
import eu.darkcube.minigame.woolbattle.common.util.schematic.SchematicRegion;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoidSchematicGenerator implements Generator {
    private static final Logger LOGGER = LoggerFactory.getLogger("SchematicGenerator");
    public static final AtomicLong time = new AtomicLong();
    private final ParsedSchematic schematic;

    public VoidSchematicGenerator(Schematic schematic) {
        this.schematic = new ParsedSchematic(schematic);
    }

    @Override
    public void generate(@NotNull GenerationUnit unit) {
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
        time.addAndGet(took);
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
                    var block = Block.fromNamespaceId(state.name());
                    if (block == null) {
                        LOGGER.error("Unknown Block: {}", state);
                        continue;
                    }
                    modifier.setBlock(x, y, z, block);
                }
            }
        }
    }

    private static AABBi aabb(GenerationUnit unit) {
        var start = unit.absoluteStart();
        var end = unit.absoluteEnd().sub(1, 1, 1);
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