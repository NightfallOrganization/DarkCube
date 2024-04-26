/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.darkessentials;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.Plugin;
import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.darkessentials.command.*;
import eu.darkcube.system.darkessentials.util.LanguageHelper;
import eu.darkcube.system.darkessentials.util.Message;
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

public class DarkEssentials extends DarkCubePlugin {
    private static DarkEssentials instance;

    public DarkEssentials() {
        super("darkessentials");
        instance = this;
    }

    @Override
    public void onEnable() {

        LanguageHelper.initialize();

        CommandAPI.instance().register(new DayCommand());
        CommandAPI.instance().register(new NightCommand());
        CommandAPI.instance().register(new GameModeCommand());
        CommandAPI.instance().register(new FlyCommand());
        CommandAPI.instance().register(new FeedCommand());
        CommandAPI.instance().register(new TpWorldCommand());
        CommandAPI.instance().register(new HealCommand());
        CommandAPI.instance().register(new MaxCommand());
    }

}
