package eu.darkcube.minigame.woolbattle.common.command;

import java.util.Locale;

import eu.darkcube.minigame.woolbattle.api.command.CommandSource;
import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommands;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.command.commands.WoolBattleRootCommand;
import eu.darkcube.system.libs.com.mojang.brigadier.CommandDispatcher;
import eu.darkcube.system.libs.com.mojang.brigadier.tree.LiteralCommandNode;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public abstract class CommonWoolBattleCommands implements WoolBattleCommands {
    protected final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();

    public void registerDefaults(CommonWoolBattleApi woolbattle) {
        register(new WoolBattleRootCommand(woolbattle));
    }

    @Override
    public final void register(@NotNull WoolBattleCommand command) {
        for (var name : command.names()) {
            name = name.toLowerCase(Locale.ROOT);
            var builder = command.builder(name);
            var node = dispatcher.register(builder);
            register0(command, name, node);
        }
        register0(command);
    }

    @Override
    public final void unregister(@NotNull WoolBattleCommand command) {
        for (var name : command.names()) {
            var node = (LiteralCommandNode<CommandSource>) dispatcher.getRoot().getChild(name);
            dispatcher.getRoot().getChildren().remove(node);
            unregister0(command, name, node);
        }
        unregister0(command);
    }

    protected void unregister0(WoolBattleCommand command) {
    }

    protected void unregister0(WoolBattleCommand command, String name, LiteralCommandNode<CommandSource> node) {
    }

    protected void register0(WoolBattleCommand command) {
    }

    protected void register0(WoolBattleCommand command, String name, LiteralCommandNode<CommandSource> node) {
    }
}
