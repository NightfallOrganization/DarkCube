/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet.command;

public class CommandPServers {

//    private static final IPlayerManager playerManager = CloudNetDriver
//            .getInstance()
//            .getServicesRegistry()
//            .getFirstService(IPlayerManager.class);

    public CommandPServers() {
//		super(SubCommandBuilder.create().prefix(exactStringIgnoreCase("deploymentExclusions"))
//				.generateCommand(
//						(subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
//							String exclusion = (String) args.argument("exclusion").orElse(null);
//							PServerModule.getInstance().addDeploymentExclusion(exclusion);
//							sender.sendMessage("Added exclusion " + exclusion);
//						}, SubCommand::async, exactStringIgnoreCase("add"),
//						dynamicString("exclusion", "Exclusion already exists",
//								e -> !PServerModule.getInstance().getDeploymentExclusions()
//										.contains(e))).generateCommand(
//						(subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
//							String exclusion = (String) args.argument("exclusion").orElse(null);
//							PServerModule.getInstance().removeDeploymentExclusion(exclusion);
//							sender.sendMessage("Removed exclusion " + exclusion);
//						}, SubCommand::async, exactStringIgnoreCase("remove"),
//						dynamicString("exclusion", "Exclusion does not exist",
//								e -> PServerModule.getInstance().getDeploymentExclusions()
//										.contains(e),
//								() -> PServerModule.getInstance().getDeploymentExclusions()))
//				.generateCommand(
//						(subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
//							Collection<String> c =
//									PServerModule.getInstance().getDeploymentExclusions();
//							sender.sendMessage("DeploymentExclusions: (" + c.size() + ")");
//							for (String s : c) {
//								sender.sendMessage(" - " + s);
//							}
//						}, SubCommand::async, exactStringIgnoreCase("list")).removeLastPrefix()
//				.prefix(exactStringIgnoreCase("create")).generateCommand(
//						(subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
//							try {
//								PServerExecutor ps = new PServerBuilder().type(Type.WORLD)
//										.accessLevel(AccessLevel.PUBLIC).create().get();
//								sender.sendMessage(
//										"PServer created! ID: " + ps.id().toString() + ", Name:"
//												+ " " + ps.serverName().get());
//							} catch (InterruptedException | ExecutionException e) {
//								throw new RuntimeException(e);
//							}
//						}, SubCommand::async, exactStringIgnoreCase("new")).generateCommand(
//						(subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
//							String taskn = (String) args.argument("task").orElse(null);
//							if (taskn == null || !CloudNet.getInstance().getServiceTaskProvider()
//									.isServiceTaskPresent(taskn)) {
//								sender.sendMessage("Task nicht gefunden!");
//								return;
//							}
//							PServerBuilder b =
//									new PServerBuilder().taskName(taskn).type(Type.GAMEMODE)
//											.accessLevel(AccessLevel.PUBLIC);
//							PServerExecutor ps;
//							try {
//								ps = b.create().get();
//								sender.sendMessage(
//										"PServer created! ID: " + ps.id().toString() + ", Name: "
//												+ ps.serverName().get());
//							} catch (InterruptedException | ExecutionException e) {
//								throw new RuntimeException(e);
//							}
//						}, SubCommand::async, exactStringIgnoreCase("by"),
//						dynamicString("task", "Task nicht gefunden",
//								s -> CloudNet.getInstance().getServiceTaskProvider()
//										.isServiceTaskPresent(s),
//								() -> CloudNet.getInstance().getServiceTaskProvider()
//										.getPermanentServiceTasks().stream()
//										.map(ServiceTask::getName).collect(Collectors.toList())))
//				.clearPrefixes().generateCommand(
//						(subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
//							try {
//								sender.sendMessage(
//										"PServers: (" + PServerProvider.instance().pservers().get()
//												.size() + ")");
//							} catch (InterruptedException | ExecutionException e) {
//								throw new RuntimeException(e);
//							}
//							try {
//								PServerProvider.instance().pservers().get().forEach(ps -> {
//									try {
//										sender.sendMessage(
//												" - " + ps.id() + " (" + ps.serverName().get()
//														+ ")");
//									} catch (InterruptedException | ExecutionException e) {
//										throw new RuntimeException(e);
//									}
//								});
//							} catch (InterruptedException | ExecutionException e) {
//								throw new RuntimeException(e);
//							}
//						}, SubCommand::async, exactStringIgnoreCase("list"))
//				.prefix(exactStringIgnoreCase("server"))
//				.prefix(dynamicString("id", "Invalid PServer ID",
//						s -> NodePServerProvider.instance().getAllTemplateIDs().contains(s),
//						() -> NodePServerProvider.instance().getAllTemplateIDs())).preExecute(
//						(subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
//							UniqueId id = new UniqueId(args.argument("id").orElse("").toString());
//							PServerExecutor ps;
//							try {
//								ps = PServerProvider.instance().pserver(id).get();
//							} catch (InterruptedException | ExecutionException e) {
//								throw new RuntimeException(e);
//							}
//							if (ps == null) {
//								sender.sendMessage("Invalid PServer: Errors expected!");
//							}
//							internalProperties.put("ps", ps);
//						}).applyHandler(CommandPServers::applyCommands)
//
//				.getSubCommands(), "pservers", "ps");
//		super.prefix = "pserver";
//		super.permission = "cloudnet.command.pserver";
//		super.description = "PServer Managing";
    }

//	private static void applyCommands(SubCommandBuilder builder) {
//		builder.generateCommand(
//				(subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
//					displayInformation(sender, (PServerExecutor) internalProperties.get("ps"));
//				});
//		builder.generateCommand(
//				(subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
//					PServerExecutor ps = (PServerExecutor) internalProperties.get("ps");
//					boolean bool;
//					try {
//						bool = ps.start().get();
//						if (bool) {
//							sender.sendMessage(
//									"PServer started! (Name: " + ps.serverName().get() + ")");
//						} else {
//							sender.sendMessage(
//									"PServer could not start! (Name: " + ps.serverName().get()
//											+ ")");
//						}
//					} catch (InterruptedException | ExecutionException e) {
//						throw new RuntimeException(e);
//					}
//				}, SubCommand::async, exactStringIgnoreCase("start"));
//		builder.generateCommand(
//				(subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
//					PServerExecutor ps = (PServerExecutor) internalProperties.get("ps");
//					ps.stop();
//				}, SubCommand::async, exactStringIgnoreCase("stop"));
//		builder.generateCommand(
//				(subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
//					PServerExecutor ps = (PServerExecutor) internalProperties.get("ps");
//					ps.stop();
//					try {
//						sender.sendMessage(
//								"PServer stopped and removed! (Name: " + ps.serverName().get()
//										+ ")");
//					} catch (InterruptedException | ExecutionException e) {
//						throw new RuntimeException(e);
//					}
//				}, SubCommand::async, exactStringIgnoreCase("remove"));
//		builder.generateCommand(
//				(subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
//					String name = (String) args.argument("player").orElse(null);
//					ICloudPlayer player = playerManager.getFirstOnlinePlayer(name);
//					PServerExecutor ps = (PServerExecutor) internalProperties.get("ps");
//					try {
//						Collection<UUID> owners = ps.owners().get();
//						UUID uuid = player.getUniqueId();
//						if (owners.contains(uuid)) {
//							sender.sendMessage("Player is already an Owner of this PServer");
//							return;
//						}
//						//			ps.addOwner(uuid);
//						ps.addOwner(uuid);
//						sender.sendMessage("Added Player to list of Owners of this PServer");
//					} catch (InterruptedException | ExecutionException e) {
//						throw new RuntimeException(e);
//					}
//				}, SubCommand::async, exactStringIgnoreCase("addOwner"),
//				dynamicString("player", "Player not found!",
//						s -> playerManager.getFirstOnlinePlayer(s) != null,
//						() -> playerManager.onlinePlayers().asNames()));
//		builder.generateCommand(
//				(subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
//					String name = (String) args.argument("player").orElse(null);
//					PServerExecutor ps = (PServerExecutor) internalProperties.get("ps");
//					Collection<UUID> owners;
//					try {
//						owners = ps.owners().get();
//					} catch (InterruptedException | ExecutionException e) {
//						throw new RuntimeException(e);
//					}
//					ICloudOfflinePlayer player = null;
//					try {
//						player = playerManager.getOfflinePlayer(UUID.fromString(name));
//					} catch (Exception ex) {
//						player = playerManager.getFirstOfflinePlayer(name);
//					}
//					if (player == null) {
//						sender.sendMessage("Player not found!");
//						return;
//					}
//					if (owners.contains(player.getUniqueId())) {
//						try {
//							ps.removeOwner(player.getUniqueId()).get();
//						} catch (InterruptedException | ExecutionException e) {
//							throw new RuntimeException(e);
//						}
//						sender.sendMessage(
//								"Removed Player from list of Owners of this PServer! (" + name
//										+ ")");
//					} else {
//						sender.sendMessage(
//								"This Player is not an Owner of this PServer! (" + name + ")");
//					}
//				}, s -> s.async(), exactStringIgnoreCase("removeOwner"),
//				dynamicString("player", () -> {
//					return playerManager.onlinePlayers().asUUIDs().stream().map(UUID::toString)
//							.collect(Collectors.toList());
//				}));
//		builder.generateCommand(
//				(subCommand, sender, command, args, commandLine, properties, internalProperties) -> {
//					PServerExecutor ps = (PServerExecutor) internalProperties.get("ps");
//					Collection<UUID> owners;
//					String serverName;
//					try {
//						owners = ps.owners().get();
//						serverName = ps.serverName().get();
//					} catch (InterruptedException | ExecutionException e) {
//						throw new RuntimeException(e);
//					}
//					sender.sendMessage("Owners of " + serverName + ": (" + owners.size() + ")");
//					for (UUID uuid : owners) {
//						sender.sendMessage(
//								" - " + playerManager.getOfflinePlayer(uuid).getName() + " (" + uuid
//										+ ")");
//					}
//				}, SubCommand::async, exactStringIgnoreCase("listOwners"));
//	}
//
//    private static void displayInformation(ICommandSender sender, PServerExecutor ps) {
//        try {
//            sender.sendMessage("* ID: " + ps.id().toString(), "* Online: " + ps
//                    .onlinePlayers()
//                    .get(), "* Ontime: " + TimeUnit.MILLISECONDS.toSeconds(ps.ontime().get()) + "s", "* ServerName: " + ps
//                    .serverName()
//                    .get(), "* Owners: " + ps.owners().get().toString(), "* State: " + ps.state().get().toString());
//        } catch (InterruptedException | ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
