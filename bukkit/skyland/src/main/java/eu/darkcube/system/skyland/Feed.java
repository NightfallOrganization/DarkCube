/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland;

import eu.darkcube.system.skyland.Equipment.*;
import eu.darkcube.system.skyland.Listener.SkylandListener;
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandPlayer;
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandPlayerClass;
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandPlayerModifier;
import eu.darkcube.system.skyland.inventoryUI.SelectActiveClass;
import eu.darkcube.system.skyland.inventoryUI.UINewClassSelect;
import eu.darkcube.system.skyland.inventoryUI.InventoryUI;
import eu.darkcube.system.skyland.inventoryUI.UIitemStack;
import eu.darkcube.system.skyland.mobs.CustomZombie;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.Command;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Feed implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


            //todo test cmd
        if (args.length > 0){
            Player p = (Player) sender;
            if (Objects.equals(args[0], "0")){
                UINewClassSelect cs = new UINewClassSelect(p);
                cs.openInv();
            }else if(args[0].equals("1")){
                SelectActiveClass cs = new SelectActiveClass(p);
                cs.openInv();
            }else if(args[0].equals("data")){
                p.sendMessage(SkylandPlayerModifier.getSkylandPlayer(p).toString());
            }else if(args[0].equals("clear")){
                SkylandPlayerModifier.getSkylandPlayer(p).resetData();
            }else if(args[0].equals("gui")){
                if (args.length>2){
                    p.showTitle(Title.title(Component.text(args[2]).
                            color(TextColor.color(0x4e, 0x5c, 0x24)), Component.text(""), Times.times(
                            Duration.of(0, ChronoUnit.SECONDS), Duration.of(Long.parseLong(args[1]), ChronoUnit.SECONDS),Duration.of(0, ChronoUnit.SECONDS))));
                    //p.sendTitle(args[1], "", 0, 100000, 0);
                }else {
                    p.sendTitle("\uEff1", "", 0, 100000, 0);



                }

            }else if(args[0].equals("weapon")){
                Weapons weapons = Weapons.createEquipent(1000, new ItemStack(Material.STRING), Rarity.RARE, 0,
                        new ArrayList<>(List.of(new Components[] {
                                new Components(Materials.DRAGON_SCALE, ComponentTypes.AXE), new Components(Materials.TESTING_IRON, ComponentTypes.AXE)})), EquipmentType.AXE, Ability.TEST);
                p.getInventory().setItemInMainHand(weapons.getModel());
            }else if(args[0].equals("armor")){
                Equipments eq = Equipments.createEquipent(1000, new ItemStack(Material.LEATHER_CHESTPLATE), Rarity.RARE,
                        new ArrayList<>(List.of(new Components[] {
                                new Components(Materials.DRAGON_SCALE, ComponentTypes.AXE)})), EquipmentType.AXE);
                p.getInventory().setItemInMainHand(eq.getModel());
            }else if(args[0].equals("lvl")){
                if (args.length > 1){
                    SkylandPlayer skp = SkylandPlayerModifier.getSkylandPlayer(p);
                    List<SkylandPlayerClass> skpc = skp.getSkylandPlayerClasses();
                    skpc.get(skp.getActiveClassID()).setLvl(
                            Integer.parseInt(args[1]));
                    skp.setSkylandPlayerClasses(skpc);
                }
            }else if(args[0].equals("mob")){
                CustomZombie customZombie = new CustomZombie(p.getLocation());
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
