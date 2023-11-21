/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module;

import dev.derklaro.aerogel.SpecifiedInjector;
import dev.derklaro.reflexion.Reflexion;
import eu.cloudnetservice.driver.ComponentInfo;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import jakarta.inject.Named;

import java.io.File;
import java.util.Locale;
import java.util.Optional;

public class DarkCubeSystemModule extends DriverModule {
    public static final String PLUGIN_NAME = new File(DarkCubeSystemModule.class
            .getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .getPath()).getName();
    private ModuleImplementation implementation = null;

    @ModuleTask(lifecycle = ModuleLifeCycle.STARTED)
    public void start(@Named("module") InjectionLayer<SpecifiedInjector> injectionLayer, ComponentInfo componentInfo) {
        try {
            String environmentName = componentInfo.environment().name();
            String simpleName = "DarkCubeSystem" + environmentName.substring(0, 1).toUpperCase(Locale.ROOT) + environmentName.substring(1);
            String className = getClass().getPackageName() + "." + environmentName + "." + simpleName;
            Optional<Reflexion> reflexion;
            try {
                Class<?> cls = Class.forName(className);
                reflexion = Optional.of(Reflexion.on(cls));
            } catch (ClassNotFoundException ignored) {
                reflexion = Optional.empty();
            }
            if (reflexion.isEmpty()) throw new AssertionError("Implementation not found: " + className);

            implementation = injectionLayer.instance(reflexion.get().getWrappedClass().asSubclass(ModuleImplementation.class));
            implementation.start(injectionLayer);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STOPPED) public void stop(@Named("module") InjectionLayer<SpecifiedInjector> injectionLayer) {
        if (implementation == null) return;
        implementation.stop(injectionLayer);
        implementation = null;
    }
}