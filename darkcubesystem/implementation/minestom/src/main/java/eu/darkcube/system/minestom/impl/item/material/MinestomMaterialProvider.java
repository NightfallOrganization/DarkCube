/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.impl.item.material;

import static net.minestom.server.item.Material.AIR;
import static net.minestom.server.item.Material.SPAWNER;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.material.Material;
import eu.darkcube.system.server.item.material.MaterialProvider;
import eu.darkcube.system.util.data.Key;

public class MinestomMaterialProvider implements MaterialProvider {
    private final Map<net.minestom.server.item.Material, Material> registry;

    public MinestomMaterialProvider() {
        var registry = new HashMap<net.minestom.server.item.Material, Material>();
        for (var material : net.minestom.server.item.Material.values()) {
            registry.put(material, new MinestomMaterialImpl(material));
        }
        this.registry = Map.copyOf(registry);
    }

    @Override
    public @NotNull Material of(@NotNull Object platformMaterial) throws IllegalArgumentException {
        if (platformMaterial instanceof Material material) return material;
        if (platformMaterial instanceof net.minestom.server.item.Material material) return registry.get(material);
        if (platformMaterial instanceof Key key) {
            var material = net.minestom.server.item.Material.fromNamespaceId(key.toString());
            if (material != null) return of(material);
        }
        throw new IllegalArgumentException("Invalid Material: " + platformMaterial);
    }

    @Override
    public @NotNull Material spawner() throws UnsupportedOperationException {
        return of(SPAWNER);
    }

    @Override
    public @NotNull Material air() {
        return of(AIR);
    }
}
