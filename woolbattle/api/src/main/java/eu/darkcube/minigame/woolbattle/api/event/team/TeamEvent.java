package eu.darkcube.minigame.woolbattle.api.event.team;

import eu.darkcube.minigame.woolbattle.api.team.Team;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface TeamEvent {
    @NotNull
    Team team();

    class Event implements TeamEvent {
        private final @NotNull Team team;

        public Event(@NotNull Team team) {
            this.team = team;
        }

        @Override
        public @NotNull Team team() {
            return team;
        }
    }

    class Cancellable extends eu.darkcube.system.event.Cancellable.Event implements TeamEvent {
        private final @NotNull Team team;

        public Cancellable(@NotNull Team team) {
            this.team = team;
        }

        @Override
        public @NotNull Team team() {
            return team;
        }
    }
}
