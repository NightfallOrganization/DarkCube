/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.extension;

import eu.darkcube.system.annotations.ThreadSafe;

import java.nio.file.Path;
import java.util.Collection;

@ThreadSafe public interface ExtensionManager {

    /**
     * Loads all extensions in the given {@code paths} in correct order
     */
    void loadExtensions(Collection<Path> paths);

    void loadExtension(Extension extension);

    void unloadExtension(Extension extension);

}