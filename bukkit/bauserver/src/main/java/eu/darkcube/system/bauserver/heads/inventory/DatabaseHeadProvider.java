/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.heads.inventory;

import java.math.BigInteger;
import java.util.ArrayList;

import eu.darkcube.system.bauserver.Main;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.inventory.item.ItemReference;
import eu.darkcube.system.server.inventory.item.ItemReferenceProvider;
import eu.darkcube.system.server.inventory.paged.PagedInventoryContentProvider;
import eu.darkcube.system.server.item.component.ItemComponent;
import eu.darkcube.system.server.item.component.components.HeadProfile;
import eu.darkcube.system.server.item.component.components.util.PlayerSkin;
import eu.darkcube.system.util.data.DataKey;
import eu.darkcube.system.util.data.PersistentDataTypes;

public class DatabaseHeadProvider implements PagedInventoryContentProvider {
    public static final DataKey<Boolean> IS_HEAD = DataKey.of(Key.key(Main.getInstance(), "head"), PersistentDataTypes.BOOLEAN);
    private final @NotNull String provider;
    private final @Nullable String category;

    public DatabaseHeadProvider(@NotNull String provider) {
        this.provider = provider;
        this.category = null;
    }

    public DatabaseHeadProvider(@NotNull String provider, @NotNull String category) {
        this.provider = provider;
        this.category = category;
    }

    @Override
    public @NotNull BigInteger size() {
        if (category == null) return BigInteger.valueOf(Main.getInstance().databaseStorage().size(provider));
        return BigInteger.valueOf(Main.getInstance().databaseStorage().size(provider, category));
    }

    @Override
    public @NotNull ItemReference @NotNull [] provideItem(@NotNull BigInteger bigIndex, int length, @NotNull ItemReferenceProvider itemReferenceProvider) {
        var index = bigIndex.intValueExact();
        var refs = new ArrayList<ItemReference>();
        var storage = Main.getInstance().databaseStorage();
        var heads = category == null ? storage.select(provider, index, length) : storage.select(provider, category, index, length);
        for (var head : heads) {
            var item = HeadProvider.headItem(head.name());
            item.set(ItemComponent.PROFILE, new HeadProfile(new PlayerSkin(head.texture(), null)));
            item.persistentDataStorage().set(IS_HEAD, true);
            refs.add(itemReferenceProvider.createFor(item));
        }
        return refs.toArray(ItemReference[]::new);
    }
}
