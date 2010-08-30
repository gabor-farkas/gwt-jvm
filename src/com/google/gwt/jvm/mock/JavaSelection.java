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

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;

/**
 * Pure-java mock for W3C Selection.
 * 
 * We can't use HtmlUnit's selection code because it only supports selection in
 * input elements.
 */
public class JavaSelection {
  JavaRange range;

  Node firstTextNodeIn(Node node) {
    for (Node child = node.getFirstChild();
          child != null;
          child = child.getNextSibling()) {
      if (child.getNodeType() == Node.TEXT_NODE) {
        return child;
      }
    }
    
    return null;
  }

  Node lastTextNodeIn(Node node) {
    for (Node child = node.getLastChild();
          child != null;
          child = child.getPreviousSibling()) {
      if (child.getNodeType() == Node.TEXT_NODE) {
        return child;
      }
    }
    
    return null;
  }
  
  Node textNodeAfter(Node node) {
    for (Node n = node;
          n != null;
          n = n.getNextSibling()) {
      Node textNode = firstTextNodeIn(n);
      if (textNode != null) {
        return textNode;
      }
    }

    if (node.getParentNode() != null) {
      return textNodeAfter(node.getParentNode());
    } else {
      return null;
    }
  }

  Node textNodeBefore(Node node) {
    for (Node n = node;
          n != null;
          n = n.getPreviousSibling()) {
      Node textNode = lastTextNodeIn(n);
      if (textNode != null) {
        return textNode;
      }
    }

    if (node.getParentNode() != null) {
      return textNodeBefore(node.getParentNode());
    } else {
      return null;
    }
  }
  
  /**
   * Notify the selection that the node will be removed. If either end of the
   * selection range is in the node (or one of the node's children), the
   * selection must be updated. 
   * @param node
   */
  void willRemoveNode(Node node) {
    if (range == null) {
      return;
    }
    
    if (node.isOrHasChild(range.startContainer())) {
      Node newStart = textNodeAfter(node);
      if (newStart == null) {
        range = null;
        return;
      }
      
      range.setStart(newStart, 0);

      if (node.isOrHasChild(range.endContainer())) {
        range.collapse(true);
      }
    } else if (node.isOrHasChild(range.endContainer())) {
      range.collapse(true);
    }
  }
  
  public final JavaRange getRangeAt(int index) {
    if (index != 0) {
      throw new RuntimeException("DOM Exception: Requested range not zero");
    }
    
    return range;
  }
  
  protected void checkRangeNotNull() {
    if (range == null) {
      throw new RuntimeException("DOM Exception: Empty selection range");
    }
  }
  
  public final Node anchorNode() {
    if (range == null) {
      return null;
    } else {
      return range.endContainer();
    }
  }

  public final Node focusNode() {
    if (range == null) {
      return null;
    } else {
      return range.startContainer();
    }
  }

  public final int anchorOffset() {
    if (range == null) {
      return 0;
    } else {
      return range.endOffset();
    }
  }

  public final int focusOffset() {
    if (range == null) {
      return 0;
    } else {
      return range.startOffset();
    }
  }
  
  public final void removeAllRanges(){
    range = null;
  }

  public final int rangeCount() {
    return range == null ? 0 : 1;
  }

  public final void addRange(JavaRange newRange) {
    range = newRange;
  }
  
  public final void setBaseAndExtent(Node anchorNode, int anchorOffset,
      Node focusNode, int focusOffset) {
    range = new JavaRange();
    range.setStart(focusNode, focusOffset);
    range.setEnd(anchorNode, anchorOffset);
  }

  // Called when a left arrow key event is fired
  public void moveLeft() {
    if (range == null) {
      return;
    }
    
    if (range.startOffset() > 0) {
      range.setStart(range.startContainer(), range.startOffset() - 1);
    } else {
      Node newStart = textNodeBefore(range.startContainer());
      // Don't move the selection if we're already at the start
      if (newStart != null) {
        range.setStart(newStart, ((Text) newStart).getLength() - 1);
      }
    }
    range.collapse(true);
  }

  // Called when a right arrow key event is fired
  public void moveRight() {
    if (range == null) {
      return;
    }
    
    Node start = range.startContainer();
    // Simply move the cursor inside the current node if we can.
    if (start.getNodeType() == Node.TEXT_NODE
        && ((Text) start).getLength() > (range.startOffset() + 1)) {
      range.setStart(start, range.startOffset() + 1);
    } else {
      Node newStart = textNodeAfter(start);
      // Don't move the selection if we're already at the start
      if (newStart != null) {
        range.setStart(newStart, 1);
      }
    }
    range.collapse(true);
  }
  
  public void deleteCharacterAtCursor() {
    // Note: This only deletes the character at the start of the region.
    if (range == null) {
      return;
    }
    
    Node start = range.startContainer();
    int offset = range.startOffset();
    
    if (start.getNodeType() != Node.TEXT_NODE
        || offset >= ((Text) start).getLength()) {
      // The cursor is at the end of the text node.
      start = textNodeAfter(start);
      if (start == null) {
        return;
      }
      offset = 0;
    }

    Text textNode = (Text) start;
    String oldText = textNode.getData();

    String newText = oldText.substring(0, offset) +
        oldText.substring(offset + 1);
    textNode.setData(newText);
  }
}
