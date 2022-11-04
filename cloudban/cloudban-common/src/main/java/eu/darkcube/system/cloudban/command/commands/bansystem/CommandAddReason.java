package eu.darkcube.system.cloudban.command.commands.bansystem;

import java.util.ArrayList;
import java.util.List;

import eu.darkcube.system.cloudban.command.Command;
import eu.darkcube.system.cloudban.command.CommandSender;
import eu.darkcube.system.cloudban.util.ban.BanUtil;
import eu.darkcube.system.cloudban.util.ban.Reason;

public class CommandAddReason extends Command {

	public CommandAddReason() {
		super(new String[] {
				"addReason"
		}, "addReason <Reason> [ListInReports] <Display>", "darkcube.bansystem.command.bansystem.addreason",
				"BanSystem", "Erstellt einen Grund", false);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String command, String[] args, String commandLine) {
		return new ArrayList<>();
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args, String commandLine) {
		if (args.length >= 2) {
			String key = args[0];
			Boolean canReportFor = null;
			canReportFor = args[1].equals("true") ? Boolean.TRUE : args[1].equals("false") ? Boolean.FALSE : null;

			StringBuilder other = new StringBuilder();
			for (int i = canReportFor == null ? 1 : 2; i < args.length; i++) {
				other.append(args[i]);
				if (i + 1 < args.length)
					other.append(" ");
			}
			if (BanUtil.getReasons().stream().map(Reason::getKey).filter(s -> s.equals(key)).count() != 0) {
				sender.sendMessage("&cDieser Grund existiert bereits!");
				return;
			}
			if (canReportFor == null) {
				canReportFor = true;
			}
			Reason reason = new Reason(key, other.toString(), canReportFor);
			BanUtil.addReason(reason);
			sender.sendMessage("&aGrund wurde &2erfolgreich &ahinzugefügt!");
			return;
		}
		sendUsage(sender);
	}
}
