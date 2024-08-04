package eu.darkcube.minigame.woolbattle.common.vote;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.vote.Poll;
import eu.darkcube.minigame.woolbattle.api.vote.Vote;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;

public class CommonPoll<Type> implements Poll<Type> {
    private final List<Type> possibilities;
    private final List<Listener<Type>> listeners = new CopyOnWriteArrayList<>();
    private final Map<UUID, CommonVote<Type>> votes = new HashMap<>();

    private CommonPoll(List<Type> possibilities) {
        this.possibilities = List.copyOf(possibilities);
    }

    @Override
    public @Unmodifiable @NotNull Collection<Vote<Type>> votes() {
        return List.copyOf(votes.values());
    }

    @Override
    public @Unmodifiable @NotNull List<Type> possibilities() {
        return possibilities;
    }

    @Override
    public void vote(@NotNull WBUser user, @NotNull Type vote) {
        var id = user.uniqueId();
        var oldVote = votes.get(id);
        if (oldVote != null && oldVote.vote() == vote) return; // Already voted for same value
        votes.put(id, new CommonVote<>(this, user, Instant.now(), vote));
        for (var listener : listeners) {
            listener.voted(user, vote);
        }
    }

    @Override
    public @Nullable Vote<Type> vote(@NotNull WBUser user) {
        var id = user.uniqueId();
        return votes.get(id);
    }

    @Override
    public @NotNull @Unmodifiable List<Type> sortedWinners() {
        var votes = new HashMap<Type, CalculationEntry<Type>>();
        for (var possibility : this.possibilities) {
            votes.put(possibility, new CalculationEntry<>(possibility));
        }
        for (var entry : this.votes.entrySet()) {
            var vote = entry.getValue();
            votes.get(vote.vote()).add(vote);
        }

        var list = new ArrayList<>(votes.keySet());
        list.sort(Comparator.<Type, CalculationEntry<Type>>comparing(votes::get).reversed());
        return List.copyOf(list);
    }

    @Override
    public void addListener(@NotNull Listener<Type> listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(@NotNull Listener<Type> listener) {
        this.listeners.remove(listener);
    }

    @Override
    public @NotNull Collection<@NotNull Listener<Type>> listener() {
        return List.copyOf(this.listeners);
    }

    public void remove(@NotNull WBUser user) {
        var removed = this.votes.remove(user.uniqueId());
        if (removed != null) {
            var vote = removed.vote();
            for (var listener : this.listeners) {
                listener.voteRemoved(user, vote);
            }
        }
    }

    private static class CalculationEntry<Type> implements Comparable<CalculationEntry<Type>> {
        private final Type type;
        private int count = 0;
        private Instant firstVote = null;

        public CalculationEntry(Type type) {
            this.type = type;
        }

        public void add(CommonVote<Type> vote) {
            this.count++;
            if (this.firstVote == null || this.firstVote.compareTo(vote.time()) > 0) {
                this.firstVote = vote.time();
            }
        }

        @Override
        public int compareTo(@NotNull CommonPoll.CalculationEntry<Type> o) {
            var cmp = Integer.compare(count, o.count);
            if (cmp != 0) return cmp;
            if (count == 0 && o.count == 0) {
                return Integer.compare(type.hashCode(), o.type.hashCode());
            }
            return firstVote.compareTo(o.firstVote);
        }
    }

    public static class Builder<Type> implements Poll.Builder<Type> {
        private final CommonVoteRegistry registry = new CommonVoteRegistry();
        private final List<Type> possibilities = new ArrayList<>();

        @NotNull
        @Override
        public Builder<Type> addPossibility(@NotNull Type type) {
            this.possibilities.add(type);
            return this;
        }

        @NotNull
        @SafeVarargs
        @Override
        public final Builder<Type> addPossibilities(@NotNull Type @NotNull ... types) {
            this.possibilities.addAll(Arrays.asList(types));
            return this;
        }

        @NotNull
        @Override
        public Builder<Type> addPossibilities(@NotNull Collection<@NotNull ? extends Type> types) {
            this.possibilities.addAll(types);
            return this;
        }

        @Override
        public @NotNull CommonPoll<Type> addToRegistry() {
            return registry.add(this);
        }

        CommonPoll<Type> build() {
            return new CommonPoll<>(this.possibilities);
        }
    }
}
