package eu.darkcube.system.cloudban.command.commands;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

import eu.darkcube.system.cloudban.command.*;
import eu.darkcube.system.cloudban.util.*;
import eu.darkcube.system.cloudban.util.Punisher.*;
import eu.darkcube.system.cloudban.util.ban.*;

public class CommandBan extends Command {

//	private CompletableFuture<Object> completable = null;
	private Object lock = new Object();

	public CommandBan() {
		super(new String[] {
				"ban"
		}, "/ban <Spieler> <Grund> [Server]", "darkcube.bansystem.command.ban", "BanSystem", "Bannt einen Spieler");
	}

	@Override
	public void execute(CommandSender sender, String command, String[] args, String commandLine) {
		Supplier<Object> sup = new Supplier<Object>() {
			@Override
			public Void get() {
				synchronized (lock) {
					if (args.length == 2 || args.length == 3) {
						final Reason reason = BanUtil.getReasonByKey(args[1]);
						if (reason == null) {
							sender.sendMessage("&cDieser Grund existiert nicht!");
							return null;
						}
						Server server = Server.GLOBAL;
						if (args.length == 3) {
							server = BanUtil.getServers()
									.stream()
									.filter(s -> s.getServer().equalsIgnoreCase(args[2]))
									.findFirst()
									.orElse(null);
							if (server == null) {
								sender.sendMessage("&cDieser Server existiert nicht!");
								return null;
							}
						}
						final String playername = args[0];
						final UUID uuid = UUIDManager.getUUID(playername);
						if (uuid == null) {
							sender.sendMessage("&cDer Spieler &5" + playername + " &ckonnte nicht gefunden werden!");
							return null;
						}
						PunishmentData data = Punisher.punish(uuid, UUIDManager.getPlayerName(uuid), reason,
								sender.getUniqueId(), sender.getName(), server);

						if (data.isHistorySuccess()) {
							sender.sendMessage("&aDer Ban wurde in den Ban-Logs des Spielers gespeichert!");
						} else {
							sender.sendMessage(
									"&cDer Ban konnte nicht in den Ban-Logs des Spielers gespeichert werden!");
						}
						if (data.isInformationSuccess()) {
							sender.sendMessage("&aDer Spieler wurde für &e" + reason.getDisplay() + " &cgebannt!");
						} else {
							sender.sendMessage("&cDer Spieler konnte nicht gebannt werden!");
						}
						return null;
					}
					sendUsage(sender);
				}
				return null;
			}
		};
		CompletableFuture.supplyAsync(sup);
//		synchronized (completable) {
//			if (completable == null || completable.isDone()) {
//				completable = CompletableFuture.supplyAsync(sup);
//			} else {
//				completable = completable.thenApply(v -> {
//					sup.get();
//					return null;
//				});
//			}
//		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String command, String[] args, String commandLine) {
		if (args.length == 1) {
			return Util.getManager()
					.onlinePlayers()
					.asNames()
					.stream()
					.filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
					.collect(Collectors.toList());
		} else if (args.length == 2) {
			return BanUtil.getReasons()
					.stream()
					.map(Reason::getKey)
					.filter(s -> s.startsWith(args[1]))
					.collect(Collectors.toList());
		} else if (args.length == 3) {
			return BanUtil.getServers()
					.stream()
					.map(Server::getServer)
					.filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
					.collect(Collectors.toList());
		}
		return new ArrayList<>();
	}
}
