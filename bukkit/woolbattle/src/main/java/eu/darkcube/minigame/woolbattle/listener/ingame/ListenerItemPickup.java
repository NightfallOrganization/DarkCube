package eu.darkcube.minigame.woolbattle.listener.ingame;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.ItemManager;

public class ListenerItemPickup extends Listener<PlayerPickupItemEvent> {
	@Override
	@EventHandler
	public void handle(PlayerPickupItemEvent e) {
		if(e.getItem().getItemStack().getType() != Material.WOOL) {
			return;
		}
		Player p = e.getPlayer();
		User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
		if (user.getTeam().getType() == TeamType.SPECTATOR) {
			e.setCancelled(true);
			return;
		}
		PlayerInventory inv = p.getInventory();
		Item entity = e.getItem();
		ItemStack item = entity.getItemStack();
		short oldColor = item.getDurability();
		if (item.getType() == Material.WOOL) {
			item.setDurability(user.getTeam().getType().getWoolColorByte());
		}
		int count = ItemManager.countItems(Material.WOOL, inv);
		int tryadd = item.getAmount();
		int fullInv = user.getMaxWoolSize();
		int freeSpace = fullInv - count;
		int remaining = tryadd - freeSpace;
		e.setCancelled(true);
		if (remaining > 0) {
			item.setAmount(remaining);
			ItemStack item2 = item.clone();
			item2.setDurability(oldColor);
			entity.setItemStack(item2);
			entity.setPickupDelay(4);
			if (tryadd - remaining > 0) {
				inv.addItem(new ItemStack(Material.WOOL, tryadd - remaining, user.getTeam().getType().getWoolColorByte()));
				playSound(p);
				WoolBattle.getInstance().getIngame().listenerDoubleJump.refresh(p);
			}
			return;
		}
		if (freeSpace > 0) {
			entity.remove();
			inv.addItem(new ItemStack(Material.WOOL, tryadd, user.getTeam().getType().getWoolColorByte()));
			playSound(p);
			WoolBattle.getInstance().getIngame().listenerDoubleJump.refresh(p);
		}
	}

	private void playSound(Player p) {
		p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1);
	}
}