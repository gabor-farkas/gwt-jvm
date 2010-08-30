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

package com.google.gwt.jvm;

import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * In order to run your GWT code in junit test your test needs to extend from
 * this class instead of junit.framework.TestCase
 */
public class JavaGWTTestCase extends TestCase {
  GwtBrowserEmulator browserEmulator = new GwtBrowserEmulator();
  
  protected GwtBrowserEmulator getBrowserEmulator() {
    // This makes it possible for tests to reset the browser emulator manually.
    return browserEmulator;
  }
  
  @Override
  public void run(final TestResult result) {
    try {
      beforeEachTest();
      super.run(result);
    } finally {
      afterEachTest();
    }
  }
  
  protected void beforeEachTest() {
    browserEmulator.reset();
  }

  private void afterEachTest() {
    // We need to make sure added classes are removed between tests.
    browserEmulator.clearDispatcher();
  }

  public void setGWTcreateClass(final Class<?> askClass, final Class<?> returnClass) {
    browserEmulator.setGWTcreateClass(askClass, returnClass);
  }

  public void setGWTcreateClass(final Class<?> askClass, final String returnClass) {
    browserEmulator.setGWTcreateClass(askClass, returnClass);
  }

  public void setGWTcreateClass(final String askClass, final String returnClass) {
    browserEmulator.setGWTcreateClass(askClass, returnClass);
  }

  public void setGWTcreateClass(final String askClass, final Class<?> returnClass) {
    browserEmulator.setGWTcreateClass(askClass, returnClass);
  }

  public <T> void setGWTcreateInstance(final Class<T> clazz, final T instance) {
    browserEmulator.setGWTcreateInstance(clazz, instance);
  }

  public void setGWTDelegate(final Class<?> nativeClass, final Class<?> mockClass) {
    browserEmulator.setGWTDelegate(nativeClass, mockClass);
  }
}