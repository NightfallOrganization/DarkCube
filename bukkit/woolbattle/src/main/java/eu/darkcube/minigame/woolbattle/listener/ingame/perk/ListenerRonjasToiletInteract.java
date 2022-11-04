package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;

public class ListenerRonjasToiletInteract extends Listener<PlayerInteractEvent> {
	public static final Item RONJAS_TOILET = PerkType.RONJAS_TOILET_SPLASH.getItem();
	public static final Item RONJAS_TOILET_COOLDOWN = PerkType.RONJAS_TOILET_SPLASH.getCooldownItem();

	@Override
	@EventHandler
	public void handle(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = e.getItem();
			Player p = e.getPlayer();
			User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
			if (item == null || item.getType() == Material.AIR) {
				return;
			}
			String itemid = ItemManager.getItemId(item);
			if (itemid == null)
				return;
			if (!itemid.equals(RONJAS_TOILET_COOLDOWN.getItemId()) && !itemid.equals(RONJAS_TOILET.getItemId())) {
				return;
			}
			Perk perk = user.getPerkByItemId(itemid);
			if (perk == null) {
				return;
			}
			e.setCancelled(true);
			p.launchProjectile(Egg.class);
		}
	}
}
