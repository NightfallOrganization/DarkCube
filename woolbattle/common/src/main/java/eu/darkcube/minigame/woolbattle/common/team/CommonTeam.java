package eu.darkcube.minigame.woolbattle.common.team;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.team.Team;
import eu.darkcube.minigame.woolbattle.api.team.TeamType;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.world.ColoredWool;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.commandapi.CommandExecutor;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;

public class CommonTeam implements Team {
    private final @NotNull Game game;
    private final @NotNull UUID uniqueId;
    private final @NotNull String key;
    private final @NotNull TeamType teamType;
    private final @NotNull Style nameStyle;
    private final @NotNull ColoredWool woolColor;
    private final @NotNull Collection<WBUser> users = new CopyOnWriteArraySet<>();
    private volatile int lifes;

    public CommonTeam(@NotNull Game game, @NotNull UUID uniqueId, @NotNull String key, @NotNull TeamType teamType, @NotNull Style nameStyle, @NotNull ColoredWool woolColor) {
        this.game = game;
        this.uniqueId = uniqueId;
        this.key = key;
        this.teamType = teamType;
        this.nameStyle = nameStyle;
        this.woolColor = woolColor;
    }

    @Override
    public @NotNull Game game() {
        return game;
    }

    @Override
    public @NotNull TeamType type() {
        return teamType;
    }

    @Override
    public @NotNull Component getName(@NotNull CommandExecutor executor) {
        return Messages.getMessage(Messages.TEAM_PREFIX + key.toUpperCase(Locale.ROOT), executor.language()).style(nameStyle());
    }

    @Override
    public @NotNull Style nameStyle() {
        return nameStyle;
    }

    @Override
    public @Unmodifiable @NotNull Collection<WBUser> users() {
        return Set.copyOf(users);
    }

    @Override
    public int lifes() {
        return lifes;
    }

    @Override
    public void lifes(int lifes) {
        this.lifes = lifes;
    }

    @Override
    public @NotNull ColoredWool wool() {
        return woolColor;
    }

    @Override
    public int addLifes(int count) {
        var newLifes = this.lifes() + count;
        lifes(newLifes);
        return newLifes;
    }

    @Override
    public int removeLifes(int count) {
        var newLifes = this.lifes() - count;
        lifes(newLifes);
        return newLifes;
    }

    @Override
    public @NotNull UUID uniqueId() {
        return uniqueId;
    }

    public @NotNull Collection<WBUser> usersModifiable() {
        return users;
    }
}
