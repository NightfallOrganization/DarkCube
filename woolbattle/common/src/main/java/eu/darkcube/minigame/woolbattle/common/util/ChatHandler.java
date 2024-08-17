/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util;

import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.empty;
import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.text;
import static eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static eu.darkcube.system.libs.net.kyori.adventure.text.format.Style.style;

import java.util.ArrayList;
import java.util.Collection;

import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.commandapi.CommandExecutor;
import eu.darkcube.system.util.GameState;
import it.unimi.dsi.fastutil.Pair;

public class ChatHandler {
    public void chat(CommonWBUser user, String message, Channel defaultChannel) {
        var mapped = mapChannel(user, message, defaultChannel);
        var channel = mapped.first();
        var msg = mapped.second().trim();
        var game = user.game();
        if (game == null) {
            user.sendMessage(user.teamPlayerName().append(text(": ", WHITE)).append(text(message)));
            return;
        }
        broadcast(game, user, channel, msg);
    }

    private void broadcast(CommonGame game, CommonWBUser user, Channel channel, String message) {
        var targets = new ArrayList<CommandExecutor>(channel.targets(game, user));
        targets.add(game.woolbattle().woolbattle().consoleSender());
        var team = user.team();
        var nameColor = team == null ? WHITE : team.nameColor();
        var ignorePrefix = channel.ignorePrefix(game, user);
        var playerName = user.teamPlayerName();

        for (var target : targets) {
            var c = ignorePrefix ? empty() : channel.message.getMessage(target).applyFallbackStyle(style(nameColor));

            c = c.append(playerName).append(text(": ", WHITE));
            c = c.append(text(message, WHITE));
            target.sendMessage(c);
        }
    }

    private Pair<Channel, String> mapChannel(CommonWBUser user, String message, Channel defaultChannel) {
        if (!message.startsWith(Channel.PREFIX)) {
            return Pair.of(defaultChannel, message);
        }
        var team = user.team();
        if (team == null || !team.canPlay()) {
            return Pair.of(defaultChannel, message);
        }
        for (var channel : Channel.values()) {
            String bestNameMatch = null;
            for (var name : channel.names) {
                var prefix = Channel.PREFIX + name;
                if (!message.startsWith(prefix)) continue;
                if (bestNameMatch == null) bestNameMatch = name;
                else if (bestNameMatch.length() < name.length()) bestNameMatch = name;
            }
            if (bestNameMatch == null) continue;
            var msg = message.substring((Channel.PREFIX + bestNameMatch).length());
            if (msg.isBlank()) continue;
            return Pair.of(channel, msg);
        }
        return Pair.of(defaultChannel, message);
    }

    public void lobbyChat(CommonWBUser user, String message) {
        chat(user, message, Channel.ALL);
    }

    public void ingameChat(CommonWBUser user, String message) {
        var team = user.team();
        var channel = team != null && team.users().size() >= 2 ? Channel.TEAM : Channel.ALL;
        chat(user, message, channel);
    }

    public enum Channel {
        TEAM(Messages.AT_TEAM, "t", "team") {
            @Override
            public Collection<? extends CommandExecutor> targets(CommonGame game, CommonWBUser user) {
                var team = user.team();
                if (team == null) return ALL.targets(game, user);
                return team.users();
            }

            @Override
            public boolean ignorePrefix(CommonGame game, CommonWBUser user) {
                return false;
            }
        },
        ALL(Messages.AT_ALL, "a", "all") {
            @Override
            public Collection<? extends CommandExecutor> targets(CommonGame game, CommonWBUser user) {
                var team = user.team();
                if (team != null && team.spectator()) {
                    return team.users();
                }
                return game.users();
            }

            @Override
            public boolean ignorePrefix(CommonGame game, CommonWBUser user) {
                return game.gameState() != GameState.INGAME;
            }
        };

        private static final String PREFIX = "@";
        private final BaseMessage message;
        private final String[] names;

        Channel(BaseMessage message, String... names) {
            this.message = message;
            this.names = names;
        }

        public abstract Collection<? extends CommandExecutor> targets(CommonGame game, CommonWBUser user);

        public abstract boolean ignorePrefix(CommonGame game, CommonWBUser user);
    }
}
