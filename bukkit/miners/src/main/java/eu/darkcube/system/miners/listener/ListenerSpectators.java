package eu.darkcube.system.miners.listener;

import eu.darkcube.system.miners.Miners;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;

/**
 * Handles all events involving a spectator player, excluding {@code PlayerLeave}, {@code PlayerKick}, {@code PlayerJoin}, {@code PlayerChat}
 */
public class ListenerSpectators implements Listener {

    /**
     * Checks whether a given entity is a) a player and b) a spectator. No player is considered a spectator in lobbyphase
     */
    public static boolean isSpectatorPlayer(Entity e) {
        if (!(e instanceof Player))
            return false;
        return Miners.getTeamManager().getPlayerTeam((Player) e) == 0 && Miners.getGamephase() > 0;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpectatorDamage(EntityDamageEvent e) {
        if (isSpectatorPlayer(e.getEntity()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpectatorBlockBreak(BlockBreakEvent e) {
        if (isSpectatorPlayer(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpectatorInteract(PlayerInteractEvent e) {
        if (!isSpectatorPlayer(e.getPlayer()))
            return;
        e.setCancelled(true);
        if (e.getClickedBlock() instanceof InventoryHolder)
            e.getPlayer().openInventory(((InventoryHolder) e.getClickedBlock()).getInventory());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpectatorBlockPlace(BlockPlaceEvent e) {
        if (isSpectatorPlayer(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpectatorInventoryInteract(InventoryInteractEvent e) {
        if (isSpectatorPlayer(e.getWhoClicked()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpectatorItemPickup(PlayerPickupItemEvent e) {
        if (isSpectatorPlayer(e.getPlayer()))
            e.setCancelled(true);
    }


}
