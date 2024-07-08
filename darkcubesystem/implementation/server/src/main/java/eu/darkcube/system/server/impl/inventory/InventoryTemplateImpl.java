package eu.darkcube.system.server.impl.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.impl.inventory.animated.AnimatedTemplateSettingsImpl;
import eu.darkcube.system.server.impl.inventory.item.ItemReferenceImpl;
import eu.darkcube.system.server.impl.inventory.paged.PagedTemplateSettingsImpl;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.InventoryType;
import eu.darkcube.system.server.inventory.item.ItemTemplate;
import eu.darkcube.system.server.inventory.listener.InventoryListener;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.Language;

public abstract class InventoryTemplateImpl<PlatformPlayer> implements InventoryTemplate {
    protected final @NotNull Key key;
    protected final @NotNull InventoryType type;
    protected final int size;
    protected final @NotNull AnimatedTemplateSettingsImpl animation;
    protected final @NotNull PagedTemplateSettingsImpl pagination;
    protected final @NotNull List<InventoryListener> listeners;
    protected final SortedMap<Integer, ItemReferenceImpl>[] contents;
    protected @Nullable Object title;

    public InventoryTemplateImpl(@NotNull Key key, @NotNull InventoryType type, int size) {
        this.key = key;
        this.type = type;
        this.size = size;
        this.animation = new AnimatedTemplateSettingsImpl(this);
        this.pagination = new PagedTemplateSettingsImpl(this);
        this.listeners = new ArrayList<>();
        this.contents = new SortedMap[size];
    }

    public SortedMap<Integer, ItemReferenceImpl>[] contents() {
        return contents;
    }

    @Override
    public @NotNull InventoryType type() {
        return type;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public @NotNull AnimatedTemplateSettingsImpl animation() {
        return animation;
    }

    @Override
    public @NotNull PagedTemplateSettingsImpl pagination() {
        return pagination;
    }

    @Override
    public void title(@Nullable Object title) {
        this.title = title;
    }

    @Override
    public @Unmodifiable @NotNull Collection<InventoryListener> listeners() {
        return List.copyOf(this.listeners);
    }

    @Override
    public void addListener(@NotNull InventoryListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(@NotNull InventoryListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public @NotNull ItemReferenceImpl setItem(int priority, int slot, @NotNull Object item) {
        var reference = new ItemReferenceImpl(item);
        if (contents[slot] == null) contents[slot] = new TreeMap<>();
        contents[slot].put(priority, reference);
        return reference;
    }

    @Override
    public void setItems(int priority, @NotNull ItemTemplate template) {
        for (var entry : template.contents().entrySet()) {
            setItem(priority, entry.getKey(), entry.getValue());
        }
    }

    @Override
    public @NotNull Key key() {
        return key;
    }

    @Override
    public @NotNull Inventory open(@NotNull Object player) {
        var platformPlayer = onlinePlayer(player);
        var user = platformPlayer == null ? null : user(platformPlayer);
        var title = calculateTitle(user);
        return open(title, platformPlayer);
    }

    protected @Nullable Component calculateTitle(@Nullable User user) {
        while (true) {
            switch (title) {
                case null -> {
                    return null;
                }
                case Component component -> {
                    return component;
                }
                case BaseMessage message -> title = message.getMessage(user == null ? Language.DEFAULT : user.language());
                case String string -> title = Component.text(string);
                default -> title = tryConvertTitle(title);
            }
        }
    }

    protected abstract @NotNull User user(@NotNull PlatformPlayer player);

    protected abstract @Nullable PlatformPlayer onlinePlayer(@NotNull Object player);

    protected abstract @NotNull Inventory open(@Nullable Component title, @Nullable PlatformPlayer player);

    protected abstract @Nullable Object tryConvertTitle(@NotNull Object title);
}
