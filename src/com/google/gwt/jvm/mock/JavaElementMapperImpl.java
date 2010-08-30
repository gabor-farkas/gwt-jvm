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

import static com.google.gwt.jvm.mock.JavaNode.w3cNode;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.impl.ElementMapperImpl;

import com.gargoylesoftware.htmlunit.html.DomNode;

import java.util.WeakHashMap;

public class JavaElementMapperImpl {

  public JavaElementMapperImpl(final ElementMapperImpl<?> elementMapperImpl) {
  }

  static WeakHashMap<DomNode, Integer> uiObjectIndex = new WeakHashMap<DomNode, Integer>();
  
  public static int getIndex(final Element element) {
    final DomNode node = w3cNode(element);
    final Integer index = uiObjectIndex.get(node);
    return index == null ? -1 : index;
  }

  public static void setIndex(final Element element, final int index) {
    final DomNode node = w3cNode(element);
    uiObjectIndex.put(node, Integer.valueOf(index));
  }

  public static void clearIndex(final Element element) {
    final DomNode node = w3cNode(element);
    uiObjectIndex.remove(node);
  }

}
