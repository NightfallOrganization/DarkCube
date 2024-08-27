/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.lobby;

import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;

public enum LobbySidebarTeam {

    ONLINE("online") {
        @Override
        public Component createContent(CommonGame game, CommonWBUser user) {
            return create(user, game.users().size());
        }
    },
    NEEDED("needed") {
        @Override
        public Component createContent(CommonGame game, CommonWBUser user) {
            return create(user, game.phase() instanceof CommonLobby lobby ? lobby.minPlayerCount : -1);
        }
    },

    MAP("map") {
        @Override
        public Component createContent(CommonGame game, CommonWBUser user) {
            return create(user, game.map().name());
        }
    },

    TIME("time") {
        @Override
        public Component createContent(CommonGame game, CommonWBUser user) {
            return create(user, game.phase() instanceof CommonLobby lobby ? lobby.timer().timeRemaining().toSeconds() : -1);
        }
    },

    LIFES("lifes") {
        @Override
        public Component createContent(CommonGame game, CommonWBUser user) {
            return create(user, game.phase() instanceof CommonLobby lobby ? lobby.lifes() : -1);
        }
    },

    EP_GLITCH("ep_glitch") {
        @Override
        public Component createContent(CommonGame game, CommonWBUser user) {
            var result = game.phase() instanceof CommonLobby lobby ? lobby.epGlitchPoll.sortedWinners().stream().findFirst().orElse(null) : null;
            return create(user, result == null ? "ERROR" : result ? Messages.EP_GLITCH_ON : Messages.EP_GLITCH_OFF);
        }
    },
    ;
    private final String id;

    LobbySidebarTeam(String id) {
        this.id = id;
    }

    public abstract Component createContent(CommonGame game, CommonWBUser user);

    public String id() {
        return id;
    }

    public Component create(CommonWBUser user, Object content) {
        return Messages.getMessage("SCOREBOARD_TEAM_LOBBY_" + name(), user.language(), content);
    }
}
