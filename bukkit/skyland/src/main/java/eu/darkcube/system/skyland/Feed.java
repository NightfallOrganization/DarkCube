/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland;

import eu.darkcube.system.skyland.Listener.SkylandListener;
import eu.darkcube.system.skyland.inventoryUI.UINewClassSelect;
import eu.darkcube.system.skyland.inventoryUI.InventoryUI;
import eu.darkcube.system.skyland.inventoryUI.UIitemStack;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.Command;

import java.util.Objects;

public class Feed implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


            //todo test cmd
        if (args.length > 0){
            Player p = (Player) sender;
            if (Objects.equals(args[0], "0")){
                UINewClassSelect cs = new UINewClassSelect(p);
                cs.openInv();
            }else if(args[0].equals("data")){
                p.sendMessage(Skyland.getInstance().getSkylandPlayers(p).toString());
            }else if(args[0].equals("clear")){
                p.getPersistentDataContainer().remove(new NamespacedKey(Skyland.getInstance(), "SkylandPlayer"));
            }
        }else {
            InventoryUI inventoryUI = new InventoryUI(6, "\uEff1", (Player) sender);
            inventoryUI.setInvSlot(new UIitemStack(true, new ItemStack(Material.DIAMOND_SWORD)), 0, 5);
            inventoryUI.openInv();
        }






        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return false;
        }

        /*
        if(!command.getName().equalsIgnoreCase("feed") || (args.length > 1)) {

            sender.sendMessage("§7Unbekannter Befehl. Nutze §b/feed (Person) 1§7, um dich oder andere zu füttern");
            return false;

        }


        if ((args.length == 1) && (Bukkit.getPlayer(args[0])!=null)) {
            Player player = Bukkit.getPlayer(args[0]);

            player.setSaturation(20);
            player.setFoodLevel(20);
            sender.sendMessage("§b"+ player.getName() +"§7 wurde gefüttert");
            return true;
        }
        else if(args.length == 0) {
            Player player = (Player) sender;

            player.setSaturation(20);
            player.setFoodLevel(20);
            sender.sendMessage("§7Du wurdest §bgefüttert");
            return true;

        }

        sender.sendMessage("§7Unbekannter Befehl. Nutze §b/feed (Person) 1§7, um dich oder andere zu füttern");
        return false;

             */
        return true;
    }


}
