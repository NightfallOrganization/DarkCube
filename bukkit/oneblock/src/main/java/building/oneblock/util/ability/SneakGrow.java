package building.oneblock.util.ability;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import static building.oneblock.manager.WorldManager.SPAWN;

public class SneakGrow implements Listener {



    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;

        Player player = event.getPlayer();
        World world = player.getWorld();

        if (!world.getName().equalsIgnoreCase(SPAWN.getName())) {
            Location playerLoc = player.getLocation();

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        // Ignoriere den Block direkt unter dem Spieler
                        if (x == 0 && y == -1 && z == 0) continue;

                        Block block = world.getBlockAt(playerLoc.clone().add(x, y, z));

                        if (isSapling(block)) {
                            block.applyBoneMeal(BlockFace.UP);
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean isSapling(Block block) {
        return block.getType().toString().endsWith("_SAPLING");
    }
}
