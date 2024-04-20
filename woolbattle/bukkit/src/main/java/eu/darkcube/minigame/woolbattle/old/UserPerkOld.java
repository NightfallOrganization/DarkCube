/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.old;

import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import org.bukkit.inventory.ItemStack;

public interface UserPerkOld {

    Item getItem();

    ItemStack calculateItem();

    PerkName getPerkName();

    int getSlot();

    void setSlot(int slot);

    int getCost();

    void setCost(int cost);

    int getCooldown();

    void setCooldown(int cooldown);

    WBUser getOwner();

}
