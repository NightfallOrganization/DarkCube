/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class Fly implements CommandExecutor, Listener {

    private final File flyStatusFile;
    private final Map<String, Boolean> flyStatusMap;

    public Fly() {
        this.flyStatusFile = new File("flystatus.txt");
        this.flyStatusMap = new HashMap<>();
        loadFlyStatus();
    }

    // Methode zum Speichern des Flugstatus
    private void saveFlyStatus() {
        try {
            Files.write(flyStatusFile.toPath(), ()-> flyStatusMap.entrySet().stream()
                    .<CharSequence>map(e -> e.getKey() + " " + e.getValue())
                    .iterator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Methode zum Laden des Flugstatus
    private void loadFlyStatus() {
        if (flyStatusFile.exists()) {
            try {
                Files.lines(flyStatusFile.toPath())
                        .map(line -> line.split(" "))
                        .forEach(parts -> flyStatusMap.put(parts[0], Boolean.parseBoolean(parts[1])));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Methode zum Anwenden des gespeicherten Flugstatus, wenn ein Spieler sich einloggt
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Boolean flyStatus = flyStatusMap.get(player.getName());
        if (flyStatus != null) {
            player.setAllowFlight(flyStatus);
        }
    }

    // Methode zum Beibehalten des Flugstatus, wenn der Spielmodus geändert wird
    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (player.getAllowFlight()) {
            Bukkit.getScheduler().runTaskLater(Citybuild.getInstance(), () -> player.setAllowFlight(true), 1L);
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return false;
        }

        if(!command.getName().equalsIgnoreCase("fly") || (args.length > 1)) {
            sender.sendMessage("§7Unbekannter Befehl. Nutze §a/fly (Person) §7um dich oder andere in den Flugmodus zu setzten");
            return false;
        }

        if ((args.length == 1) && (Bukkit.getPlayer(args[0])!=null)) {
            Player player = Bukkit.getPlayer(args[0]);

            if (!player.getAllowFlight()) {
                player.setAllowFlight(true);
                flyStatusMap.put(player.getName(), true);
                saveFlyStatus();
                sender.sendMessage("§a"+ player.getName() +"§7 wurde in den Flugmodus gesetzt");
                return true;
            } else {
                player.setAllowFlight(false);
                flyStatusMap.put(player.getName(), false);
                saveFlyStatus();
                sender.sendMessage("§a"+ player.getName() +"§7 wurde aus den Flugmodus gesetzt");
                return true;
            }
        } else if(args.length == 0) {
            Player player = (Player) sender;

            if (!player.getAllowFlight()) {
                player.setAllowFlight(true);
                flyStatusMap.put(player.getName(), true);
                saveFlyStatus();
                sender.sendMessage("§7Flugmodus §aAN");
                return false;
            } else {
                player.setAllowFlight(false);
                flyStatusMap.put(player.getName(), false);
                saveFlyStatus();
                sender.sendMessage("§7Flugmodus §aAUS");
                return true;
            }
        }

        sender.sendMessage("§7Unbekannter Befehl. Nutze §a/fly (Person) §7um dich oder andere in den Flugmodus zu setzten");
        return false;
    }
}
