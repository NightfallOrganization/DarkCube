package eu.darkcube.system.citybuild.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AddXPCommand implements CommandExecutor {

    private LevelXPManager levelXPManager;

    public AddXPCommand(LevelXPManager levelXPManager) {
        this.levelXPManager = levelXPManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§7Falsche Anzahl von Argumenten! Verwendung: §a/addxp <Spielername> <XP>");
            return false;
        }

        String playerName = args[0];
        int xpToAdd;
        try {
            xpToAdd = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§7Ungültiger §aXP-Wert§7! Bitte geben Sie eine Zahl ein");
            return false;
        }

        Player targetPlayer = Bukkit.getServer().getPlayer(playerName);
        if (targetPlayer == null) {
            sender.sendMessage("§7Spieler nicht gefunden");
            return true;
        }
        double currentXP = levelXPManager.getXP(targetPlayer);
        levelXPManager.addXP(targetPlayer, xpToAdd);
        sender.sendMessage("§7Es wurden §a" + xpToAdd + " §7XP zum Spieler §a" + targetPlayer.getName() + " §7hinzugefügt");
        return true;

    }
}
