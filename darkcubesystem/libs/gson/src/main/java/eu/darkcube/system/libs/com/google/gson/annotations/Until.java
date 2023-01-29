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
 * An annotation that indicates the version number until a member or a type should be present.
 * Basically, if Gson is created with a version number that is equal to or exceeds the value
 * stored in the {@code Until} annotation then the field will be ignored from the JSON output.
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
 *   &#64;Until(1.1) private String emailAddress;
 *   &#64;Until(1.1) private String password;
 * }
 * </pre>
 *
 * <p>If you created Gson with {@code new Gson()}, the {@code toJson()} and {@code fromJson()}
 * methods will use all the fields for serialization and deserialization. However, if you created
 * Gson with {@code Gson gson = new GsonBuilder().setVersion(1.2).create()} then the
 * {@code toJson()} and {@code fromJson()} methods of Gson will exclude the {@code emailAddress}
 * and {@code password} fields from the example above, because the version number passed to the
 * GsonBuilder, {@code 1.2}, exceeds the version number set on the {@code Until} annotation,
 * {@code 1.1}, for those fields.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 * @see GsonBuilder#setVersion(double)
 * @see Since
 * @since 1.3
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Until {

  /**
   * The value indicating a version number until this member or type should be be included.
   * The number is exclusive; annotated elements will be included if {@code gsonVersion < value}.
   */
  double value();
}
