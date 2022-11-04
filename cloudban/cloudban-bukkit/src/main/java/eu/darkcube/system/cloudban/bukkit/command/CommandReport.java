package eu.darkcube.system.cloudban.bukkit.command;

import java.util.*;
import java.util.stream.*;

import org.bukkit.command.*;
import org.bukkit.entity.*;

import com.google.gson.*;

import de.dytanic.cloudnet.common.document.gson.*;
import de.dytanic.cloudnet.driver.channel.*;
import de.dytanic.cloudnet.ext.bridge.player.*;
import eu.darkcube.system.cloudban.bukkit.*;
import eu.darkcube.system.cloudban.util.*;
import eu.darkcube.system.cloudban.util.ban.*;
import eu.darkcube.system.cloudban.util.communication.*;
import eu.darkcube.system.commandapi.*;
import eu.darkcube.system.commandapi.Command;

public class CommandReport extends Command {

	public CommandReport() {
		super(Main.getInstance(), "report", new Command[0], "Melde einen Spieler",
				new Argument("Spieler", "Der Spieler"), new Argument("Grund", "Der Grund"));
//		super(new String[] { "report", "rep", "melde" }, "/report <Spieler> <Grund>",
//				"darkcube.bansystem.command.report", "BanSystem", "Meldet einen Spieler");
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			List<String> list = Util.getManager().onlinePlayers().asNames().stream()
					.filter(p -> p.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
			return list;
		} else if (args.length == 2) {
			return BanUtil.getReasons().stream().filter(Reason::canReportFor).map(Reason::getKey)
					.filter(p -> p.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (sender.hasPermission(Main.getInstance().getReactCommand().getPermission()) && sender instanceof Player
				&& args.length == 1 && args[0].equalsIgnoreCase("list")) {
//			Message msg = Messenger.sendMessage(EnumChannelMessage.REPORT_LIST_FOR_USER,
//					new JsonDocument().append("uuid", ((Player) sender).getUniqueId().toString()));
//			Messenger.freeId(msg.getId());
			ChannelMessage.builder().channel(Messenger.CHANNEL)
					.message(EnumChannelMessage.REPORT_LIST_FOR_USER.getMessage())
					.json(new JsonDocument().append("uuid", ((Player) sender).getUniqueId().toString())).build().send();
			return true;
		}
		if (args.length == 2) {
			if (!(sender instanceof Player)) {
				return false;
			}
			List<? extends ICloudOfflinePlayer> op = Util.getManager().getOfflinePlayers(args[0]);
			if (op.size() == 0) {
				sender.sendMessage("§cDer Spieler §5" + args[0] + " §ckonnte nicht gefunden werden!");
				return true;
			}
			ICloudOfflinePlayer target = op.get(0);
			if (target.getName().equalsIgnoreCase(sender.getName())) {
				sender.sendMessage("§cDu kannst dich nicht selber reporten!");
				return true;
			}

			ChannelMessage query = ChannelMessage.builder().channel(Messenger.CHANNEL)
					.message(EnumChannelMessage.REPORT_GET_ALL.getMessage()).build().sendSingleQuery();
//			Message msg = Messenger.sendMessage(EnumChannelMessage.REPORT_GET_ALL, new JsonDocument());
//			MessageData data = Messenger.awaitMessage(msg);

			final Reason reason = BanUtil.getReasonByKey(args[1]);
			if (reason == null) {
				sender.sendMessage("§cBan-Grund §5" + args[1] + " §ckonnte nicht gefunden werden!");
				return true;
			}

			JsonArray array = new Gson().fromJson(query.getJson().getString("data"), JsonArray.class);
			for (JsonElement element : array) {
//				Report report = Report.fromDocument(new Gson().fromJson(element.getAsJsonPrimitive().getAsString(), JsonDocument.class));
				Report report = Report
						.fromObject(new Gson().fromJson(element.getAsJsonPrimitive().getAsString(), JsonObject.class)
								.getAsJsonObject("jsonObject"));
				if (report.getCreator().getUniqueId().equals(((Player) sender).getUniqueId())
						&& report.getPlayer().getUniqueId().equals(target.getUniqueId())
						&& report.getReason().getKey().equals(reason.getKey())) {
					sender.sendMessage("§cDu hast diesen Spieler bereits reportet!");
					return true;
				}
			}
			Report report = new Report(reason, target,
					Util.getManager().getOfflinePlayer(((Player) sender).getUniqueId()), Report.newId());
//			msg = Messenger.sendMessage(EnumChannelMessage.REPORT_PENDING_NEW, report.toDocument());
//			Messenger.freeId(msg.getId());
			ChannelMessage.builder().channel(Messenger.CHANNEL)
					.message(EnumChannelMessage.REPORT_PENDING_NEW.getMessage()).json(report.toDocument()).build()
					.send();
			sender.sendMessage("§aDu hast §6" + target.getName() + " §afür §5" + reason.getKey() + "§a reportet! "
					+ "Ein Teammitglied wurde kontaktiert und wird sich bald darum kümmern!");
			return true;
		}
//		sendUsage(sender);
		sender.sendMessage(this.getSimpleLongUsage());
		return true;
	}
}
