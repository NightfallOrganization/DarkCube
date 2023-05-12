/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland;

import SkylandUtil.SebUtil;
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
import eu.darkcube.system.skyland.worldGen.SkylandBiomes;
import eu.darkcube.system.skyland.worldGen.Structures.CustomPallette;
import eu.darkcube.system.skyland.worldGen.Structures.SkylandStructure;
import eu.darkcube.system.skyland.worldGen.Structures.SkylandStructureModifiers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.Command;
import org.bukkit.structure.Palette;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.bukkit.util.BlockVector;
import org.checkerframework.common.reflection.qual.GetClass;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Feed implements CommandExecutor {

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

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
				if (args.length > 2) {
					p.showTitle(Title.title(
							Component.text(args[2]).color(TextColor.color(0x4e, 0x5c, 0x24)),
							Component.text(""), Times.times(Duration.of(0, ChronoUnit.SECONDS),
									Duration.of(Long.parseLong(args[1]), ChronoUnit.SECONDS),
									Duration.of(0, ChronoUnit.SECONDS))));
					//p.sendTitle(args[1], "", 0, 100000, 0);
				} else {
					p.sendTitle("\uEff1", "", 0, 100000, 0);

				}

			} else if (args[0].equals("weapon")) {
				Weapons weapons =
						Weapons.createEquipent(1000, new ItemStack(Material.STRING), Rarity.RARE
								, 0,
								new ArrayList<>(List.of(new Components[] {
										new Components(Materials.DRAGON_SCALE, ComponentTypes.AXE),
										new Components(Materials.TESTING_IRON,
												ComponentTypes.AXE)})), EquipmentType.AXE,
								Ability.TEST);
				p.getInventory().setItemInMainHand(weapons.getModel());
			} else if (args[0].equals("armor")) {
				Equipments eq =
						Equipments.createEquipent(1000, new ItemStack(Material.LEATHER_CHESTPLATE),
								Rarity.RARE, new ArrayList<>(List.of(new Components[] {
										new Components(Materials.DRAGON_SCALE,
												ComponentTypes.AXE)})), EquipmentType.AXE);
				p.getInventory().setItemInMainHand(eq.getModel());
			} else if (args[0].equals("lvl")) {
				if (args.length > 1) {
					SkylandPlayer skp = SkylandPlayerModifier.getSkylandPlayer(p);
					List<SkylandPlayerClass> skpc = skp.getSkylandPlayerClasses();
					skpc.get(skp.getActiveClassID()).setLvl(Integer.parseInt(args[1]));
					skp.setSkylandPlayerClasses(skpc);
				}
			} else if (args[0].equals("mob")) {
				CustomZombie customZombie = new CustomZombie(p.getLocation());
			} else if (args[0].equals("copy")) {

				if (args.length > 1) {
					NamespacedKey nsk = new NamespacedKey(Skyland.getInstance(), args[1]);
					StructureManager structureManager =
							Skyland.getInstance().getServer().getStructureManager();
					Structure structure = structureManager.createStructure();

					structure.fill(((Player) sender).getLocation(), new BlockVector(5, 5, 5),
							false);

					SkylandStructure skylandStructure = new SkylandStructure(nsk,
							new SkylandStructureModifiers[] {
									new SkylandStructureModifiers(SkylandBiomes.TEST2, 10, 2, 3,
											true)});

					Palette palette = new CustomPallette(structure.getPalettes().get(0).getBlocks());

					for (BlockState state : palette.getBlocks()) {
						p.sendMessage("state found");
						if (state.getType().equals(Material.AIR)) {
							p.sendMessage("state removed");
							state.setType(Material.STRUCTURE_VOID);
						}
					}

					//todo reflection / injection to fix fucking retard bukkit implementation
					Skyland.getInstance().getServer();
					structure.getPalettes().remove(0);
					System.out.println("Paletts left after removal (1 if copy, 0 if raw): " + structure.getPalettes().size());
					structure.getPalettes().add(new CustomPallette());


					structureManager.registerStructure(nsk, structure);


					structureManager.saveStructure(nsk);



					p.sendMessage("structure saved");
				}

			} else if (args[0].equals("out")) {
				if (args.length > 1) {
					NamespacedKey nsk = new NamespacedKey(Skyland.getInstance(), args[1]);

					StructureManager structureManager =
							Skyland.getInstance().getServer().getStructureManager();
					Structure structure = structureManager.createStructure();

					for (Palette palette : structure.getPalettes()) {
						p.sendMessage(palette.getBlocks().get(0).getType() + ": "
								+ palette.getBlockCount());
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
					NamespacedKey namespacedKey = new NamespacedKey(Skyland.getInstance(),
							args[1]);
					StructureManager structureManager =
							Skyland.getInstance().getServer().getStructureManager();
					int palette = 0;
					if (args.length >2){
						palette = Integer.parseInt(args[2]);
					}

					structureManager.getStructure(namespacedKey)
							.place(((Player) sender).getLocation(), true, StructureRotation.NONE,
									Mirror.NONE, palette, 1, new Random());
					p.sendMessage("structure pasted");

				}

			} else if (args[0].equals("air")) {
				if (args.length > 1) {
					NamespacedKey namespacedKey = new NamespacedKey(Skyland.getInstance(),
							args[1]);
					StructureManager structureManager =
							Skyland.getInstance().getServer().getStructureManager();

					Structure structure = structureManager.getStructure(namespacedKey);

					for (BlockState state : structure.getPalettes().get(0).getBlocks()) {
						p.sendMessage("state found");
						if (state.getType().equals(Material.AIR)) {
							p.sendMessage("state removed");
							structure.getPalettes().get(0).getBlocks().remove(state);
						}
					}

					structureManager.saveStructure(namespacedKey);

				}
			} else if (args[0].equals("load")) {
				StructureManager structureManager =
						Skyland.getInstance().getServer().getStructureManager();
				structureManager.loadStructure(new NamespacedKey(Skyland.getInstance(), "test"));
				p.sendMessage("struc loaded");

			} else if (args[0].equals("delete")) {
				if (args.length > 1) {
					NamespacedKey namespacedKey = new NamespacedKey(Skyland.getInstance(),
							args[1]);
					for (SkylandStructure skylandStructure : Skyland.getInstance()
							.getStructures()) {
						if (skylandStructure.getNamespacedKey().equals(namespacedKey)) {
							Skyland.getInstance().getStructures().remove(skylandStructure);
							p.sendMessage("succesfully removed from list");
							return true;
						}
					}
					p.sendMessage("no structure found matching that name");
				}

			} else if (args[0].equals("pic")) {
				p.sendMessage("display pic");
				if (args.length == 3) {
					p.sendMessage("Color: " + SkylandBiomes.getColor(Integer.parseInt(args[1]),
							Integer.parseInt(args[2])));
					p.sendMessage("Intensity: " + SkylandBiomes.getBiomeIntensity(
							Integer.parseInt(args[1]), Integer.parseInt(args[2])));
					return true;
				}
				try {
					System.out.println("test");
					int[][] colors = SebUtil.convertTo3DWithoutUsingGetRGB(
							ImageIO.read(new File("biomes.png")));
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
			}

		} else {
			InventoryUI inventoryUI = new InventoryUI(6, "\uEff1", (Player) sender);
			inventoryUI.setInvSlot(new UIitemStack(true, new ItemStack(Material.DIAMOND_SWORD)), 0,
					5);
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
