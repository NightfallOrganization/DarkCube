package eu.darkcube.system.minestom.impl.inventory;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.minestom.impl.adventure.AdventureUtils;
import eu.darkcube.system.minestom.inventory.MinestomInventoryType;
import eu.darkcube.system.server.impl.inventory.AbstractInventory;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.ContainerInventory;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.item.ItemStack;

public class MinestomInventory extends AbstractInventory<ItemStack> {
    protected final Inventory inventory;

    public MinestomInventory(@Nullable Component title, @NotNull MinestomInventoryType type) {
        super(title, type);
        this.inventory = new ContainerInventory(type.minestomType(), AdventureUtils.convert(title));
    }

    @Override
    protected void setItem0(int slot, @NotNull ItemStack item) {
        inventory.setItemStack(slot, item);
    }

    @Override
    protected ItemStack getItem0(int slot) {
        return inventory.getItemStack(slot);
    }

    @Override
    public void open(@Nullable Object player) {
        var minestomPlayer = MinestomInventoryUtils.player(player);
        if (minestomPlayer == null) {
            // player not online, do not open
            return;
        }
        doOpen(minestomPlayer);
    }

    protected void doOpen(@NotNull Player player) {
        var user = UserAPI.instance().user(player.getUuid());
        opened.add(user);
        player.openInventory(inventory);
    }

    @Override
    public boolean opened(@NotNull Object player) {
        while (true) {
            if (player instanceof Player minestomPlayer) {
                player = UserAPI.instance().user(minestomPlayer.getUuid());
            } else {
                break;
            }
        }
        var user = (User) player;
        return opened.contains(user);
    }
}
