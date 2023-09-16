/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util.gameregistry;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.util.data.PersistentDataType;

public record RegistryEntry(String taskName, String data, Document protocol) {

    public static final PersistentDataType<RegistryEntry> TYPE = new PersistentDataType<>() {
        @Override public RegistryEntry deserialize(Document doc, String key) {
            return doc.readObject(key, RegistryEntry.class);
        }

        @Override public void serialize(Document.Mutable doc, String key, RegistryEntry data) {
            doc.append(key, data);
        }

        @Override public RegistryEntry clone(RegistryEntry object) {
            return object;
        }
    };

    public RegistryEntry(String taskName, String data, Document protocol) {
        this.taskName = taskName;
        this.data = data;
        this.protocol = protocol.immutableCopy();
    }
}
