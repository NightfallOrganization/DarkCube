package eu.darkcube.system.darkessentials.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.Main;

public class CommandOnlineplayers extends Command implements Listener {

	public CommandOnlineplayers() {
		super(Main.getInstance(), "onlineplayers", new Command[0], "Zeit dir alle Spieler auf dem Server.");
		setAliases("d_onlineplayers", "players");
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length != 0)
			return false;
		if (!(sender instanceof Player)) {
			listPlayersInChat(sender);
			return true;
		}
		((Player) sender).openInventory(getPlayersInInv(1));
		return true;
	}

	private void listPlayersInChat(CommandSender sender) {
		StringBuilder sb = new StringBuilder(ChatColor.GRAY.toString());
		for (Player current : Bukkit.getOnlinePlayers()) {
			sb.append(current.getName()).append(", ");
		}
		Main.getInstance().sendMessage(Main.cConfirm() + "Online sind:", sender);
		Main.getInstance().sendMessageWithoutPrefix(sb.toString().substring(0, sb.toString().length() - 2), sender);
	}

	private Inventory getPlayersInInv(int page) {
		if (page < 1)
			return getPlayersInInv(1);
		Inventory newInv = Bukkit.createInventory(null, 6 * 9, "/onlineplayers - Seite " + page);

		ItemStack headerNormal = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
		ItemMeta headerNormalMeta = headerNormal.getItemMeta();
		headerNormalMeta.setDisplayName(" ");
		headerNormal.setItemMeta(headerNormalMeta);

		ItemStack headerBack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
		ItemMeta headerBackMeta = headerBack.getItemMeta();
		headerBackMeta.setDisplayName(ChatColor.RESET + "Vorherige Seite");
		headerBack.setItemMeta(headerBackMeta);

		ItemStack headerForward = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
		ItemMeta headerForwardMeta = headerForward.getItemMeta();
		headerForwardMeta.setDisplayName(ChatColor.RESET + "Nächste Seite");
		headerForward.setItemMeta(headerForwardMeta);

		ItemStack filler = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0);
		ItemMeta fillerMeta = filler.getItemMeta();
		fillerMeta.setDisplayName(" ");
		filler.setItemMeta(fillerMeta);

		ItemStack headerPageNumber = new ItemStack(Material.NETHER_STAR, page);
		ItemMeta headerPageNumberMeta = headerPageNumber.getItemMeta();
		headerPageNumberMeta.setDisplayName(ChatColor.RESET + "Seite " + page);
		headerPageNumber.setItemMeta(headerPageNumberMeta);

		newInv.setContents(new ItemStack[] { null, headerNormal, headerNormal, headerNormal, headerPageNumber,
				headerNormal, headerNormal, headerNormal, null });
		if (page == 1) {
			newInv.setItem(0, headerNormal);
		} else {
			newInv.setItem(0, headerBack);
		}

		List<Player> list = new ArrayList<>();
		list.addAll(Bukkit.getOnlinePlayers());
		boolean lastPage = false;
		for (int i = 0; i < 36; i++) {
			try {
				String playerName = list.get((page - 1) * 36 + i).getName();
				ItemStack playerHead = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
				SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
				meta.setOwner(playerName);
				meta.setDisplayName(ChatColor.RESET + playerName);
				playerHead.setItemMeta(meta);
				newInv.setItem(9 + i, playerHead);
			} catch (Exception e) {
				newInv.setItem(9 + i, filler);
				lastPage = true;
			}
		}

		if (lastPage) {
			newInv.setItem(8, headerNormal);
		} else {
			newInv.setItem(8, headerForward);
		}

		for (int j = 0; j < 9; j++) {
			newInv.setItem(45 + j, headerNormal);
		}

		return newInv;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void handle(InventoryClickEvent e) {
		if (e.getInventory().getTitle().startsWith("/onlineplayers")) {
			e.setCancelled(true);
			if (e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().contains("Vorherige Seite")) {
				e.getWhoClicked().openInventory(getPlayersInInv(e.getInventory().getItem(4).getAmount() - 1));
			} else if (e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().contains("Nächste Seite")) {
				e.getWhoClicked().openInventory(getPlayersInInv(e.getInventory().getItem(4).getAmount() + 1));
			}
		}
	}
}
