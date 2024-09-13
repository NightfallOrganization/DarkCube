/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.heads.remote;

import java.util.List;

public class Providers {
    private static final List<RemoteHeadProvider> PROVIDERS = List.of(new MinecraftHeadsComProvider());

    public static List<RemoteHeadProvider> providers() {
        return PROVIDERS;
    }
}
