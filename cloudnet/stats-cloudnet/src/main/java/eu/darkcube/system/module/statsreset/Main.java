/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.statsreset;

import java.util.ArrayList;
import java.util.List;

import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.driver.module.ModuleLifeCycle;
import de.dytanic.cloudnet.driver.module.ModuleTask;
import de.dytanic.cloudnet.driver.module.driver.DriverModule;
import eu.darkcube.system.module.statsreset.mysql.MySQL;
import eu.darkcube.system.stats.api.Duration;

public class Main extends DriverModule {

//   public static final String PLUGIN_NAME = "cloudnet-statsreset.jar";
   public static Main INSTANCE;
   public MySQL mysql;

   private List<Resetter> res = new ArrayList<>();

   @ModuleTask(order = 126, event = ModuleLifeCycle.LOADED)
   public void initConfig() {
      INSTANCE = this;
   }

   @ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.LOADED)
   public void load() {
      try {
         Class.forName("com.mysql.jdbc.Driver");
      } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
      }
      mysql = new MySQL("127.0.0.1", "3306", "statsapi", "statsapi",
            "rOTGpd8WeG5PH6teDfvKGykY8BUO9SAG05P4AzjYa5XCdOQz0pKvQZxodKt9PBJXZ6mMxmHk1zext6sj4M5FfY29JprrqbMJjHfs");
      mysql.connect();
   }

   @ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.STARTED)
   public void start() {
      for (Duration duration : Duration.values()) {
         Resetter resetter = new Resetter(duration);
         resetter.start();
         res.add(resetter);
      }
   }

   @ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.STOPPED)
   public void stop() {
      res.forEach(r -> r.stop());
   }

   @ModuleTask(order = Byte.MAX_VALUE, event = ModuleLifeCycle.UNLOADED)
   public void unload() {

   }

   public static final CloudNet getCloudNet() {
      return CloudNet.getInstance();
   }
}
