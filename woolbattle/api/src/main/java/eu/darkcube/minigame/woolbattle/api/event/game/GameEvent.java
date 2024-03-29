package eu.darkcube.minigame.woolbattle.api.event.game;

import eu.darkcube.minigame.woolbattle.api.event.user.UserEvent;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface GameEvent {
    @NotNull Game game();

    class Event implements GameEvent {
        private final @NotNull Game game;

        public Event(@NotNull Game game) {
            this.game = game;
        }

        @Override
        public @NotNull Game game() {
            return game;
        }
    }

    interface User extends GameEvent, UserEvent {
        class Event extends UserEvent.Event implements GameEvent {
            private final @NotNull Game game;

            public Event(@NotNull WBUser user, @NotNull Game game) {
                super(user);
                this.game = game;
            }

            @Override
            public @NotNull Game game() {
                return game;
            }
        }
    }
}
