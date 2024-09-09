package eu.darkcube.minigame.woolbattle.api.event.perk;

import eu.darkcube.minigame.woolbattle.api.event.game.GameEvent;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.perk.PerkRegistry;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public record RegisterPerksEvent(@NotNull Game game, @NotNull PerkRegistry registry) implements GameEvent {
}
