/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.darkcube.system.ReflectionUtils.PackageType;

public class ChatBaseComponent {

	private static final Class<?> CL_ICHATBASECOMPONENT = ReflectionUtils.getClass("IChatBaseComponent",
			PackageType.MINECRAFT_SERVER);

	private static final Class<?> CL_PACKETPLAYOUTCHAT = ReflectionUtils.getClass("PacketPlayOutChat",
			PackageType.MINECRAFT_SERVER);

//	private static final Constructor<?> C_PACKETPLAYOUTCHAT = getConstructor(CL_PACKETPLAYOUTCHAT,
//			CL_ICHATBASECOMPONENT, byte.class);
	private static final Constructor<?> C_PACKETPLAYOUTCHAT = ReflectionUtils.getConstructor(
			ChatBaseComponent.CL_PACKETPLAYOUTCHAT, ChatBaseComponent.CL_ICHATBASECOMPONENT, byte.class);

	private Object component;

	private Consumer<Object> consumer = con -> {
//		con.sendPacket(new PacketPlayOutChat(component, (byte) 0));
		Object packet = ReflectionUtils.instantiateObject(ChatBaseComponent.C_PACKETPLAYOUTCHAT, this.component,
				(byte) 0);
		Plugin.sendPacket(con, packet);
//		Plugin.sendPacket(con, newInstance(ChatBaseComponent.C_PACKETPLAYOUTCHAT, this.component, (byte) 0));
	};

	ChatBaseComponent(Object component) {
		this.component = component;
	}

	ChatBaseComponent(Object component, Consumer<Object> cons) {
		this.component = component;
		this.consumer = cons;
	}

	public Object getComponent() {
		return this.component;
	}

	public void setComponent(Object component) {
		this.component = component;
	}

	public void sendServer() {
		this.sendPlayer(Bukkit.getOnlinePlayers().toArray(new Player[] {}));
	}

	public void sendPlayer(Player... players) {
		Arrays.asList(players).stream().map(p -> Plugin.playerConnection(p)).forEach(this.consumer::accept);
//		Packet<?> packet = new PacketPlayOutChat(component);
//		for (Player player : players) {
//			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
//		}
	}

}
