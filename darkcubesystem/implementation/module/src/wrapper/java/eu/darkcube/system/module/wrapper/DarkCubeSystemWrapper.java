/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.wrapper;

import dev.derklaro.aerogel.Inject;
import dev.derklaro.aerogel.Singleton;
import dev.derklaro.aerogel.SpecifiedInjector;
import dev.derklaro.aerogel.binding.BindingBuilder;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.module.ModuleImplementation;
import eu.darkcube.system.module.wrapper.userapi.WrapperUserAPI;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.AsyncExecutor;

@Singleton
public class DarkCubeSystemWrapper implements ModuleImplementation {
    private WrapperUserAPI userAPI;

    @Inject
    public DarkCubeSystemWrapper(WrapperUserAPI userAPI) {
        this.userAPI = userAPI;
        InjectionLayer.boot().install(BindingBuilder.create().bind(UserAPI.class).toInstance(userAPI));
    }

    @Override
    public void start(InjectionLayer<SpecifiedInjector> injectionLayer) {
        AsyncExecutor.start();
        PacketAPI.init();
    }

    @Override
    public void stop(InjectionLayer<SpecifiedInjector> injectionLayer) {
        userAPI.close();
        AsyncExecutor.stop();
    }
}
