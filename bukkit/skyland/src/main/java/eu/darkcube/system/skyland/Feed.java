/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland;

import eu.darkcube.system.skyland.equipment.Material;
import eu.darkcube.system.skyland.inventoryui.*;
import eu.darkcube.system.skyland.mobs.CustomZombie;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import util.SebUtil;
import eu.darkcube.system.skyland.equipment.*;
import eu.darkcube.system.skyland.skylandclasssystem.SkylandPlayer;
import eu.darkcube.system.skyland.skylandclasssystem.SkylandPlayerClass;
import eu.darkcube.system.skyland.skylandclasssystem.SkylandPlayerModifier;

import eu.darkcube.system.skyland.skillsandability.BeamOfLight;
import eu.darkcube.system.skyland.worldgen.SkylandBiome;
import eu.darkcube.system.skyland.worldgen.structures.CustomPallette;
import eu.darkcube.system.skyland.worldgen.structures.SkylandStructure;
import eu.darkcube.system.skyland.worldgen.structures.SkylandStructureModifiers;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.Command;
import org.bukkit.structure.Palette;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Feed implements CommandExecutor {

    @Override @SuppressWarnings("deprecation") public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        //todo test cmd
        if (args.length > 0) {
            Player p = (Player) sender;
            if (Objects.equals(args[0], "0")) {
                UINewClassSelect cs = new UINewClassSelect(p);
                cs.openInv();
            } else if (args[0].equals("1")) {
                SelectActiveClass cs = new SelectActiveClass(p);
                cs.openInv();
            } else if (args[0].equals("data")) {
                p.sendMessage(SkylandPlayerModifier.getSkylandPlayer(p).toString());
            } else if (args[0].equals("clear")) {
                SkylandPlayerModifier.getSkylandPlayer(p).resetData();
            } else if (args[0].equals("gui")) {
                if (args.length > 3) {
                    p.showTitle(Title.title(net.kyori.adventure.text.Component
                            .text(args[2]).color(TextColor.color(0x4e, 0x5c, 0x24)), net.kyori.adventure.text.Component
                            .text(args[3])
                            .color(TextColor.color(0x4e, 0x5c, 0x24)), Times.times(Duration.of(0, ChronoUnit.SECONDS), Duration.of(Long.parseLong(args[1]), ChronoUnit.SECONDS), Duration.of(0, ChronoUnit.SECONDS))));
                    //p.sendTitle(args[1], "", 0, 100000, 0);
                } else {
                    if (args.length > 2) {
                        p.showTitle(Title.title(net.kyori.adventure.text.Component
                                .text(args[2])
                                .color(TextColor.color(0x4e, 0x5c, 0x24)), net.kyori.adventure.text.Component.text(""), Times.times(Duration.of(0, ChronoUnit.SECONDS), Duration.of(Long.parseLong(args[1]), ChronoUnit.SECONDS), Duration.of(0, ChronoUnit.SECONDS))));
                    } else {
                        p.sendTitle("\uEff1", "", 0, 100000, 0);
                    }

                }

            } else if (args[0].equals("weapon")) {
                Weapon weapon = Weapon.createEquipment(1000, new ItemStack(org.bukkit.Material.STRING), Rarity.RARE, 0, new ArrayList<>(List.of(new Component[]{new Component(Material.DRAGON_SCALE, ComponentType.AXE), new Component(Material.TESTING_IRON, ComponentType.AXE)})), EquipmentType.AXE, Ability.TEST);
                p.getInventory().setItemInMainHand(weapon.getModel());
            } else if (args[0].equals("armor")) {
                Equipment eq = Equipment.createEquipment(1000, new ItemStack(org.bukkit.Material.LEATHER_CHESTPLATE), Rarity.RARE, new ArrayList<>(List.of(new Component[]{new Component(Material.DRAGON_SCALE, ComponentType.AXE)})), EquipmentType.AXE);
                p.getInventory().setItemInMainHand(eq.getModel());
            } else if (args[0].equals("lvl")) {
                if (args.length > 1) {
                    SkylandPlayer skp = SkylandPlayerModifier.getSkylandPlayer(p);
                    List<SkylandPlayerClass> skpc = skp.getSkylandPlayerClasses();
                    skpc.get(skp.getActiveClassID()).setLvl(Integer.parseInt(args[1]));
                    skp.setSkylandPlayerClasses(skpc);
                }
            } else if (args[0].equals("mob")) {
                if (args.length > 1){
                    for (int i = 0; i < Integer.parseInt(args[1]); i++) {
                        CustomZombie customZombie = new CustomZombie(p.getLocation());
                    }
                }else {
                    CustomZombie customZombie = new CustomZombie(p.getLocation());
                }

            } else if (args[0].equals("copy")) {

                if (args.length > 1) {
                    NamespacedKey nsk = new NamespacedKey(Skyland.getInstance(), args[1]);
                    StructureManager structureManager = Skyland.getInstance().getServer().getStructureManager();
                    Structure structure = structureManager.createStructure();

                    structure.fill(((Player) sender).getLocation(), new BlockVector(32, 32, 32), false);

                    SkylandStructure skylandStructure = new SkylandStructure(nsk, new SkylandStructureModifiers[]{new SkylandStructureModifiers(SkylandBiome.TEST2, 10, 2, 3, true, new Vector(0, 0, 0))});

                    structure.getPalettes().remove(0);
                    System.out.println("Paletts left after removal (1 if copy, 0 if raw): " + structure.getPalettes().size());
                    structure.getPalettes().add(new CustomPallette());

                    structureManager.registerStructure(nsk, structure);

                    structureManager.saveStructure(nsk);
                    Skyland.getInstance().getStructures().add(skylandStructure);

                    p.sendMessage("structure saved");
                }

            } else if (args[0].equals("out")) {
                if (args.length > 1) {
                    NamespacedKey nsk = new NamespacedKey(Skyland.getInstance(), args[1]);

                    StructureManager structureManager = Skyland.getInstance().getServer().getStructureManager();
                    Structure structure = structureManager.createStructure();

                    for (Palette palette : structure.getPalettes()) {
                        p.sendMessage(palette.getBlocks().get(0).getType() + ": " + palette.getBlockCount());
                        for (BlockState block : palette.getBlocks()) {
                            p.sendMessage("mat: " + block.getType());
                        }
                    }

                    if (structure.getPalettes().isEmpty()) {
                        p.sendMessage("palettes is empty");
                    }
                }
            } else if (args[0].equals("paste")) {
                if (args.length > 1) {
                    NamespacedKey namespacedKey = new NamespacedKey(Skyland.getInstance(), args[1]);
                    SkylandStructure skylandStructure = Skyland.getInstance().getStructure(namespacedKey);
                    skylandStructure.enqueuStructures(p.getLocation());
					/*
					StructureManager structureManager =
							Skyland.getInstance().getServer().getStructureManager();
					int palette = 0;
					if (args.length >2){
						palette = Integer.parseInt(args[2]);
					}

					structureManager.getStructure(namespacedKey)
							.place(((Player) sender).getLocation(), true, StructureRotation.NONE,
									Mirror.NONE, palette, 1, new Random());

					 */
                    p.sendMessage("structure pasted");

                }

            } else if (args[0].equals("air")) {
                if (args.length > 1) {
                    NamespacedKey namespacedKey = new NamespacedKey(Skyland.getInstance(), args[1]);
                    StructureManager structureManager = Skyland.getInstance().getServer().getStructureManager();

                    Structure structure = structureManager.getStructure(namespacedKey);

                    for (BlockState state : structure.getPalettes().get(0).getBlocks()) {
                        p.sendMessage("state found");
                        if (state.getType().equals(org.bukkit.Material.AIR)) {
                            p.sendMessage("state removed");
                            structure.getPalettes().get(0).getBlocks().remove(state);
                        }
                    }

                    structureManager.saveStructure(namespacedKey);

                }
            } else if (args[0].equals("load")) {
                StructureManager structureManager = Skyland.getInstance().getServer().getStructureManager();
                structureManager.loadStructure(new NamespacedKey(Skyland.getInstance(), "test"));
                p.sendMessage("struc loaded");

            } else if (args[0].equals("delete")) {
                if (args.length > 1) {
                    NamespacedKey namespacedKey = new NamespacedKey(Skyland.getInstance(), args[1]);
                    for (SkylandStructure skylandStructure : Skyland.getInstance().getStructures()) {
                        if (skylandStructure.getNamespacedKey().equals(namespacedKey)) {
                            Skyland.getInstance().getStructures().remove(skylandStructure);
                            p.sendMessage("succesfully removed from list");
                            return true;
                        }
                    }
                    p.sendMessage("no structure found matching that name");

                }
            } else if (args[0].equals("craft")) {

                UICrafting crafting = new UICrafting("Crafting", p);
                crafting.openInv();

            } else if (args[0].equals("pic")) {
                p.sendMessage("display pic");
                if (args.length == 3) {
                    p.sendMessage("Color: " + SkylandBiome.getColor(Integer.parseInt(args[1]), Integer.parseInt(args[2])));
                    p.sendMessage("Intensity: " + SkylandBiome.getBiomeIntensity(Integer.parseInt(args[1]), Integer.parseInt(args[2])));
                    return true;
                }
                try {
                    System.out.println("test");
                    int[][] colors = SebUtil.convertTo3DWithoutUsingGetRGB(ImageIO.read(new File("biomes.png")));
                    for (int[] pixels : colors) {
                        String out = "";
                        for (int pixel : pixels) {
                            out += pixel + ", ";
                        }
                        System.out.println(out);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (args[0].equals("gm")) {
                if (args.length == 1) {
                    p.sendMessage("nope.");
                } else {
                    if (args[1].equals("0")) {
                        p.setGameMode(GameMode.SURVIVAL);
                    } else if (args[1].equals("1")) {
                        p.setGameMode(GameMode.CREATIVE);
                    } else if (args[1].equals("3")) {
                        p.setGameMode(GameMode.SPECTATOR);
                    } else {
                        p.sendMessage("nein.");
                    }
                }
            } else if (args[0].equals("ascend")) {
                p.teleport(new Location(Bukkit.getWorld("test"), 0, 200, 0));
            } else if (args[0].equals("memory")) {
                System.out.println(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() + "/" + Runtime
                        .getRuntime()
                        .totalMemory());
                sender.sendMessage(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() + "/" + Runtime
                        .getRuntime()
                        .totalMemory());

            } else if (args[0].equals("skill")) {
                p.sendMessage("beam!");
                new BeamOfLight().activate(p);

            }else if (args[0].equals("sample")){
                SimplexOctaveGenerator biomeBorderGen = new SimplexOctaveGenerator(new Random(1123214124), 8);
                int xNoise = (int) (biomeBorderGen.noise(p.getLocation().getBlockX(), p.getLocation().getBlockZ(),1, 1, 1, true) * 10000);
                int zNoise = (int) (biomeBorderGen.noise(p.getLocation().getBlockX(), p.getLocation().getBlockZ(),2, 1, 1, true)  * 10000);
                p.sendMessage("xnoise is noice: " + xNoise);
                p.sendMessage("znoise is noice: " + zNoise);
                p.sendMessage("xmoduliert: " + xNoise%(3*2+1));
                p.sendMessage("zmoduliert: " + zNoise%(3*2+1));
            }

        } else {
            InventoryUI inventoryUI = new InventoryUI(6, "\uEff1", (Player) sender);
            inventoryUI.setInvSlot(new UIitemStack(true, new ItemStack(org.bukkit.Material.DIAMOND_SWORD)), 0, 5);
            inventoryUI.openInv();
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return false;
        }

        /*
        if(!command.getName().equalsIgnoreCase("feed") || (args.length > 1)) {

            sender.sendMessage("§7Unbekannter Befehl. Nutze §b/feed (Person) 1§7, um dich oder
            andere zu füttern");
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

        sender.sendMessage("§7Unbekannter Befehl. Nutze §b/feed (Person) 1§7, um dich oder andere
         zu füttern");
        return false;

             */
        return true;
    }

}
