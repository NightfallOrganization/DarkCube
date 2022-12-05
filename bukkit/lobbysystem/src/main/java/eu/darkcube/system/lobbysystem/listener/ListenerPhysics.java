package eu.darkcube.system.lobbysystem.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import eu.darkcube.system.lobbysystem.Lobby;

public class ListenerPhysics extends BaseListener {

	@EventHandler
	public void handle(PlayerInteractEvent e) {
		if (e.getAction() == Action.PHYSICAL) {
			e.setCancelled(true);
			if (e.getClickedBlock()
					.equals(Lobby.getInstance().getDataManager().getJumpAndRunPlate().getBlock())) {
				Lobby.getInstance().getJaRManager().startJaR(e.getPlayer());
			}
		}
	}

	@EventHandler
	public void handle(BlockPhysicsEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void handle(BlockFadeEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void handle(BlockFormEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void handle(BlockSpreadEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void handle(BlockFromToEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void handle(BlockGrowEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void handle(LeavesDecayEvent e) {
		e.setCancelled(true);
	}
}
