/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.npc;

import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.StringArgumentType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.ClickEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.ClickEvent.Action;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.command.arguments.GamemodeConnectorArgument;
import eu.darkcube.system.lobbysystem.command.arguments.ServiceTaskArgument;
import eu.darkcube.system.lobbysystem.npc.ConnectorNPC;
import eu.darkcube.system.lobbysystem.parser.Locations;
import eu.darkcube.system.lobbysystem.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class CommandGamemodeConnector extends LobbyCommandExecutor {
    public CommandGamemodeConnector() {
        super("connector", b -> b
                .then(Commands
                        .literal("modify")
                        .then(Commands
                                .argument("connector", new GamemodeConnectorArgument())
                                .then(Commands.literal("destroy").executes(ctx -> {
                                    ConnectorNPC npc = ctx.getArgument("connector", ConnectorNPC.class);
                                    npc.hide();
                                    ConnectorNPC.save();
                                    ctx.getSource().sendMessage(Message.CONNECTOR_NPC_REMOVED, npc.key());
                                    return 0;
                                }))
                                .then(Commands
                                        .literal("v2")
                                        .then(Commands
                                                .literal("set")
                                                .then(Commands.argument("v2key", StringArgumentType.word()).executes(ctx -> {
                                                    ConnectorNPC npc = ctx.getArgument("connector", ConnectorNPC.class);
                                                    String v2key = StringArgumentType.getString(ctx, "v2key");
                                                    npc.v2Key(v2key);
                                                    npc.update();
                                                    ConnectorNPC.save();
                                                    ctx.getSource().sendMessage(Message.CONNECTOR_NPC_V2_SET);
                                                    return 0;
                                                })))
                                        .then(Commands.literal("unset").executes(ctx -> {
                                            ConnectorNPC npc = ctx.getArgument("connector", ConnectorNPC.class);
                                            npc.v2Key(null);
                                            npc.update();
                                            ConnectorNPC.save();
                                            ctx.getSource().sendMessage(Message.CONNECTOR_NPC_V2_UNSET);
                                            return 0;
                                        })))
                                .then(Commands
                                        .literal("addPermission")
                                        .then(Commands.argument("permission", StringArgumentType.string()).executes(ctx -> {
                                            String permission = StringArgumentType.getString(ctx, "permission");
                                            ConnectorNPC npc = ctx.getArgument("connector", ConnectorNPC.class);
                                            if (!npc.getPermissions().contains(permission)) npc.getPermissions().add(permission);
                                            if (Bukkit.getPluginManager().getPermission(permission) == null) {
                                                Bukkit.getPluginManager().addPermission(new Permission(permission));
                                            }
                                            ConnectorNPC.save();
                                            ctx.getSource().sendMessage(Message.CONNECTOR_NPC_PERMISSION_ADDED, permission);
                                            return 0;
                                        })))
                                .then(Commands.literal("listPermissions").executes(ctx -> {
                                    ConnectorNPC npc = ctx.getArgument("connector", ConnectorNPC.class);
                                    ctx.getSource().sendMessage(Message.CONNECTOR_NPC_PERMISSION_LIST, npc.getPermissions().size());
                                    for (String permission : npc.getPermissions()) {
                                        ctx
                                                .getSource()
                                                .sendMessage(Component
                                                        .text(" - ")
                                                        .color(NamedTextColor.GRAY)
                                                        .append(Component
                                                                .text(permission)
                                                                .color(NamedTextColor.DARK_PURPLE)
                                                                .clickEvent(ClickEvent.clickEvent(Action.SUGGEST_COMMAND, "/lobbysystem npc connector modify " + npc.key() + " removePermission " + permission))));
                                    }
                                    return 0;
                                }))
                                .then(Commands
                                        .literal("removePermission")
                                        .then(Commands.argument("permission", StringArgumentType.string()).executes(ctx -> {
                                            String permission = StringArgumentType.getString(ctx, "permission");
                                            ConnectorNPC npc = ctx.getArgument("connector", ConnectorNPC.class);
                                            npc.getPermissions().remove(permission);
                                            ConnectorNPC.save();
                                            ctx.getSource().sendMessage(Message.CONNECTOR_NPC_PERMISSION_REMOVED, permission);
                                            return 0;
                                        })))))
                .then(Commands.literal("create").then(Commands.argument("task", ServiceTaskArgument.serviceTask()).executes(ctx -> {
                    Player player = ctx.getSource().asPlayer();
                    Location loc = Locations.getNiceLocation(player.getLocation());
                    ConnectorNPC npc = new ConnectorNPC(ServiceTaskArgument.getServiceTask(ctx, "task").name(), loc);
                    npc.show();
                    ConnectorNPC.save();
                    ctx.getSource().sendMessage(Message.CONNECTOR_NPC_CREATED, npc.key());
                    return 0;
                }))));
    }
}
