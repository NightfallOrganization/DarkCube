/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.util.item;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.item.ItemFactory;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;

public interface Item extends ItemFactory {
    /**
     * @return a copy of the {@link ItemBuilder}
     */
    @NotNull
    ItemBuilder builder();

    @NotNull
    String key();

    @NotNull
    String itemId();

    @NotNull
    ItemBuilder getItem(@NotNull WBUser user);

    @NotNull
    ItemBuilder getItem(@NotNull WBUser user, @NotNull Object @NotNull ... replacements);

    @NotNull
    ItemBuilder getItem(@NotNull WBUser user, @NotNull Object @NotNull [] replacements, @NotNull Object @NotNull ... loreReplacements);

    @NotNull
    ItemBuilder getItem(@NotNull User user);

    @NotNull
    ItemBuilder getItem(@NotNull User user, @NotNull Object @NotNull ... replacements);

    @NotNull
    ItemBuilder getItem(@NotNull User user, @NotNull Object @NotNull [] replacements, @NotNull Object @NotNull ... loreReplacements);
}
