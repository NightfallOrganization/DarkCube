package eu.darkcube.minigame.woolbattle.common.util.item;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;

public abstract class WrapperItem implements CommonItem {
    private final CommonItem handle;

    public WrapperItem(CommonItem handle) {
        this.handle = handle;
    }

    @Override
    public @NotNull ItemBuilder builder() {
        return handle.builder();
    }

    @Override
    public @NotNull String key() {
        return handle.key();
    }

    @Override
    public ItemBuilder mapItem(User user, ItemBuilder item) {
        return handle.mapItem(user, item);
    }

    @Override
    public boolean storeIdOnItem() {
        return handle.storeIdOnItem();
    }

    @Override
    public Object[] defaultReplacements(User user) {
        return handle.defaultReplacements(user);
    }

    @Override
    public Object[] defaultLoreReplacements(User user) {
        return handle.defaultLoreReplacements(user);
    }
}
