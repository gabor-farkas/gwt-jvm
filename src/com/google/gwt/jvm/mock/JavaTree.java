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

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Tree;

/**
 * A mock Tree. This mock handles calls to the native method shouldTreeDelegateFocusToElement
 * from {@link Tree}, and delegates the rest of the calls to the Tree class. 
 */
public class JavaTree extends JavaUIObject {

  protected JavaTree(final Tree delegate) {
    super(delegate);
  }
  
  static boolean shouldTreeDelegateFocusToElement(Element elem) {
    String name = elem.getNodeName(); 
    return ((name.equals("SELECT")) || 
        (name.equals("TESXTAREA")) ||
        (name.equals("OPTION")) ||
        (name.equals("BUTTON")) ||
        (name.equals("LABEL")));    
  }
}
