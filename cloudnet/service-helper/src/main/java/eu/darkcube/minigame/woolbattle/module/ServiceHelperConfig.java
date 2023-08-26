/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.module;

import java.nio.file.Path;
import java.util.Set;

public record ServiceHelperConfig(Set<ServiceTaskEntry> entries, Set<CustomStorage> customStorages) {

    public ServiceHelperConfig(Set<ServiceTaskEntry> entries, Set<CustomStorage> customStorages) {
        this.entries = Set.copyOf(entries);
        this.customStorages = Set.copyOf(customStorages);
    }

    public record CustomStorage(String name, Path path) {
    }

    public record ServiceTaskEntry(String taskName, int minServiceCount, int discrepancy) {
    }
}
