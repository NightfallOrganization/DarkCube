/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.darkessentials.command;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EntityArgument;
import eu.darkcube.system.commandapi.v3.arguments.EnumArgument;
import eu.darkcube.system.darkessentials.DarkEssentials;
import eu.darkcube.system.darkessentials.util.Message;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class CommandSpawner extends EssentialsCommand implements Listener {
	private static final PersistentDataType<EntityType> entityType =
			PersistentDataTypes.enumType(EntityType.class);
	private static final Key spawnerType = new Key(DarkEssentials.getInstance(), "spawnerType");

	@SuppressWarnings("deprecation")
	public CommandSpawner() {
		super("spawner", b -> b.then(Commands.argument("type",
						EnumArgument.enumArgument(EntityType.values(),
								t -> t.getName() != null ? new String[] {t.getName()} : new String[0]))
				.executes(ctx -> execute(ctx.getSource(),
						EnumArgument.getEnumArgument(ctx, "type", EntityType.class),
						Collections.singleton(ctx.getSource().asPlayer())))
				.then(Commands.argument("players", EntityArgument.players()).executes(
						ctx -> execute(ctx.getSource(),
								EnumArgument.getEnumArgument(ctx, "type", EntityType.class),
								EntityArgument.getPlayers(ctx, "players"))))));
		Bukkit.getPluginManager().registerEvents(this, DarkEssentials.getInstance());
	}

	@SuppressWarnings("deprecation")
	private static int execute(CommandSource source, EntityType type, Collection<Player> players) {
		String name = type.getName();
		ItemStack item =
				ItemBuilder.spawner().persistentDataStorage().iset(spawnerType, entityType, type)
						.builder().displayname(ChatColor.GOLD + name + ChatColor.GRAY + " Spawner")
						.build();
		players.forEach(p -> p.getInventory().addItem(item));
		if (players.size() == 1) {
			if (players.stream().findFirst().orElse(null) == source.getEntity()) {
				source.sendMessage(
						Message.SPAWNER_GIVEN_TO_SELF.getMessage(source.getSource(), name));
			} else {
				source.sendMessage(
						Message.SPAWNER_GIVEN_TO_PLAYER.getMessage(source.getSource(), name,
								Objects.requireNonNull(players.stream().findFirst().orElse(null))
										.getDisplayName()));
			}
		} else {
			source.sendMessage(Message.SPAWNER_GIVEN_TO_PLAYERS.getMessage(source.getSource(), name,
					players.size()));
		}
		return 0;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void handle(BlockPlaceEvent e) {
		if (e.isCancelled())
			return;
		ItemStack itemStack = e.getItemInHand();
		ItemBuilder b = ItemBuilder.item(itemStack);
		if (!b.persistentDataStorage().has(spawnerType)) {
			return;
		}
		EntityType entity = b.persistentDataStorage().get(spawnerType, entityType);
		if (entity == null) {
			return;
		}
		CreatureSpawner spawner = (CreatureSpawner) e.getBlock().getState();
		spawner.setSpawnedType(entity);
		spawner.update(true);
	}
}
