/*
 * Copyright 2019 OpenFeign Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package feign.support;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class AuditingExecutor implements Executor {

  private Set<Long> threads = new HashSet<>();
  private AtomicInteger executionCount = new AtomicInteger();
  private Executor delegate;

  public AuditingExecutor() {
    super();
  }

  public AuditingExecutor(Executor delegate) {
    this.delegate = delegate;
  }

  @Override
  public void execute(Runnable command) {
    executionCount.incrementAndGet();
    this.threads.add(Thread.currentThread().getId());

    /* run on the current thread */
    if (delegate != null) {
      delegate.execute(command);
    } else {
      command.run();
    }
  }

  public Set<Long> getThreads() {
    return this.threads;
  }

  public int getExecutionCount() {
    return this.executionCount.intValue();
  }
}
