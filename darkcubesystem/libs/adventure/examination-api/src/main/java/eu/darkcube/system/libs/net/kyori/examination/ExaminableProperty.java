/*
 * Copyright (c) 2018-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.libs.net.kyori.examination;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

/**
 * An examinable property.
 *
 * @since 1.0.0
 */
public abstract class ExaminableProperty {
  private ExaminableProperty() {
  }

  /**
   * Gets the name.
   *
   * @return the name
   * @since 1.0.0
   */
  public abstract @NotNull String name();

  /**
   * Gets the value.
   *
   * @param examiner the examiner
   * @param <R> the result type
   * @return the value
   * @since 1.0.0
   */
  public abstract <R> @NotNull R examine(final @NotNull Examiner<? extends R> examiner);

  @Override
  public String toString() {
    return "ExaminableProperty{" + this.name() + "}";
  }

  /**
   * Creates a property.
   *
   * @param name the name
   * @param value the value
   * @return the property
   * @since 1.0.0
   */
  @SuppressWarnings("DuplicatedCode")
  public static @NotNull ExaminableProperty of(final @NotNull String name, final @Nullable Object value) {
    return new ExaminableProperty() {
      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public <R> @NotNull R examine(final @NotNull Examiner<? extends R> examiner) {
        return examiner.examine(value);
      }
    };
  }

  /**
   * Creates a property.
   *
   * @param name the name
   * @param value the value
   * @return the property
   * @since 1.0.0
   */
  @SuppressWarnings("DuplicatedCode")
  public static @NotNull ExaminableProperty of(final @NotNull String name, final @Nullable String value) {
    return new ExaminableProperty() {
      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public <R> @NotNull R examine(final @NotNull Examiner<? extends R> examiner) {
        return examiner.examine(value);
      }
    };
  }

  /**
   * Creates a property.
   *
   * @param name the name
   * @param value the value
   * @return the property
   * @since 1.0.0
   */
  @SuppressWarnings("DuplicatedCode")
  public static @NotNull ExaminableProperty of(final @NotNull String name, final boolean value) {
    return new ExaminableProperty() {
      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public <R> @NotNull R examine(final @NotNull Examiner<? extends R> examiner) {
        return examiner.examine(value);
      }
    };
  }

  /**
   * Creates a property.
   *
   * @param name the name
   * @param value the value
   * @return the property
   * @since 1.0.0
   */
  @SuppressWarnings("DuplicatedCode")
  public static @NotNull ExaminableProperty of(final @NotNull String name, final boolean[] value) {
    return new ExaminableProperty() {
      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public <R> @NotNull R examine(final @NotNull Examiner<? extends R> examiner) {
        return examiner.examine(value);
      }
    };
  }

  /**
   * Creates a property.
   *
   * @param name the name
   * @param value the value
   * @return the property
   * @since 1.0.0
   */
  @SuppressWarnings("DuplicatedCode")
  public static @NotNull ExaminableProperty of(final @NotNull String name, final byte value) {
    return new ExaminableProperty() {
      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public <R> @NotNull R examine(final @NotNull Examiner<? extends R> examiner) {
        return examiner.examine(value);
      }
    };
  }

  /**
   * Creates a property.
   *
   * @param name the name
   * @param value the value
   * @return the property
   * @since 1.0.0
   */
  @SuppressWarnings("DuplicatedCode")
  public static @NotNull ExaminableProperty of(final @NotNull String name, final byte[] value) {
    return new ExaminableProperty() {
      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public <R> @NotNull R examine(final @NotNull Examiner<? extends R> examiner) {
        return examiner.examine(value);
      }
    };
  }

  /**
   * Creates a property.
   *
   * @param name the name
   * @param value the value
   * @return the property
   * @since 1.0.0
   */
  @SuppressWarnings("DuplicatedCode")
  public static @NotNull ExaminableProperty of(final @NotNull String name, final char value) {
    return new ExaminableProperty() {
      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public <R> @NotNull R examine(final @NotNull Examiner<? extends R> examiner) {
        return examiner.examine(value);
      }
    };
  }

  /**
   * Creates a property.
   *
   * @param name the name
   * @param value the value
   * @return the property
   * @since 1.0.0
   */
  @SuppressWarnings("DuplicatedCode")
  public static @NotNull ExaminableProperty of(final @NotNull String name, final char[] value) {
    return new ExaminableProperty() {
      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public <R> @NotNull R examine(final @NotNull Examiner<? extends R> examiner) {
        return examiner.examine(value);
      }
    };
  }

  /**
   * Creates a property.
   *
   * @param name the name
   * @param value the value
   * @return the property
   * @since 1.0.0
   */
  @SuppressWarnings("DuplicatedCode")
  public static @NotNull ExaminableProperty of(final @NotNull String name, final double value) {
    return new ExaminableProperty() {
      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public <R> @NotNull R examine(final @NotNull Examiner<? extends R> examiner) {
        return examiner.examine(value);
      }
    };
  }

  /**
   * Creates a property.
   *
   * @param name the name
   * @param value the value
   * @return the property
   * @since 1.0.0
   */
  @SuppressWarnings("DuplicatedCode")
  public static @NotNull ExaminableProperty of(final @NotNull String name, final double[] value) {
    return new ExaminableProperty() {
      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public <R> @NotNull R examine(final @NotNull Examiner<? extends R> examiner) {
        return examiner.examine(value);
      }
    };
  }

  /**
   * Creates a property.
   *
   * @param name the name
   * @param value the value
   * @return the property
   * @since 1.0.0
   */
  @SuppressWarnings("DuplicatedCode")
  public static @NotNull ExaminableProperty of(final @NotNull String name, final float value) {
    return new ExaminableProperty() {
      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public <R> @NotNull R examine(final @NotNull Examiner<? extends R> examiner) {
        return examiner.examine(value);
      }
    };
  }

  /**
   * Creates a property.
   *
   * @param name the name
   * @param value the value
   * @return the property
   * @since 1.0.0
   */
  @SuppressWarnings("DuplicatedCode")
  public static @NotNull ExaminableProperty of(final @NotNull String name, final float[] value) {
    return new ExaminableProperty() {
      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public <R> @NotNull R examine(final @NotNull Examiner<? extends R> examiner) {
        return examiner.examine(value);
      }
    };
  }

  /**
   * Creates a property.
   *
   * @param name the name
   * @param value the value
   * @return the property
   * @since 1.0.0
   */
  @SuppressWarnings("DuplicatedCode")
  public static @NotNull ExaminableProperty of(final @NotNull String name, final int value) {
    return new ExaminableProperty() {
      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public <R> @NotNull R examine(final @NotNull Examiner<? extends R> examiner) {
        return examiner.examine(value);
      }
    };
  }

  /**
   * Creates a property.
   *
   * @param name the name
   * @param value the value
   * @return the property
   * @since 1.0.0
   */
  @SuppressWarnings("DuplicatedCode")
  public static @NotNull ExaminableProperty of(final @NotNull String name, final int[] value) {
    return new ExaminableProperty() {
      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public <R> @NotNull R examine(final @NotNull Examiner<? extends R> examiner) {
        return examiner.examine(value);
      }
    };
  }

  /**
   * Creates a property.
   *
   * @param name the name
   * @param value the value
   * @return the property
   * @since 1.0.0
   */
  @SuppressWarnings("DuplicatedCode")
  public static @NotNull ExaminableProperty of(final @NotNull String name, final long value) {
    return new ExaminableProperty() {
      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public <R> @NotNull R examine(final @NotNull Examiner<? extends R> examiner) {
        return examiner.examine(value);
      }
    };
  }

  /**
   * Creates a property.
   *
   * @param name the name
   * @param value the value
   * @return the property
   * @since 1.0.0
   */
  @SuppressWarnings("DuplicatedCode")
  public static @NotNull ExaminableProperty of(final @NotNull String name, final long[] value) {
    return new ExaminableProperty() {
      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public <R> @NotNull R examine(final @NotNull Examiner<? extends R> examiner) {
        return examiner.examine(value);
      }
    };
  }

  /**
   * Creates a property.
   *
   * @param name the name
   * @param value the value
   * @return the property
   * @since 1.0.0
   */
  @SuppressWarnings("DuplicatedCode")
  public static @NotNull ExaminableProperty of(final @NotNull String name, final short value) {
    return new ExaminableProperty() {
      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public <R> @NotNull R examine(final @NotNull Examiner<? extends R> examiner) {
        return examiner.examine(value);
      }
    };
  }

  /**
   * Creates a property.
   *
   * @param name the name
   * @param value the value
   * @return the property
   * @since 1.0.0
   */
  @SuppressWarnings("DuplicatedCode")
  public static @NotNull ExaminableProperty of(final @NotNull String name, final short[] value) {
    return new ExaminableProperty() {
      @Override
      public @NotNull String name() {
        return name;
      }

      @Override
      public <R> @NotNull R examine(final @NotNull Examiner<? extends R> examiner) {
        return examiner.examine(value);
      }
    };
  }
}
