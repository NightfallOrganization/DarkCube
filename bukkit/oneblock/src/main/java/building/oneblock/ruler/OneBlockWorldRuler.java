package building.oneblock.ruler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static building.oneblock.manager.WorldManager.SPAWN;

public class OneBlockWorldRuler implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockFall(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK && event.getTo() == Material.AIR && !event.getBlock().getWorld().equals(SPAWN)) {
            event.setCancelled(true);
            event.getBlock().getState().update(false, false);
        }
    }

//    public List<Location> getOneBlockLocation() {
//        return Bukkit.getWorlds().stream()
//                .filter(world -> !world.getName().equalsIgnoreCase(SPAWN.getName()))
//                .map(world -> new Location(world, 0, 99, 0))
//                .collect(Collectors.toList());
//    }
}
