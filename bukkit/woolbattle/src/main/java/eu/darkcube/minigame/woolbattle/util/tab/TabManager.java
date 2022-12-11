/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.tab;

import java.lang.reflect.Field;

import eu.darkcube.minigame.woolbattle.user.User;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;

public class TabManager {

	public static void setHeaderFooter(User user, Header header, Footer footer) {
		PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
		try {
			Field f1 = packet.getClass().getDeclaredField("a");
			Field f2 = packet.getClass().getDeclaredField("b");
			f1.setAccessible(true);
			f2.setAccessible(true);
			f1.set(packet, new ChatMessage(header.getMessage()));
			f2.set(packet, new ChatMessage(footer.getMessage()));
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
			ex.printStackTrace();
		}
		user.sendPacket(packet);
	}
	
}
