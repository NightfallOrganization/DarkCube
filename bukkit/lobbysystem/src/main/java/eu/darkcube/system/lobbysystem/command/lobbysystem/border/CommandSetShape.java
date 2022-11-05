package eu.darkcube.system.lobbysystem.command.lobbysystem.border;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.CommandArgument;
import eu.darkcube.system.lobbysystem.util.Border;
import eu.darkcube.system.lobbysystem.util.Border.Shape;

public class CommandSetShape extends Command {
	public CommandSetShape() {
		super(Lobby.getInstance(), "setShape", new Command[0], "Setzt die Form der Border",
				CommandArgument.SHAPE);
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Arrays.asList(Shape.values()).stream().map(Shape::name)
					.filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length != 1)
			return false;
		Shape s = null;
		for (Shape shape : Shape.values()) {
			if (shape.name().equalsIgnoreCase(args[0])) {
				s = shape;
			}
		}
		if (s == null) {
			sender.sendMessage("§cUnbekannte Form!");
			return true;
		}
		Border border = Lobby.getInstance().getDataManager().getBorder();
		border = new Border(s, border.getRadius(), border.getLoc1(), border.getLoc2());
		Lobby.getInstance().getDataManager().setBorder(border);
		sender.sendMessage("§aForm neugesetzt!");
		return true;
	}
}
