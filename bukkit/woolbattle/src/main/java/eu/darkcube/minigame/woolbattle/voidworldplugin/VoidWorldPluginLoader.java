package eu.darkcube.minigame.woolbattle.voidworldplugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.apache.commons.lang.Validate;
import org.bukkit.Server;
import org.bukkit.Warning;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.UnknownDependencyException;
import co.aikar.timings.TimedEventExecutor;

public class VoidWorldPluginLoader implements PluginLoader {

  public static final File file = new File("WoolBattleVoidWorldPluginInternal.plugin");
  private static final Pattern[] patterns =
      new Pattern[] {Pattern.compile("^WoolBattleVoidWorldPluginInternal\\.plugin$")};

  private Server server;

  public VoidWorldPluginLoader(Server server) {
    this.server = server;
  }

  @Override
  public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(
      Listener listener, Plugin plugin) {
    Validate.notNull(plugin, "Plugin can not be null");
    Validate.notNull(listener, "Listener can not be null");
    this.server.getPluginManager().useTimings();
    final Map<Class<? extends Event>, Set<RegisteredListener>> ret = new HashMap<>();
    Set<Method> methods;
    try {
      final Method[] publicMethods = listener.getClass().getMethods();
      final Method[] privateMethods = listener.getClass().getDeclaredMethods();
      methods = new HashSet<>(publicMethods.length + privateMethods.length, 1.0f);
      Method[] array;
      for (int length = (array = publicMethods).length, i = 0; i < length; ++i) {
        final Method method = array[i];
        methods.add(method);
      }
      Method[] array2;
      for (int length2 = (array2 = privateMethods).length, j = 0; j < length2; ++j) {
        final Method method = array2[j];
        methods.add(method);
      }
    } catch (NoClassDefFoundError e) {
      plugin.getLogger()
          .severe("Plugin " + plugin.getDescription().getFullName()
              + " has failed to register events for " + listener.getClass() + " because "
              + e.getMessage() + " does not exist.");
      return ret;
    }
    for (final Method method2 : methods) {
      final EventHandler eh = method2.getAnnotation(EventHandler.class);
      if (eh == null) {
        continue;
      }
      if (method2.isBridge()) {
        continue;
      }
      if (method2.isSynthetic()) {
        continue;
      }
      final Class<?> checkClass;
      if (method2.getParameterTypes().length != 1
          || !Event.class.isAssignableFrom(checkClass = method2.getParameterTypes()[0])) {
        plugin.getLogger()
            .severe(String.valueOf(plugin.getDescription().getFullName())
                + " attempted to register an invalid EventHandler method signature \""
                + method2.toGenericString() + "\" in " + listener.getClass());
      } else {
        final Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
        method2.setAccessible(true);
        Set<RegisteredListener> eventSet = ret.get(eventClass);
        if (eventSet == null) {
          eventSet = new HashSet<>();
          ret.put(eventClass, eventSet);
        }
        Class<?> clazz = eventClass;
        while (Event.class.isAssignableFrom(clazz)) {
          if (clazz.getAnnotation(Deprecated.class) != null) {
            final Warning warning = clazz.getAnnotation(Warning.class);
            final Warning.WarningState warningState = this.server.getWarningState();
            if (!warningState.printFor(warning)) {
              break;
            }
            plugin.getLogger().log(Level.WARNING, String.format(
                "\"%s\" has registered a listener for %s on method \"%s\", but the event is Deprecated. \"%s\"; please notify the authors %s.",
                plugin.getDescription().getFullName(), clazz.getName(), method2.toGenericString(),
                (warning != null && warning.reason().length() != 0) ? warning.reason()
                    : "Server performance will be affected",
                Arrays.toString(plugin.getDescription().getAuthors().toArray())),
                (warningState == Warning.WarningState.ON) ? new AuthorNagException((String) null)
                    : null);
            break;
          }
          clazz = clazz.getSuperclass();
        }
        final EventExecutor executor = new TimedEventExecutor(new EventExecutor() {
          @Override
          public void execute(final Listener listener, final Event event) throws EventException {
            try {
              if (!eventClass.isAssignableFrom(event.getClass())) {
                return;
              }
              method2.invoke(listener, event);
            } catch (InvocationTargetException ex) {
              throw new EventException(ex.getCause());
            } catch (Throwable t) {
              throw new EventException(t);
            }
          }
        }, plugin, method2, eventClass);
        eventSet.add(new RegisteredListener(listener, executor, eh.priority(), plugin,
            eh.ignoreCancelled()));
      }
    }
    return ret;
  }

  @Override
  public void disablePlugin(Plugin plugin) {
    if (plugin.isEnabled()) {
      ((VoidWorldPlugin) plugin).setEnabled(false);
    }
  }

  @Override
  public void enablePlugin(Plugin plugin) {
    if (!plugin.isEnabled()) {
      ((VoidWorldPlugin) plugin).setEnabled(true);
    }
  }

  @Override
  public Plugin loadPlugin(File file) throws InvalidPluginException, UnknownDependencyException {
    if (!file.equals(VoidWorldPluginLoader.file)) {
      throw new InvalidPluginException("Stop hacking me :(");
    }
    return new VoidWorldPlugin(this.server, this);
  }

  @Override
  public PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
    return VoidWorldPluginDescription.get();
  }

  @Override
  public Pattern[] getPluginFileFilters() {
    return VoidWorldPluginLoader.patterns;
  }

}
