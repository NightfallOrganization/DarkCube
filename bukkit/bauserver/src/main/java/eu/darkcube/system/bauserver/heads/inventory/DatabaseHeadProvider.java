/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.heads.inventory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import eu.darkcube.system.bauserver.Main;
import eu.darkcube.system.bauserver.heads.Head;
import eu.darkcube.system.bauserver.heads.database.DatabaseStorage;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.JoinConfiguration;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
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
    private DatabaseStorage.Token[] tokens = null;

    public DatabaseHeadProvider(@NotNull String provider) {
        this.provider = provider;
        this.category = null;
    }

    public DatabaseHeadProvider(@NotNull String provider, @NotNull String category) {
        this.provider = provider;
        this.category = category;
    }

    public void tokens(DatabaseStorage.Token[] tokens) {
        this.tokens = tokens;
    }

    @Override
    public @NotNull BigInteger size() {
        return SIZE_UNKNOWN;
    }

    @Override
    public @NotNull ItemReference @NotNull [] provideItem(@NotNull BigInteger bigIndex, int length, @NotNull ItemReferenceProvider itemReferenceProvider) {
        var index = bigIndex.intValueExact();
        var refs = new ArrayList<ItemReference>();
        var storage = Main.getInstance().databaseStorage();
        List<Head> heads;
        if (tokens == null) {
            if (category == null) {
                heads = storage.select(provider, index, length);
            } else {
                heads = storage.select(provider, category, index, length);
            }
        } else {
            if (category == null) {
                heads = storage.search(provider, index, length, tokens);
            } else {
                heads = storage.search(provider, category, index, length, tokens);
            }
        }
        for (var head : heads) {
            var item = HeadProvider.headItem(head.name());
            item.set(ItemComponent.PROFILE, new HeadProfile(new PlayerSkin(head.texture(), null)));
            if (!head.tags().isEmpty()) {
                item.lore(Component.text("Tags: ").append(Component.join(JoinConfiguration.arrayLike(), head.tags().stream().map(t -> Component.text(t, NamedTextColor.GOLD)).toList())).applyFallbackStyle(Style.style(NamedTextColor.GRAY)));
            }
            item.persistentDataStorage().set(IS_HEAD, true);
            refs.add(itemReferenceProvider.createFor(item));
        }
        return refs.toArray(ItemReference[]::new);
    }
}
