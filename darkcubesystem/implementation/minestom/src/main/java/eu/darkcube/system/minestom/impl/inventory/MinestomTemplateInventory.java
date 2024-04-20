package eu.darkcube.system.minestom.impl.inventory;

import java.util.SortedMap;
import java.util.TreeMap;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.minestom.inventory.MinestomInventoryType;
import eu.darkcube.system.server.impl.inventory.item.ItemReferenceImpl;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

public class MinestomTemplateInventory extends MinestomInventory {
    private final @Nullable SortedMap<Integer, ItemReferenceImpl> @NotNull [] contents;
    private final @NotNull Player player;

    public MinestomTemplateInventory(@Nullable Component title, @NotNull MinestomInventoryType type, @NotNull MinestomInventoryTemplate template, @NotNull Player player) {
        super(title, type);
        this.player = player;
        for (var listener : template.listeners()) {
            this.addListener(listener);
        }
        this.contents = deepCopy(template.contents());
    }

    @Override
    protected void setItem0(int slot, @NotNull ItemStack item) {
    }

    @Override
    protected ItemStack getItem0(int slot) {
    }

    @Override
    protected void doOpen(@NotNull Player player) {
        if (player != this.player) {
            // Can't open the inventory for someone else than the original player
            return;
        }
        var user = UserAPI.instance().user(player.getUuid());
        setItems(player, user);
        opened.add(user);
        player.openInventory(inventory);
    }

    /**
     * Sets the items in the inventory.
     * Starts the animation, etc.
     */
    protected void setItems(@NotNull Player player, @NotNull User user) {
        
    }

    private static @Nullable SortedMap<Integer, ItemReferenceImpl> @NotNull [] deepCopy(@Nullable SortedMap<Integer, ItemReferenceImpl> @NotNull [] maps) {
        var result = maps.clone();
        for (var i = 0; i < result.length; i++) {
            var data = result[i];
            if (data == null) continue;
            var map = result[i] = new TreeMap<>();
            for (var entry : data.entrySet()) {
                map.put(entry.getKey(), entry.getValue().clone());
            }
        }
        return result;
    }
}
