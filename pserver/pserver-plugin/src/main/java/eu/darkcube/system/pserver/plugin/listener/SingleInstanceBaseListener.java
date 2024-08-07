/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.listener;

import eu.darkcube.system.pserver.plugin.util.SingleInstance;

public class SingleInstanceBaseListener extends SingleInstance implements BaseListener {

    public SingleInstanceBaseListener() {
        register();
    }
}
