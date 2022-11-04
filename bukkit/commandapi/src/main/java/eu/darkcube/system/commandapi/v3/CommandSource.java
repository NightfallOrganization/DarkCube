package eu.darkcube.system.commandapi.v3;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import eu.darkcube.system.commandapi.v3.arguments.EntityAnchorArgument;
import eu.darkcube.system.commandapi.v3.arguments.EntityAnchorArgument.Type;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandSource implements ISuggestionProvider {
	public static final SimpleCommandExceptionType REQUIRES_PLAYER_EXCEPTION_TYPE = new SimpleCommandExceptionType(
					new LiteralMessage("You need to be a player!"));
	public static final SimpleCommandExceptionType REQUIRES_ENTITY_EXCEPTION_TYPE = new SimpleCommandExceptionType(
					new LiteralMessage("You need to be an entity!"));

	private final ICommandExecutor source;
	private final Vector3d pos;
	private final World world;
	private final String name;
	private final String displayName;
	private final boolean feedbackDisabled;
	private final Entity entity;
	private final ResultConsumer<CommandSource> resultConsumer;
	private final EntityAnchorArgument.Type entityAnchorType;
	private final Vector2f rotation;
	private final Map<String, Object> extra;

	public static CommandSource create(CommandSender sender) {
		ICommandExecutor executor = new BukkitCommandExecutor(sender);
		Vector3d pos = null;
		World world = null;
		String name = sender.getName();
		String displayName = sender.getName();
		Entity entity = null;
		Vector2f rotation = null;
		if (sender instanceof Entity) {
			entity = (Entity) sender;
			pos = Vector3d.position(entity.getLocation());
			displayName = entity.getCustomName();
			rotation = new Vector2f(entity.getLocation().getYaw(),
							entity.getLocation().getPitch());
			world = entity.getWorld();
		} else if (sender instanceof BlockCommandSender) {
			BlockCommandSender b = (BlockCommandSender) sender;
			pos = Vector3d.position(b.getBlock().getLocation().add(0.5, 0.5, 0.5));
			rotation = new Vector2f(0, 0);
			world = b.getBlock().getWorld();
		}
		return new CommandSource(executor, pos, world, name, displayName,
						entity, rotation, new HashMap<>());
	}

	public CommandSource(ICommandExecutor source, Vector3d pos, World world,
					String name, String displayName, Entity entity,
					Vector2f rotation, Map<String, Object> extra) {
		this(source, pos, world, name, displayName, false, entity,
						new ResultConsumer<CommandSource>() {
							@Override
							public void onCommandComplete(
											CommandContext<CommandSource> context,
											boolean success, int result) {
							}
						}, Type.FEET, rotation, extra);
	}

	public CommandSource(ICommandExecutor source, Vector3d pos, World world,
					String name, String displayName, boolean feedbackDisabled,
					Entity entity, ResultConsumer<CommandSource> resultConsumer,
					Type entityAnchorType, Vector2f rotation,
					Map<String, Object> extra) {
		super();
		this.source = source;
		this.pos = pos;
		this.world = world;
		this.name = name;
		this.displayName = displayName;
		this.feedbackDisabled = feedbackDisabled;
		this.entity = entity;
		this.resultConsumer = resultConsumer;
		this.entityAnchorType = entityAnchorType;
		this.rotation = rotation;
		this.extra = extra;
	}

	public CommandSource withEntity(Entity entity) {
		return this.entity == entity ? this
						: new CommandSource(source, pos, world, name,
										displayName, feedbackDisabled, entity,
										resultConsumer, entityAnchorType,
										rotation, extra);
	}

	public CommandSource withPos(Vector3d pos) {
		return this.pos == pos ? this
						: new CommandSource(source, pos, world, name,
										displayName, feedbackDisabled, entity,
										resultConsumer, entityAnchorType,
										rotation, extra);
	}

	public CommandSource withRotation(Vector2f rotation) {
		return this.rotation == rotation ? this
						: new CommandSource(source, pos, world, name,
										displayName, feedbackDisabled, entity,
										resultConsumer, entityAnchorType,
										rotation, extra);
	}

	public CommandSource withResultConsumer(
					ResultConsumer<CommandSource> resultConsumer) {
		return this.resultConsumer == resultConsumer ? this
						: new CommandSource(source, pos, world, name,
										displayName, feedbackDisabled, entity,
										resultConsumer, entityAnchorType,
										rotation, extra);
	}

	public CommandSource withFeedbackDisabled(boolean feedbackDisabled) {
		return this.feedbackDisabled == feedbackDisabled ? this
						: new CommandSource(source, pos, world, name,
										displayName, feedbackDisabled, entity,
										resultConsumer, entityAnchorType,
										rotation, extra);
	}

	public CommandSource withAnchorType(Type entityAnchorType) {
		return this.entityAnchorType == entityAnchorType ? this
						: new CommandSource(source, pos, world, name,
										displayName, feedbackDisabled, entity,
										resultConsumer, entityAnchorType,
										rotation, extra);
	}

	public CommandSource withWorld(World world) {
		return this.world == world ? this
						: new CommandSource(source, pos, world, name,
										displayName, feedbackDisabled, entity,
										resultConsumer, entityAnchorType,
										rotation, extra);
	}

	public CommandSource with(String key, Object object) {
		Map<String, Object> extra = new HashMap<>(this.extra);
		extra.put(key, object);
		return new CommandSource(source, pos, world, name, displayName,
						feedbackDisabled, entity, resultConsumer,
						entityAnchorType, rotation, extra);
	}

	public <T> T get(String key) {
		return get(key, null);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, T defaultValue) {
		return (T) extra.getOrDefault(key, defaultValue);
	}

	public Map<String, Object> getExtra() {
		return extra;
	}

	public CommandSource withRotation(Entity entity, Type anchorType) {
		return this.withRotation(anchorType.apply(entity));
	}

	public CommandSource withRotation(Vector3d lookPos) {
		Vector3d vector3d = this.entityAnchorType.apply(this);
		double d0 = lookPos.x - vector3d.x;
		double d1 = lookPos.y - vector3d.y;
		double d2 = lookPos.z - vector3d.z;
		double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
		float f = MathHelper.wrapDegrees((float) (-(MathHelper.atan2(d1, d3)
						* (180F / (float) Math.PI))));
		float f1 = MathHelper.wrapDegrees((float) (MathHelper.atan2(d2, d0)
						* (180F / (float) Math.PI)) - 90.0F);
		return this.withRotation(new Vector2f(f, f1));
	}

	public String getDisplayName() {
		return displayName;
	}

	public Entity getEntity() {
		return entity;
	}

	public void sendFeedback(TextComponent[] message, boolean allowLogging) {
		if (this.source.shouldReceiveFeedback() && !this.feedbackDisabled) {
			this.source.sendMessage(message);
		}
		if (allowLogging && this.source.allowLogging()
						&& !this.feedbackDisabled) {
			this.logFeedback(message);
		}
	}

	private void logFeedback(TextComponent... message) {
	}

	public Entity assertIsEntity() throws CommandSyntaxException {
		if (this.entity == null) {
			throw REQUIRES_ENTITY_EXCEPTION_TYPE.create();
		} else {
			return this.entity;
		}
	}

	public Player asPlayer() throws CommandSyntaxException {
		if (!(this.entity instanceof Player)) {
			throw REQUIRES_PLAYER_EXCEPTION_TYPE.create();
		} else {
			return (Player) this.entity;
		}
	}

	public void sendErrorMessage(Message message, Object... components) {
		if (this.source.shouldReceiveErrors() && !this.feedbackDisabled) {
			this.source.sendMessage(message.apply(b -> b.color(ChatColor.RED), this.source.getMessageToStringFunction(), components));
		}
	}

	public void sendErrorMessage(com.mojang.brigadier.Message message,
					Object... components) {
		if (this.source.shouldReceiveErrors() && !this.feedbackDisabled) {
			this.source.sendMessage(b -> b.color(ChatColor.RED), message, components);
		}
	}

	public void sendErrorMessage(TextComponent... message) {
		if (this.source.shouldReceiveErrors() && !this.feedbackDisabled) {
			this.source.sendMessage(b -> b.color(ChatColor.RED), message);
		}
	}

	public void onCommandComplete(CommandContext<CommandSource> context,
					boolean success, int result) {
		if (this.resultConsumer != null) {
			this.resultConsumer.onCommandComplete(context, success, result);
		}
	}

	@Override
	public Collection<String> getPlayerNames() {
		return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
	}

	public EntityAnchorArgument.Type getEntityAnchorType() {
		return entityAnchorType;
	}

	public String getName() {
		return name;
	}

	public Vector3d getPos() {
		return pos;
	}

	public ResultConsumer<CommandSource> getResultConsumer() {
		return resultConsumer;
	}

	public Vector2f getRotation() {
		return rotation;
	}

	public ICommandExecutor getSource() {
		return source;
	}

	public World getWorld() {
		return world;
	}

}
