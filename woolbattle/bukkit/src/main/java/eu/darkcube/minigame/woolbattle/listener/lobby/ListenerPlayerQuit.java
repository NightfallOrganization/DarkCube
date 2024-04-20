/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.lobby;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.game.Lobby;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scoreboard.ScoreboardHelper;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class ListenerPlayerQuit extends Listener<PlayerQuitEvent> {
    private final WoolBattleBukkit woolBattle;

    public ListenerPlayerQuit(WoolBattleBukkit woolBattle) {
        this.woolBattle = woolBattle;
    }

    @Override
    @EventHandler
    public void handle(PlayerQuitEvent e) {
        WBUser user = WBUser.getUser(e.getPlayer());
        Lobby lobby = woolBattle.lobby();
        lobby.VOTES_MAP.remove(user);
        lobby.VOTES_EP_GLITCH.remove(user);
        lobby.VOTES_LIFES.remove(user);
        woolBattle.lobby().recalculateMap();
        woolBattle.lobby().recalculateEpGlitch();

        woolBattle.sendMessage(Message.PLAYER_LEFT, user.getTeamPlayerName());

        WBUser.onlineUsers().forEach(ScoreboardHelper::setOnline);
        e.setQuitMessage(null);
        woolBattle.lobby().checkUnload(Bukkit.getOnlinePlayers().size() - 1);
        user.setTeam(null);
    }
}
