package eu.darkcube.system.pserver.node.command;

import static de.dytanic.cloudnet.command.sub.SubCommandArgumentTypes.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.command.ICommandSender;
import de.dytanic.cloudnet.command.sub.SubCommandBuilder;
import de.dytanic.cloudnet.command.sub.SubCommandHandler;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.ext.bridge.player.ICloudOfflinePlayer;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.PServer.State;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.UniqueIdProvider;
import eu.darkcube.system.pserver.common.packet.PServerSerializable;
import eu.darkcube.system.pserver.node.NodePServer;
import eu.darkcube.system.pserver.node.NodePServerProvider;
import eu.darkcube.system.pserver.node.PServerModule;

public class CommandPServers extends SubCommandHandler {

	private static final IPlayerManager playerManager = CloudNetDriver.getInstance()
			.getServicesRegistry()
			.getFirstService(IPlayerManager.class);

	public CommandPServers() {
		super( // @formatter:off
				SubCommandBuilder.create()
				.prefix(exactStringIgnoreCase("deploymentExclusions"))
				.generateCommand((subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
					String exclusion = (String)args.argument("exclusion").orElse(null);
					PServerModule.getInstance().addDeploymentExclusion(exclusion);
					sender.sendMessage("Added exclusion " + exclusion);
				}, s -> s.async(), exactStringIgnoreCase("add"), dynamicString("exclusion", "Exclusion already exists", 
						e -> !PServerModule.getInstance().getDeploymentExclusions().contains(e)))
				.generateCommand((subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
					String exclusion = (String)args.argument("exclusion").orElse(null);
					PServerModule.getInstance().removeDeploymentExclusion(exclusion);
					sender.sendMessage("Removed exclusion " + exclusion);
				}, s -> s.async(), exactStringIgnoreCase("remove"), dynamicString("exclusion", "Exclusion does not exist", 
						e -> PServerModule.getInstance().getDeploymentExclusions().contains(e), 
						() -> PServerModule.getInstance().getDeploymentExclusions()))
				.generateCommand((subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
					Collection<String> c = PServerModule.getInstance().getDeploymentExclusions();
					sender.sendMessage("DeploymentExclusions: (" + c.size() + ")");
					for(String s : c) {
						sender.sendMessage(" - " + s);
					}
				}, s -> s.async(), exactStringIgnoreCase("list"))
				.removeLastPrefix()
				.generateCommand((subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
					String idname = (String)args.argument("id").orElse(null);
					UniqueId uid = new UniqueId(idname);
					if(NodePServerProvider.getInstance().isPServer(uid)) {
						sender.sendMessage("PServer already loaded!");
						return;
					}
					int online = 0;
					PServer.State state = State.OFFLINE;
					boolean privateServer = false;
					boolean temporary = false;
					long startedAt = System.currentTimeMillis();
					Collection<UUID> owners = PServerProvider.getInstance().getOwners(uid);
					String serverName = PServerProvider.getInstance().newName();
					PServerSerializable ser = new PServerSerializable(uid, online, state, privateServer, temporary, 
							startedAt, owners, null, serverName);
					NodePServer ps = NodePServerProvider.getInstance().createPServer(ser);
					sender.sendMessage("PServer loaded! ID: " + ps.getId().toString() + ", Name: " + ps.getServerName());
				}, s-> s.async(), exactStringIgnoreCase("load"), dynamicString("id", "PServer does not exist", s ->
						NodePServerProvider.getInstance().getAllTemplateIDs().contains(s), 
						() -> NodePServerProvider.getInstance().getAllTemplateIDs())
				)
				.prefix(exactStringIgnoreCase("create"))
				.generateCommand((subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
					UniqueId uid = UniqueIdProvider.getInstance().newUniqueId();
					int online = 0;
					PServer.State state = State.OFFLINE;
					boolean privateServer = false;
					boolean temporary = false;
					long startedAt = System.currentTimeMillis();
					Collection<UUID> owners = new ArrayList<>();
					String serverName = PServerProvider.getInstance().newName();
					PServerSerializable ser = new PServerSerializable(uid, online, state, privateServer, temporary, 
							startedAt, owners, null, serverName);
					NodePServer ps = NodePServerProvider.getInstance().createPServer(ser);
					sender.sendMessage("PServer created! ID: " + ps.getId().toString() + ", Name: " + ps.getServerName());
				}, s -> s.async(), exactStringIgnoreCase("new"))
				.generateCommand((subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
					String taskn = (String) args.argument("task").orElse(null);
					if(taskn == null || !CloudNet.getInstance().getServiceTaskProvider().isServiceTaskPresent(taskn)) {
						sender.sendMessage("Task nicht gefunden!");
						return;
					}
					ServiceTask task = CloudNet.getInstance().getServiceTaskProvider().getServiceTask(taskn);
					UniqueId uid = UniqueIdProvider.getInstance().newUniqueId();
					int online = 0;
					PServer.State state = State.OFFLINE;
					boolean privateServer = false;
					boolean temporary = true;
					long startedAt = System.currentTimeMillis();
					Collection<UUID> owners = new ArrayList<>();
					String serverName = PServerProvider.getInstance().newName();
					PServerSerializable ser = new PServerSerializable(uid, online, state, privateServer, temporary, 
							startedAt, owners, null, serverName);
					NodePServer ps = NodePServerProvider.getInstance().createPServer(ser, task);
					sender.sendMessage("PServer created! ID: " + ps.getId().toString() + ", Name: " + ps.getServerName());
				}, s -> s.async(), exactStringIgnoreCase("by"), dynamicString("task", "Task nicht gefunden", 
						s -> CloudNet.getInstance().getServiceTaskProvider().isServiceTaskPresent(s), 
						() -> CloudNet.getInstance().getServiceTaskProvider().getPermanentServiceTasks()
						.stream().map(ServiceTask::getName).collect(Collectors.toList())))
				.clearPrefixes()
				.generateCommand((subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
					sender.sendMessage("PServers: (" + PServerProvider.getInstance().getPServers().size() + ")");
					PServerProvider.getInstance().getPServers().forEach(ps -> {
						sender.sendMessage(" - " + ps.getId() + " (" + ps.getServerName() + ")");
					});
				}, s -> s.async(), exactStringIgnoreCase("list"))
				.prefix(exactStringIgnoreCase("server"))
				.prefix(
						dynamicString("id", "Invalid PServer ID", 
								s -> PServerModule.getCurrentPServerIDs().contains(new UniqueId(s)), 
								() -> PServerModule.getCurrentPServerIDs().stream().map(Object::toString).collect(Collectors.toList()))
				)
				.preExecute((subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
					UniqueId id = new UniqueId(args.argument("id").orElse("").toString());
					PServer ps = PServerProvider.getInstance().getPServer(id);
					if(ps == null) {
						sender.sendMessage("Invalid PServer: Errors expected!");
					}
					internalProperties.put("ps", ps);
				})
				.applyHandler(CommandPServers::applyCommands)
				
				.getSubCommands()
				// @formatter:on
				, "pservers", "ps");
		super.prefix = "pserver";
		super.permission = "cloudnet.command.pserver";
		super.description = "PServer Managing";
	}

	private static SubCommandBuilder applyCommands(SubCommandBuilder builder) {
		// @formatter:off
		builder.generateCommand(
				(subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
					displayInformation(sender, (PServer)internalProperties.get("ps"));
		});
		builder.generateCommand(
				(subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
			PServer ps = (PServer)internalProperties.get("ps");
			boolean bool = ps.start();
			if(bool) {
				sender.sendMessage("PServer started! (Name: " + ps.getServerName() + ")");
			} else {
				sender.sendMessage("PServer could not start! (Name: " + ps.getServerName() + ")");
			}
		}, s -> s.async(), exactStringIgnoreCase("start"));
		builder.generateCommand((subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
			PServer ps = (PServer)internalProperties.get("ps");
			boolean bool = ps.stop();
			if(bool) {
				sender.sendMessage("PServer stopped! (Name: " + ps.getServerName() + ")");
			} else {
				sender.sendMessage("PServer could not stop! (Name: " + ps.getServerName() + ")");
			}
		}, s -> s.async(), exactStringIgnoreCase("stop"));
		builder.generateCommand((subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
			PServer ps = (PServer)internalProperties.get("ps");
			ps.remove();
			sender.sendMessage("PServer stopped and removed! (Name: " + ps.getServerName() + ")");
		}, s-> s.async(), exactStringIgnoreCase("remove"));
		builder.generateCommand((subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
			String name = (String)args.argument("player").orElse(null);
			ICloudPlayer player = playerManager.getFirstOnlinePlayer(name);
			PServer ps = (PServer) internalProperties.get("ps");
			UUID uuid = player.getUniqueId();
			if(ps.getOwners().contains(uuid)) {
				sender.sendMessage("Player is already an Owner of this PServer");
				return;
			}
//			ps.addOwner(uuid);
			PServerProvider.getInstance().addOwner(ps.getId(), uuid);
			sender.sendMessage("Added Player to list of Owners of this PServer");
		}, s -> s.async(), exactStringIgnoreCase("addOwner"), dynamicString("player", "Player not found!", 
				s -> playerManager.getFirstOnlinePlayer(s) != null, 
				() -> playerManager.onlinePlayers().asNames()));
		builder.generateCommand((subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
			String name = (String)args.argument("player").orElse(null);
			PServer ps = (PServer)internalProperties.get("ps");
			ICloudOfflinePlayer player = null;
			try {
				player = playerManager.getOfflinePlayer(UUID.fromString(name));
			} catch (Exception ex) {
				player = playerManager.getFirstOfflinePlayer(name);
			}
			if(player == null) {
				sender.sendMessage("Player not found!");
				return;
			}
			if(ps.getOwners().contains(player.getUniqueId())) {
//				ps.removeOwner(player.getUniqueId());
				PServerProvider.getInstance().removeOwner(ps.getId(), player.getUniqueId());
				sender.sendMessage("Removed Player from list of Owners of this PServer! (" + name + ")");
			} else {
				sender.sendMessage("This Player is not an Owner of this PServer! (" + name + ")");
			}
		}, s -> s.async(), exactStringIgnoreCase("removeOwner"), dynamicString("player", () -> {
			return playerManager.onlinePlayers().asUUIDs().stream().map(UUID::toString).collect(Collectors.toList());
		}));
		builder.generateCommand((subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
			PServer ps = (PServer)internalProperties.get("ps");
			sender.sendMessage("Owners of " + ps.getServerName() + ": (" + ps.getOwners().size() + ")");
			for(UUID uuid : ps.getOwners()) {
				sender.sendMessage(" - " + playerManager.getOfflinePlayer(uuid).getName() + " (" + uuid + ")");
			}
		}, s -> s.async(), exactStringIgnoreCase("listOwners"));
		// @formatter:on
		return builder;
	}

	private static void displayInformation(ICommandSender sender, PServer ps) {
		sender.sendMessage(new String[] {
				// @formatter:off
				"* ID: " + ps.getId().toString(), 
				"* Online: " + ps.getOnlinePlayers() + "/" + ((NodePServer)ps).getSnapshot().getProperty(BridgeServiceProperty.MAX_PLAYERS).orElse(-1),
				"* Ontime: " + TimeUnit.MILLISECONDS.toSeconds(ps.getOntime()) + "s",
				"* ServerName: " + ps.getServerName(),
				"* Owners: " + ps.getOwners().toString(),
				"* State: " + ps.getState().toString()
				// @formatter:on
		});
	}
}
