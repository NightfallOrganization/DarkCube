package eu.darkcube.system.lobbysystem.listener;

import java.math.BigInteger;
import java.util.Random;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.lobbysystem.inventory.InventoryDailyReward;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.userapi.UserAPI;

public class ListenerDailyReward extends BaseListener {

	@EventHandler
	public void handle(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		LobbyUser user = UserWrapper.fromUser(UserAPI.getInstance().getUser(p));
		if (user.getOpenInventory().getType() != InventoryDailyReward.type_daily_reward) {
			return;
		}
		ItemStack item = e.getCurrentItem();
		if (item == null) {
			return;
		}
		int id = new ItemBuilder(item).getUnsafe().getInt("reward");
		if (id == 0) {
			return;
		}

		Set<Integer> used = user.getRewardSlotsUsed();
		// used.clear();
		if (used.contains(id)) {
			p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1, 1);
			return;
		}

		used.add(id);
		user.setRewardSlotsUsed(used);

		int coins = (new Random().nextInt(60) + 39) * 2 + new Random().nextInt(3);
		user.getUser().setCubes(user.getUser().getCubes().add(BigInteger.valueOf(coins)));
		item = new ItemStack(Material.SULPHUR);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§a" + coins);
		item.setItemMeta(meta);
		user.setLastDailyReward(System.currentTimeMillis());
		e.setCurrentItem(item);
		p.sendMessage(Message.REWARD_COINS.getMessage(user.getUser(), Integer.toString(coins)));
		p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
	}

}
