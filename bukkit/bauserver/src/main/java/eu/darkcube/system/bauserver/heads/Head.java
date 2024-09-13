/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.heads;

import java.util.List;

public record Head(String name, String texture, String category, String provider, List<String> tags) {
    public static final String CATEGORY_MANUAL = "manual";
    public static final String PROVIDER_MANUAL = "manual";

    public Head {
        if (tags == null) tags = List.of();
        if (category == null) category = CATEGORY_MANUAL;
        if (provider == null) provider = PROVIDER_MANUAL;
        tags = List.copyOf(tags);
    }
}
