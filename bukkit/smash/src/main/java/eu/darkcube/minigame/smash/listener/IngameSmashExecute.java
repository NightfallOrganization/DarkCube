package eu.darkcube.minigame.smash.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.smash.api.SmashAPI;
import eu.darkcube.minigame.smash.api.user.User;
import eu.darkcube.minigame.smash.util.Item;
import eu.darkcube.minigame.smash.util.ItemManager;

public class IngameSmashExecute extends BaseListener {

	@EventHandler
	public void handle(EntityDamageByEntityEvent e) {
		e.setCancelled(true);
		if (e.getEntity() instanceof Player) {
			if (e.getDamager() instanceof Player) {
				Player p = (Player) e.getDamager();
				handle(new PlayerInteractEvent(p, Action.LEFT_CLICK_AIR, p.getItemInHand(), null, null));
			}
		}
	}

	@EventHandler
	public void handle(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		User user = SmashAPI.getApi().getUser(p);
		ItemStack item = e.getItem();
		if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			user.getCharacter().getAttackSmash().execute(user);
			return;
		}
		if (item == null || !item.hasItemMeta()) {
			return;
		}
		String itemid = ItemManager.getItemId(item);
		if (itemid == null) {
			return;
		}
		if (itemid.equals(Item.UP_SMASH.getItemId())) {
			user.getCharacter().getUpSmash().execute(user);
		} else if (itemid.equals(Item.DOWN_SMASH.getItemId())) {
			user.getCharacter().getDownSmash().execute(user);
		} else if (itemid.equals(Item.FRONT_SMASH.getItemId())) {
			user.getCharacter().getFrontSmash().execute(user);
		} else if (itemid.equals(Item.BASE_SMASH.getItemId())) {
			user.getCharacter().getBaseSmash().execute(user);
		} else if (itemid.equals(Item.JUMP_SMASH.getItemId())) {
			user.getCharacter().getJumpSmash().execute(user);
		} else if (itemid.equals(Item.SHIELD_SMASH.getItemId())) {
			user.getCharacter().getShieldSmash().execute(user);
		} else {
			return;
		}
		e.setCancelled(true);
	}
}
