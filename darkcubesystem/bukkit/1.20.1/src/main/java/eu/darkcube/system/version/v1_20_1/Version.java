/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.version.v1_20_1;

import eu.darkcube.system.DarkCubeSystem;
import eu.darkcube.system.provider.via.ViaSupport;
import eu.darkcube.system.util.WorkbenchUtil;
import eu.darkcube.system.version.BukkitVersion;
import eu.darkcube.system.version.v1_20_1.util.WorkbenchUtil1_20_1;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class Version extends BukkitVersion {

    @Override public void init() {
        super.init();
        this.commandApi = new CommandAPI1_20_1();
        this.itemProvider = new ItemProvider1_20_1();
        this.classifier = "1_20_1";
        this.protocolVersion = Bukkit.getUnsafe().getProtocolVersion();
        provider.register(ViaSupport.class, ViaSupport.wrapper(null)); // Unsupported
        provider.register(WorkbenchUtil.class, new WorkbenchUtil1_20_1());
    }

    @Override public void enabled(DarkCubeSystem system) {
        super.enabled(system);
        Bukkit.getPluginManager().registerEvents((Listener) commandApi, system);
        ((CommandAPI1_20_1) commandApi).enabled(system);
    }
}
