package eu.darkcube.system.miners.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.player.Message;

public class ListenerPlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        handlePlayerLeave(e.getPlayer());
    }


    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        e.setLeaveMessage(null);
        handlePlayerLeave(e.getPlayer());
    }

    private void handlePlayerLeave(Player player) {
        if (!ListenerSpectators.isSpectatorPlayer(player) && Miners.getGamephase() != 3) // don't send leave messages for spectators or in endphase
            Miners.sendTranslatedMessageAll(Message.PLAYER_LEFT, player.getName());

        Miners.getPlayerManager().removePlayer(player);
        updateLobbyTimer(); // automatic checking in the timer occurs only once per second
    }

    private void updateLobbyTimer() {
        if (Miners.getGamephase() == 0 && Miners.getLobbyPhase().getTimer().isRunning()) {
            if (Bukkit.getOnlinePlayers().size() == 2)
                Miners.getLobbyPhase().getTimer().cancel(false);
        }
    }

}
