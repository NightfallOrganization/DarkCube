package eu.darkcube.minigame.woolbattle.minestom.world;

import static net.minestom.server.item.Material.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import eu.darkcube.minigame.woolbattle.api.world.ColoredWool;
import eu.darkcube.minigame.woolbattle.api.world.ColoredWoolProvider;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.item.material.MinestomMaterial;
import eu.darkcube.system.server.item.material.Material;

public class MinestomColoredWoolProvider implements ColoredWoolProvider {
    private final List<Material> woolMaterials = List.of(Stream.of(WHITE_WOOL, ORANGE_WOOL, MAGENTA_WOOL, LIGHT_BLUE_WOOL, YELLOW_WOOL, LIME_WOOL, PINK_WOOL, GRAY_WOOL, LIGHT_GRAY_WOOL, CYAN_WOOL, PURPLE_WOOL, BLUE_WOOL, BROWN_WOOL, GREEN_WOOL, RED_WOOL, BLACK_WOOL).map(Material::of).toArray(Material[]::new));
    private final List<MinestomColoredWool> woolColors = List.of(woolMaterials.stream().map(MinestomColoredWool::new).toArray(MinestomColoredWool[]::new));

    @Override
    public @NotNull MinestomColoredWool defaultWool() {
        return new MinestomColoredWool(woolMaterials.getFirst());
    }

    @Override
    public @NotNull String serializeToString(@NotNull ColoredWool wool) {
        var minestom = (MinestomColoredWool) wool;
        var material = ((MinestomMaterial) minestom.material()).minestomType();
        return material.namespace().toString();
    }

    @Override
    public @NotNull MinestomColoredWool deserializeFromString(@NotNull String serialized) {
        var minestomMaterial = fromNamespaceId(serialized);
        if (minestomMaterial == null) return defaultWool();
        var material = Material.of(minestomMaterial);
        return new MinestomColoredWool(material);
    }

    @Override
    public @NotNull Collection<MinestomColoredWool> woolColors() {
        return woolColors;
    }

    public List<Material> woolMaterials() {
        return woolMaterials;
    }
}
