/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.command;

import static eu.darkcube.system.bukkit.commandapi.Commands.argument;
import static eu.darkcube.system.bukkit.commandapi.Commands.literal;

import java.util.List;
import java.util.Objects;

import eu.darkcube.system.bauserver.Main;
import eu.darkcube.system.bauserver.heads.Head;
import eu.darkcube.system.bauserver.heads.inventory.HeadInventories;
import eu.darkcube.system.bauserver.heads.inventory.HeadProvider;
import eu.darkcube.system.bauserver.util.Message;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.StringArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.ItemComponent;
import eu.darkcube.system.server.item.component.components.HeadProfile;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HeadsCommand extends BaseCommand {
    public HeadsCommand() {
        // @formatter:off
        super("heads", b -> b
                .then(literal("storage")
                        .then(literal("list")
                                .executes(ctx -> {
                                    // @formatter:on
                                    var player = ctx.getSource().asPlayer();
                                    HeadInventories.STORED_LIST.open(player);
                                    return 0;
                                    // @formatter:off
                                })
                        )
                        .then(literal("add")
                                .executes(ctx -> storeHead(ctx.getSource().asPlayer(),null))
                                .then(argument("name", StringArgumentType.greedyString())
                                        .executes(ctx -> storeHead(ctx.getSource().asPlayer(), StringArgumentType.getString(ctx, "name")))
                                )
                        )
                )
                .then(literal("player")
                        .then(argument("player", StringArgumentType.word())
                                .executes(ctx -> {
                                    // @formatter:on
                                    var player = ctx.getSource().asPlayer();
                                    var playerName = StringArgumentType.getString(ctx, "player");
                                    var item = HeadProvider.headItem(playerName + "'s Head");
                                    item.set(ItemComponent.PROFILE, new HeadProfile(playerName, null, List.of()));
                                    player.getInventory().addItem(item.<ItemStack>build());
                                    return 0;
                                    // @formatter:off
                                })
                        )
                )
                .then(literal("database")
                        .then(literal("update")
                                .executes(HeadsCommand::databaseUpdate)
                        )
                        .executes(ctx -> {
                            // @formatter:on
                            HeadInventories.DATABASE_ROOT.open(ctx.getSource().asPlayer());
                            return 0;
                            // @formatter:off
                        })
                )
        );
        // @formatter:on
    }

    private static int databaseUpdate(CommandContext<CommandSource> ctx) {
        Thread.ofVirtual().start(() -> {
            var source = ctx.getSource();
            try {
                source.sendMessage(Message.DATABASE_UPDATING);
                var storage = Main.getInstance().databaseStorage();
                storage.clear();
                storage.fetchFromProviders();
                source.sendMessage(Message.DATABASE_UPDATED);
            } catch (Throwable t) {
                source.sendMessage(Message.DATABASE_UPDATE_FAILED);
                t.printStackTrace();
            }
        });
        return 0;
    }

    private static int storeHead(Player player, @Nullable String name) {
        var user = UserAPI.instance().user(player.getUniqueId());
        var itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() != Material.PLAYER_HEAD) {
            user.sendMessage(Message.NOT_A_PLAYER_HEAD);
            return 0;
        }
        var item = ItemBuilder.item(itemInHand);
        if (!item.has(ItemComponent.PROFILE)) {
            user.sendMessage(Message.NO_TEXTURE);
            return 0;
        }
        var profile = Objects.requireNonNull(item.get(ItemComponent.PROFILE));
        var skin = profile.skin();
        if (skin == null) {
            user.sendMessage(Message.NO_TEXTURE);
            return 0;
        }
        if (name == null) {
            if (item.has(ItemComponent.CUSTOM_NAME)) {
                name = PlainTextComponentSerializer.plainText().serialize(item.get(ItemComponent.CUSTOM_NAME, Component.empty()));
            }
            if ((name == null || name.isBlank()) && item.has(ItemComponent.ITEM_NAME)) {
                name = PlainTextComponentSerializer.plainText().serialize(item.get(ItemComponent.ITEM_NAME, Component.empty()));
            }
            if ((name == null || name.isBlank())) {
                var playerName = profile.name();
                if (playerName != null) name = playerName + "'s Head";
            }
        }
        if (name == null || name.isBlank()) {
            user.sendMessage(Message.SPECIFY_HEAD_NAME);
            return 0;
        }
        name = name.trim();
        var head = new Head(name, skin.textures(), Head.CATEGORY_MANUAL, Head.PROVIDER_MANUAL, List.of());
        Main.getInstance().headStorage().addHead(head);
        user.sendMessage(Message.ADDED_HEAD);
        return 0;
    }
}
