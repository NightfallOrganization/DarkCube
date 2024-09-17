/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.heads.inventory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.function.Function;

import eu.darkcube.system.bauserver.Main;
import eu.darkcube.system.bauserver.heads.HeadStorage;
import eu.darkcube.system.bauserver.util.Message;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.item.ItemReference;
import eu.darkcube.system.server.inventory.item.ItemReferenceProvider;
import eu.darkcube.system.server.inventory.paged.PagedInventoryContentProvider;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.ItemComponent;
import eu.darkcube.system.server.item.component.components.HeadProfile;
import eu.darkcube.system.server.item.component.components.util.PlayerSkin;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.data.DataKey;
import eu.darkcube.system.util.data.PersistentDataTypes;

public class StoredHeadProvider implements PagedInventoryContentProvider {
    public static final DataKey<Integer> INDEX = DataKey.of(Key.key(Main.getInstance(), "index"), PersistentDataTypes.INTEGER);
    private final HeadStorage headStorage = Main.getInstance().headStorage();

    @Override
    public @NotNull BigInteger size() {
        return BigInteger.valueOf(headStorage.size());
    }

    @Override
    public @NotNull ItemReference @NotNull [] provideItem(@NotNull BigInteger bigIndex, int length, @NotNull ItemReferenceProvider itemReferenceProvider) {
        var index = bigIndex.intValueExact();
        var maxSize = headStorage.size();
        var endIndexExclusive = Math.min(index + length, maxSize);
        var refs = new ArrayList<ItemReference>();
        for (var i = index; i < endIndexExclusive; i++) {
            var head = headStorage.head(i);
            var item = HeadProvider.headItem(head.name());
            item.persistentDataStorage().set(INDEX, i);
            item.set(ItemComponent.PROFILE, new HeadProfile(new PlayerSkin(head.texture(), null)));
            refs.add(itemReferenceProvider.createFor(func(item)));
        }
        return refs.toArray(ItemReference[]::new);
    }

    private Function<User, Object> func(ItemBuilder item) {
        return user -> {
            var i = item.clone();
            i.lore(Message.SHIFT_RIGHT_TO_REMOVE.getMessage(user));
            return i;
        };
    }
}
