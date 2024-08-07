/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldgen;

import eu.darkcube.system.bukkit.DarkCubePlugin;

public class SkylandWorldGenPlugin extends DarkCubePlugin {

    private static SkylandWorldGenPlugin instance;

    public SkylandWorldGenPlugin() {
        super("skylandWorldGen");
        instance = this;
    }
}
