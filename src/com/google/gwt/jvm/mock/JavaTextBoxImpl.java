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
import com.google.gwt.user.client.ui.impl.TextBoxImpl;

import java.util.WeakHashMap;

/**
 * Mock implementation of TextBoxImpl
 */
public class JavaTextBoxImpl {
  private static WeakHashMap<Element, Integer> curserPos =
    new WeakHashMap<Element, Integer>();
  
  private static WeakHashMap<Element, Integer> selectionLength =
    new WeakHashMap<Element, Integer>();

  public JavaTextBoxImpl(TextBoxImpl textBoxImpl) {}

  public int getCursorPos(Element elem) {
    Integer pos = curserPos.get(elem);
    if (pos == null) {
      return 0;
    }
    
    return pos.intValue();
  }

  public int getSelectionLength(Element elem) {
    Integer length = selectionLength.get(elem);
    if (length == null) {
      return 0;
    }
    
    return length.intValue();
  }

  public void setSelectionRange(Element elem, int pos, int length) {
    curserPos.put(elem, Integer.valueOf(pos));
    selectionLength.put(elem, Integer.valueOf(length));
  }
}
