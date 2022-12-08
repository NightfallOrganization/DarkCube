/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.Main;
import eu.darkcube.system.darkessentials.util.EssentialCollections;

public class CommandEnchant extends Command {

	public CommandEnchant() {
		super(Main.getInstance(), "enchant", new Command[0], "Verzaubert ein Item.",
				new Argument("Verzauberung", "Dier Verzauberung, die dem Item hinzugefügt werden soll"),
				new Argument("Level", "Das Level der Verzauberung (Keine Angabe: Level 1", false),
				new Argument("Spieler", "Der Spieler, für den verzaubert werden soll.", false));
		setAliases("d_enchant", "ench");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0)
			return false;
		Enchantment ench = null;
		switch (args[0].toLowerCase()) {
		case "aqua_affinity":
			ench = Enchantment.WATER_WORKER;
			break;
		case "bane_of_arthropods":
			ench = Enchantment.DAMAGE_ARTHROPODS;
			break;
		case "blast_protection":
			ench = Enchantment.PROTECTION_EXPLOSIONS;
			break;
		case "depth_strider":
			ench = Enchantment.DEPTH_STRIDER;
			break;
		case "efficiency":
			ench = Enchantment.DIG_SPEED;
			break;
		case "feather_falling":
			ench = Enchantment.PROTECTION_FALL;
			break;
		case "fire_aspect":
			ench = Enchantment.FIRE_ASPECT;
			break;
		case "fire_protection":
			ench = Enchantment.PROTECTION_FIRE;
			break;
		case "flame":
			ench = Enchantment.ARROW_FIRE;
			break;
		case "fortune":
			ench = Enchantment.LOOT_BONUS_BLOCKS;
			break;
		case "infinity":
			ench = Enchantment.ARROW_INFINITE;
			break;
		case "knockback":
			ench = Enchantment.KNOCKBACK;
			break;
		case "looting":
			ench = Enchantment.LOOT_BONUS_MOBS;
			break;
		case "luck_of_the_sea":
		case "lots":
			ench = Enchantment.LUCK;
			break;
		case "lure":
			ench = Enchantment.LURE;
			break;
		case "power":
			ench = Enchantment.ARROW_DAMAGE;
			break;
		case "projectile_protection":
		case "proj_prot":
			ench = Enchantment.PROTECTION_PROJECTILE;
			break;
		case "protection":
		case "prot":
			ench = Enchantment.PROTECTION_ENVIRONMENTAL;
			break;
		case "punch":
			ench = Enchantment.ARROW_KNOCKBACK;
			break;
		case "respiration":
			ench = Enchantment.OXYGEN;
			break;
		case "sharpness":
		case "sharp":
			ench = Enchantment.DAMAGE_ALL;
			break;
		case "silk_touch":
			ench = Enchantment.SILK_TOUCH;
			break;
		case "smite":
			ench = Enchantment.DAMAGE_UNDEAD;
			break;
		case "thorns":
			ench = Enchantment.THORNS;
			break;
		case "unbreaking":
			ench = Enchantment.DURABILITY;
			break;
		default:
			Main.getInstance().sendMessage(Main.cFail() + "Du musst eine Verzauberung angeben!", sender);
			return true;
		}
		args[0] = "%processed%";
		int value = 1;
		Set<Player> players = new HashSet<>();
		Set<String> unresolvedNames = new HashSet<>();
		if (args.length >= 2) {
			try {
				value = Short.parseShort(args[1]);
				args[1] = "%processed%";
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
		if (value < 1) {
			Main.getInstance().sendMessage(Main.cFail() + "Die angegebene Zal muss positiv sein!", sender);
			return true;
		}
		if (players.isEmpty() && unresolvedNames.isEmpty()) {
			if (sender instanceof Player) {
				players.add((Player) sender);
			} else {
				Main.sendMessagePlayernameRequired(sender);
				return true;
			}
		}
		Main.sendMessagePlayerNotFound(unresolvedNames, sender);
		int count = 0;
		for (Player current : players) {
			current.getItemInHand().addUnsafeEnchantment(ench, value);
			Main.getInstance().sendMessage(Main.cConfirm() + "Das Item in deiner Hand wurde verzaubert!", current);
			count++;
		}
		if (!(players.size() == 1 && players.contains(sender)))
			Main.getInstance().sendMessage(
					Main.cValue() + String.valueOf(count) + Main.cConfirm() + " Items verzaubert!", sender);

		return true;
	}

	private static final List<String> enchantmentsAsStrings = Arrays.asList("aqua_affinity", "bane_of_arthropods",
			"blast_protection", "depth_strider", "efficiency", "feather_falling", "fire_aspect", "feather_falling",
			"fire_aspect", "fire_protection", "flame", "fortune", "infinity", "knockback", "looting", "luck_of_the_sea",
			"lure", "power", "projectile_protection", "protection", "punch", "respiration", "sharpness", "silk_touch",
			"smite", "thorns", "unbreaking");

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return EssentialCollections.toSortedStringList(enchantmentsAsStrings, args[0]);
		}
		if (args.length > 1) {
			return Main.getPlayersStartWith(args);
		}
		return Collections.emptyList();
	}
}
