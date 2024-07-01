/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.impl.adventure;

import eu.darkcube.system.minestom.util.adventure.MinestomAdventureSupport;
import eu.darkcube.system.minestom.util.adventure.MinestomAudienceProvider;

public class MinestomAdventureSupportImpl implements MinestomAdventureSupport {
    private final MinestomAudienceProvider audienceProvider = new MinestomAudienceProviderImpl();

    @Override public MinestomAudienceProvider audienceProvider() {
        return audienceProvider;
    }
}
