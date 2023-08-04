/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.glyphwidthloader;

public record ResourceLocation(String namespace, String path) {

    public static ResourceLocation fromString(String string) {
        String[] a = string.split(":");
        if (a.length == 1) a = new String[]{"minecraft", a[0]};
        return new ResourceLocation(a[0], a[1]);
    }
}
