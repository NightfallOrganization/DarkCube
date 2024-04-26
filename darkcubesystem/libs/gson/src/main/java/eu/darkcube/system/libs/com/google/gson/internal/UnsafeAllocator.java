/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.google.gson.internal;

import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Do sneaky things to allocate objects without invoking their constructors.
 *
 * @author Joel Leitch
 * @author Jesse Wilson
 */
public abstract class UnsafeAllocator {
  public abstract <T> T newInstance(Class<T> c) throws Exception;

  /**
   * Asserts that the class is instantiable. This check should have already occurred
   * in {@link ConstructorConstructor}; this check here acts as safeguard since trying
   * to use Unsafe for non-instantiable classes might crash the JVM on some devices.
   */
  private static void assertInstantiable(Class<?> c) {
    String exceptionMessage = ConstructorConstructor.checkInstantiable(c);
    if (exceptionMessage != null) {
      throw new AssertionError("UnsafeAllocator is used for non-instantiable type: " + exceptionMessage);
    }
  }

  public static final UnsafeAllocator INSTANCE = create();

  private static UnsafeAllocator create() {
    // try JVM
    // public class Unsafe {
    //   public Object allocateInstance(Class<?> type);
    // }
    try {
      Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
      Field f = unsafeClass.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      final Object unsafe = f.get(null);
      final Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
      return new UnsafeAllocator() {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T newInstance(Class<T> c) throws Exception {
          assertInstantiable(c);
          return (T) allocateInstance.invoke(unsafe, c);
        }
      };
    } catch (Exception ignored) {
    }

    // try dalvikvm, post-gingerbread
    // public class ObjectStreamClass {
    //   private static native int getConstructorId(Class<?> c);
    //   private static native Object newInstance(Class<?> instantiationClass, int methodId);
    // }
    try {
      Method getConstructorId = ObjectStreamClass.class
          .getDeclaredMethod("getConstructorId", Class.class);
      getConstructorId.setAccessible(true);
      final int constructorId = (Integer) getConstructorId.invoke(null, Object.class);
      final Method newInstance = ObjectStreamClass.class
          .getDeclaredMethod("newInstance", Class.class, int.class);
      newInstance.setAccessible(true);
      return new UnsafeAllocator() {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T newInstance(Class<T> c) throws Exception {
          assertInstantiable(c);
          return (T) newInstance.invoke(null, c, constructorId);
        }
      };
    } catch (Exception ignored) {
    }

    // try dalvikvm, pre-gingerbread
    // public class ObjectInputStream {
    //   private static native Object newInstance(
    //     Class<?> instantiationClass, Class<?> constructorClass);
    // }
    try {
      final Method newInstance = ObjectInputStream.class
          .getDeclaredMethod("newInstance", Class.class, Class.class);
      newInstance.setAccessible(true);
      return new UnsafeAllocator() {
        @Override
        @SuppressWarnings("unchecked")
        public <T> T newInstance(Class<T> c) throws Exception {
          assertInstantiable(c);
          return (T) newInstance.invoke(null, c, Object.class);
        }
      };
    } catch (Exception ignored) {
    }

    // give up
    return new UnsafeAllocator() {
      @Override
      public <T> T newInstance(Class<T> c) {
        throw new UnsupportedOperationException("Cannot allocate " + c + ". Usage of JDK sun.misc.Unsafe is enabled, "
            + "but it could not be used. Make sure your runtime is configured correctly.");
      }
    };
  }
}
