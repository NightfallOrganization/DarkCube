/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.bukkit;

import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.wrapper.event.ServiceInfoPropertiesConfigureEvent;
import eu.darkcube.system.pserver.common.ServiceInfoUtil;
import eu.darkcube.system.pserver.common.UniqueId;

public class ConfigurationListener {

    private final UniqueId uniqueId;

    public ConfigurationListener(UniqueId uniqueId) {
        this.uniqueId = uniqueId;
    }

    @EventListener public void handle(ServiceInfoPropertiesConfigureEvent event) {
        event.propertyHolder().writeProperty(ServiceInfoUtil.property_uid, uniqueId);
    }
}
