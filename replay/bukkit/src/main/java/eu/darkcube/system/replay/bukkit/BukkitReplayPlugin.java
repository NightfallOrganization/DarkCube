/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.replay.bukkit;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.util.ReflectionUtils;
import eu.darkcube.system.version.VersionSupport;

public class BukkitReplayPlugin extends DarkCubePlugin {

    private final SimpleReplayPlugin plugin;

    public BukkitReplayPlugin() {
        super("replay");
        Class<? extends SimpleReplayPlugin> cls = ReflectionUtils
                .getClass(SimpleReplayPlugin.class.getPackage().getName() + ".v" + VersionSupport
                        .version()
                        .getClassifier() + ".ReplayPlugin")
                .asSubclass(SimpleReplayPlugin.class);
        this.plugin = ReflectionUtils.instantiateObject(cls);
    }

    @Override
    public void onLoad() {
        plugin.onLoad();
    }

    @Override
    public void onEnable() {
        plugin.onEnable();
    }

    @Override
    public void onDisable() {
        plugin.onDisable();
    }
}
