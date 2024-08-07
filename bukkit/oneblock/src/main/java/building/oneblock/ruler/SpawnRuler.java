package building.oneblock.ruler;

import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;
import static building.oneblock.manager.WorldManager.SPAWN;

public class SpawnRuler implements Listener {

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getLocation().getWorld().equals(SPAWN)) {
            event.setCancelled(true);
        }
    }

//    @EventHandler
//    public void onWorldLoad(WorldLoadEvent event) {
//        World world = event.getWorld();
//        if (world.equals(SPAWN)) {
//            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
//            world.setTime(6000);
//        }
//    }


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity().getWorld().equals(SPAWN)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        if (block != null && block.getWorld().equals(SPAWN) && player.getGameMode() == GameMode.SURVIVAL) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockFall(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK && event.getTo() == Material.AIR && event.getBlock().getWorld().equals(SPAWN)) {
            event.setCancelled(true);
            event.getBlock().getState().update(false, false);
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (!event.getBlock().getWorld().equals(SPAWN)) {
            return;
        }

        if (event.getBlock().getType() == Material.WATER || event.getBlock().getType() == Material.LAVA) {
            event.setCancelled(true);
        }
    }

}