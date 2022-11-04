package eu.darkcube.system.cloudban.command.commands.bansystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.darkcube.system.cloudban.command.Command;
import eu.darkcube.system.cloudban.command.CommandSender;
import eu.darkcube.system.cloudban.util.ban.BanUtil;
import eu.darkcube.system.cloudban.util.ban.Reason;

public class CommandListReasons extends Command {

	public CommandListReasons() {
		super(new String[] { "listReasons" }, "listReasons", "darkcube.bansystem.command.bansystem.listreasons",
				"BanSystem", "Listet alle Gründe auf", false);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String command, String[] arguments, String commandLine) {
		return new ArrayList<>();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] arguments, String commandLine) {
		Collection<Reason> reasons = BanUtil.getReasons();
		if (reasons.size() != 0) {
			sender.sendMessage("&aAlle Gründe: ");
			for (Reason reason : reasons) {
				sender.sendMessage("&b" + reason.getKey() + "&7 | &e" + reason.getDisplay());
			}
		} else {
			sender.sendMessage("&cEs existieren keine Gründe!");
		}
	}
}
