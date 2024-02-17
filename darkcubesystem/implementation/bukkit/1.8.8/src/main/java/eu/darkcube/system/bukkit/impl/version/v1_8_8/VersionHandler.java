/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.impl.version.v1_8_8;

import dev.derklaro.aerogel.binding.BindingBuilder;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.bukkit.impl.version.AbstractVersionHandler;
import eu.darkcube.system.bukkit.impl.version.v1_8_8.item.ItemProviderImpl;
import eu.darkcube.system.server.item.ItemProvider;

public class VersionHandler extends AbstractVersionHandler {
    public VersionHandler() {
        var ext = InjectionLayer.ext();
        ext.install(BindingBuilder.create().bind(ItemProvider.class).toInstance(new ItemProviderImpl()));
    }
}
