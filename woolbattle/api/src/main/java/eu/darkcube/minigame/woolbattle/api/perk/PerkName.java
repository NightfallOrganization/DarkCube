/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.perk;

import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;

public record PerkName(String name) {
    public static final PersistentDataType<PerkName> TYPE = PersistentDataTypes.map(PersistentDataTypes.STRING, PerkName::name, PerkName::new, p -> p);

    @Override
    public String toString() {
        return name;
    }
}
