/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.holograms;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import eu.darkcube.system.holograms.nms.Book;
import eu.darkcube.system.userapi.UserAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Holograms extends DarkCubePlugin implements Listener {

	private Collection<Player> editing = new ArrayList<>();

	@Override
	public void onEnable() {
		CommandAPI.getInstance().register(
				new CommandExecutor("holograms", "hologram", new String[0],
						b -> b.then(Commands.literal("create").executes(context -> {
							Player p = context.getSource().asPlayer();
							if (this.editing.contains(p)) {
								return 0;
							}
							UserAPI.getInstance().getUser(p).openBook(
									eu.darkcube.system.libs.net.kyori.adventure.inventory.Book.builder().);
							Book book = new Book();
							book.addPage(new CustomComponentBuilder("tasdasd").event(
											new HoverEvent(HoverEvent.Action.SHOW_TEXT,
													new CustomComponentBuilder("asddsad").create()))
									.event(new ClickEvent(Action.SUGGEST_COMMAND, "asdhasdhsad"))
									.create());
							p.setItemInHand(book.build());
							this.editing.add(p);
							return 0;
						})))); Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void handle(PlayerEditBookEvent event) {
		if (this.editing.contains(event.getPlayer())) {
			this.editing.remove(event.getPlayer());
			List<String> lines = event.getNewBookMeta().getPages();
			Hologram hologram = new Hologram();
			hologram.position(event.getPlayer().getEyeLocation());
			lines.forEach(hologram::addText);
			hologram.create();
		}
	}

}
