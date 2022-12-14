package eu.darkcube.system.miners.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.gamephase.miningphase.Miningphase;
import eu.darkcube.system.miners.items.Item;
import eu.darkcube.system.miners.player.Message;

public class ListenerPlaceBlock implements Listener {

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (!(e.getItemInHand().getType().equals(Material.COBBLESTONE) && Miners.getGamephase() == 1)) {
			e.setCancelled(true);
			Miners.sendTranslatedMessage(e.getPlayer(), Message.FAIL_PLACE_BLOCK);
			return;
		}
		if (e.getBlockPlaced().getLocation()
				.distance(Miningphase.getSpawn(Miners.getTeamManager().getPlayerTeam(e.getPlayer()))) < 2) {
			e.setCancelled(true);
			Miners.sendTranslatedMessage(e.getPlayer(), Message.FAIL_PLACE_BLOCK_AT_SPAWN);
			return;
		}
		if (!e.isCancelled()) {
			e.getPlayer().getInventory().setItemInHand(Item.COBBLESTONE.getItem(e.getPlayer()));
		}
	}

}
