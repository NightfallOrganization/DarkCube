/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.version;

@Deprecated(forRemoval = true) public class VersionSupport {

    /**
     * @deprecated {@link Version#version()}
     */
    public static Version version() {
        return Version.version();
    }
}
