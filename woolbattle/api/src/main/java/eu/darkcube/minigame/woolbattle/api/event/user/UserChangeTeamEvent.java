package eu.darkcube.minigame.woolbattle.api.event.user;

import eu.darkcube.minigame.woolbattle.api.team.Team;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public class UserChangeTeamEvent extends UserEvent.Event {
    private final @Nullable Team oldTeam;
    private final @Nullable Team newTeam;

    public UserChangeTeamEvent(@NotNull WBUser user, @Nullable Team oldTeam, @Nullable Team newTeam) {
        super(user);
        this.oldTeam = oldTeam;
        this.newTeam = newTeam;
    }

    public @Nullable Team oldTeam() {
        return oldTeam;
    }

    public @Nullable Team newTeam() {
        return newTeam;
    }
}
