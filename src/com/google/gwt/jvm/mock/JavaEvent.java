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

import static com.google.gwt.jvm.JavaJSObject.unwrap;

import com.google.gwt.jvm.JavaJSObject;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;

import java.util.ArrayList;

public class JavaEvent {  
  private final String type;
  private EventTarget eventTarget;
  private EventTarget currentEventTarget;
  private EventTarget relatedEventTarget;
  
  private boolean preventDefault;
  
  private boolean altKey;
  private boolean ctrlKey;
  private boolean shiftKey;
  private boolean metaKey;
  
  // For key down / key up, this is the code associated with the physical key.
  // 0 otherwise.
  private int nativeKeyCode;
  
  // For key pressed, this is the code of the character pressed. 0 otherwise.
  private int charCode;
  
  // Mouse position
  private int clientX, clientY;
  
  // Mouse buttons clicked (NativeEvent.BUTTON_{LEFT|MIDDLE|RIGHT})
  int mouseButton;
  
  public JavaEvent(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public NativeEvent toNativeEvent() {
    return JavaJSObject.wrap(this);
  }

  public EventTarget eventTarget() {
    return eventTarget;
  }

  public void eventPreventDefault() {
    preventDefault = true;
  }
  
  public boolean isPreventDefault() {
    return preventDefault;
  }

  @Override
  public String toString() {
    return type;
  }
  
  public EventTarget getEventTarget() {
    return eventTarget;
  }

  public void setEventTarget(EventTarget eventTarget) {
    this.eventTarget = eventTarget;
  }

  public EventTarget getCurrentEventTarget() {
    return currentEventTarget;
  }

  /**
   * Simple helper wrapper around setEventTarget for a JavaElement
   * @param target The target of the event
   */
  public void setEventTargetElement(JavaNode target) {
    this.eventTarget = JavaJSObject.wrap(target);
  }
  
  /**
   * Simple helper wrapper around getEventTarget for a JavaElement target
   * @return The target of the event as a JavaElement
   */
  public JavaElement getEventTargetElement() {
    return JavaJSObject.unwrap(eventTarget);
  }

  public void setCurrentEventTarget(EventTarget currentEventTarget) {
    this.currentEventTarget = currentEventTarget;
  }

  public EventTarget getRelatedEventTarget() {
    return relatedEventTarget;
  }

  public void setRelatedEventTarget(EventTarget relatedEventTarget) {
    this.relatedEventTarget = relatedEventTarget;
  }

  public boolean getAltKey() {
    return altKey;
  }

  public void setAltKey(boolean altKey) {
    this.altKey = altKey;
  }

  public boolean getCtrlKey() {
    return ctrlKey;
  }

  public void setCtrlKey(boolean ctrlKey) {
    this.ctrlKey = ctrlKey;
  }

  public boolean getShiftKey() {
    return shiftKey;
  }

  public void setShiftKey(boolean shiftKey) {
    this.shiftKey = shiftKey;
  }

  public boolean getMetaKey() {
    return metaKey;
  }

  public void setMetaKey(boolean metaKey) {
    this.metaKey = metaKey;
  }
  
  public int getKeyCode() {
    if (getType().equals("keypress")) {
      return charCode;
    } else {
      return nativeKeyCode;
    }
  }
  
  public int getNativeKeyCode() {
    return nativeKeyCode;
  }

  public void setNativeKeyCode(int keyCode) {
    this.nativeKeyCode = keyCode;
  }

  public int getCharCode() {
    return charCode;
  }

  public void setCharCode(int charCode) {
    this.charCode = charCode;
  }
  
  public void setCharCodeFromCharacter(char c) {
    charCode = c;
  }
  
  public char getCharacter() {
    return (char) getKeyCode();
  }

  public int getClientX() {
    return clientX;
  }

  public void setClientX(int clientX) {
    this.clientX = clientX;
  }

  public int getClientY() {
    return clientY;
  }

  public void setClientY(int clientY) {
    this.clientY = clientY;
  }

  public int getMouseButton() {
    return mouseButton;
  }

  public void setMouseButton(int mouseButton) {
    this.mouseButton = mouseButton;
  }

  public void setPreventDefault(boolean preventDefault) {
    this.preventDefault = preventDefault;
  }

  public final String getString() {
    return toString();
  }

  boolean propagate = true;
  public void stopPropagation() {
    propagate = false;
  }
  
  public boolean isPropagating() {
    return propagate;
  }
  
  // This implementation of events is based on the W3C standard:
  // http://www.w3.org/TR/DOM-Level-2-Events/events.html
  // Currently supported: Keyboard keys (typing, backspace, delete, left & right
  // arrows)
  private void fireEventInternal() {
    JavaNode target = unwrap(eventTarget);

    // Phase 1: Event capture. Any ancestor of this element should have the
    // opportunity to capture the event. This happens from the root of the DOM
    // (the document) to the element.
    
    ArrayList<Node> ancestors = target.getAncestors();
    
    // Iterate from the last (the document) to the first (the immediate
    // parent of this element)
    //   (Event capture is not supported yet.)
//    for (int i = ancestors.size() - 1; i >= 0; i--) { }
    
    // Phase 2: Event listeners are fired on the event, then all events up the
    // chain unless the event's stopPropogation() method is called.
    if (target instanceof JavaElement) {
      ((JavaElement) target).invokeEventHandlers(this);
    }
    
    // Bubble up the ancestor chain
    for (Node ancestor : ancestors) {
      if (!isPropagating()) {
        break;
      }
      
      JavaNode node = unwrap(ancestor);
      if (node instanceof JavaElement) {
        ((JavaElement) node).invokeEventHandlers(this);
      }
    }
    
    // Phase 4: If the event wasn't cancelled (via preventDefault) then the
    // event's 'native' action happens.
    if (!isPreventDefault()) {
      target.performNativeEventAction(this);
    }
  }
  
  /**
   * Handle the passed event. This is the main entrypoint for injecting
   * fake browser events into the java mock.
   */
  public void fireEvent() {
    try {
      fireEventInternal();
      JavaSchedulerImpl.get().runDeferredCmds();            
    } finally {
      JavaSchedulerImpl.get().runFinallyCmds();
    }
  }
}
