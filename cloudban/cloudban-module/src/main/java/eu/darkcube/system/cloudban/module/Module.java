package eu.darkcube.system.cloudban.module;

import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.driver.module.ModuleLifeCycle;
import de.dytanic.cloudnet.driver.module.ModuleTask;
import de.dytanic.cloudnet.driver.module.driver.DriverModule;
import eu.darkcube.system.cloudban.module.command.*;
import eu.darkcube.system.cloudban.module.listener.ListenerPluginInclude;
import eu.darkcube.system.cloudban.module.listener.ListenerReceiveMessage;

public class Module extends DriverModule {

	public static final String PLUGIN_NAME = "cloudnet-cloudban.jar";
	public static Module INSTANCE;
	public ListenerReceiveMessage listenerReceiveMessage;
	
	@ModuleTask(order = 126, event = ModuleLifeCycle.LOADED)
	public void initConfig() {
		INSTANCE = this;
	}

	@ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.LOADED)
	public void load() {
		CloudCommandWrapper.create();
	}

	@ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.STARTED)
	public void start() {
		getCloudNet().getEventManager().registerListener(new ListenerPluginInclude());

		listenerReceiveMessage = new ListenerReceiveMessage();
	}

	@ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.STOPPED)
	public void stop() {

	}

	@ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.UNLOADED)
	public void unload() {

	}

	public static final CloudNet getCloudNet() {
		return CloudNet.getInstance();
	}
}
