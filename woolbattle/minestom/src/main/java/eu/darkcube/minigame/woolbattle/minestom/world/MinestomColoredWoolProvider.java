/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.world;

import static net.minestom.server.item.Material.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import eu.darkcube.minigame.woolbattle.api.world.ColoredWool;
import eu.darkcube.minigame.woolbattle.api.world.ColoredWoolProvider;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.minestom.item.material.MinestomMaterial;
import eu.darkcube.system.server.item.material.Material;
import net.minestom.server.MinecraftServer;
import net.minestom.server.gamedata.tags.Tag;

public class MinestomColoredWoolProvider implements ColoredWoolProvider {
    private final List<Material> woolMaterials;
    private final List<MinestomColoredWool> woolColors;

    public MinestomColoredWoolProvider() {
        var tag = Objects.requireNonNull(MinecraftServer.getTagManager().getTag(Tag.BasicType.ITEMS, "minecraft:wool"));
        woolMaterials = List.copyOf(tag.getValues().stream().map(Material::of).toList());
        woolColors = List.copyOf(woolMaterials.stream().map(MinestomColoredWool::new).toList());
    }

    @Override
    public @NotNull MinestomColoredWool defaultWool() {
        return woolColors.getFirst();
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
        if (minestomMaterial == null) {
            // Try fallback color names
            var wool = switch (serialized) {
                case "red" -> RED_WOOL;
                case "blue", "aqua" -> BLUE_WOOL;
                case "green", "lime" -> GREEN_WOOL;
                case "white" -> WHITE_WOOL;
                case "purple" -> PURPLE_WOOL;
                case "yellow" -> YELLOW_WOOL;
                case "brown" -> BROWN_WOOL;
                case "black" -> BLACK_WOOL;
                default -> null;
            };
            if (wool != null) {
                return Objects.requireNonNull(woolFrom(Material.of(wool)));
            }
            return defaultWool();
        }
        var material = Material.of(minestomMaterial);
        return Objects.requireNonNull(woolFrom(material));
    }

    @Override
    public @Nullable MinestomColoredWool woolFrom(@NotNull Material material) {
        for (var woolColor : woolColors) {
            if (woolColor.material() == material) return woolColor;
        }
        return null;
    }

    @Override
    public @NotNull Collection<MinestomColoredWool> woolColors() {
        return woolColors;
    }

    public List<Material> woolMaterials() {
        return woolMaterials;
    }
}
