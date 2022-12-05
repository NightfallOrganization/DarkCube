package eu.darkcube.system.packetapi;

import eu.darkcube.system.DarkCubePlugin;

public class PluginPacketAPI extends DarkCubePlugin {
	@Override
	public void onLoad() {
		PacketAPI.init();
		PacketAPI.getInstance().load();
	}
}
