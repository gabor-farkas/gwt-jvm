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

import com.google.gwt.core.client.GWT;

/**
 */
public class JavaGWTTestCaseTest extends JavaGWTTestCase {

  static class Mark {
  }

  public void testItShouldAllowMockingOfGWTcreateThroughClass() throws Exception {
    setGWTcreateClass(Object.class, Mark.class);
    assertSame(GWT.create(Object.class).getClass(), Mark.class);
    assertNotSame(GWT.create(Object.class), GWT.create(Object.class));
  }

  public void testItShouldAllowMockingOfGWTcreateThroughInstance() throws Exception {
    Object instance = new Object();
    setGWTcreateInstance(Object.class, instance);
    assertSame(instance, GWT.create(Object.class));
  }

}
