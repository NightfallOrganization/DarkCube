/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.bukkit.version.v1_8_8;

import eu.darkcube.system.bukkit.DarkCubeSystemBukkit;
import eu.darkcube.system.bukkit.provider.via.ViaSupport;
import eu.darkcube.system.bukkit.version.BukkitVersionImpl;
import eu.darkcube.system.bukkit.version.v1_8_8.provider.via.ViaSupport1_8_8;

public class VersionImpl extends BukkitVersionImpl {
    public VersionImpl() {
        this.classifier = "1_8_8";
        this.commandApiUtils = new CommandAPIUtils1_8_8();
        this.itemProvider = new ItemProvider1_8_8();
        this.protocolVersion = 47;
        try {
            provider.register(ViaSupport.class, new ViaSupport1_8_8());
        } catch (Throwable t) {
            provider.register(ViaSupport.class, ViaSupport.wrapper(null));
        }
    }

    @Override public void enabled(DarkCubeSystemBukkit system) {
        super.enabled(system);
        ViaSupport support = provider.service(ViaSupport.class);
        if (support.supported()) ((ViaSupport1_8_8) support).enable();
    }
}
