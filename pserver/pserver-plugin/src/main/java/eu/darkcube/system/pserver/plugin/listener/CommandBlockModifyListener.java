package eu.darkcube.system.pserver.plugin.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;

public class CommandBlockModifyListener extends SingleInstanceBaseListener {

	@EventHandler
	public void handle(ServerCommandEvent event) {
		CommandSender sender = event.getSender();
		if (sender instanceof BlockCommandSender) {
			BlockCommandSender bsender = (BlockCommandSender) sender;
			String command = event.getCommand();
			String commandName = command.split(" ")[0];
			if (!PServerExecutor.TOTAL_COMMAND_NAMES.contains(commandName)) {
				bsender.sendMessage("Invalid command: " + commandName);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void handle(PlayerInteractEvent event) {
		org.bukkit.event.block.Action action = event.getAction();
		Block clicked = event.getClickedBlock();
		ItemStack item = event.getItem();
		BlockFace face = event.getBlockFace();
		Player player = event.getPlayer();
		if (action == Action.RIGHT_CLICK_BLOCK) {
			if (item != null && item.getType() == Material.COMMAND) {
				Block toPlace = clicked.getRelative(face);
				if (clicked.getType() != Material.COMMAND
								|| player.isSneaking()) {
					event.setUseInteractedBlock(Result.DENY);
					switch (player.getGameMode()) {
					case SURVIVAL:
						item.setAmount(item.getAmount() - 1);
						event.getPlayer().setItemInHand(item);
					case CREATIVE:
						toPlace.setType(Material.COMMAND);
					break;
					case ADVENTURE:
						event.setCancelled(true);
					case SPECTATOR:
						event.setCancelled(true);
					break;
					}
				}
			} else {
				if (event.getClickedBlock().getType() == Material.COMMAND) {
					switch (player.getGameMode()) {
					case CREATIVE:
					case SURVIVAL:
						event.setCancelled(true);
						player.closeInventory();
					break;
					default:
						event.setCancelled(true);
					break;
					}
				}
			}
		}
	}
}
