/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.impl.common.userapi;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataStorage;

@ApiStatus.Internal public interface CommonPersistentDataStorage extends PersistentDataStorage {
    /**
     * Called when the node sends a remove update
     */
    void remove(@NotNull Key key);

    void merge(@NotNull Document document);

    void update(@NotNull Document document);
}
