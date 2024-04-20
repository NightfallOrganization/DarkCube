package eu.darkcube.minigame.woolbattle.api.event.item;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;

public interface ItemEvent {
    @NotNull ItemBuilder item();
}
