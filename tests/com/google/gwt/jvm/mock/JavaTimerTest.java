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

import com.google.gwt.core.client.Duration;
import com.google.gwt.jvm.JavaGWTTestCase;
import com.google.gwt.user.client.Timer;

/**
 * Tests for JavaTimer
 */
public class JavaTimerTest extends JavaGWTTestCase {
  class Flag {
    public int value = 0;
    
    public Timer advanceTimer = new Timer() {
      @Override
      public void run() {
        value++;
      }
    };
  }
  
  public void testScheduledTimerIsCalled() {
    final Flag flag = new Flag();
    flag.advanceTimer.schedule(300);
    
    JavaTimer.advanceTimeBy(299);
    assertEquals(0, flag.value);
    JavaTimer.advanceTimeBy(1);
    assertEquals(1, flag.value);
  }
  

  public void testTimeIsAdvanced() {
    final Flag flag = new Flag();
    
    double startingTime = Duration.currentTimeMillis();
    flag.advanceTimer.schedule(300);
    assertEquals(startingTime, Duration.currentTimeMillis(), 0.1);
    JavaTimer.advanceTimeBy(150);
    assertEquals(startingTime + 150, Duration.currentTimeMillis(), 0.1);
    JavaTimer.advanceTimeBy(250);    
    assertEquals(startingTime + 150 + 250, Duration.currentTimeMillis(), 0.1);
  }

  public void testRepeatedTimerIsCalled() {
    final Flag flag = new Flag();
    flag.advanceTimer.scheduleRepeating(200);
    
    JavaTimer.advanceTimeBy(150);
    assertEquals(0, flag.value);
    JavaTimer.advanceTimeBy(400);
    assertEquals(2, flag.value);
    
    flag.advanceTimer.cancel();
    JavaTimer.advanceTimeBy(400);
    assertEquals(2, flag.value);
  }
  
  public void testTimerCanCancelItself() {
    final Flag flag = new Flag();
    
    Timer timer = new Timer() {
      @Override
      public void run() {
        cancel();
        flag.value++;
      }
    };
    
    timer.scheduleRepeating(200);
    JavaTimer.advanceTimeBy(250);
    assertEquals(1, flag.value);
    JavaTimer.advanceTimeBy(400);
    assertEquals(1, flag.value);
  }
  
  public void testNegativeTime() {
    try {
      JavaTimer.advanceTimeBy(-1);
      fail("advanceTimeBy(-1) should throw an exception");
    } catch (IllegalArgumentException e) {
      // This exception is expected.
    }
    
    final Flag flag = new Flag();
    try {
      flag.advanceTimer.schedule(-1);
      fail("schedule(-1) should throw an exception");
    } catch (IllegalArgumentException e) {
      // This exception is expected.
    }
  }
  
  public void testSameTime() {
    final Flag a = new Flag();
    final Flag b = new Flag();
    
    a.advanceTimer.schedule(10);
    b.advanceTimer.schedule(10);
    
    JavaTimer.advanceTimeBy(9);
    assertEquals(0, a.value);
    assertEquals(0, b.value);

    JavaTimer.advanceTimeBy(1);
    assertEquals(1, a.value);
    assertEquals(1, b.value);
  }
  
  public void testMaxValueAndReturnValue() {
    JavaTimer.advanceTimeUntilNextEvent();

    final Flag flag = new Flag();
    flag.advanceTimer.schedule(100000);
    
    long time = JavaTimer.advanceTimeUntilNextEvent();
    assertEquals(1, flag.value);
    assertEquals(time, 100000);
    
    // should do nothing
    time = JavaTimer.advanceTimeUntilNextEvent();
    assertEquals(time, 0);
  }
  
  public void testThrowsIfTimerTaskAdvancesTime() {
    Timer timer = new Timer() {
      @Override
      public void run() {
        JavaTimer.advanceTimeBy(0);
      }
    };
    timer.schedule(10);
    
    try {
      JavaTimer.advanceTimeUntilNextEvent();
      fail("Timer task advancing time should throw an exception");
    } catch (IllegalStateException e) {
      // This exception is expected.
    }
  }
}
