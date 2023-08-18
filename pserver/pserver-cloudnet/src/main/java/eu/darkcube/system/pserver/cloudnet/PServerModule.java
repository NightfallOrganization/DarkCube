/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet;

import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.document.DocumentFactory;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import eu.darkcube.system.pserver.cloudnet.database.DatabaseProvider;
import eu.darkcube.system.pserver.cloudnet.database.PServerDatabase;
import eu.darkcube.system.pserver.common.UniqueId;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PServerModule extends DriverModule {

    public static final String PLUGIN_NAME = new File(PServerModule.class
            .getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .getPath()).getName();
    private static PServerModule instance;
    public Listener listener;
    public String sqlDatabase;
    private PServerConfiguration configuration;
    private List<Pattern> compiledDeploymentExclusions = null;

    public PServerModule() {
        PServerModule.instance = this;
    }

    public static PServerModule getInstance() {
        return PServerModule.instance;
    }

    public static Collection<UniqueId> usedPServerIDs() {
        return DatabaseProvider.get("pserver").cast(PServerDatabase.class).getUsedPServerIDs();
    }

    @ModuleTask(order = Byte.MAX_VALUE, lifecycle = ModuleLifeCycle.LOADED) public void loadConfig() {
        configuration = this.readConfig(PServerConfiguration.class, () -> new PServerConfiguration("h2", defaultExclusions()), DocumentFactory.json());
        this.sqlDatabase = configuration.database();
    }

    private Set<String> defaultExclusions() {
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream("default_deployment_exclusions.txt");
            if (in == null) return Collections.emptySet();
            String data = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            String[] lines = data.split("\\n");
            in.close();
            return Set.of(lines);
        } catch (Throwable t) {
            t.printStackTrace();
            return Collections.emptySet();
        }
    }

    @ModuleTask(order = Byte.MAX_VALUE, lifecycle = ModuleLifeCycle.STARTED) public void load() {
        ClassLoaderFixRelocation.load(this);
    }

    private void invalidateCompiledDeploymentExclusions() {
        compiledDeploymentExclusions = null;
    }

    public List<Pattern> compiledDeploymentExclusions() {
        if (compiledDeploymentExclusions == null) {
            compiledDeploymentExclusions = configuration.deploymentExclusions().stream().map(Pattern::compile).collect(Collectors.toList());
        }
        return compiledDeploymentExclusions;
    }

    public boolean addDeploymentExclusion(String exclusion) {
        boolean changed = configuration.deploymentExclusions().add(exclusion);
        if (changed) {
            saveConfig();
            invalidateCompiledDeploymentExclusions();
        }
        return changed;
    }

    public boolean removeDeploymentExclusion(String exclusion) {
        boolean changed = configuration.deploymentExclusions().remove(exclusion);
        if (changed) {
            saveConfig();
            invalidateCompiledDeploymentExclusions();
        }
        return changed;
    }

    private void saveConfig() {
        writeConfig(Document.newJsonDocument().appendTree(configuration));
    }
}
