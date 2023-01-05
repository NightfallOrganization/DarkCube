package eu.darkcube.system.miners.player;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.listener.ListenerBlockBreak;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TNTManager implements Listener {

    private static final Map<TNTPrimed, Player> TNTs = new HashMap<>();

    /**
     * Explodes a TNT. Player and their teammates won't take damage.
     *
     * @param l Location to spawn tnt
     * @param p Player that placed tnt. null for generated tnt.
     */
    public static void explodeTNT(Location l, Player p, int fuse) {
        TNTPrimed tnt = (TNTPrimed) l.getWorld().spawnEntity(l.clone().add(0.5, 0, 0.5), EntityType.PRIMED_TNT);
        tnt.setFuseTicks(fuse);
        TNTs.put(tnt, p);
    }

    public static Player getTNTOwner(TNTPrimed tnt) {
        return TNTs.get(tnt);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (!e.getEntityType().equals(EntityType.PRIMED_TNT))
            return;
        if (Miners.getGamephase() != 1)
            e.blockList().clear();
        List<Block> l = new ArrayList<>();
        for (Block b : e.blockList())
            if (ListenerBlockBreak.DONT_BREAK.contains(b.getType()))
                l.add(b);
        e.blockList().removeAll(l);
    }

    // moved to ListenerPlayerDamage
//    @EventHandler(priority = EventPriority.HIGH)
//    public void onPlayerDamage(EntityDamageByEntityEvent e) {
//        if (!(e.getEntity() instanceof Player))
//            return;
//        if (!(e.getDamager() instanceof TNTPrimed))
//            return;
//        if (!TNTs.containsKey(e.getDamager()))
//            return;
//        if (Miners.getTeamManager().isInSameTeam((Player) e.getEntity(), TNTManager.getTNTOwner((TNTPrimed) e.getDamager())))
//            e.setCancelled(true);
//    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlaceTNT(BlockPlaceEvent e) {
        if (!e.getItemInHand().getType().equals(Material.TNT))
            return;
        e.setCancelled(false);
        explodeTNT(e.getBlock().getLocation(), e.getPlayer(), 10);
        e.getBlockPlaced().setType(Material.AIR);
    }

}
