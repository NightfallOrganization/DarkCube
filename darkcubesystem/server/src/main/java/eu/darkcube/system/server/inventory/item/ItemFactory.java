package eu.darkcube.system.server.inventory.item;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;

public interface ItemFactory {
    @NotNull
    ItemBuilder createItem(@NotNull User user);
}
