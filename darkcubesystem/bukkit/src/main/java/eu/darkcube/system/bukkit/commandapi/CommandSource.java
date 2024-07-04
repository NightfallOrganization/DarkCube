/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.avaje.ebean.validation.NotNull;
import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.bukkit.commandapi.argument.EntityAnchorArgument;
import eu.darkcube.system.commandapi.CommandExecutor;
import eu.darkcube.system.commandapi.util.MathHelper;
import eu.darkcube.system.commandapi.util.Messages.MessageWrapper;
import eu.darkcube.system.commandapi.util.Vector2f;
import eu.darkcube.system.commandapi.util.Vector3d;
import eu.darkcube.system.libs.com.mojang.brigadier.LiteralMessage;
import eu.darkcube.system.libs.com.mojang.brigadier.Message;
import eu.darkcube.system.libs.com.mojang.brigadier.ResultConsumer;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestion;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.tree.CommandNode;
import eu.darkcube.system.libs.com.mojang.brigadier.tree.LiteralCommandNode;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.audience.ForwardingAudience;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.ClickEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CommandSource implements ISuggestionProvider, ForwardingAudience {

    public static final SimpleCommandExceptionType REQUIRES_PLAYER_EXCEPTION_TYPE = new SimpleCommandExceptionType(new LiteralMessage("You need to be a player!"));

    public static final SimpleCommandExceptionType REQUIRES_ENTITY_EXCEPTION_TYPE = new SimpleCommandExceptionType(new LiteralMessage("You need to be an entity!"));

    private final CommandExecutor source;
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

    public CommandSource(CommandExecutor source, Vector3d pos, World world, String name, String displayName, Entity entity, Vector2f rotation, Map<String, Object> extra) {
        this(source, pos, world, name, displayName, false, entity, (context, success, result) -> {
        }, EntityAnchorArgument.Type.FEET, rotation, extra);
    }

    public CommandSource(CommandExecutor source, Vector3d pos, World world, String name, String displayName, boolean feedbackDisabled, Entity entity, ResultConsumer<CommandSource> resultConsumer, EntityAnchorArgument.Type entityAnchorType, Vector2f rotation, Map<String, Object> extra) {
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

    public static CommandSource create(CommandSender sender) {
        return create(BukkitCommandExecutor.create(sender));
    }

    public static CommandSource create(BukkitCommandExecutor executor) {
        CommandSender sender = executor.sender();
        Vector3d pos = null;
        World world = null;
        String name = sender.getName();
        String displayName = sender.getName();
        Entity entity = null;
        Vector2f rotation = null;
        if (sender instanceof Entity) {
            entity = (Entity) sender;
            pos = BukkitVector3d.position(entity.getLocation());
            displayName = entity instanceof Player ? name : entity.getCustomName();
            rotation = new Vector2f(entity.getLocation().getYaw(), entity.getLocation().getPitch());
            world = entity.getWorld();
        } else if (sender instanceof BlockCommandSender) {
            BlockCommandSender b = (BlockCommandSender) sender;
            pos = BukkitVector3d.position(b.getBlock().getLocation().add(0.5, 0.5, 0.5));
            rotation = new Vector2f(0, 0);
            world = b.getBlock().getWorld();
        }
        return new CommandSource(executor, pos, world, name, displayName, entity, rotation, new HashMap<>());
    }

    public void sendMessage(Message message) {
        if (message instanceof MessageWrapper) {
            MessageWrapper w = (MessageWrapper) message;
            BaseMessage m = w.message();
            sendMessage(m.getMessage(source, w.components()));
        } else {
            sendMessage(Component.text(message.getString()).color(NamedTextColor.RED));
        }
    }

    public void sendMessage(BaseMessage message, Object... objects) {
        sendMessage(message.getMessage(source, objects));
    }

    @Override public @NotNull Iterable<? extends Audience> audiences() {
        return Collections.singleton(source);
    }

    public CommandSource withEntity(Entity entity) {
        return this.entity == entity ? this : new CommandSource(this.source, this.pos, this.world, this.name, this.displayName, this.feedbackDisabled, entity, this.resultConsumer, this.entityAnchorType, this.rotation, this.extra);
    }

    public CommandSource withPos(Vector3d pos) {
        return this.pos == pos ? this : new CommandSource(this.source, pos, this.world, this.name, this.displayName, this.feedbackDisabled, this.entity, this.resultConsumer, this.entityAnchorType, this.rotation, this.extra);
    }

    public CommandSource withRotation(Vector2f rotation) {
        return this.rotation == rotation ? this : new CommandSource(this.source, this.pos, this.world, this.name, this.displayName, this.feedbackDisabled, this.entity, this.resultConsumer, this.entityAnchorType, rotation, this.extra);
    }

    public CommandSource withResultConsumer(ResultConsumer<CommandSource> resultConsumer) {
        return this.resultConsumer == resultConsumer ? this : new CommandSource(this.source, this.pos, this.world, this.name, this.displayName, this.feedbackDisabled, this.entity, resultConsumer, this.entityAnchorType, this.rotation, this.extra);
    }

    public CommandSource withFeedbackDisabled(boolean feedbackDisabled) {
        return this.feedbackDisabled == feedbackDisabled ? this : new CommandSource(this.source, this.pos, this.world, this.name, this.displayName, feedbackDisabled, this.entity, this.resultConsumer, this.entityAnchorType, this.rotation, this.extra);
    }

    public CommandSource withAnchorType(EntityAnchorArgument.Type entityAnchorType) {
        return this.entityAnchorType == entityAnchorType ? this : new CommandSource(this.source, this.pos, this.world, this.name, this.displayName, this.feedbackDisabled, this.entity, this.resultConsumer, entityAnchorType, this.rotation, this.extra);
    }

    public CommandSource withWorld(World world) {
        return this.world == world ? this : new CommandSource(this.source, this.pos, world, this.name, this.displayName, this.feedbackDisabled, this.entity, this.resultConsumer, this.entityAnchorType, this.rotation, this.extra);
    }

    public CommandSource with(String key, Object object) {
        Map<String, Object> extra = new HashMap<>(this.extra);
        extra.put(key, object);
        return new CommandSource(this.source, this.pos, this.world, this.name, this.displayName, this.feedbackDisabled, this.entity, this.resultConsumer, this.entityAnchorType, this.rotation, extra);
    }

    public <T> T get(String key) {
        return this.get(key, null);
    }

    public <T> T get(String key, T defaultValue) {
        return (T) this.extra.getOrDefault(key, defaultValue);
    }

    public Map<String, Object> getExtra() {
        return this.extra;
    }

    public CommandSource withRotation(Entity entity, EntityAnchorArgument.Type anchorType) {
        return this.withRotation(anchorType.apply(entity));
    }

    public CommandSource withRotation(Vector3d lookPos) {
        Vector3d vector3d = this.entityAnchorType.apply(this);
        double d0 = lookPos.x - vector3d.x;
        double d1 = lookPos.y - vector3d.y;
        double d2 = lookPos.z - vector3d.z;
        double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
        float f = MathHelper.wrapDegrees((float) (-(MathHelper.atan2(d1, d3) * (180F / (float) Math.PI))));
        float f1 = MathHelper.wrapDegrees((float) (MathHelper.atan2(d2, d0) * (180F / (float) Math.PI)) - 90.0F);
        return this.withRotation(new Vector2f(f, f1));
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public Entity assertIsEntity() throws CommandSyntaxException {
        if (this.entity == null) {
            throw CommandSource.REQUIRES_ENTITY_EXCEPTION_TYPE.create();
        }
        return this.entity;
    }

    public Player asPlayer() throws CommandSyntaxException {
        if (!(this.entity instanceof Player)) {
            throw CommandSource.REQUIRES_PLAYER_EXCEPTION_TYPE.create();
        }
        return (Player) this.entity;
    }

    public void sendCompletions(String commandLine, Suggestions suggestions) {
        sendCompletions(commandLine, suggestions, new HashMap<>());
    }

    public void sendCompletions(String commandLine, Suggestions suggestions, Map<CommandNode<CommandSource>, String> usages) {
        List<String> possibilities = new ArrayList<>();
        Map<String, String> usageMap = new HashMap<>();
        int idx = commandLine.lastIndexOf(' ');
        String lastWord = commandLine.substring(idx + 1) + " ";
        Map<String, CommandNode<CommandSource>> reverseUsages = new HashMap<>();
        for (Map.Entry<CommandNode<CommandSource>, String> entry : usages.entrySet()) {
            String usage = entry.getValue().replace('|', '┃');
            if (usage.length() > 30) {
                usage = usage.substring(0, 30);
                int i = usage.lastIndexOf(' ', 30);
                if (i != -1) {
                    usage = usage.substring(0, i);
                }
            }
            usageMap.put(entry.getKey().getName(), usage);
            reverseUsages.put(usage, entry.getKey());
            possibilities.add(entry.getKey().getName());
        }
        for (Suggestion completion : suggestions.getList()) possibilities.add(completion.getText());

        possibilities = possibilities.stream().distinct().sorted().collect(Collectors.toList());

        Map<String, Component> components = new HashMap<>();

//        final TextColor DARKER_AQUA_VALUE = TextColor.color(0x44EEEE);
        final TextColor DARKER_AQUA_VALUE = NamedTextColor.BLUE;

        for (Suggestion completion : suggestions.getList()) {
            String text = completion.getText();
            String cmd = source.commandPrefix() + completion.apply(commandLine);
            String display2 = null;
            if (usageMap.containsKey(text)) {
                String usage = usageMap.remove(text);
                display2 = usage.substring(text.length());
            }
            Component clickable = Component.text(text, NamedTextColor.AQUA);
            if (display2 != null) {
                clickable = clickable.append(Component.text(display2, DARKER_AQUA_VALUE));
            }

            clickable = clickable.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd));

            Component hover = Component.empty();

            if (completion.getTooltip() != null && completion.getTooltip().getString() != null) hover = hover.append(Component.text(completion.getTooltip().getString())).append(Component.newline());

            hover = hover.append(Component.text("Click to insert command", NamedTextColor.GRAY).append(Component.newline()).append(Component.text(cmd, NamedTextColor.GRAY)));

            clickable = clickable.hoverEvent(HoverEvent.showText(hover));

            Component c = Component.text(" - ", NamedTextColor.GREEN).append(clickable);
            components.put(text, c);
        }

        for (Map.Entry<String, String> entry : usageMap.entrySet()) {
            String e = entry.getValue();
            if (!e.startsWith(lastWord)) {
                if (reverseUsages.get(e) instanceof LiteralCommandNode) {
                    continue;
                }
//                int i = e.indexOf(' ');
//                e = e.substring(i + 1);
            } else {
                e = e.substring(lastWord.length());
            }
            Component c = Component.text(" - ", NamedTextColor.GREEN).append(Component.text(e, DARKER_AQUA_VALUE));
            components.put(entry.getKey(), c);
        }
        for (String possibility : possibilities) {
            Component component = components.get(possibility);
            if (component != null) sendMessage(component);
        }
    }

    public EntityAnchorArgument.Type getEntityAnchorType() {
        return this.entityAnchorType;
    }

    public String getName() {
        return this.name;
    }

    public Vector3d getPos() {
        return this.pos;
    }

    public Vector2f getRotation() {
        return this.rotation;
    }

    public CommandExecutor getSource() {
        return this.source;
    }

    public World getWorld() {
        return this.world;
    }

}
