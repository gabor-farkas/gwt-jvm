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

import static com.google.gwt.jvm.JavaJSObject.wrap;
import static com.google.gwt.jvm.mock.JavaNode.javaNode;

import com.google.gwt.dom.client.Node;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;

import java.util.Iterator;

/**
 * @param <T> a type that extends Node.
 *    Typically this is a {@link com.google.gwt.dom.client.Element}
 */
public class JavaNodeList<T extends Node> {

  private final DomNodeList<? extends DomNode> nodes;

  public JavaNodeList(final DomNodeList<? extends DomNode> nodes) {
    this.nodes = nodes;
  }

  public T getItem(final int index) {
    return wrap(javaNode(nodes.get(index)));
  }

  public int getLength(){
   return nodes.size();
  }
  
  @Override
  public String toString() {
    Iterator<? extends Object> iter = nodes.iterator();
    if (!iter.hasNext()) {
        return "[]";
    }
    
    StringBuilder builder = new StringBuilder("[");
    builder.append(iter.next().toString());
    while (iter.hasNext()) {
        builder.append(", ").append(iter.next());
    }
    builder.append("]");
    
    return builder.toString();
  }
}
