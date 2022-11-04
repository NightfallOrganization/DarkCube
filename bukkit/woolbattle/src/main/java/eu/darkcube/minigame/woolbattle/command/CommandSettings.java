package eu.darkcube.minigame.woolbattle.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.user.UserSettings;
import eu.darkcube.system.commandapi.Command;

public class CommandSettings extends Command {

	public CommandSettings() {
		super(Main.getInstance(), "settings", new Command[0], "Einstellungen");
		setAliases("einstellungen");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player p = (Player) sender;
		User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
		UserSettings.openSettings(user);
		return true;
	}
}
