/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.impl.version.v1_8_8;

import eu.darkcube.system.bukkit.impl.DarkCubeSystemBukkit;
import eu.darkcube.system.bukkit.impl.version.BukkitVersionImpl;
import eu.darkcube.system.bukkit.impl.version.v1_8_8.provider.via.ViaSupportImpl;
import eu.darkcube.system.bukkit.provider.via.ViaSupport;

public class Version extends BukkitVersionImpl {
    public Version() {
        this.commandApiUtils = new CommandAPIUtilsImpl();
        this.protocolVersion = 47;
        try {
            provider.register(ViaSupport.class, new ViaSupportImpl());
        } catch (Throwable t) {
            provider.register(ViaSupport.class, ViaSupport.wrapper(null));
        }
    }

    @Override public void enabled(DarkCubeSystemBukkit system) {
        super.enabled(system);
        var support = provider.service(ViaSupport.class);
        if (support.supported()) ((ViaSupportImpl) support).enable();
    }
}
