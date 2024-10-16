/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.EntityArgument;
import eu.darkcube.system.bukkit.commandapi.argument.EnumArgument;
import eu.darkcube.system.commandapi.argument.BooleanArgument;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.builder.ArgumentBuilder;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.tree.CommandNode;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServer;
import eu.darkcube.system.pserver.plugin.effect.PotionEffect;
import eu.darkcube.system.server.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class EffectCommand extends PServer {

    public EffectCommand() {
        super("effect", new String[0], b -> {
            {
                CommandNode<CommandSource> give = Commands.literal("give").build();
                {
                    {
                        CommandNode<CommandSource> giveTargets = Commands.literal("targets").then(Commands.argument("targets", EntityArgument.entities()).redirect(give, context -> context.getSource().with("targets", EntityArgument.getEntities(context, "targets")))).build();
                        give.addChild(giveTargets);
                        CommandNode<CommandSource> giveEffect = Commands.literal("effect").then(Commands.argument("effect", EnumArgument.enumArgument(PotionEffect.values(), effect -> new String[]{effect.getKey()})).executes(context -> {
                            var source = context.getSource();
                            int level = source.get("level", 1);
                            int duration = source.get("duration", 30);
                            boolean particles = source.get("particles", true);
                            boolean ambient = source.get("ambient", false);
                            boolean force = source.get("force", false);
                            Collection<? extends Entity> targets = source.get("targets", Collections.singleton(source.asPlayer()));
                            var type = EnumArgument.getEnumArgument(context, "effect", PotionEffect.class);
                            return EffectCommand.giveEffect(context, targets, type, level, duration, ambient, particles, force);
                        })).build();
                        give.addChild(giveEffect);

                        CommandNode<CommandSource> giveLevel = Commands.literal("level").then(Commands.argument("level", IntegerArgumentType.integer(1, 256)).redirect(give, context -> context.getSource().with("level", IntegerArgumentType.getInteger(context, "level")))).build();
                        give.addChild(giveLevel);

                        CommandNode<CommandSource> giveDuration = Commands.literal("duration").then(Commands.argument("duration", IntegerArgumentType.integer(1, 99999)).redirect(give, context -> context.getSource().with("duration", IntegerArgumentType.getInteger(context, "duration")))).build();
                        give.addChild(giveDuration);

                        CommandNode<CommandSource> giveParticles = Commands.literal("particles").then(Commands.argument("particles", BooleanArgument.booleanArgument()).redirect(give, context -> context.getSource().with("particles", BooleanArgument.getBoolean(context, "particles")))).build();
                        give.addChild(giveParticles);

                        var item = ItemBuilder.item().material(Material.LEATHER_CHESTPLATE).amount(5).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4).unbreakable(true).displayname(Component.text("Duckness stinkkt"));

                        // ArgumentBuilder<CommandSource, ?> b_giveTargetsEffect =
                        // Commands.argument("effect",
                        // EnumArgument.enumArgument(PotionEffect.values(), effect -> new String[] {
                        // effect.getKey()
                        // }));
                        // Command<CommandSource> effectCommand = context -> {
                        // CommandSource source = context.getSource();
                        // int level = source.get("level", 1);
                        // int duration = source.get("duration", 30);
                        // boolean particles = source.get("particles", true);
                        // boolean ambient = source.get("ambient", false);
                        // boolean force = source.get("force", false);
                        // Collection<? extends Entity> targets =
                        // EntityArgument.getEntities(context, "targets");
                        // PotionEffect type = EnumArgument.getEnumArgument(context, "effect",
                        // PotionEffect.class);
                        // return giveEffect(context, targets, type, level, duration, ambient,
                        // particles, force);
                        // };
                        // b_giveTargetsEffect.executes(effectCommand);
                        // CommandNode<CommandSource> giveTargetsEffect =
                        // b_giveTargetsEffect.build();
                        // giveTargetsEffect.addChild(Commands.literal("level").then(Commands.argument("level",
                        // IntegerArgumentType.integer(1,
                        // 256)).executes(effectCommand).redirect(giveTargetsEffect, context -> {
                        // return context.getSource().with("level",
                        // IntegerArgumentType.getInteger(context, "level"));
                        // })).build());
                        //
                        // b_giveTargets.then(giveTargetsEffect);
                    }
                    // b_give.then(giveTargets);
                }
                b.then(give);
            }

            {
                ArgumentBuilder<CommandSource, ?> b_clear = Commands.literal("clear");
                b_clear.executes(context -> {
                    return EffectCommand.clearEffects(context, Collections.singleton(context.getSource().asPlayer()), Arrays.asList(PotionEffect.values()));
                });
                {
                    ArgumentBuilder<CommandSource, ?> b_clearTargets = Commands.argument("targets", EntityArgument.entities());
                    b_clearTargets.executes(context -> {
                        return EffectCommand.clearEffects(context, EntityArgument.getEntities(context, "targets"), Arrays.asList(PotionEffect.values()));
                    });
                    b_clear.then(b_clearTargets);
                }
                b.then(b_clear);
            }

            // b.then(give).then(Commands.literal("clear").executes(context -> {
            // return clearEffects(context, Collections.singleton(context.getSource().asPlayer()),
            // Arrays.asList(PotionEffect.values()));
            // }).then(Commands.argument("targets", EntityArgument.entities()).executes(context -> {
            // return clearEffects(context, EntityArgument.getEntities(context, "targets"),
            // Arrays.asList(PotionEffect.values()));
            // }));
        });
    }

    private static int giveEffect(CommandContext<CommandSource> context, Collection<? extends Entity> targets, PotionEffect effect, int level, int duration, boolean ambient, boolean particles, boolean force) {
        var source = context.getSource();
        Collection<LivingEntity> failed = new HashSet<>();
        Collection<LivingEntity> added = new HashSet<>();
        for (var target : targets) {
            if (!(target instanceof LivingEntity living)) {
                source.sendMessage(Message.INVALID_ENTITY, target.getName());
                continue;
            }
            if (living.addPotionEffect(new org.bukkit.potion.PotionEffect(effect.getType(), duration * 20, level - 1, ambient, particles), force)) {
                added.add(living);
            } else {
                failed.add(living);
            }
        }
        if (added.size() == 1) {
            source.sendMessage(Message.ADDED_EFFECT_SINGLE, effect.getKey(), added.stream().findAny().get().getName(), level, duration);
        } else if (failed.size() == 0 || added.size() != 0) {
            source.sendMessage(Message.ADDED_EFFECT_MULTIPLE, effect.getKey(), added.size(), level, duration);
        }
        for (var living : failed) {
            source.sendMessage(Message.COULD_NOT_ADD_EFFECT, effect.getKey(), living.getName());
        }
        return 0;
    }

    private static int clearEffects(CommandContext<CommandSource> context, Collection<? extends Entity> targets, Collection<PotionEffect> potionEffects) {
        var source = context.getSource();
        Map<LivingEntity, Collection<PotionEffect>> removed = new HashMap<>();
        for (var target : targets) {
            if (!(target instanceof LivingEntity living)) {
                source.sendMessage(Message.INVALID_ENTITY, target.getName());
                continue;
            }
            removed.put(living, new ArrayList<>());
            for (var effect : potionEffects) {
                if (living.hasPotionEffect(effect.getType())) {
                    living.removePotionEffect(effect.getType());
                    removed.get(living).add(effect);
                }
            }
        }
        var single = removed.size() == 1;
        Collection<PotionEffect> totalRemovedEffects = new HashSet<>();
        for (var living : removed.keySet()) {
            var effects = removed.get(living);
            totalRemovedEffects.addAll(effects);
            if (single) {
                if (effects.size() == 1) {
                    source.sendMessage(Message.CLEARED_EFFECT_SINGLE, effects.stream().findAny().get().getKey(), living.getName());
                } else {
                    source.sendMessage(Message.CLEARED_MULTIPLE_EFFECTS_SINGLE, effects.size(), living.getName());
                }
            }
        }
        if (!single) {
            if (totalRemovedEffects.size() == 1) {
                source.sendMessage(Message.CLEARED_EFFECT_MULTIPLE, totalRemovedEffects.stream().findAny().get().getKey(), removed.size());
            } else {
                source.sendMessage(Message.CLEARED_MULTIPLE_EFFECTS_MULTIPLE, totalRemovedEffects.size(), removed.size());
            }
        }
        return 0;
    }
}
