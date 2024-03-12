/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.perk;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.minigame.woolbattle.api.util.item.Item;

/**
 * This is the class for all perk types. May need renaming in the future, {@code
 * ActivationType}
 * is not quite accurate.<br><br>
 * <p>
 * {@link ActivationType#PRIMARY_WEAPON PRIMARY_WEAPON},
 * {@link ActivationType#SECONDARY_WEAPON SECONDARY_WEAPON} and
 * {@link ActivationType#ARROW ARROW} are perks internally, makes it easier to track with
 * existing system and helps with code reuse. This may also help for the future to make them
 * changeable
 */
public enum ActivationType {
    // Order is important here, as the inventory will be filled in that order for new players
    PRIMARY_WEAPON("primaryWeapon", 1),
    SECONDARY_WEAPON("secondaryWeapon", 1),

    ACTIVE("active", 2),
    MISC("misc", 1),
    PASSIVE("passive", 1),

    DOUBLE_JUMP("doubleJump", 1),

    ARROW("arrow", 1);
    private final String type;
    private final int maxCount;
    private final Item displayItem;

    ActivationType(String type, int maxCount) {
        this.type = type;
        this.maxCount = maxCount;
        this.displayItem = InjectionLayer.ext().instance(ItemProvider.class).getItem(type);
    }

    @Override public String toString() {
        return type;
    }

    public String type() {
        return type;
    }

    public int maxCount() {
        return maxCount;
    }

    public Item displayItem() {
        return displayItem;
    }

    public interface ItemProvider {
        Item getItem(String activationType);
    }
}
