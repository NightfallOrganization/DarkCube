package eu.darkcube.system.miners.listener;

import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.miners.items.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ListenerItemPickup implements Listener {

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent e) {
        ItemBuilder ib = new ItemBuilder(e.getItem().getItemStack());
        if (!ib.unsafe().containsKey("ITEM"))
            return;
        e.getItem().setItemStack(Item.valueOf(ib.unsafe().getString("ITEM")).getItem(e.getPlayer(), e.getItem().getItemStack().getAmount()));
    }

}
