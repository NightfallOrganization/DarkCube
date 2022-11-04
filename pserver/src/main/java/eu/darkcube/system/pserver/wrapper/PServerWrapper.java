package eu.darkcube.system.pserver.wrapper;

import org.bukkit.scheduler.*;

import de.dytanic.cloudnet.driver.service.*;
import de.dytanic.cloudnet.wrapper.*;
import eu.darkcube.system.*;
import eu.darkcube.system.commandapi.*;
import eu.darkcube.system.pserver.common.*;
import eu.darkcube.system.pserver.common.packet.*;
import eu.darkcube.system.pserver.common.packet.packets.*;
import eu.darkcube.system.pserver.wrapper.command.*;
import eu.darkcube.system.pserver.wrapper.packethandler.*;

public class PServerWrapper extends Plugin {

	private static PServerWrapper instance;
	private static PServerCommand PServerCommand;
//	private UniqueId id;

	public PServerWrapper() {
		instance = this;
	}

	@Override
	public void onLoad() {

		WrapperUniqueIdProvider.init();
		WrapperServiceInfoUtil.init();
		WrapperPServerProvider.init();

		PacketManager pm = PacketManager.getInstance();
		pm.registerHandler(PacketNodeWrapperAddPServer.class, new HandlerAddPServer());
		pm.registerHandler(PacketNodeWrapperRemovePServer.class, new HandlerRemovePServer());
		pm.registerHandler(PacketNodeWrapperUpdateInfo.class, new HandlerUpdateInfo());
		pm.registerHandler(PacketNodeWrapperAddOwner.class, new HandlerAddOwner());
		pm.registerHandler(PacketNodeWrapperRemoveOwner.class, new HandlerRemoveOwner());

		UniqueId id;
		try {
			id = new PacketWrapperNodeGetUniqueId(Wrapper.getInstance().getServiceId())
					.sendQuery(PacketNodeWrapperUniqueId.class).getUniqueId();
			ServiceInfoSnapshot info = Wrapper.getInstance().getCurrentServiceInfoSnapshot();
			info.setProperty(ServiceInfoUtil.property_uid, id);
			Wrapper.getInstance().publishServiceInfoUpdate(info);
			System.out.println("[PSERVER] LOADING PSERVER...");
			WrapperPServerProvider.getInstance().pserver = WrapperPServerProvider.getInstance().getPServer(id);
			System.out.println("[PSERVER] PSERVER ID: " + id);
		} catch (Exception ex) {
			System.out.println("[PSERVER] LOADING PSERVER API...");
		}
	}

	@Override
	public void onEnable() {
		CommandAPI.enable(this, new CommandPServer());
		if (WrapperPServerProvider.getInstance().isPServer()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					System.out.println("[PServer] Set status as RUNNING");
					new PacketWrapperNodeSetRunning(WrapperPServerProvider.getInstance().getCurrentPServer().getId())
							.sendAsync();
				}
			}.runTaskAsynchronously(PServerWrapper.getInstance());
		}
	}

	@Override
	public void onDisable() {

	}

	@Override
	public String getCommandPrefix() {
		return "PServer";
	}

	public static void setPServerCommand(PServerCommand pServerCommand) {
		PServerWrapper.PServerCommand = pServerCommand;
	}

	public static PServerCommand getPServerCommand() {
		return PServerCommand;
	}

	public static PServerWrapper getInstance() {
		return instance;
	}
}