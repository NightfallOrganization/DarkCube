package eu.darkcube.minigame.woolbattle.api.vote;

import java.util.Collection;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;

/**
 * A registry for many polls, for example all lobby polls
 */
public interface VoteRegistry {
    @Unmodifiable
    @NotNull
    Collection<@NotNull Poll<?>> polls();

    @NotNull
    <T> Poll.Builder<T> pollBuilder();

    void removeFromAll(@NotNull WBUser user);
}
