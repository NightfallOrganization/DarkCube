package eu.darkcube.minigame.woolbattle.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.commandapi.Command;

public class CommandFix extends Command {

	public CommandFix() {
		super(Main.getInstance(), "fix", new Command[0], "Fix");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (sender instanceof Player && Main.getInstance().getIngame().isEnabled()) {
			new Scheduler() {
				@Override
				public void run() {
					Player p = (Player) sender;
					User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
					Main.getInstance().getIngame().setPlayerItems(user);
				}
			}.runTaskLater(1);
		}
		return true;
	}
}