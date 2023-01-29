/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.google.gson.annotations;

import eu.darkcube.system.libs.com.google.gson.GsonBuilder;
import eu.darkcube.system.libs.com.google.gson.Gson;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that indicates the version number since a member or a type has been present.
 * This annotation is useful to manage versioning of your JSON classes for a web-service.
 *
 * <p>
 * This annotation has no effect unless you build {@link Gson} with a
 * {@code GsonBuilder} and invoke the {@link GsonBuilder#setVersion(double)} method.
 *
 * <p>Here is an example of how this annotation is meant to be used:</p>
 * <pre>
 * public class User {
 *   private String firstName;
 *   private String lastName;
 *   &#64;Since(1.0) private String emailAddress;
 *   &#64;Since(1.0) private String password;
 *   &#64;Since(1.1) private Address address;
 * }
 * </pre>
 *
 * <p>If you created Gson with {@code new Gson()}, the {@code toJson()} and {@code fromJson()}
 * methods will use all the fields for serialization and deserialization. However, if you created
 * Gson with {@code Gson gson = new GsonBuilder().setVersion(1.0).create()} then the
 * {@code toJson()} and {@code fromJson()} methods of Gson will exclude the {@code address} field
 * since it's version number is set to {@code 1.1}.</p>
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 * @see GsonBuilder#setVersion(double)
 * @see Until
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Since {
  /**
   * The value indicating a version number since this member or type has been present.
   * The number is inclusive; annotated elements will be included if {@code gsonVersion >= value}.
   */
  double value();
}
