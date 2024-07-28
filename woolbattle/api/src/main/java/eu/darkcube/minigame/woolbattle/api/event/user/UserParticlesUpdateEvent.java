package eu.darkcube.minigame.woolbattle.api.event.user;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class UserParticlesUpdateEvent extends UserEvent.Event {
    private final boolean newParticles;

    public UserParticlesUpdateEvent(@NotNull WBUser user, boolean newParticles) {
        super(user);
        this.newParticles = newParticles;
    }

    public boolean newParticles() {
        return newParticles;
    }
}
