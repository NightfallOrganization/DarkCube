/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.heads.remote;

import java.util.List;

import eu.darkcube.system.bauserver.heads.Head;

public interface RemoteHeadProvider {
    String name();

    List<String> categories();

    List<Head> heads(String category);
}
