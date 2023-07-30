
package eu.darkcube.system.citybuild.commands;

import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GetItem implements CommandExecutor {

	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if (s instanceof Player player) {
			Inventory inventory = Bukkit.createInventory(null, 9 * 4,
					Component.text().content("§f\uDAFF\uDFEFḊ")
							.color(TextColor.color(140, 202, 255)).build());
			User user = UserAPI.getInstance().getUser(player);

			inventory.setItem(0, CustomItemManager.getSwiftSword());
			inventory.setItem(1, CustomItemManager.getCustomFireworkStar());
			inventory.setItem(2, CustomItemManager.getEnderBag());
			inventory.setItem(3, CustomItemManager.getRingOfHealing());
			inventory.setItem(4, CustomItemManager.getRingOfSpeed());

			player.openInventory(inventory);

		} else {
			s.sendMessage("§7Du bist kein Spieler!");
		}

		return false;
	}
}
