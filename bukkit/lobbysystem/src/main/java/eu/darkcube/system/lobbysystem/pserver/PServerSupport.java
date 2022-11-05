package eu.darkcube.system.lobbysystem.pserver;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryPServer;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.pserver.common.PServer;

public class PServerSupport {

	private static Boolean supported = null;

	public static boolean isSupported() {
		return supported == null ? false : supported.booleanValue();
	}

	static {
		try {
//			eu.darkcube.system.pserver.server.PServer.class.getClass();
			Class.forName(PServer.class.getName());
			supported = true;
			Register.register();
		} catch (Throwable ex) {
			supported = false;
		}
	}

	public static void init() {

	}

	private static class Register {
		private static void register() {
			eu.darkcube.system.pserver.wrapper.PServerWrapper
					.setPServerCommand(new eu.darkcube.system.pserver.wrapper.command.PServerCommand() {
						@Override
						public boolean execute(CommandSender sender, String[] args) {
							if (sender instanceof Player) {
								User user = UserWrapper.getUser(((Player) sender).getUniqueId());
								user.setOpenInventory(new InventoryPServer(user));
							}
							return true;
						}
					});
		}
	}
}
