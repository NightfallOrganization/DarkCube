package eu.darkcube.system.cloudban.command.commands;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

import eu.darkcube.system.cloudban.command.*;
import eu.darkcube.system.cloudban.util.*;
import eu.darkcube.system.cloudban.util.Punisher.*;
import eu.darkcube.system.cloudban.util.ban.*;

public class CommandUnban extends Command {

	public CommandUnban() {
		super(new String[] { "unban" }, "/unban <Spieler> <Grund> [Server]", "darkcube.bansystem.command.unban",
				"BanSystem", "Entbannt einen Spieler");
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args, String commandLine) {
		CompletableFuture.supplyAsync(new Supplier<Void>() {
			@Override
			public Void get() {
				if (args.length == 2 || args.length == 3) {
					final Reason reason = BanUtil.getReasonByKey(args[1]);
					if (reason == null) {
						sender.sendMessage("&cDieser Grund existiert nicht!");
						return null;
					}
					Server server = Server.GLOBAL;
					if (args.length == 3) {
						server = BanUtil.getServers().stream().filter(s -> s.getServer().equalsIgnoreCase(args[2])).findFirst()
								.orElse(null);
						if (server == null) {
							sender.sendMessage("&cDieser Server existiert nicht!");
							return null;
						}
					}
					final Server server0 = server;
					final String playername = args[0];
					final UUID uuid = UUIDManager.getUUID(playername);
					if (uuid == null) {
						sender.sendMessage("&cDieser Spieler existiert nicht!");
						return null;
					}
					Set<Ban> bans = new HashSet<>();
					bans.addAll(BanUtil.getUserInformation(uuid).getBans());

					Ban ban = bans.stream().filter(b -> b.getReason().equals(reason) && b.getServer().equals(server0))
							.findFirst().orElse(null);
					if (ban == null) {
						sender.sendMessage("&cDieser Spieler ist nicht gebannt!");
						return null;
					}

					PunishmentData data = Punisher.pardon(ban);
					if (data.isHistorySuccess()) {
						sender.sendMessage("&aDer Ban (" + data.getBan().getBanType() + " | "
								+ data.getBan().getDuration().toText() + ") wurde aus den Ban-Logs entfernt!");
					} else {
						sender.sendMessage("&cDer Ban(" + data.getBan().getBanType() + " | "
								+ data.getBan().getDuration().toText() + ") konnte nicht aus den Ban-Logs entfernt werden!");
					}
					if (data.isInformationSuccess()) {
						sender.sendMessage("&aDer Spieler wurde entbannt! \n(" + data.getBan().getBanType() + " | "
								+ data.getBan().getDuration().toText() + ")");
					} else {
						sender.sendMessage("&cDer Spieler konnte nicht entbannt werden! \n(" + data.getBan().getBanType()
								+ " | " + data.getBan().getDuration().toText() + ")");
					}
					return null;
				} else if (args.length == 1) {
					final String playername = args[0];
					final UUID uuid = UUIDManager.getUUID(playername);
					if (uuid == null) {
						sender.sendMessage("&cDieser Spieler existiert nicht!");
						return null;
					}
					Set<Ban> bans = new HashSet<>();
					bans.addAll(BanUtil.getUserInformation(uuid).getBans());
					Set<PunishmentData> datas = new HashSet<>();
					for (Ban ban : bans) {
						datas.add(Punisher.pardon(ban));
					}
					int infoCount = (int) datas.stream().filter(PunishmentData::isInformationSuccess).count();
					int histCount = (int) datas.stream().filter(PunishmentData::isHistorySuccess).count();
					sender.sendMessage("&aDem Spieler wurden &e" + histCount + " &aBans aus den Ban-Logs gelöscht!");
					sender.sendMessage("&aDem Spieler wurden &e" + infoCount + " &aaktive Bans gelöscht!");
					return null;
				}
				sendUsage(sender);
				return null;
			}
		});
		
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String command, String[] args, String commandLine) {
		if (args.length == 1) {
			return new ArrayList<>();
		} else if (args.length == 2) {
			String playername = args[0];
			UUID uuid = UUIDManager.getUUID(playername);
			if (uuid == null) {
				return new ArrayList<>();
			}
			BanInformation info = BanUtil.getUserInformation(uuid);
			return info.getBans().stream().map(Ban::getReason).map(Reason::getKey).filter(k -> k.startsWith(args[1]))
					.collect(Collectors.toList());
		} else if (args.length == 3) {
			String playername = args[0];
			UUID uuid = UUIDManager.getUUID(playername);
			if (uuid == null) {
				return new ArrayList<>();
			}
			BanInformation info = BanUtil.getUserInformation(uuid);
			String reason = args[1];
			return info.getBans().stream().filter(b -> b.getReason().getKey().equals(reason)).map(Ban::getServer)
					.map(Server::getServer).collect(Collectors.toList());
		}
//		if (args.length == 1) {
//			return NodePlayerManager.getInstance().getOnlinePlayers().stream().map(ICloudPlayer::getName)
//					.filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
//		} else if (args.length == 2) {
//			return BanUtil.getReasons().stream().map(Reason::getKey).filter(s -> s.startsWith(args[1]))
//					.collect(Collectors.toList());
//		} else if (args.length == 3) {
//			return BanUtil.getServers().stream().map(Server::getServer)
//					.filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase())).collect(Collectors.toList());
//		}
		return new ArrayList<>();
	}
}
