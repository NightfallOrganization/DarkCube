/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.bukkit.event;

import eu.cloudnetservice.driver.event.Event;
import eu.darkcube.system.pserver.common.PServerExecutor;

public abstract class PServerEvent extends Event {

    private final PServerExecutor pserver;

    public PServerEvent(PServerExecutor pserver) {
        super();
        this.pserver = pserver;
    }

    public PServerExecutor pserver() {
        return pserver;
    }
}
