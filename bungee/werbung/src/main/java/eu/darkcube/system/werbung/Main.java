/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.werbung;

import java.util.*;
import java.util.concurrent.*;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.ComponentBuilder.*;
import net.md_5.bungee.api.chat.hover.content.*;
import net.md_5.bungee.api.plugin.*;

public class Main extends Plugin {

	private String prefix = "&8⋆&7☆&5✕&7--&8--&d--&5-- &7[&dWerbung&7] &5--&d--&8--&7--&5✕&7☆&8⋆";
	private String suffix = "&8⋆&7☆&5✕&7--&8--&d----&5---------&d----&8--&7--&5✕&7☆&8⋆";
	private BaseComponent[][] messages = new BaseComponent[4][];

	@Override
	public void onLoad() {
		ComponentBuilder b = new ComponentBuilder().append("Hey! Schaut doch bei unserem \nPartner ")
				.color(ChatColor.GRAY);
		ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, "https://terminal-hosting.de");
		HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new Text(new ComponentBuilder().append("§aKlick mich!").create()));
		Arrays.asList(TextComponent.fromLegacyText("§dTerminal§7-§dHosting")).stream().map(c -> {
			c.setClickEvent(click);
			c.setHoverEvent(hover);
			return c;
		}).forEach(c -> b.append(c, FormatRetention.FORMATTING));
		b.append(" vorbei.", FormatRetention.FORMATTING).color(ChatColor.GRAY);

		messages[0] = b.create();
		ComponentBuilder b2 = new ComponentBuilder().append("Hey! Schaut doch bei unserem \nTeamSpeak ")
				.color(ChatColor.GRAY);
		Arrays.asList(TextComponent.fromLegacyText("§dDarkCube§7.§deu")).stream()
				.forEach(c -> b2.append(c, FormatRetention.FORMATTING));
		b2.append(" vorbei.", FormatRetention.FORMATTING).color(ChatColor.GRAY);
		messages[1] = b2.create();

		ComponentBuilder b3 = new ComponentBuilder().append("Hey! Schaut doch bei unserem \nDarkCube ")
				.color(ChatColor.GRAY);
		ClickEvent click2 = new ClickEvent(ClickEvent.Action.OPEN_URL, "http://discord.darkcube.eu");
		HoverEvent hover2 = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new Text(new ComponentBuilder().append("§aKlick mich!").create()));
		Arrays.asList(TextComponent.fromLegacyText("§dDiscord")).stream().map(c -> {
			c.setClickEvent(click2);
			c.setHoverEvent(hover2);
			return c;
		}).forEach(c -> b3.append(c, FormatRetention.FORMATTING));
		b3.append(" vorbei.", FormatRetention.FORMATTING).color(ChatColor.GRAY);
		messages[2] = b3.create();

		ComponentBuilder b4 = new ComponentBuilder().append("Hey! Schaut doch bei unserer \nWebsite ")
				.color(ChatColor.GRAY);
		ClickEvent click3 = new ClickEvent(ClickEvent.Action.OPEN_URL, "https://darkcube.eu");
		HoverEvent hover3 = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new Text(new ComponentBuilder().append("§aKlick mich!").create()));
		Arrays.asList(TextComponent.fromLegacyText("§dDarkCube§7.§deu")).stream().map(c -> {
			c.setClickEvent(click3);
			c.setHoverEvent(hover3);
			return c;
		}).forEach(c -> b4.append(c, FormatRetention.FORMATTING));
		b4.append(" vorbei.", FormatRetention.FORMATTING).color(ChatColor.GRAY);

		messages[3] = b4.create();
//		https://discord.gg/r32UYFVuTA
	}

	@Override
	public void onEnable() {
		Runnable r = new Runnable() {
			int id = -1;

			@Override
			public void run() {
				id = (id + 1) % messages.length;

//				String idmsg = ChatColor.translateAlternateColorCodes('&', messages[id]);

//				String msg = prefix suffix;
//				getProxy().broadcast(new TextComponent(msg));
				ComponentBuilder b = new ComponentBuilder();
				b.append("\n");
				b.append(ChatColor.translateAlternateColorCodes('&', prefix));
				b.append("\n");
				b.append(messages[id], FormatRetention.FORMATTING);
				b.append("\n");
				b.append(ChatColor.translateAlternateColorCodes('&', suffix));
				b.append("\n");

				getProxy().broadcast(b.create());
//				getProxy().broadcast();

			}
		};
		getProxy().getScheduler().schedule(Main.this, r, 5, 5, TimeUnit.MINUTES);

	}
}
