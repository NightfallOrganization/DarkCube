/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.loader;

import java.nio.file.*;

/**
 * Represents the plugins classloader
 */
public interface PluginClassLoader {

    void loadJar(Path file);

}
