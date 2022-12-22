/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util;

import eu.darkcube.system.version.VersionSupport;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatBaseComponent {

	private ChatUtils.ChatEntry[] entries;
	private Display display;
	private TitleType titleType;
	private int in, stay, out;

	ChatBaseComponent(ChatUtils.ChatEntry[] entries, Display display) {
		this.entries = entries;
		this.display = display;
	}

	ChatBaseComponent(ChatUtils.ChatEntry[] entries, Display display, TitleType titleType,
			int in, int stay, int out) {
		this.entries = entries;
		this.display = display;
		this.titleType = titleType;
		this.in = in;
		this.stay = stay;
		this.out = out;
	}

	public TitleType getTitleType() {
		return titleType;
	}

	public int getIn() {
		return in;
	}

	public int getStay() {
		return stay;
	}

	public int getOut() {
		return out;
	}

	public ChatUtils.ChatEntry[] getEntries() {
		return this.entries;
	}

	public Display getDisplay() {
		return display;
	}

	public void send(CommandSender... senders) {
		VersionSupport.getVersion().sendMessage(this, senders);
	}

	public enum Display {
		ACTIONBAR, CHAT, TITLE
	}


	public enum TitleType {
		TITLE, SUBTITLE, CLEAR
	}

}
