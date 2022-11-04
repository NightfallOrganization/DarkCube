package eu.darkcube.system.cloudban.bukkit.command;

import org.bukkit.command.*;
import org.bukkit.entity.*;

import de.dytanic.cloudnet.ext.bridge.player.*;
import eu.darkcube.system.cloudban.bukkit.*;
import eu.darkcube.system.cloudban.util.*;
import eu.darkcube.system.commandapi.*;
import eu.darkcube.system.commandapi.Command;

public class CommandReactReport extends Command {

	public CommandReactReport() {
		super(Main.getInstance(), "reactreport", new Command[0], "React report", new Argument("ReportId", "ReportId"));
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
			ICloudPlayer target = Util.getManager().getOnlinePlayer(report.getPlayer().getUniqueId());
			if (target == null) {
				sender.sendMessage("§cDieser Spieler ist nicht online!");
				return true;
			}
			
			Util.getManager().getPlayerExecutor(((Player)sender).getUniqueId()).connect(target.getConnectedService().getServerName());
//			Util.getManager().proxySendPlayer(Util.getManager().getOnlinePlayer(((Player) sender).getUniqueId()),
//					target.getConnectedService().getServerName());
			return true;
		}
		sender.sendMessage(getSimpleLongUsage());
		return true;
	}
}