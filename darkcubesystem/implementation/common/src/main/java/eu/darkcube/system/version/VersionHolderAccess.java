/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.version;

import eu.darkcube.system.annotations.Api;

@Api public interface VersionHolderAccess {
    @Api static void instance(Version version) {
        VersionHolder.instance(version);
    }
}
