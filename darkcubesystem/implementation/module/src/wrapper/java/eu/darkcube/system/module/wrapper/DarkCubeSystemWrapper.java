/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.wrapper;

import dev.derklaro.aerogel.Singleton;
import dev.derklaro.aerogel.SpecifiedInjector;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.module.ModuleImplementation;
import eu.darkcube.system.packetapi.PacketAPI;

@Singleton public class DarkCubeSystemWrapper implements ModuleImplementation {
    @Override public void start(InjectionLayer<SpecifiedInjector> injectionLayer) {
        PacketAPI.init();
    }

    @Override public void stop(InjectionLayer<SpecifiedInjector> injectionLayer) {

    }
}
