package eu.darkcube.system.darkessentials.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.Main;
import eu.darkcube.system.darkessentials.util.EssentialCollections;

public class CommandRepair extends Command {

	public CommandRepair() {
		super(Main.getInstance(), "repair", new Command[0],
						"Repariert ein Item.", new Argument[] {
										new Argument("hand|helmet|chest|leggings|boots|armor|inventory|all",
														"Die Items, die repariert werden sollen. (Default: hand)",
														false),
										new Argument("Wert",
														"Der Wert, um den ein Item repariert werden soll (Default: Vollständig)",
														false),
										new Argument("Spieler",
														"Der Spieler, dessen items repariert werden sollen.",
														false)
						});
		setAliases("d_repair");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Set<Player> players = new HashSet<>();
		int value = Integer.MAX_VALUE;
		boolean hand = true;
		boolean helmet = false;
		boolean chest = false;
		boolean legs = false;
		boolean boots = false;
		boolean inv = false;
		Set<String> unresolvedNames = new HashSet<>();
		if (args.length != 0) {
			switch (args[0].toLowerCase()) {
			case "all":
				helmet = true;
				chest = true;
				legs = true;
				boots = true;
				inv = true;
				args[0] = "%processed%";
			break;
			case "helmet":
				hand = false;
				helmet = true;
				args[0] = "%processed%";
			break;
			case "chest":
				hand = false;
				chest = true;
				args[0] = "%processed%";
			break;
			case "leggings":
				hand = false;
				legs = true;
				args[0] = "%processed%";
			break;
			case "boots":
				hand = false;
				boots = true;
				args[0] = "%processed%";
			break;
			case "armor":
				hand = false;
				helmet = true;
				chest = true;
				legs = true;
				boots = true;
				args[0] = "%processed%";
			break;
			case "inventory":
			case "inv":
				inv = true;
				args[0] = "%processed%";
			break;
			case "hand":
				args[0] = "%processed%";
			break;
			}
			try {
				if (args.length > 1) {
					value = Short.parseShort(args[1]);
					args[1] = "%processed%";
				} else {
					value = Short.parseShort(args[0]);
					args[0] = "%processed%";
				}
			} catch (Exception e) {
			}
			for (String playerName : args) {
				if (!playerName.equals("%processed%")) {
					if (Bukkit.getPlayer(playerName) != null) {
						players.add(Bukkit.getPlayer(playerName));
					} else {
						unresolvedNames.add(playerName);
					}
				}
			}
		}
		if (players.isEmpty() && unresolvedNames.isEmpty()) {
			if (sender instanceof Player) {
				players.add((Player) sender);
			} else {
				Main.sendMessagePlayernameRequired(sender);
				return true;
			}
		}
		if (value == 0) {
			Main.getInstance().sendMessage(Main.cFail()
							+ "Die angegebene Zahl darf nicht " + Main.cValue()
							+ "0 " + Main.cFail() + "sein!", sender);
			return true;
		}
		Main.sendMessagePlayerNotFound(unresolvedNames, sender);
		int itemCount = 0;
		int playerCount = 0;
		for (Player current : players) {
			int tempItemCount = 0;
			if (hand && (isRepairable(current.getItemInHand()) || value < 0)) {
				repairItem(current.getItemInHand(), value);
				tempItemCount++;
				itemCount++;
			}
			if (helmet || chest || legs || boots || inv) {
				PlayerInventory currentInv = current.getInventory();
				if (helmet && (isRepairable(currentInv.getHelmet())
								|| value < 0)) {
					repairItem(currentInv.getHelmet(), value);
					tempItemCount++;
					itemCount++;
				}
				if (helmet && (isRepairable(currentInv.getChestplate())
								|| value < 0)) {
					repairItem(currentInv.getChestplate(), value);
					tempItemCount++;
					itemCount++;
				}
				if (helmet && (isRepairable(currentInv.getLeggings())
								|| value < 0)) {
					repairItem(currentInv.getLeggings(), value);
					tempItemCount++;
					itemCount++;
				}
				if (helmet && (isRepairable(currentInv.getBoots())
								|| value < 0)) {
					repairItem(currentInv.getBoots(), value);
					tempItemCount++;
					itemCount++;
				}
				if (inv) {
					for (ItemStack item : currentInv.getContents()) {
						if (isRepairable(item) || value < 0) {
							repairItem(item, value);
							tempItemCount++;
							itemCount++;
						}
					}
				}
			}
			if (value > 0) {
				Main.getInstance().sendMessage(Main.cValue()
								+ String.valueOf(tempItemCount)
								+ Main.cConfirm()
								+ " Items wurden repariert.", sender);
			} else {
				Main.getInstance().sendMessage(Main.cValue()
								+ String.valueOf(tempItemCount)
								+ Main.cConfirm()
								+ " Items wurden beschädigt.", sender);
			}
			playerCount++;
		}
		if (!(players.size() == 1 && players.contains(sender)))

		{
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(Main.cValue()).append(itemCount).append(Main.cConfirm()).append(" Items für ").append(Main.cValue()).append(playerCount).append(Main.cConfirm()).append(" Spieler");
			if (value > 0) {
				stringBuilder.append(" repariert.");
			} else {
				stringBuilder.append(" beschädigt.");
			}
			Main.getInstance().sendMessage(stringBuilder.toString(), sender);
		}
		return true;
	}

	private void repairItem(ItemStack item, int value) {
		if (value == Integer.MAX_VALUE) {
			item.setDurability((short) 0);
		} else if (item.getType().getMaxDurability() - item.getDurability()
						- value * -1 < 0) {
			item.setDurability(item.getType().getMaxDurability());
		} else {
			item.setDurability((short) (item.getDurability() - value));
		}
	}

	private boolean isRepairable(ItemStack item) {
		try {
			return item.getType().getMaxDurability() != 0
							&& item.getDurability() != 0;
//			return (CraftItemStack.asNMSCopy(item).getItem().usesDurability() && item.getDurability() != 0);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		List<String> list = new ArrayList<>();
		if (args.length == 1) {
			list.addAll(EssentialCollections.toSortedStringList(EssentialCollections.asList(new String[] {
							"hand", "helmet", "chest", "chest", "leggings",
							"boots", "armor", "all", "inventory"
			}), args[0]));
		}
		if (args.length != 0) {
			list.addAll(Main.getPlayersStartWith(args));
		}
		return list;
	}
}
