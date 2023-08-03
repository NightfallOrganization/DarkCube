package eu.darkcube.system.citybuild.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBarTask extends BukkitRunnable {

	private final TextComponent survivalMessage;
	private final TextComponent creativeMessage;

	public ActionBarTask(String survivalMessageText, String creativeMessageText) {
		// Erstellen Sie TextComponent-Objekte für die Nachrichten und setzen Sie die Farbe
		this.survivalMessage = new TextComponent(survivalMessageText);
		this.survivalMessage.setColor(ChatColor.of("#4e5c24"));

		this.creativeMessage = new TextComponent(creativeMessageText);
		this.creativeMessage.setColor(ChatColor.of("#4e5c24"));
	}

	@Override
	public void run() {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			TextComponent message;
			// Überprüfen Sie den Spielmodus des Spielers und setzen Sie die Nachricht entsprechend
			if (player.getGameMode() == GameMode.CREATIVE) {
				message = creativeMessage;
			} else {
				message = survivalMessage;
			}

			// Senden Sie die Nachricht in der Action Bar an den Spieler
			//player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
			player.sendActionBar(Component.text(""));
		}
	}
}
