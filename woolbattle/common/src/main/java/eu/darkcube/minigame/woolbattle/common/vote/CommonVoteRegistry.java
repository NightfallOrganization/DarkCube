package eu.darkcube.minigame.woolbattle.common.vote;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.vote.Poll;
import eu.darkcube.minigame.woolbattle.api.vote.VoteRegistry;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;

public class CommonVoteRegistry implements VoteRegistry {
    private final Set<CommonPoll<?>> polls = new CopyOnWriteArraySet<>();

    @Override
    public @Unmodifiable @NotNull Collection<@NotNull Poll<?>> polls() {
        return List.copyOf(polls);
    }

    @Override
    public <T> CommonPoll.@NotNull Builder<T> pollBuilder() {
        return new CommonPoll.Builder<>();
    }

    @Override
    public void removeFromAll(@NotNull WBUser user) {
        for (var poll : this.polls) {
            poll.remove(user);
        }
    }

    <T> CommonPoll<T> add(CommonPoll.Builder<T> builder) {
        var poll = builder.build();
        polls.add(poll);
        return poll;
    }
}
