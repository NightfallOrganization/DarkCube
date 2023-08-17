/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet;

import eu.cloudnetservice.driver.document.DocumentFactory;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import eu.darkcube.system.pserver.cloudnet.database.DatabaseProvider;
import eu.darkcube.system.pserver.cloudnet.database.PServerDatabase;
import eu.darkcube.system.pserver.common.UniqueId;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class PServerModule extends DriverModule {

    public static final String PLUGIN_NAME = new File(PServerModule.class
            .getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .getPath()).getName();
    private static PServerModule instance;
    public Listener listener;

    public String sqlDatabase;
    List<Pattern> deploymentExclusions;

    public PServerModule() {
        PServerModule.instance = this;
    }

    public static PServerModule getInstance() {
        return PServerModule.instance;
    }

    public static Collection<UniqueId> getUsedPServerIDs() {
        return DatabaseProvider.get("pserver").cast(PServerDatabase.class).getUsedPServerIDs();
    }

    @ModuleTask(order = Byte.MAX_VALUE, lifecycle = ModuleLifeCycle.LOADED) public void loadConfig() {
        PServerConfiguration configuration = this.readConfig(PServerConfiguration.class, () -> new PServerConfiguration("h2", Set.of("paper.jar", "server.properties", "help.yml", "commands.yml", "eula.txt", "bukkit.yml", "banned-ips.json", "banned-players.json", "paper.yml", "whitelist.json", "permissions.yml", "webif.yml", "usercache.json", "ops.json", "")), DocumentFactory.json());
        this.sqlDatabase = configuration.database();
    }

    @ModuleTask(order = Byte.MAX_VALUE, lifecycle = ModuleLifeCycle.STARTED) public void load() {
        ClassLoaderFixRelocation.load(this);
    }

//    public void addDeploymentExclusion(String exclusion) {
//        this.deploymentExclusions.add(Pattern.compile(exclusion));
//        this.getConfig().append("deploymentExclusions", this.deploymentExclusions);
//        this.saveConfig();
//    }
//
//    public void removeDeploymentExclusion(String exclusion) {
//        this.deploymentExclusions.remove(exclusion);
//        this.getConfig().append("deploymentExclusions", this.deploymentExclusions);
//        this.saveConfig();
//    }

    public List<Pattern> getDeploymentExclusions() {
        return Collections.unmodifiableList(this.deploymentExclusions);
    }
}
