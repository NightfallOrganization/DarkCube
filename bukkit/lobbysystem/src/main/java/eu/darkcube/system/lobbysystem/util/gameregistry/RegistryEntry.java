/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util.gameregistry;

import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;

public record RegistryEntry(String taskName, String data, JsonObject protocol) {

    public static final PersistentDataType<RegistryEntry> TYPE = PersistentDataTypes.simple(RegistryEntry.class, entry -> new RegistryEntry(entry.taskName(), entry.data(), entry.protocol().deepCopy()));

    public RegistryEntry(String taskName, String data, JsonObject protocol) {
        this.taskName = taskName;
        this.data = data;
        this.protocol = protocol.deepCopy();
    }
}
