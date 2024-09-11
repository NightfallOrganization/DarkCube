/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import dev.derklaro.aerogel.Inject;
import dev.derklaro.aerogel.Singleton;
import eu.cloudnetservice.driver.service.ServiceTask;
import eu.cloudnetservice.modules.bridge.player.CloudOfflinePlayer;
import eu.cloudnetservice.modules.bridge.player.PlayerManager;
import eu.cloudnetservice.node.command.annotation.CommandAlias;
import eu.cloudnetservice.node.command.exception.ArgumentNotAvailableException;
import eu.cloudnetservice.node.command.source.CommandSource;
import eu.darkcube.system.pserver.cloudnet.PServerModule;
import eu.darkcube.system.pserver.common.PServerBuilder;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;

@Singleton
@CommandAlias({"pserver", "ps"})
@CommandDescription("PServer Management")
@Permission("darkcube.command.pserver")
public class CommandPServers {

    @Inject
    private PlayerManager playerManager;

    @Parser(suggestions = "uniqueid")
    public PServerExecutor pserverExecutorParser(CommandContext<CommandSource> ctx, Queue<String> input) {
        UniqueId uniqueId = uniqueIdParser(ctx, input);
        PServerExecutor ex = PServerProvider.instance().pserver(uniqueId).join();
        if (ex == null) throw new ArgumentNotAvailableException("PServer nicht gefunden: " + uniqueId);
        return ex;
    }

    @Suggestions("uniqueid")
    public List<String> suggestUniqueIds(CommandContext<CommandSource> ctx, String input) {
        return PServerProvider.instance().pservers().join().stream().map(PServerExecutor::id).map(UniqueId::toString).toList();
    }

    @Parser(suggestions = "uniqueid")
    public UniqueId uniqueIdParser(CommandContext<CommandSource> ctx, Queue<String> input) {
        var string = input.remove();
        try {
            UUID uuid = UUID.fromString(string);
            return new UniqueId(uuid.toString());
        } catch (IllegalArgumentException ex) {
            throw new ArgumentNotAvailableException("Invalid UniqueId");
        }
    }

    @Suggestions("onlinePlayerUUIDs")
    public List<String> suggestOnlinePlayerUUIDs(CommandContext<CommandSource> ctx, String input) {
        return playerManager.onlinePlayers().uniqueIds().stream().map(UUID::toString).toList();
    }

    @Parser(name = "playerParser", suggestions = "onlinePlayerUUIDs")
    public UUID playerParser(CommandContext<CommandSource> ctx, Queue<String> input) {
        var string = input.remove();
        try {
            return UUID.fromString(string);
        } catch (IllegalArgumentException ex) {
            throw new ArgumentNotAvailableException("Invalid UniqueId");
        }
    }

    @Command("pservers|pserver|ps deploymentExclusions remove <exclusion>")
    public void deploymentExclusionsRemove(CommandSource source, @Argument("exclusion") String exclusion) {
        if (PServerModule.getInstance().removeDeploymentExclusion(exclusion)) {
            source.sendMessage("Removed deploymentExclusion");
        } else {
            source.sendMessage("Exclusion doesn't exist");
        }
    }

    @Command("pservers|pserver|ps deploymentExclusions add <exclusion>")
    public void deploymentExclusionsAdd(CommandSource source, @Argument("exclusion") String exclusion) {
        if (PServerModule.getInstance().addDeploymentExclusion(exclusion)) {
            source.sendMessage("Added deploymentExclusion");
        } else {
            source.sendMessage("Exclusion already exists");
        }
    }

    @Command("pservers|pserver|ps deploymentExclusions list")
    public void deploymentExclusionsList(CommandSource source) {
        Collection<String> c = PServerModule.getInstance().compiledDeploymentExclusions().stream().map(Pattern::pattern).toList();
        source.sendMessage("DeploymentExclusions: (" + c.size() + ")");
        for (String s : c) {
            source.sendMessage(" - " + s);
        }
    }

    @Command("pservers|pserver|ps create new")
    public void createNew(CommandSource source) {
        PServerExecutor ps = new PServerBuilder().type(PServerExecutor.Type.WORLD).accessLevel(PServerExecutor.AccessLevel.PUBLIC).create().join();
        source.sendMessage("PServer created! ID: " + ps.id() + ", Name:" + " " + ps.serverName().join());
    }

    @Command("pservers|pserver|ps create by <task>")
    public void createBy(CommandSource source, @Argument("task") ServiceTask task) {
        PServerBuilder b = new PServerBuilder().taskName(task.name()).type(PServerExecutor.Type.GAMEMODE).accessLevel(PServerExecutor.AccessLevel.PUBLIC);
        PServerExecutor executor = b.create().join();
        source.sendMessage("PServer created: ID: " + executor.id() + ", Name: " + executor.serverName().join());
    }

    @Command("pservers|pserver|ps list")
    public void list(CommandSource source) {
        Collection<? extends PServerExecutor> pservers = PServerProvider.instance().pservers().join();
        source.sendMessage("PServers: (" + pservers.size() + ")");
        pservers.forEach(ps -> {
            source.sendMessage(" - " + ps.id() + " (" + ps.serverName().join() + ")");
        });
    }

    @Command("pservers|pserver|ps server <id>")
    public void displayServer(CommandSource source, @Argument("id") PServerExecutor executor) {
        Collection<String> messages = new ArrayList<>();
        messages.add("* ID: " + executor.id());
        messages.add("* Online: " + executor.onlinePlayers().join());
        messages.add("* Ontime: " + TimeUnit.MILLISECONDS.toSeconds(executor.ontime().join()) + "s");
        messages.add("* ServerName: " + executor.serverName().join());
        messages.add("* Owners: " + executor.owners().join());
        messages.add("* State: " + executor.state().join());
        source.sendMessage(messages);
    }

    @Command("pservers|pserver|ps server <id> start")
    public void start(CommandSource source, @Argument("id") PServerExecutor executor) {
        executor.start().whenComplete((bool, t) -> {
            if (t != null) source.sendMessage("An error occurred while starting the PServer");
            else if (bool) source.sendMessage("PServer started! (Name: " + executor.serverName().join() + ")");
            else source.sendMessage("PServer could not start! (Name: " + executor.serverName().join() + ")");

        });
    }

    @Command("pservers|pserver|ps server <id> stop")
    public void stop(CommandSource source, @Argument("id") PServerExecutor executor) {
        executor.stop();
    }

    @Command("pservers|pserver|ps server <id> addOwner <player>")
    public void addOwner(CommandSource source, @Argument("id") PServerExecutor executor, @Argument(value = "player", parserName = "playerParser") UUID player) {
        if (executor.owners().join().contains(player)) {
            source.sendMessage("Player is already an Owner of this PServer");
            return;
        }
        executor.addOwner(player).whenComplete((bool, t) -> {
            if (t != null) source.sendMessage("An error occurred while adding player to the list of owners");
            else if (bool) source.sendMessage("Added player to the list of Owners of this PServer");
            else source.sendMessage("Could not add owner!");
        });
    }

    @Command("pservers|pserver|ps server <id> removeOwner <player>")
    public void removeOwner(CommandSource source, @Argument("id") PServerExecutor executor, @Argument(value = "player", parserName = "playerParser") UUID player) {
        Collection<UUID> owners = executor.owners().join();
        if (!owners.contains(player)) {
            source.sendMessage("Player is not an owner of this PServer");
            return;
        }
        executor.removeOwner(player).whenComplete((bool, t) -> {
            if (t != null) source.sendMessage("An error occurred while removing player from the list of owners");
            else if (bool) source.sendMessage("Removed player from the list of Owners of this PServer");
            else source.sendMessage("Could not remove owner!");
        });
    }

    @Command("pservers|pserver|ps server <id> listOwners")
    public void listOwners(CommandSource source, @Argument("id") PServerExecutor executor) {
        Collection<UUID> owners = executor.owners().join();
        String serverName = executor.serverName().join();
        source.sendMessage("Owners of " + serverName + ": (" + owners.size() + ")");
        for (UUID uuid : owners) {
            CloudOfflinePlayer offlinePlayer = playerManager.offlinePlayer(uuid);
            String playerName = offlinePlayer == null ? "Unknown Player" : offlinePlayer.name();
            source.sendMessage(" - " + playerName + " (" + uuid + ")");
        }
    }
}
