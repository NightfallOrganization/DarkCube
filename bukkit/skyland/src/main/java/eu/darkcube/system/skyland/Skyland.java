package eu.darkcube.system.skyland;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.Plugin;

public class Skyland extends DarkCubePlugin {

    @Override
    public void onEnable() {
        System.out.println("enable");
        super.onEnable();
    }

    @Override
    public void onDisable() {
        System.out.println("disable");
        super.onDisable();
    }
}
