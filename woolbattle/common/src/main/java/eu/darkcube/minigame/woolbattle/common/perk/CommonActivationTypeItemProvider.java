package eu.darkcube.minigame.woolbattle.common.perk;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.util.item.Item;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public class CommonActivationTypeItemProvider implements ActivationType.ItemProvider {
    @Override
    public @Nullable Item getItem(String activationType) {
        return switch (activationType) {
            case "primaryWeapon" -> Items.DEFAULT_BOW;
            case "secondaryWeapon" -> Items.DEFAULT_SHEARS;
            case "active" -> Items.PERKS_ACTIVE;
            case "passive" -> Items.PERKS_PASSIVE;
            case "misc" -> Items.PERKS_MISC;
            case "doubleJump" -> null;
            case "arrow" -> Items.DEFAULT_ARROW;
            default -> throw new IllegalStateException("Unexpected value: " + activationType);
        };
    }
}
