package eu.darkcube.minigame.woolbattle.common.game;

import java.util.function.Consumer;

import eu.darkcube.system.event.EventListener;

public abstract class ConfiguredListener<T> implements Consumer<T> {
    private final Class<T> type;

    public ConfiguredListener(Class<T> type) {
        this.type = type;
    }

    public EventListener<T> create() {
        return EventListener.builder(type).handler(this).build();
    }
}
