/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module;

import dev.derklaro.aerogel.SpecifiedInjector;
import eu.cloudnetservice.driver.inject.InjectionLayer;

public interface ModuleImplementation {

    void start(InjectionLayer<SpecifiedInjector> injectionLayer);

    void stop(InjectionLayer<SpecifiedInjector> injectionLayer);

}
