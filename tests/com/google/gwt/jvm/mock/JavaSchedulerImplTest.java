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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.jvm.JavaGWTTestCase;

/**
 * Tests for JavaSchedulerImpl
 */
public class JavaSchedulerImplTest extends JavaGWTTestCase{
  String log = "";
  
  ScheduledCommand appendXToLog = new ScheduledCommand() {
    @Override
    public void execute() {
      log += "x";
    }
  };

  ScheduledCommand appendYToLog = new ScheduledCommand() {
    @Override
    public void execute() {
      log += "y";
    }
  };
  
  public void testDeferredRuns() {
    log = "";
    
    Scheduler.get().scheduleDeferred(appendXToLog);
    Scheduler.get().scheduleDeferred(appendYToLog);
    
    assertEquals("", log);
    JavaSchedulerImpl.get().runAndFlushScheduler(new ScheduledCommand() {
      @Override
      public void execute() {
        assertEquals("", log);
      }
    });
    
    assertEquals("xy", log);
  }
  
  public void testDeferredIsRunOnlyOnce() {
    log = "";

    Scheduler.get().scheduleDeferred(appendXToLog);
    
    JavaSchedulerImpl.get().runAndFlushScheduler(new ScheduledCommand() {
      @Override
      public void execute() {
        assertEquals("", log);
      }
    });

    JavaSchedulerImpl.get().runAndFlushScheduler(new ScheduledCommand() {
      @Override
      public void execute() {
        assertEquals("x", log);
      }
    });
    
    assertEquals("x", log);
  }
  
  class TestException extends RuntimeException {}
  
  public void testFinallyRuns() {
    log = "";
    
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      @Override
      public void execute() {
        throw new TestException();
      }
    });
    
    Scheduler.get().scheduleFinally(appendXToLog);
    
    try {
      JavaSchedulerImpl.get().runAndFlushScheduler(new ScheduledCommand() {
        @Override
        public void execute() {
          assertEquals("", log);
        }
      });
      
      fail("runAndFlushScheduler should have thrown an exception");
    } catch (TestException e) {
      // This should happen.
    }
    
    assertEquals("x", log);
  }
}
