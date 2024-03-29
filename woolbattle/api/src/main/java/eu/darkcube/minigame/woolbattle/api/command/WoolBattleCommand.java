package eu.darkcube.minigame.woolbattle.api.command;

import java.util.function.Consumer;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

@Api
public class WoolBattleCommand {
    private final @NotNull String name;
    private final @NotNull String permission;
    private final @NotNull String[] aliases;
    private final @NotNull String[] names;
    private final @NotNull Consumer<LiteralArgumentBuilder<CommandSource>> builder;

    @Api
    public WoolBattleCommand(@NotNull String name, @NotNull Consumer<LiteralArgumentBuilder<CommandSource>> builder) {
        this(name, new String[0], builder);
    }

    @Api
    public WoolBattleCommand(@NotNull String name, @NotNull String[] aliases, @NotNull Consumer<LiteralArgumentBuilder<CommandSource>> builder) {
        this(name, "woolbattle." + name, aliases, builder);
    }

    @Api
    public WoolBattleCommand(@NotNull String name, @NotNull String permission, @NotNull String[] aliases, @NotNull Consumer<LiteralArgumentBuilder<CommandSource>> builder) {
        this.name = name;
        this.permission = permission;
        this.aliases = aliases.clone();
        this.builder = builder;
        this.names = new String[this.aliases.length + 1];
        this.names[0] = this.name;
        System.arraycopy(this.aliases, 0, this.names, 1, this.aliases.length);
    }

    @Api
    public @NotNull String name() {
        return name;
    }

    @Api
    public @NotNull String permission() {
        return permission;
    }

    @Api
    public @NotNull String[] aliases() {
        return aliases;
    }

    @Api
    public @NotNull String[] names() {
        return names;
    }

    @Api
    public @NotNull LiteralArgumentBuilder<CommandSource> builder() {
        return builder(name);
    }

    @Api
    public @NotNull LiteralArgumentBuilder<CommandSource> builder(String name) {
        var b = LiteralArgumentBuilder.<CommandSource>literal(name);
        builder.accept(b);
        return b;
    }
}
