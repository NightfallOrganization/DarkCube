package eu.darkcube.system.cloudban.bukkit.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.channel.ChannelMessage;
import eu.darkcube.system.cloudban.bukkit.Main;
import eu.darkcube.system.cloudban.util.Report;
import eu.darkcube.system.cloudban.util.communication.EnumChannelMessage;
import eu.darkcube.system.cloudban.util.communication.Messenger;
import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;

public class CommandRemoveReport extends Command {

	public CommandRemoveReport() {
		super(Main.getInstance(), "removereport", new Command[0], "Remove report",
				new Argument("ReportId", "ReportId"));
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			if (!(sender instanceof Player)) {
				return false;
			}
			int id;
			try {
				id = Integer.parseInt(args[0]);
			} catch (Exception ex) {
				sender.sendMessage("§cUngültige Id. Wahrscheinlich wurde der Report bereits bearbeitet!");
				return true;
			}
			Report report = Report.byId(id);
			if (report == null) {
				sender.sendMessage("§cUngültige Id. Wahrscheinlich wurde der Report bereits bearbeitet!");
				return true;
			}
//			Messenger.sendMessage(EnumChannelMessage.REPORT_REMOVE, new JsonDocument().append("id", id));
			ChannelMessage.builder().channel(Messenger.CHANNEL).message(EnumChannelMessage.REPORT_REMOVE.getMessage())
					.json(new JsonDocument().append("id", id)).build().send();
			sender.sendMessage("§aReport entfernt!");
			return true;
		}
		sender.sendMessage(getSimpleLongUsage());
		return true;
	}
}
