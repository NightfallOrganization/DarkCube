package eu.darkcube.system.pserver.node;

import java.io.File;

import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.service.CloudServiceInfoUpdateEvent;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.util.DefaultModuleHelper;
import de.dytanic.cloudnet.event.service.CloudServicePreStartEvent;
import de.dytanic.cloudnet.event.service.CloudServicePreStopEvent;
import de.dytanic.cloudnet.service.ICloudService;
import eu.darkcube.system.pserver.common.ServiceInfoUtil;
import eu.darkcube.system.pserver.common.UniqueId;

public class Listener {
//
//	@EventListener
//	public void handle(ChannelMessageReceiveEvent event) {
//		if (event.getChannel().equals("connecttt")) {
//			System.out.println("receive");
//			new HandlerConnect().handle(new PacketWrapperNodeConnect(new UniqueId(event.getBuffer().readString())));
//		}
//	}

	@EventListener
	public void handle(CloudServicePreStartEvent e) {
		File file = getFile(e.getCloudService());
		if (file.exists()) {
			copy(e.getCloudService());
		}
	}

	@EventListener
	public void handle(CloudServicePreStopEvent e) {
		UniqueId id = ServiceInfoUtil.getInstance().getUniqueId(e.getCloudService().getServiceInfoSnapshot());
		if (NodePServerProvider.getInstance().isPServer(id)) {
			NodePServer ps = NodePServerProvider.getInstance().getPServer(id);
			e.getCloudService().deployResources(false);
			ps.remove();
		}
	}

	@EventListener
	public void handle(CloudServiceInfoUpdateEvent e) {
		ServiceInfoSnapshot info = e.getServiceInfo();
		for (NodePServer ps : NodePServerProvider.getInstance().getPServers()) {
			if (ps.getServiceId() != null) {
				if (ps.getServiceId().getUniqueId().equals(info.getServiceId().getUniqueId())) {
					ps.setSnapshot(info);
					break;
				}
			}
		}
	}

	private void copy(ICloudService service) {
		File file = getFile(service);
		file.delete();
		DefaultModuleHelper.copyCurrentModuleInstanceFromClass(Listener.class, file.toPath());
	}

	private File getFile(ICloudService service) {
		File folder = new File(service.getDirectoryPath().toFile(), "plugins");
		folder.mkdirs();
		return new File(folder, PServerModule.PLUGIN_NAME);
	}
}
