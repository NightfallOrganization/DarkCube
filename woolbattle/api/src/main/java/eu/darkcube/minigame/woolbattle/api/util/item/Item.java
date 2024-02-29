/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.util.item;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.server.item.ItemBuilder;

public interface Item {
    /**
     * @return a copy of the {@link ItemBuilder}
     */
    ItemBuilder builder();

    String key();

    String itemId();

    ItemBuilder getItem(WBUser user);

    ItemBuilder getItem(WBUser user, Object... replacements);

    ItemBuilder getItem(WBUser user, Object[] replacements, Object... loreReplacements);
}
