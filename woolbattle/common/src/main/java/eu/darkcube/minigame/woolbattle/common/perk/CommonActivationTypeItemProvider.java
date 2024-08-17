/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.perk;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.util.item.Item;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class CommonActivationTypeItemProvider implements ActivationType.ItemProvider {
    @Override
    public @NotNull Item getItem(String activationType) {
        return switch (activationType) {
            case "primary_weapon" -> Items.DEFAULT_BOW;
            case "secondary_weapon" -> Items.DEFAULT_SHEARS;
            case "active" -> Items.PERKS_ACTIVE;
            case "passive" -> Items.PERKS_PASSIVE;
            case "misc" -> Items.PERKS_MISC;
            case "double_jump" -> Items.PERK_DOUBLE_JUMP;
            case "arrow" -> Items.DEFAULT_ARROW;
            default -> throw new IllegalStateException("Unexpected value: " + activationType);
        };
    }
}
