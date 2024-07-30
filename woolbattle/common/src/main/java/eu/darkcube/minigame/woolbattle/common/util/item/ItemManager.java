/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.item;

import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.data.PersistentDataTypes;

public class ItemManager {
    private final Key itemId;

    public ItemManager(CommonWoolBattleApi api) {
        itemId = Key.key(api, "item_id");
    }

    public static ItemManager instance() {
        return ItemManagerImpl.MANAGER;
    }

    public Key itemId() {
        return itemId;
    }

    public void setItemId(@NotNull ItemBuilder item, @NotNull String id) {
        setId(item, itemId, id);
    }

    public @Nullable String getItemId(@NotNull ItemBuilder item) {
        return getId(item, itemId);
    }

    public @Nullable String getId(@NotNull ItemBuilder item, @NotNull Key key) {
        return item.persistentDataStorage().get(key, PersistentDataTypes.STRING);
    }

    public void setId(@NotNull ItemBuilder item, @NotNull Key key, @NotNull String id) {
        item.persistentDataStorage().set(key, PersistentDataTypes.STRING, id);
    }
}
