/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.registry;

import static eu.darkcube.system.woolmania.enums.Tiers.*;
import static eu.darkcube.system.woolmania.util.message.CustomItemNames.*;
import static org.bukkit.Material.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.woolmania.enums.Tiers;
import eu.darkcube.system.woolmania.util.message.CustomItemNames;
import org.bukkit.Material;

public class WoolRegistry {

    private final Map<Tiers, List<Entry>> byTier = new HashMap<>();
    private final Map<CustomItemNames, Entry> byName = new HashMap<>();
    private final Map<Material, Entry> byMaterial = new HashMap<>();

    public WoolRegistry() {
        register(ITEM_NAME_WHITE_WOOL, WHITE_WOOL, TIER1);
        register(ITEM_NAME_ORANGE_WOOL, ORANGE_WOOL, TIER2);
        register(ITEM_NAME_MAGENTA_WOOL, MAGENTA_WOOL, TIER3);
        register(ITEM_NAME_LIGHT_BLUE_WOOL, LIGHT_BLUE_WOOL, TIER4);
        register(ITEM_NAME_YELLOW_WOOL, YELLOW_WOOL, TIER5);
        register(ITEM_NAME_LIME_WOOL, LIME_WOOL, TIER6);
        register(ITEM_NAME_PINK_WOOL, PINK_WOOL, TIER7);
        register(ITEM_NAME_GRAY_WOOL, GRAY_WOOL, TIER8);
        register(ITEM_NAME_LIGHT_GRAY_WOOL, LIGHT_GRAY_WOOL, TIER9);
        register(ITEM_NAME_CYAN_WOOL, CYAN_WOOL, TIER10);
        register(ITEM_NAME_PURPLE_WOOL, PURPLE_WOOL, TIER11);
        register(ITEM_NAME_BLUE_WOOL, BLUE_WOOL, TIER12);
        register(ITEM_NAME_BROWN_WOOL, BROWN_WOOL, TIER13);
        register(ITEM_NAME_GREEN_WOOL, GREEN_WOOL, TIER14);
        register(ITEM_NAME_RED_WOOL, RED_WOOL, TIER15);
        register(ITEM_NAME_BLACK_WOOL, BLACK_WOOL, TIER16);
    }

    private void register(CustomItemNames name, Material material, Tiers tier) {
        Entry entry = new Entry(name, material, tier);
        byTier.computeIfAbsent(tier, _ -> new ArrayList<>()).add(entry);
        byName.put(name, entry);
        byMaterial.put(material, entry);
    }

    public @Nullable Collection<Entry> get(Tiers tier) {
        if (!byTier.containsKey(tier)) throw new IllegalStateException("WoolRegistry does not contain an entry with tier " + tier);
        return Collections.unmodifiableCollection(byTier.get(tier));
    }

    public Entry getSingle(Tiers tier) {
        List<Entry> c = byTier.get(tier);
        if (c == null || c.size() != 1) throw new IllegalStateException("WoolRegistry does not contain a single entry with tier " + tier);
        return c.getFirst();
    }

    public Entry get(CustomItemNames name) {
        if (!byName.containsKey(name)) throw new IllegalStateException("WoolRegistry does not contain a single entry with name " + name);
        return byName.get(name);
    }

    public Entry get(Material material) {
        if (!byMaterial.containsKey(material)) throw new IllegalStateException("WoolRegistry does not contain a single entry with material " + material);
        return byMaterial.get(material);
    }

    public boolean contains(Material material) {
        return byMaterial.containsKey(material);
    }

    public record Entry(CustomItemNames name, Material material, Tiers tier) {
    }
}
