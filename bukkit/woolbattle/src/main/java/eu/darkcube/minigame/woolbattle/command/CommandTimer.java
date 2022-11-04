package eu.darkcube.minigame.woolbattle.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.system.commandapi.Command;

public class CommandTimer extends Command {

	public CommandTimer() {
		super(Main.getInstance(), "timer", new Command[0], "Setze den Timer",
						CommandArgument.TIMER);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			User user = null;
			if (sender instanceof Player)
				user = Main.getInstance().getUserWrapper().getUser(((Player) sender).getUniqueId());
			Integer timer = null;
			try {
				timer = Integer.valueOf(args[0]);
				if (timer < 0)
					timer = null;
			} catch (Exception ex) {
			}
			if (timer == null) {
				if (user != null)
					sender.sendMessage(Message.ENTER_POSITIVE_NUMBER.getMessage(user));
				else
					sender.sendMessage(Message.ENTER_POSITIVE_NUMBER.getServerMessage());
				return true;
			}
			Main.getInstance().getLobby().setOverrideTimer(timer <= 1 ? 2
							: timer * 20);
			if (user != null)
				sender.sendMessage(Message.TIMER_CHANGED.getMessage(user, timer.toString()));
			else
				sender.sendMessage(Message.TIMER_CHANGED.getServerMessage(timer.toString()));
			return true;
		}
		return false;
	}
}