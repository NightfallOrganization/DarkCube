/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.DarkEssentials;
import eu.darkcube.system.darkessentials.util.EssentialCollections;
import eu.darkcube.system.inventoryapi.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CommandSpawner extends Command implements Listener {

	public CommandSpawner() {
		super(DarkEssentials.getInstance(), "spawner", new Command[0], "Gibt dir einen Monsterspawner",
				new Argument[] { new Argument("Entity", "Das zu Spawnende Entity"),
						new Argument("Spieler", "Der Spieler, der den Spawner bekommen soll.", false) });
		this.setAliases("d_spawner");
		Bukkit.getPluginManager().registerEvents(this, DarkEssentials.getInstance());
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0)
			return false;
		Set<Player> players = new HashSet<>();

		EntityType entity = null;
		try {
			try {
				entity = EntityType.valueOf(args[0].toUpperCase(Locale.ENGLISH));
			} catch (Exception ex) {
			}
			if (entity == null) {
				switch (args[0].toLowerCase()) {
				case "boneman":
					entity = EntityType.SKELETON;
					break;
				case "pigzombie":
				case "zombie_pigman":
				case "zombiepigman":
					entity = EntityType.PIG_ZOMBIE;
					break;
				case "cavespider":
					entity = EntityType.CAVE_SPIDER;
					break;
				case "magmacube":
				case "magma_slime":
				case "magmaslime":
					entity = EntityType.MAGMA_CUBE;
					break;
				case "enderdragon":
				case "dragon":
					entity = EntityType.ENDER_DRAGON;
					break;
				case "mushroomcow":
				case "mooshroom":
					entity = EntityType.MUSHROOM_COW;
					break;
				case "snow_man":
				case "snowgolem":
				case "snow_golem":
					entity = EntityType.SNOWMAN;
					break;
				case "cat":
					entity = EntityType.OCELOT;
					break;
				case "irongolem":
				case "golem":
				case "villager_golem":
				case "villagergolem":
					entity = EntityType.IRON_GOLEM;
					break;
				default:
					throw new IllegalArgumentException();
				}
			}
		} catch (Exception e) {
			DarkEssentials.getInstance().sendMessage(DarkEssentials.cFail() + "Du musst ein Mob angeben!", sender);
			return true;
		}
		args[0] = "%processed%";
		Set<String> unresolvedNames = new HashSet<>();
		for (String playerName : args) {
			if (!playerName.equals("%processed%")) {
				if (Bukkit.getPlayer(playerName) != null) {
					players.add(Bukkit.getPlayer(playerName));
				} else {
					unresolvedNames.add(playerName);
				}
			}
		}
		if (players.isEmpty()) {
			if (sender instanceof Player) {
				players.add((Player) sender);
			} else {
				DarkEssentials.sendMessagePlayernameRequired(sender);
				return true;
			}
		}
		DarkEssentials.sendMessagePlayerNotFound(unresolvedNames, sender);
		ItemStack item = new ItemBuilder(Material.MOB_SPAWNER).unsafe().setString("spawnertype", entity.name())
				.builder().displayname(ChatColor.GOLD + entity.name() + ChatColor.GRAY + " Spawner").build();
//		ItemStack nmsSpawner = CraftItemStack.asNMSCopy(bukkitSpawner);
//		NBTTagCompound tag = nmsSpawner.getTag();
//		if (tag == null)
//			tag = new NBTTagCompound();
//		tag.set("spawnertype", new NBTTagString(entity.name()));
//		nmsSpawner.setTag(tag);
//		ItemMeta meta = CraftItemStack.getItemMeta(nmsSpawner);
//		meta.setDisplayName(ChatColor.GOLD + entity.name() + ChatColor.GRAY
//						+ " Spawner");
//		bukkitSpawner.setItemMeta(meta);
		int count = 0;
		for (Player current : players) {
			current.getInventory().addItem(item);
			DarkEssentials.getInstance().sendMessage(
					DarkEssentials.cConfirm() + "Du hast einen " + DarkEssentials.cValue() + entity.name()
					+ DarkEssentials.cConfirm() + "-Spawner erhalten.", sender);
			count++;
		}
		if (!(players.size() == 1 && players.contains(sender))) {
			DarkEssentials.getInstance().sendMessage(
					DarkEssentials.cValue() + count + DarkEssentials.cConfirm() + " Spielern einen Spawner gegeben.",
					sender);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return EssentialCollections.toSortedStringList(EntityType.values(), args[0]);
		}
		if (args.length > 1) {
			return DarkEssentials.getPlayersStartWith(args);
		}
		return super.onTabComplete(args);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void handle(BlockPlaceEvent e) {
		if (e.isCancelled())
			return;
		if (!e.getItemInHand().getType().equals(Material.MOB_SPAWNER))
			return;
		ItemStack itemStack = e.getItemInHand();
		ItemBuilder b = new ItemBuilder(itemStack);
		if (b.getMeta() == null) {
			return;
		}
		String type = b.unsafe().getString("spawnertype");
		if (type == null || type.isEmpty()) {
			return;
		}
		EntityType entity = this.find(type);
		if (entity == null) {
			return;
		}
		CreatureSpawner spawner = (CreatureSpawner) e.getBlock().getState();
		spawner.setSpawnedType(entity);
		spawner.update();
		spawner.update(true);
	}

	private EntityType find(String type) {
		for (EntityType e : EntityType.values()) {
			if (e.name().equals(type)) {
				return e;
			}
		}
		return null;
	}

}
