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

import com.google.gwt.jvm.JavaJSObject;
import com.google.gwt.dom.client.Node;

/**
 * Mock for W3C Range. Needed by JavaSelection.
 * http://www.w3.org/TR/DOM-Level-2-Traversal-Range/ranges.html
 */
public class JavaRange {
  private JavaNode startContainer, endContainer;
  private int startOffset, endOffset;

  public final void setStartBefore(Node node) {
    startContainer = JavaJSObject.unwrap(node.getParentNode());
    startOffset = JavaNode.javaNode(node).positionInParent();
  }

  public final void setStartAfter(Node node) {
    startContainer = JavaJSObject.unwrap(node.getParentNode());
    startOffset = JavaNode.javaNode(node).positionInParent() + 1;
  }

  public final void setEndBefore(Node node) {
    endContainer = JavaJSObject.unwrap(node.getParentNode());
    endOffset = JavaNode.javaNode(node).positionInParent();
  }

  public final void setEndAfter(Node node) {
    endContainer = JavaJSObject.unwrap(node.getParentNode());
    endOffset = JavaNode.javaNode(node).positionInParent() + 1;
  }

  public final void collapse(boolean toStart) {
    if (toStart) {
      endContainer = startContainer;
      endOffset = startOffset;
    } else {
      startContainer = endContainer;
      startOffset = endOffset;
    }
  }

  public final void setStart(Node parent, int offset) {
    startContainer = JavaJSObject.unwrap(parent);
    startOffset = offset;
  }
  
  public final void setEnd(Node parent, int offset) {
    endContainer = JavaJSObject.unwrap(parent);
    endOffset = offset;
  }

  public final Node startContainer() {
    return JavaJSObject.wrap(startContainer);
  }

  public final int startOffset() {
    return startOffset;
  }

  public final Node endContainer() {
    return JavaJSObject.wrap(endContainer);
  }

  public final int endOffset() {
    return endOffset;
  }
}
