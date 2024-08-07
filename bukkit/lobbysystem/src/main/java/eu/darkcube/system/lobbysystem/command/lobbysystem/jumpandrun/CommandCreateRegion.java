/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.jumpandrun;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommand;
import eu.darkcube.system.lobbysystem.jumpandrun.JaRRegion;
import eu.darkcube.system.lobbysystem.listener.BaseListener;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class CommandCreateRegion extends LobbyCommand {

	public CommandCreateRegion() {
		super("createRegion", b -> b.executes(ctx -> {
			if (ctx.getSource().asPlayer().hasMetadata("creatingRegion")) {
				ctx.getSource().sendMessage(Component.text("Du erstellst bereits eine Region!")
						.color(NamedTextColor.RED));
				return 0;
			}
			ctx.getSource().asPlayer().sendMessage(
					"§aRegion erstellung angefangen! Wähle die Region durch das zerstören von Blöcken aus!");
			ctx.getSource().asPlayer().setMetadata("creatingRegion",
					new FixedMetadataValue(Lobby.getInstance(), new CData(ctx.getSource())));

			return 0;
		}));
	}

	public static class CListener extends BaseListener {
		private CommandSource source;
		private CData data;

		public CListener(CommandSource source) {
			this.source = source;
		}

		@EventHandler
		public void handle(BlockBreakEvent event) {
			try {
				if (event.getPlayer().equals(source.asPlayer())) {
					if (data.loc1 == null) {
						data.loc1 = event.getBlock();
						source.sendMessage(Component.text("Wähle nun die zweite Ecke aus!")
								.color(NamedTextColor.GOLD));
					} else if (data.loc2 == null) {
						data.loc2 = event.getBlock();
						data.stopCreating();
					}
				}
			} catch (CommandSyntaxException ex) {
				ex.printStackTrace();
			}
		}

		@EventHandler
		public void handle(PlayerQuitEvent event) {
			try {
				if (event.getPlayer().equals(source.asPlayer())) {
					data.stopCreating();
				}
			} catch (CommandSyntaxException ex) {
				ex.printStackTrace();
			}
		}
	}


	public static class CData {
		public CListener l;
		public Block loc1;
		public Block loc2;

		public CData(CommandSource s) {
			l = new CListener(s);
			l.data = this;
		}

		public void stopCreating() {
			l.unregister();
			if (loc1 != null && loc2 != null) {
				if (loc1.getWorld() != loc2.getWorld()) {
					l.source.sendMessage(
							Component.text("Die Blöcke sind nicht in der gleichen Welt!")
									.color(NamedTextColor.RED));
					return;
				}
				int distX = Math.abs(loc1.getX() - loc2.getX());
				int distZ = Math.abs(loc1.getZ() - loc2.getZ());
				int distY = Math.abs(loc1.getY() - loc2.getY());
				if (distX < 30 || distZ < 30 || distY < 15) {
					l.source.sendMessage(Component.text("Die Region muss größer als 30x15x30 sein")
							.color(NamedTextColor.RED));
					return;
				}
				int x = Math.min(loc1.getX(), loc2.getX());
				int y = Math.min(loc1.getY(), loc2.getY());
				int z = Math.min(loc1.getZ(), loc2.getZ());
				JaRRegion reg = new JaRRegion(loc1.getWorld(), x, y, z, distX, distY, distZ);
				Lobby.getInstance().getJaRManager().getRegions().add(reg);
				Lobby.getInstance().getJaRManager().saveRegions();
				l.source.sendMessage(
						Component.text("Region erstellt!").color(NamedTextColor.GREEN));
			}
		}
	}
}
