/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.perk;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public class PerkRegistry {
    private final Map<PerkName, Perk> perks = new HashMap<>();
    private final Map<ActivationType, Perk[]> cache = new HashMap<>();

    public void register(Perk perk) {
        if (perks.containsKey(perk.perkName())) {
            throw new IllegalStateException("Another perk is already registered under name " + perk.perkName());
        }
        perks.put(perk.perkName(), perk);
        cache.remove(perk.activationType());
    }

    public boolean contains(@NotNull PerkName perk) {
        return perks.containsKey(perk);
    }

    public Perk[] perks(ActivationType type) {
        return cache.computeIfAbsent(type, n -> perks.values().stream().filter(p -> p.activationType() == n).toArray(Perk[]::new)).clone();
    }

    public Map<PerkName, Perk> perks() {
        return Collections.unmodifiableMap(perks);
    }

    @Nullable
    public Perk perk(@NotNull PerkName name) {
        return perks.get(name);
    }
}
