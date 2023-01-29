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
 * does not allow blocking methods execution.
 *
 * <p>
 * If a given executor allows blocking calls, {@link BlockingExecutor} should be used.
 *
 * <p>
 * Example 1 (Kotlin coroutines):
 * <pre><code>
 *  class NonBlockingExampleService {
 *      val dispatcher: @NonBlockingExecutor CoroutineContext
 *          get() { ... }
 *
 *      suspend fun foo() {
 *          val result = withContext(dispatcher) {
 *              blockingBuzz() // IDE warning: `Possibly blocking call in non-blocking context`
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
 *  class NonBlockingExampleService {
 *      private static final @NonBlockingExecutor Scheduler operationsScheduler =
 *              Schedulers.newParallel("parallel");
 *
 *      public Flux&lt;String&gt; foo(Flux&lt;String&gt; urls) {
 *          return urls.publishOn(operationsScheduler)
 *                  .filter(url -&gt; blockingBuzz(url) != null);  // IDE warning: `Possibly blocking call in non-blocking context`
 *      }
 *
 *      &#064;Blocking
 *      private String blockingBuzz(String url) { ... }
 *  }
 * </code></pre>
 *  @see Blocking
 *  @see NonBlocking
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.TYPE_USE})
public @interface NonBlockingExecutor {
}
