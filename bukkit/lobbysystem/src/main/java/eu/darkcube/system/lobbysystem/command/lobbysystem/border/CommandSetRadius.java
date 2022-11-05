package eu.darkcube.system.lobbysystem.command.lobbysystem.border;

import java.util.List;

import org.bukkit.command.CommandSender;

import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.CommandArgument;
import eu.darkcube.system.lobbysystem.util.Border;

public class CommandSetRadius extends Command {
	public CommandSetRadius() {
		super(Lobby.getInstance(), "setRadius", new Command[0], "Setzt den Radius der Border",
				CommandArgument.RADIUS);
	}

	@Override
	public List<String> onTabComplete(String[] args) {
//		if (args.length == 1) {
//			return Arrays.asList(Shape.values()).stream().map(Shape::name)
//					.filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
//		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length != 1)
			return false;
		double r = -1;
		try {
			r = Double.parseDouble(args[0]);
		} catch (Exception ex) {
		}
		if (r < 0) {
			sender.sendMessage("§cUngültiger Radius!");
			return true;
		}
		Border border = Lobby.getInstance().getDataManager().getBorder();
		border = new Border(border.getShape(), r, border.getLoc1(), border.getLoc2());
		Lobby.getInstance().getDataManager().setBorder(border);
		sender.sendMessage("§aRadius neugesetzt!");
		return true;
	}
}
