/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.bukkit.version.v1_20_1;

import eu.darkcube.system.bukkit.DarkCubeSystemBukkit;
import eu.darkcube.system.bukkit.provider.via.ViaSupport;
import eu.darkcube.system.bukkit.util.WorkbenchUtil;
import eu.darkcube.system.bukkit.version.BukkitVersionImpl;
import eu.darkcube.system.bukkit.version.v1_20_1.util.WorkbenchUtil1_20_1;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class VersionImpl extends BukkitVersionImpl {
    public VersionImpl() {
        this.commandApiUtils = new CommandAPIUtils1_20_1();
        this.itemProvider = new ItemProvider1_20_1();
        this.classifier = "1_20_1";
        this.protocolVersion = Bukkit.getUnsafe().getProtocolVersion();
        provider.register(ViaSupport.class, ViaSupport.wrapper(null)); // Unsupported
        provider.register(WorkbenchUtil.class, new WorkbenchUtil1_20_2());
    }

    @Override public void enabled(DarkCubeSystemBukkit system) {
        super.enabled(system);
        Bukkit.getPluginManager().registerEvents((Listener) commandApiUtils, system);
        ((CommandAPIUtils1_20_1) commandApiUtils).enabled(system);
    }
}
