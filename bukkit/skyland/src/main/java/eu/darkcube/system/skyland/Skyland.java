package eu.darkcube.system.skyland;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Skyland extends JavaPlugin {

    private static Skyland instance;
    public Skyland() {
        instance = this;
    }

    public static Skyland getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        System.out.println("enable");
        super.onEnable();
        instance.getCommand("GM").setExecutor(new GM());
    }

    @Override
    public void onDisable() {
        System.out.println("disable");
        super.onDisable();
    }
}
