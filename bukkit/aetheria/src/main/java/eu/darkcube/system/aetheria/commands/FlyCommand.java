package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.Aetheria;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class FlyCommand implements CommandExecutor, Listener {

    private final NamespacedKey flyKey;

    public FlyCommand() {
        this.flyKey = new NamespacedKey(Aetheria.getInstance(), "flyStatus");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PersistentDataContainer data = player.getPersistentDataContainer();

        if (data.has(flyKey, PersistentDataType.BYTE) && data.get(flyKey, PersistentDataType.BYTE) == 1) {
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (player.getAllowFlight()) {
            Bukkit.getScheduler().runTaskLater(Aetheria.getInstance(), () -> player.setAllowFlight(true), 1L);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PersistentDataContainer data = player.getPersistentDataContainer();

        if (data.has(flyKey, PersistentDataType.BYTE) && data.get(flyKey, PersistentDataType.BYTE) == 1) {
            Bukkit.getScheduler().runTaskLater(Aetheria.getInstance(), () -> player.setAllowFlight(true), 1L);
        }
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        PersistentDataContainer data = player.getPersistentDataContainer();

        if (data.has(flyKey, PersistentDataType.BYTE) && data.get(flyKey, PersistentDataType.BYTE) == 1) {
            player.setAllowFlight(true);
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return false;
        }

        Player player = (args.length == 1) ? Bukkit.getPlayer(args[0]) : (Player) sender;
        if (player == null) {
            sender.sendMessage("Spieler nicht gefunden!");
            return true;
        }

        PersistentDataContainer data = player.getPersistentDataContainer();
        if (!player.getAllowFlight()) {
            player.setAllowFlight(true);
            data.set(flyKey, PersistentDataType.BYTE, (byte) 1);
            sender.sendMessage("§a" + player.getName() + "§7 wurde in den Flugmodus gesetzt");
        } else {
            player.setAllowFlight(false);
            data.remove(flyKey);
            sender.sendMessage("§a" + player.getName() + "§7 wurde aus dem Flugmodus gesetzt");
        }

        return true;
    }
}
