package eu.darkcube.system;

import static eu.darkcube.system.Reflection.*;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatBaseComponent {

	private static final Class<?> CL_ICHATBASECOMPONENT = getVersionClass(MINECRAFT_PREFIX, "IChatBaseComponent");
	private static final Class<?> CL_PACKETPLAYOUTCHAT = getVersionClass(MINECRAFT_PREFIX, "PacketPlayOutChat");
	private static final Constructor<?> C_PACKETPLAYOUTCHAT = getConstructor(CL_PACKETPLAYOUTCHAT,
			CL_ICHATBASECOMPONENT, byte.class);

	private Object component;
	private Consumer<Object> consumer = con -> {
//		con.sendPacket(new PacketPlayOutChat(component, (byte) 0));
		Plugin.sendPacket(con, newInstance(C_PACKETPLAYOUTCHAT, component, (byte) 0));
	};

	ChatBaseComponent(Object component) {
		this.component = component;
	}

	ChatBaseComponent(Object component, Consumer<Object> cons) {
		this.component = component;
		this.consumer = cons;
	}

	public Object getComponent() {
		return component;
	}

	public void setComponent(Object component) {
		this.component = component;
	}

	public void sendServer() {
		sendPlayer(Bukkit.getOnlinePlayers().toArray(new Player[] {}));
	}

	public void sendPlayer(Player... players) {
		Arrays.asList(players).stream().map(p -> Plugin.playerConnection(p)).forEach(consumer::accept);
//		Packet<?> packet = new PacketPlayOutChat(component);
//		for (Player player : players) {
//			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
//		}
	}
}
