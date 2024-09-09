/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.vote;

import java.util.Collection;
import java.util.List;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;

/**
 * A poll with all votes from players
 */
public interface Poll<Type> {

    @Unmodifiable
    @NotNull
    Collection<@NotNull Vote<Type>> votes();

    @Unmodifiable
    @NotNull
    List<Type> possibilities();

    void vote(@NotNull WBUser user, @NotNull Type vote);

    @Nullable
    Vote<Type> vote(@NotNull WBUser user);

    @NotNull
    @Unmodifiable
    List<Type> sortedWinners();

    void addListener(@NotNull Listener<Type> listener);

    default void onVote(@NotNull Listener.Voted<Type> listener) {
        addListener(listener);
    }

    default void onVoteRemoved(@NotNull Listener.VoteRemoved<Type> listener) {
        addListener(listener);
    }

    default void onUpdate(@NotNull Listener.Updated<Type> listener) {
        addListener(listener);
    }

    void removeListener(@NotNull Listener<Type> listener);

    @NotNull
    Collection<@NotNull Listener<Type>> listener();

    interface Listener<Type> {
        void voted(@NotNull WBUser user, @NotNull Type vote);

        void voteRemoved(@NotNull WBUser user, @NotNull Type vote);

        interface Voted<Type> extends Listener<Type> {
            @Override
            default void voteRemoved(@NotNull WBUser user, @NotNull Type vote) {
            }
        }

        interface VoteRemoved<Type> extends Listener<Type> {
            @Override
            default void voted(@NotNull WBUser user, @NotNull Type vote) {
            }
        }

        interface Updated<Type> extends Listener<Type> {
            @Override
            default void voted(@NotNull WBUser user, @NotNull Type vote) {
                updated(user);
            }

            @Override
            default void voteRemoved(@NotNull WBUser user, @NotNull Type vote) {
                updated(user);
            }

            void updated(@NotNull WBUser user);
        }
    }

    interface Builder<Type> {
        @NotNull
        Builder<Type> addPossibility(@NotNull Type type);

        @SuppressWarnings("unchecked")
        @NotNull
        Builder<Type> addPossibilities(@NotNull Type @NotNull ... types);

        @NotNull
        Builder<Type> addPossibilities(@NotNull Collection<@NotNull ? extends Type> types);

        @Api
        @NotNull
        Poll<Type> addToRegistry();
    }
}
