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

import static com.google.gwt.jvm.asm.Type.invokeMethod;

import com.google.gwt.jvm.mock.JavaUIObject;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 */
public class GwtNativeDispatchTest extends JavaGWTTestCase {

  static class JavaMyWidget extends JavaUIObject {
    public JavaMyWidget(UIObject object) {
      super(object);
    }

    String greet(String name) {
      return "Hello " + name;
    }
  }

  abstract static class MyAbstractWidget extends Widget {
    native String greet(String name);
  }

  static class MyWidget extends MyAbstractWidget {

  }

  public void testBugWhereWrongClassDelegateGetsAssociatedWithInstance() throws Exception {
    setGWTDelegate(MyAbstractWidget.class, JavaMyWidget.class);
    MyWidget widget = new MyWidget();

    //first call UiWidget method to cause wrong association to JavaUIObject
    assertEquals(0.0d, invokeMethod(widget, "extractLengthValue", ""));

    // now call a sub-class method to expose wrong association to JavaUIObject
    assertEquals("Hello Misko", widget.greet("Misko"));
  }

}
