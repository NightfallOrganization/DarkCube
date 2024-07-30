package eu.darkcube.minigame.woolbattle.api.vote;

import java.time.Instant;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

/**
 * A vote on a poll for a specific user
 */
public interface Vote<Type> {
    @NotNull
    Poll<Type> poll();

    @NotNull
    WBUser user();

    @NotNull
    Instant time();

    @NotNull
    Type vote();
}
