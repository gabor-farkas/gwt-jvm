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

public class RegressionTest extends JavaGWTTestCase {

  static class BaseClass {

    public native boolean nativeMethod(String s) /*- {
      return true;
    } -*/;

    public boolean nonNativeMethod() {
      return nativeMethod("testing");
    }
  }

  static class ChildClass extends BaseClass {
    public boolean anotherNonNativeMethod() {
      return nativeMethod("boo");
    }
  }

  static class BaseClassDelegate {
    public BaseClassDelegate(BaseClass baseClass) {}

    public boolean nativeMethod(String s) {
      return true;
    }
  }

  public void testItShouldDelegateToTheRightParentClass() throws Exception {
    setGWTDelegate(BaseClass.class, BaseClassDelegate.class);

    BaseClass baseClass = new BaseClass();
    assertTrue(baseClass.nonNativeMethod());

    ChildClass instance = new ChildClass();
    assertTrue(instance.anotherNonNativeMethod());
    assertTrue(instance.nonNativeMethod());
  }
}
