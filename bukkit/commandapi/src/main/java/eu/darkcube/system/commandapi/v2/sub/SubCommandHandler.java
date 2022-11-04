package eu.darkcube.system.commandapi.v2.sub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import eu.darkcube.system.commandapi.v2.Command;
import eu.darkcube.system.commandapi.v2.ICommandExecutor;
import eu.darkcube.system.commandapi.v2.ITabCompleter;
import eu.darkcube.system.commons.AsyncExecutor;

public abstract class SubCommandHandler extends Command implements ITabCompleter {

	private Collection<SubCommand> subCommands = new ArrayList<>();

	public SubCommandHandler(String... names) {
		super(names);
	}

	public SubCommandHandler(Collection<SubCommand> subCommands, String... names) {
		super(names);
		this.subCommands = subCommands;
	}

	public SubCommandHandler(String[] names, String permission, Collection<SubCommand> subCommands) {
		super(names, permission);
		this.subCommands = subCommands;
	}

	public SubCommandHandler(String[] names, String permission, String description,
			Collection<SubCommand> subCommands) {
		super(names, permission, description);
		this.subCommands = subCommands;
	}

	public SubCommandHandler(String[] names, String permission, String description, String usage, String prefix,
			Collection<SubCommand> subCommands) {
		super(names, permission, description, usage, prefix);
		this.subCommands = subCommands;
	}

	@Override
	public String getUsage() {
		Collection<String> messages = new ArrayList<>();
		for (SubCommand subCommand : this.subCommands) {
			String message = super.getNames()[0] + " " + subCommand.getArgsAsString() + subCommand.getExtendedUsage();

			if (subCommand.getPermission() != null) {
				message += " | " + subCommand.getPermission();
			}

			if (subCommand.getDescription() != null) {
				message += " | " + subCommand.getDescription();
			}
			messages.add(message);
		}
		if (messages.isEmpty()) {
			return null;
		}
		if (messages.size() == 1) {
			return messages.iterator().next();
		}
		return "\n - " + String.join("\n - ", messages);
	}

	@Override
	public void execute(ICommandExecutor sender, String command, String[] args, String commandLine,
			Properties properties) {
		Optional<String> optionalInvalidMessage = this.subCommands.stream()
				.map(subCommand -> subCommand.getInvalidArgumentMessage(args))
				.filter(Objects::nonNull)
				.filter(pair -> pair.getSecond() == 0) // all static values must match
				.findFirst()
				.map(Pair::getFirst);

		Optional<Pair<SubCommand, SubCommandArgument<?>[]>> optionalSubCommand = this.subCommands.stream()
				.map(subCommand -> new Pair<>(subCommand, subCommand.parseArgs(args)))
				.filter(pair -> pair.getSecond() != null
//				&& pair.getSecond().length != 0
				)
				.findFirst();

		if (optionalInvalidMessage.isPresent() && !optionalSubCommand.isPresent()) {
			sender.sendMessage(optionalInvalidMessage.get());
			return;
		}

		if (!optionalSubCommand.isPresent()) {
			this.sendHelp(sender);
			return;
		}

		Pair<SubCommand, SubCommandArgument<?>[]> subCommandPair = optionalSubCommand.get();

		SubCommand subCommand = subCommandPair.getFirst();
		SubCommandArgument<?>[] parsedArgs = subCommandPair.getSecond();

		if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
			sender.sendMessage("You do not have permission to perform this command!");
			return;
		}

		if (subCommand.isAsync()) {
			AsyncExecutor.service()
					.submit(() -> subCommand.execute(subCommand, sender, command,
							new SubCommandArgumentWrapper(parsedArgs), commandLine, subCommand.parseProperties(args),
							new HashMap<>()));
		} else {
			subCommand.execute(subCommand, sender, command, new SubCommandArgumentWrapper(parsedArgs), commandLine,
					subCommand.parseProperties(args), new HashMap<>());
		}
	}

	protected void sendHelp(ICommandExecutor sender) {
		for (String usageLine : this.getUsage().split("\n")) {
			sender.sendMessage(usageLine);
		}
	}

	@Override
	public Collection<String> tabComplete(String commandLine, String[] args, Properties properties) {
		return this.subCommands.stream()
				.map(subCommand -> subCommand.getNextPossibleArgumentAnswers(args))
				.filter(Objects::nonNull)
				.filter(responses -> !responses.isEmpty())
				.flatMap(Collection::parallelStream)
				.collect(Collectors.toSet());
	}

	protected void setSubCommands(Collection<SubCommand> subCommands) {
		this.subCommands = subCommands;
	}
}