package eu.darkcube.system.darkessentials.command;

import eu.darkcube.system.bukkit.commandapi.argument.WorldArgument;
import eu.darkcube.system.darkessentials.DarkCommand;
import eu.darkcube.system.darkessentials.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class ImportKurzCommand extends DarkCommand {

    public ImportKurzCommand() {
        super("importworldkurz",builder-> builder.executes(context -> {

            Bukkit.createWorld(WorldCreator.name("Abyss"));

            return 0;
        }));
    }

}
