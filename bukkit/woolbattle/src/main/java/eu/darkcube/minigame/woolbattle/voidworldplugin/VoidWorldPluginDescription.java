package eu.darkcube.minigame.woolbattle.voidworldplugin;

import java.io.StringReader;

import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;

public class VoidWorldPluginDescription {
	private static PluginDescriptionFile file;

	public static PluginDescriptionFile get() {
		if (file != null)
			return file;
		StringReader r = new StringReader(
						String.join("\n", "main: WoolBattleVoidWorldPlugin", "name: WoolBattleVoidWorld", "author: DasBabyPixel", "version: internal", "load: startup"));
		PluginDescriptionFile desc;
		try {
			desc = new PluginDescriptionFile(r);
		} catch (InvalidDescriptionException ex) {
			throw new RuntimeException(ex);
		}
		return file = desc;
	}

}
