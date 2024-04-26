/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util;

import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.document.DocumentFactory;

public interface Serializable {

    default String serialize() {
        return GsonSerializer.gson.toJson(this);
    }

    default Document serializeToDocument() {
        return DocumentFactory.json().parse(serialize());
    }
}
