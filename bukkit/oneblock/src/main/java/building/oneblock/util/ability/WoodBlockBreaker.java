package building.oneblock.util.ability;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class WoodBlockBreaker implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (isLog(block)) {
            breakAbove(block, player);
        }
    }

    private void breakAbove(Block block, Player player) {
        Block above = block.getRelative(0, 1, 0);
        if (isLog(above)) {
            addToInventory(above, player);
            above.setType(Material.AIR);
            breakAbove(above, player);
        }
    }

    private void addToInventory(Block block, Player player) {
        ItemStack itemStack = new ItemStack(block.getType(), 1);
        player.getInventory().addItem(itemStack);
    }

    private boolean isLog(Block block) {
        return block.getType().toString().endsWith("_LOG");
    }

}
