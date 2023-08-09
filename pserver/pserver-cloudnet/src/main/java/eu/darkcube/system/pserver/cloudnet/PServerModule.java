/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet;

import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.driver.module.ModuleLifeCycle;
import de.dytanic.cloudnet.driver.module.ModuleTask;
import de.dytanic.cloudnet.driver.module.driver.DriverModule;
import eu.darkcube.system.pserver.cloudnet.database.DatabaseProvider;
import eu.darkcube.system.pserver.cloudnet.database.PServerDatabase;
import eu.darkcube.system.pserver.common.UniqueId;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PServerModule extends DriverModule {

    public static final String PLUGIN_NAME = new File(PServerModule.class
            .getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .getPath()).getName();
    private static PServerModule instance;
    public Listener listener;

    public String sqlDatabase;

    List<String> deploymentExclusions;

    public PServerModule() {
        PServerModule.instance = this;
    }

    public static String getSelf() {
        return PServerModule.getCloudNet().getCurrentNetworkClusterNodeInfoSnapshot().getNode().getUniqueId();
    }

    public static CloudNet getCloudNet() {
        return CloudNet.getInstance();
    }

    public static PServerModule getInstance() {
        return PServerModule.instance;
    }

    public static Collection<UniqueId> getUsedPServerIDs() {
        return DatabaseProvider.get("pserver").cast(PServerDatabase.class).getUsedPServerIDs();
    }

    @ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.LOADED) public void loadConfig() {
        this.sqlDatabase = this.getConfig().getString("database", "h2");
    }

    @ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.STARTED) public void load() {
        ClassLoaderFixRelocation.load(this);
    }

    @ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.STARTED) public void start() {
        PServerModule.getCloudNet().getEventManager().registerListener((this.listener = new Listener()));
    }

    @ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.STOPPED) public void stop() {

    }

    @ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.UNLOADED) public void unload() {

    }

    public void addDeploymentExclusion(String exclusion) {
        this.deploymentExclusions.add(exclusion);
        this.getConfig().append("deploymentExclusions", this.deploymentExclusions);
        this.saveConfig();
    }

    public void removeDeploymentExclusion(String exclusion) {
        this.deploymentExclusions.remove(exclusion);
        this.getConfig().append("deploymentExclusions", this.deploymentExclusions);
        this.saveConfig();
    }

    public List<String> getDeploymentExclusions() {
        return Collections.unmodifiableList(this.deploymentExclusions);
    }

}
