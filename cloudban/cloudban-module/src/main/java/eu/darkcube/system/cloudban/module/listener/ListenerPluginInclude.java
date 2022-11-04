package eu.darkcube.system.cloudban.module.listener;

import java.io.File;

import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.util.DefaultModuleHelper;
import de.dytanic.cloudnet.event.service.CloudServicePreStartEvent;
import eu.darkcube.system.cloudban.module.Module;

public class ListenerPluginInclude {

	@EventListener
	public void handle(final CloudServicePreStartEvent e) {
		new File(e.getCloudService().getDirectoryPath().toFile(), "plugins").mkdirs();
		File file = new File(e.getCloudService().getDirectoryPath().toFile(), "plugins/" + Module.PLUGIN_NAME);
		file.delete();
		DefaultModuleHelper.copyCurrentModuleInstanceFromClass(ListenerPluginInclude.class, file.toPath());
	}

}
