/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet;

import java.util.HashSet;
import java.util.Set;

public record PServerConfiguration(String database, Set<String> deploymentExclusions) {
    public PServerConfiguration(String database, Set<String> deploymentExclusions) {
        this.database = database;
        this.deploymentExclusions = new HashSet<>(deploymentExclusions);
    }
}
