package de.pixel.bedwars.command.bedwars.spawner;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.spawner.ItemSpawner;
import de.pixel.bedwars.spawner.ItemSpawnerBronze;
import de.pixel.bedwars.spawner.ItemSpawnerGold;
import de.pixel.bedwars.spawner.ItemSpawnerIron;
import de.pixel.bedwars.spawner.io.SpawnerIO;
import de.pixel.bedwars.util.Arrays;
import eu.darkcube.system.commandapi.Command;

public class CommandCreate extends Command {

	public CommandCreate() {
		super(Main.getInstance(), "create", new Command[0], "Erstellt einen Spawner");
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Arrays.toSortedStringList(new String[] { "bronze", "iron", "gold" }, args[0].toLowerCase());
		}
		return super.onTabComplete(args);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player p = (Player) sender;
		Block block = p.getLocation().subtract(0, 1, 0).getBlock();
		if (args.length == 1) {
			String type = args[0].toLowerCase();
			ItemSpawner spawner;
			switch (type) {
			case "bronze":
				p.sendBlockChange(block.getLocation(), Material.BRICK, (byte) 0);
				spawner = new ItemSpawnerBronze(block);
				break;
			case "iron":
				p.sendBlockChange(block.getLocation(), Material.IRON_BLOCK, (byte) 0);
				spawner = new ItemSpawnerIron(block);
				break;
			case "gold":
				p.sendBlockChange(block.getLocation(), Material.GOLD_BLOCK, (byte) 0);
				spawner = new ItemSpawnerGold(block);
				break;
			default:
				sender.sendMessage("§cUngültiger Spawn-Typ");
				return true;
			}
			SpawnerIO.save(spawner);
			sender.sendMessage("§aDu hast einen Spawner vom Typ "
					+ spawner.getClass().getSimpleName().replace("ItemSpawner", "") + " erstellt");
			return true;
		}
		return false;
	}
}
