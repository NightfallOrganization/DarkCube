package eu.darkcube.minigame.woolbattle.listener.ingame;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerGameModeChange extends Listener<PlayerGameModeChangeEvent> {

	@Override
	@EventHandler
	public void handle(PlayerGameModeChangeEvent e) {
		new Scheduler(() -> WoolBattle.getInstance().getIngame().listenerDoubleJump.refresh(e.getPlayer())).runTask();
	}
}