/* Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gwt.jvm.mock;

import com.google.gwt.user.client.Timer;

import java.util.PriorityQueue;

/**
 * JavaTimer is a mock implementation of com.google.gwt.user.client.Timer.
 * Rather than using a system timer, JavaTimer uses a priority queue of tasks
 * which are executed sequentially when time is advanced.
 * 
 * Time is advanced manually by calling the static advanceTimeBy* functions.
 */
public class JavaTimer implements Comparable<JavaTimer> {
  // 'current time' in ms
  private static long now = 0;
  
  protected int id;
  protected Timer gwtTimer;
  protected long triggerTime;
  protected boolean repeats;
  // For timer tasks which repeat, the period is the time between calls.
  protected long period = -1;

  protected static int nextId = 0;
  
  public JavaTimer(Timer gwtTimer, boolean repeats, int delay) {
    if (delay < 0) {
      throw new RuntimeException("Negative delay when creating timer");
    }
    id = nextId++;
    this.repeats = repeats;
    this.gwtTimer = gwtTimer;
    triggerTime = now + delay;
    if (repeats) {
      period = delay;
    }
  }
  
  public static long currentTimeMillis() {
    return now;
  }
  
  @Override
  public int compareTo(JavaTimer o) {
    return (int) (triggerTime - o.triggerTime);
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
    JavaTimer other = (JavaTimer) obj;
    if (id != other.id) { return false; }
    return true;
  }

  void run() {
    gwtTimer.run();
  }
  
  static final PriorityQueue<JavaTimer> tasks = new PriorityQueue<JavaTimer>();
  
  protected static void removeTimerWithId(int id) {
    // Unfortunately, there's no nice way to do this.
    JavaTimer[] timers = tasks.toArray(new JavaTimer[0]);
    for (JavaTimer timer : timers) {
      if (timer.id == id) {
        tasks.remove(timer);
      }
    }
  }
  
  public static void clearInterval(int id) {
    removeTimerWithId(id);
  }

  public static void clearTimeout(int id) {
    removeTimerWithId(id);
  }

  public static int createInterval(final Timer gwtTimer, int period) {
    JavaTimer timer = new JavaTimer(gwtTimer, true, period);
    tasks.add(timer);
    return timer.id;
  }

  public static int createTimeout(final Timer gwtTimer, int delay) {
    JavaTimer timer = new JavaTimer(gwtTimer, false, delay);
    tasks.add(timer);
    return timer.id;
  }

  /**
   * Keep track of whether we're currently executing a task, to make sure
   * no task inadvertently advances time and starts another task.
   */
  private static boolean taskRunning = false;

  protected static void checkTaskNotRunning() {
    if (taskRunning) {
      throw new IllegalStateException(
          "advanceTime cannot be called from inside a timer task");
    }    
  }
  
  /**
   * Advance time until either the next event triggers. If the event queue is
   * empty, no time will pass.
   * 
   * @return Amount of time which actually passed.
   */
  public static long advanceTimeUntilNextEvent() {
    checkTaskNotRunning();
    
    if (tasks.isEmpty()) {
      return 0;
    }

    long oldNow = now;
    JavaTimer nextTask = tasks.remove();
    assert(nextTask.triggerTime >= now);
    now = nextTask.triggerTime;
    if (nextTask.repeats) {
      nextTask.triggerTime += nextTask.period;
      tasks.add(nextTask);
    }
    
    taskRunning = true;
    try {
      nextTask.run();      
    } finally {
      taskRunning = false;      
    }
    
    return now - oldNow;
  }

  /**
   * Advance time until either the next event triggers, or maxDelay
   * time has passed.
   * 
   * @param maxDelay The maximum time to wait for an event to trigger. Pass
   * Long.MAX_VALUE to indicate you are willing to wait forever.
   * 
   * Note that this function executes in constant time regardless
   * of the value passed into this function.
   * 
   * @return Amount of time which actually passed. 0 <= return value <= maxDelay
   * If no events are scheduled, this will return maxDelay.
   */
  public static long advanceTimeUntilNextEvent(long maxDelay) {
    checkTaskNotRunning();

    if (maxDelay < 0) {
      throw new IllegalArgumentException(
          "maxDelay must be positive");
    }
    
    JavaTimer nextTask = tasks.peek();
    
    if (nextTask != null && (nextTask.triggerTime - now <= maxDelay)) {
      return advanceTimeUntilNextEvent();
    } else {
      now += maxDelay;
      return maxDelay;
    }
  }

  private static boolean taskScheduledNow() {
    JavaTimer nextTask = tasks.peek();
    return nextTask != null && nextTask.triggerTime == now;
  }
  
  /**
   * Advance time by the specified amount, triggering events as necessary.
   * 
   * @param time Number of milliseconds to advance time by. Must be >= 0. 
   */
  public static void advanceTimeBy(long time) {
    checkTaskNotRunning();
    
    if (time < 0) {
      throw new IllegalArgumentException(
          "Cannot advance the clock by a negative amount of time");
    }
    
    long finalTime = now + time;
    
    while (time > 0 || taskScheduledNow()) {
      time -= advanceTimeUntilNextEvent(time);
    }
    
    assert(now == finalTime);
  }
  
  /**
   * Query if the timer has any scheduled tasks
   * @return true if the set of tasks is not empty, otherwise false.
   */
  public static boolean hasTasks() {
    return !tasks.isEmpty();
  }
  
  public static void reset() {
    checkTaskNotRunning();
    now = 0;
    tasks.clear();
  }
}
