package eu.darkcube.system.miners.listener;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.player.Message;

public class ListenerPlayerJoin implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 0, false, false));
		Miners.getPlayerManager().addPlayer(e.getPlayer());
		Miners.getPlayerManager().getMinersPlayer(e.getPlayer()).updatePlayer();
		e.getPlayer().setGameMode(GameMode.SURVIVAL);
		e.setJoinMessage(null);
		Miners.sendTranslatedMessageAll(Message.PLAYER_JOINED, e.getPlayer().getCustomName());
	}

}
