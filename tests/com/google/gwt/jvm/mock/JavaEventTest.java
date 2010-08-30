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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.jvm.JavaGWTTestCase;
import com.google.gwt.jvm.JavaJSObject;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

/**
 * Tests for JavaEvent.
 */
public class JavaEventTest extends JavaGWTTestCase{
  void type(char character, JavaElement target) {
    JavaEvent down = new JavaEvent("keydown");
    down.setNativeKeyCode(character);
    down.setEventTargetElement(target);
    down.fireEvent();
    
    JavaEvent press = new JavaEvent("keypress");
    press.setCharCode(character);
    press.setEventTargetElement(target);
    press.fireEvent();
    
    JavaEvent up = new JavaEvent("keyup");
    up.setNativeKeyCode(character);
    up.setEventTargetElement(target);
    up.fireEvent();
  }
  
  /**
   * Fire a control key like KeyCodes.Left
   * @param key
   * @param target
   */
  void fireCommand(int key, JavaNode target) {
    JavaEvent down = new JavaEvent("keydown");
    down.setNativeKeyCode(key);
    down.setEventTargetElement(target);
    down.fireEvent();
    
    JavaEvent up = new JavaEvent("keyup");
    up.setNativeKeyCode(key);
    up.setEventTargetElement(target);
    up.fireEvent();    
  }
  
  class Listener implements EventListener {
    boolean fired = false;
    
    @Override
    public void onBrowserEvent(Event event) {
      // TODO(gentle): Check that this event is fired exactly once for each
      // event.
      fired = true;
    }
    
    public boolean wasFired() {
      return fired;
    }
  }

  InputElement addTextBox() {
    Document doc = Document.get();
    InputElement textbox = doc.createTextInputElement();
    doc.getBody().appendChild(textbox);

    return textbox;
  }
  
  public void testTypeCharactersInTextBox() {
    InputElement textbox = addTextBox();
    Listener listener = new Listener();
    JavaElement javaElement = JavaJSObject.unwrap(textbox);
    javaElement.setEventListener(listener);

    assertEquals("", textbox.getValue());
    type('a', javaElement);
    assertEquals("a", textbox.getValue());
    assertEquals(true, listener.wasFired());
  }
  
  public void testTypeAtSelection() {
    InputElement textbox = addTextBox();
    JavaElement javaElement = JavaJSObject.unwrap(textbox);

    textbox.setValue("acde");
    JavaWindow.getSelection().setBaseAndExtent(textbox, 1, textbox, 1);
    type('b', javaElement);
    assertEquals("abcde", textbox.getValue());
  }
  
  public void testTypeLeftArrow() {
    InputElement textbox = addTextBox();
    JavaElement javaElement = JavaJSObject.unwrap(textbox);

    textbox.setValue("b");
    JavaWindow.getSelection().setBaseAndExtent(textbox, 1, textbox, 1);

    fireCommand(KeyCodes.KEY_LEFT, javaElement);
    
    type('a', javaElement);
    assertEquals("ab", textbox.getValue());
    
    // The right arrow doesn't work correctly for text input elements because
    // the selection code doesn't move the selection correctly inside those 
    // elements.
  }
  
  public void testEditContentEditable() {
    Document doc = Document.get();
    Element div = doc.createDivElement();
    Node text = doc.createTextNode("Hi there.");
    doc.getBody().appendChild(div);
    div.appendChild(text);

    div.setAttribute("contentEditable", "true");
    JavaWindow.getSelection().setBaseAndExtent(text, 1, text, 1);
    
    // Typing inside a contenteditable is not yet supported.
    
    fireCommand(KeyCodes.KEY_DELETE, JavaNode.javaNode(text));
    assertEquals("H there.", text.getNodeValue());
    
    fireCommand(KeyCodes.KEY_LEFT, JavaNode.javaNode(text));
    assertEquals("H there.", text.getNodeValue());
    
    fireCommand(KeyCodes.KEY_DELETE, JavaNode.javaNode(text));
    assertEquals(" there.", text.getNodeValue());
    
    fireCommand(KeyCodes.KEY_RIGHT, JavaNode.javaNode(text));
    assertEquals(" there.", text.getNodeValue());
    
    fireCommand(KeyCodes.KEY_DELETE, JavaNode.javaNode(text));
    assertEquals(" here.", text.getNodeValue());
  }
}
