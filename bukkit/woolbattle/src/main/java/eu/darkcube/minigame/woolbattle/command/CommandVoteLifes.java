package eu.darkcube.minigame.woolbattle.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;

public class CommandVoteLifes extends Command {

	public CommandVoteLifes() {
		super(Main.getInstance(), "votelifes", new Command[0], "Vote für eine Lebensanzahl",
				new Argument("Leben", "Die Leben"));
		setAliases("vl", "vlifes");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (args.length != 1) {
				sender.sendMessage(getSimpleLongUsage());
				return true;
			}
			int lifes = 0;
			try {
				lifes = Integer.parseInt(args[0]);
				if (lifes < 3 || lifes > 30) {
					lifes = 0;
				}
			} catch (Exception ex) {
			}
			if (lifes == 0) {
				sender.sendMessage("§cBitte gib eine Zahl von 3-30 an");
				return true;
			}
			Player p = (Player) sender;
			User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
			Main.getInstance().getLobby().VOTES_LIFES.put(user, lifes);
			sender.sendMessage("§aDu hast für §5" + lifes + " §aLeben abgestimmt!");
			return true;
		}
		sender.sendMessage(getSimpleLongUsage());
		return true;
	}
}