/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.impl.item.enchant;

import java.util.stream.Stream;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.enchant.Enchantment;
import eu.darkcube.system.server.item.enchant.EnchantmentProvider;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

public class BukkitEnchantmentProvider implements EnchantmentProvider {
    private final Map<Integer, Enchantment> enchantments;

    public BukkitEnchantmentProvider() {
        this.enchantments = HashMap.ofAll(Stream.of(org.bukkit.enchantments.Enchantment.values()), (org.bukkit.enchantments.Enchantment enchantment) -> Map.entry(enchantment.hashCode(), new BukkitEnchantmentImpl(enchantment)));
    }

    @NotNull @Override public Enchantment of(@NotNull Object platformObject) {
        if (platformObject instanceof Enchantment enchantment) return enchantment;
        if (platformObject instanceof org.bukkit.enchantments.Enchantment enchantment) {
            return this.enchantments.get(enchantment.hashCode()).getOrElseThrow(IllegalArgumentException::new);
        }
        throw new IllegalArgumentException("Not a valid enchantment: " + platformObject);
    }
}
