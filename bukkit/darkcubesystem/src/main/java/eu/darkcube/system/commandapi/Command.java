package eu.darkcube.system.commandapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Command implements Taber {

//	static final Map<CommandAPI, Set<Command>> COMMANDS = new HashMap<>();

	private String name;
	private String permission;
	private Set<Command> childs;
	private CommandPosition pos;
	private Argument[] arguments;
	private String[] aliases;
	private String simpleUsage;
	private String simpleLongUsage;
	private String beschreibung;
	CommandAPI instance;

	public Command(JavaPlugin plugin, String name, Command[] childs, String beschreibung, Argument... arguments) {
		this.name = name;
		this.childs = values(childs);
		this.arguments = arguments;
		this.beschreibung = beschreibung;
		try {
			this.permission = "system.plugin." + plugin.getName().toLowerCase().replace(" ", "") + ".command";
		} catch (NullPointerException ex) {
			throw new InvalidCommandException(
					"Please call the Mehthod CommandAPI#install before initializing the Command. "
							+ "Keep in mind that the code where the installing and initializing may have to be synchronized");
		}
		reloadPermissions();
		loadSimpleUsage();
		setAliases(new String[0]);
	}

	public Command(JavaPlugin plugin, String name, Command[] childs, String beschreibung) {
		this(plugin, name, childs, beschreibung, new Argument[0]);
	}

	public String getSimpleLongUsage() {
		return simpleLongUsage;
	}

	public void setAliases(String... aliases) {
		this.aliases = aliases;
	}

	public String[] getAliases() {
		return aliases;
	}

	private final void loadSimpleUsage() {
		StringBuilder usage = new StringBuilder();
		usage.append(this.getName());
		for (Argument argument : this.arguments) {
			if (argument.isNeeded()) {
				usage.append(' ').append('<').append(argument.getName()).append('>');
			} else {
				usage.append(' ').append('[').append(argument.getName()).append(']');
			}
		}
		this.simpleUsage = usage.toString();
	}

	private final Command findMainCommand() {
		return instance.getMainCommand();
	}

	final void loadSimpleLongUsage() {
		Command cmd = findMainCommand();
		StringBuilder usageBuilder = new StringBuilder();
		usageBuilder.append('§').append('7').append('/');
		loadSimpleLongUsageRecursiveCall(cmd, usageBuilder, true);
		this.simpleLongUsage = usageBuilder.toString();
		for (Command child : this.getChilds()) {
			child.loadSimpleLongUsage();
		}
	}

	private final boolean loadSimpleLongUsageRecursiveCall(Command cmd, StringBuilder builder) {
		return loadSimpleLongUsageRecursiveCall(cmd, builder, false);
	}

	private final boolean loadSimpleLongUsageRecursiveCall(Command cmd, StringBuilder builder, boolean first) {
		if (!first) {
			builder.append(' ');
		}
		if (cmd.equals(this)) {
			builder.append(cmd.simpleUsage);
			return true;
		}
		for (Command c : cmd.getChilds()) {
			StringBuilder commandBuilder = new StringBuilder();
			if (loadSimpleLongUsageRecursiveCall(c, commandBuilder)) {
				builder.append(cmd.simpleUsage);
				builder.append(commandBuilder.toString());
				return true;
			}
		}
		return false;
	}

	public void reloadPermissions() {
		String p = getName().toLowerCase();
		this.permission += '.' + p;
		for (Command child : childs) {
			child.permission = this.permission;
			child.reloadPermissions();
		}
	}

	public final void onCommand(CommandSender sender, String[] args, String start) {
		if (hasPermission(sender)) {
			if (!execute(sender, args)) {
				sendUsage(sender, start);
			}
		} else
			sender.sendMessage(Message.NO_PERMISSION.getMessage());
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		return Collections.emptyList();
	}

	public final String getUsage(CommandSender sender) {
		return getUsage(sender, "");
	}

	public final String getUsage(CommandSender sender, String start) {
		if (hasPermission(sender)) {
			StringBuilder builder = new StringBuilder();
			if (hasSubCommands()) {
				builder.append(getUsageCommands(sender, start));
			} else {
				String niceCommandName = this.name;
				if (niceCommandName.length() > 0)
					niceCommandName = Character.toString(niceCommandName.charAt(0)).toUpperCase()
							+ niceCommandName.substring(1);
				builder.append(this.instance.prefix).append("§3§lCommand Beschreibung - §b").append(niceCommandName)
						.append('\n').append(this.instance.prefix).append("§b> §7").append(this.beschreibung);
				if (arguments.length != 0) {
					builder.append('\n').append(this.instance.prefix).append("§3Argumente: ");
					for (Argument argument : arguments) {
						builder.append('\n').append(this.instance.prefix).append("§b- §8");
						if (argument.isNeeded()) {
							builder.append("<§7").append(argument.getName()).append("§8>");
						} else {
							builder.append("[§7").append(argument.getName()).append("§8]");
						}
						if (argument.getDescription() != null && argument.getDescription().length() != 0) {
							builder.append("§3 -> §7").append(argument.getDescription());
						}
					}
				}
			}
			return builder.toString();
		}
		return Message.NO_PERMISSION.getMessage();
	}

	private final String getUsageCommands(CommandSender sender, String start) {
		if (hasPermission(sender)) {
			if (hasSubCommands()) {
				StringBuilder usage = new StringBuilder();
				String niceCommandName = this.name;
				if (niceCommandName.length() > 0)
					niceCommandName = Character.toString(niceCommandName.charAt(0)).toUpperCase()
							+ niceCommandName.substring(1);
				usage.append(instance.prefix).append("§b").append(niceCommandName).append(" Sub Commands: ");
				usage.append("§7(").append(this.simpleLongUsage).append("§7)");

				for (Command cmd : getChilds()) {
					if (hasPermission(sender, cmd.getPermission())) {
						if (cmd.getName().toLowerCase().startsWith(start.toLowerCase())) {
							usage.append("\n§3> §a").append(cmd.getName());
							if (cmd.arguments.length != 0) {
								usage.append(" §3-");
								for (Argument argument : cmd.arguments) {
									if (argument.isNeeded()) {
										usage.append(" §8<§7").append(argument.getName()).append("§8>");
									} else {
										usage.append(" §8[§7").append(argument.getName()).append("§8]");
									}
								}
							}
						}
					}
				}
				return usage.toString();
			}
			return "\n§cThis command has no Sub commands";
		}
		return Message.NO_PERMISSION.getMessage();
	}

	public final boolean hasPermission(CommandSender sender) {
		return hasPermission(sender, getPermission());
	}

	public final boolean hasPermission(CommandSender sender, String permission) {
		if (sender.isOp() || permission == null)
			return true;
		for (PermissionAttachmentInfo perminfo : sender.getEffectivePermissions()) {
			if (perminfo.getPermission().startsWith(permission) || perminfo.getPermission().equals("*"))
				return true;
		}
		return false;
	}

	public final void sendUsage(CommandSender sender) {
		sender.sendMessage(getUsage(sender));
	}

	public final void sendUsage(CommandSender sender, String start) {
		sender.sendMessage(getUsage(sender, start));
	}

	public static List<String> vv(CommandSender sender, Set<Command> commands, String start) {
		return Command.value(sender, Command.value(commands), start);
	}

	public static Set<Command> values(Command[] commands) {
		List<Command> cmds = new ArrayList<>();
		for (Command cmd : commands)
			cmds.add(cmd);
		return values(cmds);
	}

	public static Set<Command> values(List<Command> commands) {
		Set<Command> cmds = new HashSet<>();
		for (Command cmd : commands)
			cmds.add(cmd);
		return cmds;
	}

	public static List<Command> value(Set<Command> commands) {
		return new ArrayList<>(commands);
	}

	public static List<String> value(CommandSender sender, List<Command> commands, String start) {
		List<String> sol = new ArrayList<>();
		for (Command cmd : commands) {
			if (cmd.hasPermission(sender))
				if (cmd.getName().toLowerCase().startsWith(start.toLowerCase()))
					sol.add(cmd.getName());
		}
		return sol;
	}

	public static Command getCommand(Set<Command> commands, String cmd) {
		for (Command c : commands) {
			for (String alias : c.getAliases()) {
				if (alias.equalsIgnoreCase(cmd))
					return c;
			}
			if (c.getName().equalsIgnoreCase(cmd))
				return c;
		}
		return null;
	}

	public final String getName() {
		return name;
	}

	public final String getPermission() {
		return permission;
	}

	protected final void setPositions(CommandPosition owner) {
		CommandPosition cpos = owner.next();
		this.pos = cpos;
		for (Command child : childs) {
			child.setPositions(cpos);
		}
	}

	public final Set<Command> getChilds() {
		return new HashSet<>(childs);
	}

	public final Set<Command> getAllChilds() {
		Set<Command> childs = new HashSet<>(this.childs);
		for (Command child : this.childs) {
			childs.addAll(child.getAllChilds());
		}
		return childs;
	}

	protected CommandPosition getPosition() {
		return pos;
	}

	public final List<String> getSolutions(CommandSender sender) {
		return value(sender, value(childs), "");
	}

	public final boolean hasSubCommands() {
		return !childs.isEmpty();
	}

	public abstract boolean execute(CommandSender sender, String[] args);
}
