/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.version.v1_8_8;

import eu.darkcube.system.DarkCubeSystem;
import eu.darkcube.system.provider.via.ViaSupport;
import eu.darkcube.system.version.BukkitVersion;
import eu.darkcube.system.version.v1_8_8.provider.via.ViaSupport1_8_8;

public class Version extends BukkitVersion {
    @Override
    public void init() {
        super.init();
        this.classifier = "1_8_8";
        this.commandApi = new CommandAPI1_8_8();
        this.itemProvider = new ItemProvider1_8_8();
        try {
            provider.register(ViaSupport.class, new ViaSupport1_8_8());
        } catch (Throwable t) {
            t.printStackTrace();
            provider.register(ViaSupport.class, ViaSupport.wrapper(null));
        }
    }

    @Override
    public void enabled(DarkCubeSystem system) {
        super.enabled(system);
        ViaSupport support = provider.service(ViaSupport.class);
        if (support.supported()) ((ViaSupport1_8_8) support).enable();
    }
}
