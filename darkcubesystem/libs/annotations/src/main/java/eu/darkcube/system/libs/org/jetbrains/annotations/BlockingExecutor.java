/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.org.jetbrains.annotations;

import java.lang.annotation.*;

/**
 * Indicates that the annotated executor (CoroutineContext, Scheduler)
 * allows blocking methods execution.
 * <p>
 * If a given executor does not allow blocking calls, {@link NonBlockingExecutor} should be used.
 *
 * <p>
 * Example 1 (Kotlin coroutines):
 * <pre><code>
 *  class BlockingExampleService {
 *      val dispatcher: @BlockingExecutor CoroutineContext
 *          get() { ... }
 *
 *      suspend fun foo() {
 *          val result = withContext(dispatcher) {
 *              blockingBuzz() // no IDE warning
 *          }
 *      }
 *
 *      &#064;Blocking fun blockingBuzz() { ... }
 *  }
 * </code></pre>
 *
 * <p>
 * Example 2 (Java with Reactor framework):
 * <pre><code>
 * class BlockingExampleService {
 *     private static final @BlockingExecutor Scheduler blockingScheduler =
 *             Schedulers.newBoundedElastic(4, 10, "executor");
 *
 *     public Flux&lt;String&gt; foo(Flux&lt;String&gt; urls) {
 *         return urls.publishOn(blockingScheduler)
 *                 .map(url -&gt; blockingBuzz(url));  // no IDE warning
 *     }
 *
 *     &#064;Blocking
 *     private String blockingBuzz(String url) { ... }
 * }
 * </code></pre>
 *
 * @see Blocking
 * @see NonBlocking
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.TYPE_USE})
public @interface BlockingExecutor {
}
