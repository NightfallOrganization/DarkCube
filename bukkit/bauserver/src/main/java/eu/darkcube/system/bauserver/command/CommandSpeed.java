package eu.darkcube.system.bauserver.command;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.bauserver.Main;
import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;

public class CommandSpeed extends Command {

	public CommandSpeed() {
		super(Main.getInstance(), "speed", new Command[0], "Ändert deine Geschwindigkeit",
				new Argument("Speed", "Geschwindigkeit (0-20)"));
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length == 1) {
				float speed;
				try {
					speed = Integer.parseInt(args[0]);
					if(speed < 0 || speed > 20) {
						throw new IllegalArgumentException();
					}
				} catch (Exception ex) {
					Main.getInstance().sendMessage("§cBitte gib eine Zahl von 0-20 ein.", p);
					return true;
				}
				
				speed /= 20F;
				if(p.getLocation().subtract(0, 1, 0).getBlock().getType() == Material.AIR) {
					p.setFlySpeed(speed);
					Main.getInstance().sendMessage("§aFlug-Geschwindigkeit auf §6" + speed * 20, p);
				} else {
					p.setWalkSpeed(speed);
					Main.getInstance().sendMessage("§aLauf-Geschwindigkeit auf §6" + speed * 20, p);
				}
				return true;
			}
			Main.getInstance().sendMessage("§cBitte gib eine Zahl von 0-20 ein.", p);
			return true;
		}
		return false;
	}

}
