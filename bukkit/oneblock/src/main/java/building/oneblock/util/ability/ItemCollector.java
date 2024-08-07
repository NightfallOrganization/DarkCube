package building.oneblock.util.ability;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

public class ItemCollector implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack[] drops = event.getBlock().getDrops(player.getInventory().getItemInMainHand()).toArray(new ItemStack[0]);
        event.setDropItems(false);

        if (block.getType() == Material.PACKED_ICE) {
            ItemStack packedIce = new ItemStack(Material.PACKED_ICE, 1);
            player.getInventory().addItem(packedIce);
        }

        for (ItemStack drop : drops) {
            if (addItemToInventoryOrDrop(player, drop)) {
                event.setExpToDrop(0);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player)) return;

        Player player = event.getEntity().getKiller();
        event.getDrops().removeIf(drop -> addItemToInventoryOrDrop(player, drop));
        event.setDroppedExp(0);
    }

    private boolean addItemToInventoryOrDrop(Player player, ItemStack item) {

        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            return false;
        } else {

            player.getInventory().addItem(item);
            return true;
        }
    }
}
