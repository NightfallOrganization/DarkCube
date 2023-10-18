/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.darkessentials;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import eu.darkcube.system.Plugin;
import eu.darkcube.system.commandapi.CommandAPI;
import eu.darkcube.system.darkessentials.command.*;
import eu.darkcube.system.darkessentials.util.*;
import eu.darkcube.system.darkessentials.util.EssentialCollections.SortingRule;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.libs.net.kyori.adventure.title.Title;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.Language;
import org.bukkit.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class DarkEssentials extends Plugin {

    public static FileConfiguration config;
    public static String colorFail, colorConfirm, colorValue;

    public DarkEssentials() {
        super("darkessentials");
    }

    public static DarkEssentials getInstance() {
        return DarkEssentials.getPlugin(DarkEssentials.class);
    }

    public static void sendTitle(String title, String subtitle, int come, int stay, int go) {
        sendTitle(LegacyComponentSerializer.legacySection().deserialize(title), LegacyComponentSerializer
                .legacySection()
                .deserialize(subtitle), come, stay, go);
    }

    public static void sendTitle(Player p, String title, String subtitle, int come, int stay, int go) {
        sendTitle(p, LegacyComponentSerializer.legacySection().deserialize(title), LegacyComponentSerializer
                .legacySection()
                .deserialize(subtitle), come, stay, go);
    }

    public static void sendTitle(Component title, Component subtitle, int come, int stay, int go) {
        Bukkit.getOnlinePlayers().forEach(p -> sendTitle(p, title, subtitle, come, stay, go));
    }

    public static void sendTitle(Player p, Component title, Component subtitle, int come, int stay, int go) {
        UserAPI
                .instance()
                .user(p.getUniqueId())
                .showTitle(Title.title(title, subtitle, Title.Times.times(Duration.of(come / 50, ChronoUnit.MILLIS), Duration.of(stay / 50, ChronoUnit.MILLIS), Duration.of(go / 50, ChronoUnit.MILLIS))));
    }

    public static void sendMessagePlayernameRequired(CommandSender sender) {
        DarkEssentials
                .getPlugin(DarkEssentials.class)
                .sendMessage(DarkEssentials.cFail() + "Du bist kein Spieler, deshalb musst du einen Spielernamen angeben!", sender);
    }

    public static void sendMessagePlayerNotFound(Set<String> unresolvedNames, CommandSender sender) {
        if (unresolvedNames.size() != 0) {
            StringBuilder sb = new StringBuilder();
            if (unresolvedNames.size() > 1) {
                sb.append(DarkEssentials.cFail()).append("Die Spieler ").append(ChatColor.GRAY).append("\"");
            } else {
                sb.append(DarkEssentials.cFail()).append("Der Spieler ").append(ChatColor.GRAY).append("\"");
            }
            for (String name : unresolvedNames) {
                sb.append(DarkEssentials.cValue()).append(name).append(ChatColor.GRAY).append("\", \"");
            }
            String substring = sb.substring(0, sb.toString().length() - 3);
            if (unresolvedNames.size() > 1) {
                DarkEssentials
                        .getPlugin(DarkEssentials.class)
                        .sendMessage(substring + DarkEssentials.cFail() + " konnten nicht gefunden werden!", sender);
            } else {
                DarkEssentials
                        .getPlugin(DarkEssentials.class)
                        .sendMessage(substring + DarkEssentials.cFail() + " konnte nicht gefunden werden!", sender);
            }
        }
    }

    public static boolean sendMessageAndReturnTrue(String message, CommandSender... senders) {
        DarkEssentials.getPlugin(DarkEssentials.class).sendMessage(message, Arrays.asList(senders));
        return true;
    }

    public static List<String> getPlayersStartWith(String[] args) {
        List<String> list = new ArrayList<>();
        for (Player current : Bukkit.getOnlinePlayers()) {
            String playerName = current.getName().toLowerCase(Locale.ENGLISH);
            if (playerName.startsWith(args[args.length - 1].toLowerCase(Locale.ENGLISH)) && !KesUtils.containsStringIgnoreCase(playerName, args)) {
                list.add(current.getName());
            }
        }
        return list;
    }

    public static Set<WarpPoint> updateWarps() {
        try {
            Set<WarpPoint> warps = new Gson().fromJson(DarkEssentials.config.getString("warps"), new TypeToken<HashSet<WarpPoint>>() {
            }.getType());
            warps.forEach(WarpPoint::isValid);
            return warps;
        } catch (Exception e) {
            return new HashSet<>();
        }
    }

    public static String cConfirm() {
        return colorConfirm;
    }

    public static String cFail() {
        return colorFail;
    }

    public static String cValue() {
        return colorValue;
    }

    @Override public String getCommandPrefix() {
        return "§5Dark§dCube";
    }

    @Override public void onLoad() {
    }

    @Override public void onDisable() {
        saveConfig();
    }

    @Override public void onEnable() {
        createConfig("worlds");
        try {
            Language.ENGLISH.registerLookup(getClass().getClassLoader(), "messages_en.properties", k -> Message.getPrefix() + k);
            Language.GERMAN.registerLookup(getClass().getClassLoader(), "messages_de.properties", k -> Message.getPrefix() + k);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        saveDefaultConfig();
        config = getConfig();

        colorConfirm = ChatColor.valueOf(config.getString("main.colors.confirmColor")).toString();
        colorFail = ChatColor.valueOf(config.getString("main.colors.failColor")).toString();
        colorValue = ChatColor.valueOf(config.getString("main.colors.valueColor")).toString();

        //		System.out.println(config.getBoolean("command.burn.enabled"));

        Bukkit.getPluginManager().registerEvents(new Pinger(), this);
        Bukkit.getPluginManager().registerEvents(new ChatColorHandler(), this);
        Bukkit.getPluginManager().registerEvents(new TextSound(), this);

        EssentialCollections.addSortingRule(new SortingRule<World>() {
            @Override public boolean instanceOf(Object obj) {
                return obj instanceof World;
            }

            @Override public String toString(World t) {
                return t.getName();
            }

            @Override public Class<World> getRuleClass() {
                return World.class;
            }
        });
        EssentialCollections.addSortingRule(new SortingRule<Player>() {
            @Override public boolean instanceOf(Object obj) {
                return obj instanceof World;
            }

            @Override public String toString(Player t) {
                return t.getName();
            }

            @Override public Class<Player> getRuleClass() {
                return Player.class;
            }
        });
        EssentialCollections.addSortingRule(new SortingRule<EntityType>() {

            @Override public boolean instanceOf(Object obj) {
                return obj instanceof EntityType;
            }

            @SuppressWarnings("deprecation") @Override public String toString(EntityType t) {
                return t.getName();
            }

            @Override public Class<EntityType> getRuleClass() {
                return EntityType.class;
            }
        });
        EssentialCollections.addSortingRule(new SortingRule<WarpPoint>() {

            @Override public boolean instanceOf(Object obj) {
                return obj instanceof WarpPoint;
            }

            @Override public String toString(WarpPoint t) {
                return t.getName();
            }

            @Override public Class<WarpPoint> getRuleClass() {
                return WarpPoint.class;
            }
        });
        EssentialCollections.addSortingRule(new SortingRule<Material>() {

            @Override public boolean instanceOf(Object obj) {
                return obj instanceof Material;
            }

            @Override public String toString(Material t) {
                return t.toString();
            }

            @Override public Class<Material> getRuleClass() {
                return Material.class;
            }
        });
        if (config.getBoolean("command.burn.enabled")) CommandAPI.enable(this, new CommandBurn());
        if (config.getBoolean("command.day.enabled")) CommandAPI.enable(this, new CommandDay());
        CommandAPI.enable(this, new CommandNight());
        CommandAPI.enable(this, new CommandStop());
        CommandAPI.enable(this, new CommandEnderChest());
        eu.darkcube.system.commandapi.v3.CommandAPI.getInstance().register(new CommandHeal());
        CommandAPI.enable(this, new CommandFeed());
        CommandAPI.enable(this, new CommandTrash());
        CommandAPI.enable(this, new CommandPTime());
        CommandAPI.enable(this, new CommandPWeather());
        CommandAPI.enable(this, new CommandSpeed());
        eu.darkcube.system.commandapi.v3.CommandAPI.getInstance().register(new CommandXp());
        eu.darkcube.system.commandapi.v3.CommandAPI.getInstance().register(new CommandSpawner());
        eu.darkcube.system.commandapi.v3.CommandAPI.getInstance().register(new CommandLoadWorld());
        CommandAPI.enable(this, new CommandFly());
        CommandAPI.enable(this, new CommandPing());
        CommandAPI.enable(this, new CommandRepair());
        CommandAPI.enable(this, new CommandGM());
        CommandAPI.enable(this, new CommandEnchant());
        CommandAPI.enable(this, new CommandPos());
        CommandAPI.enable(this, new CommandInvsee());
        CommandAPI.enable(this, new CommandHat());
        CommandAPI.enable(this, new CommandNear());
        CommandAPI.enable(this, new CommandSkull());
        CommandAPI.enable(this, new CommandItemclear());
        CommandAPI.enable(this, new CommandCraft());
        CommandAPI.enable(this, new CommandGms());
        CommandAPI.enable(this, new CommandGmc());
        CommandAPI.enable(this, new CommandGma());
        CommandAPI.enable(this, new CommandGmsp());
        CommandAPI.enable(this, new CommandTimefreeze());
        CommandAPI.enable(this, new CommandTeleportto());
        CommandAPI.enable(this, new CommandGamemodeAll());
        CommandAPI.enable(this, new CommandTeleportWorld());
        if (config.getBoolean("command.warp.enabled")) {
            CommandAPI.enable(this, new CommandWarp());
            CommandAPI.enable(this, new CommandWarpEdit());
        }

        List<String> worlds = getConfig("worlds").getStringList("worlds");
        for (String world : worlds) {
            new WorldCreator(world).createWorld();
        }
    }
}
