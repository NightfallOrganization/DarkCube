package eu.darkcube.system.darkessentials.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.Main;

public class CommandPTime extends Command {

	public CommandPTime() {
		super(Main.getInstance(), "ptime", new Command[0], "Setzt die Zeit nur für dich selbst.",
				new Argument("Uhrzeit", "Die Uhrzeit, die du für dich gesetzt wird."));
		setAliases("d_ptime");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			Main.getInstance().sendMessage(Main.cFail() + "Du musst ein Spieler sein, um diesen Command auszuführen!",
					sender);
			return true;
		}
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("reset")) {
				((Player) sender).resetPlayerTime();
				Main.getInstance().sendMessage(Main.cConfirm() + "Zeit zurückgesetzt", sender);
				return true;
			}
			int time;
			try {
				time = Integer.parseInt(args[0]);
			} catch (Exception ex) {
				Main.getInstance().sendMessage(Main.cFail() + "Du musst eine Zahl angeben!", sender);
				return true;
			}
			((Player) sender).setPlayerTime(time, true);
			Main.getInstance().sendMessage(new StringBuilder().append(Main.cConfirm()).append("Zeit auf ")
					.append(Main.cValue()).append(time).append(Main.cConfirm()).append(" gesetzt.").toString(), sender);
		}
		return false;
	}

}
