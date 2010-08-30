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
import static com.google.gwt.jvm.JavaJSObject.unwrap;
import static com.google.gwt.jvm.JavaJSObject.wrap;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.user.client.EventListener;

public class JavaDOMImpl {
  public JavaDOMImpl(final Object domImpl) {
    // we take object since the real class is package private and we can not compile against it.
  }

  public Element createElement(final Document doc, final String tag) {
    final JavaDocument document = unwrap(doc);
    return wrap(document.createElement(tag));
  }

  public Element getParentElement(final Node node) {
    final JavaNode javaNode = unwrap(node);
    return wrap(javaNode.getParentElement());
  }
  

  public Element getNextSiblingElement(Element elem) {
    final JavaElement javaElement = unwrap(elem);
    return wrap(javaElement.getNextSibling());
  }
  
  public InputElement createInputElement(final Document doc, final String type) {
    final JavaDocument document = unwrap(doc);
    final JavaElement element = document.createElement("INPUT");
    element.setAttribute("type", type);
    return wrap(element);
  }
  public InputElement createInputRadioElement(Document doc, String name) {
    InputElement input = createInputElement(doc, "radio");
    input.setAttribute("name", name);
    return input;
  }

  public ButtonElement createButtonElement(final Document doc, final String type) {
    final JavaDocument document = unwrap(doc);
    final JavaElement element = document.createElement("BUTTON");
    element.setAttribute("type", type);
    return wrap(element);
  }

  public SelectElement createSelectElement(final Document doc) {
    final JavaDocument document = unwrap(doc);
    final JavaElement element = document.createElement("select");
    return wrap(element);
  }

  public OptionElement selectAdd(final SelectElement select, final OptionElement newElement,
      final OptionElement before) {
    final JavaElement javaElement = unwrap(select);
    if (before == null) {
      javaElement.appendChild(newElement);
    } else {
      javaElement.insertBefore(newElement, before);
    }

    return newElement;
  }

  public void selectClear(final SelectElement select) {
    final JavaElement javaElement = unwrap(select);
    javaElement.removeAttribute("selected");
  }

  public NodeList<OptionElement> selectGetOptions(final SelectElement select) {
    final JavaElement javaElement = unwrap(select);
    return javaElement.getChildNodes();
  }

  public void setInnerText(final Element elem, final String text) {
    final JavaNode javaElement = unwrap(elem);
    javaElement.setInnerText(text);
  }

  public String getInnerText(final Element node) {
    final JavaNode javaElement = unwrap(node);
    return javaElement.getInnerText();
  }

  public String getInnerHTML(final Element node) {
    final JavaElement javaElement = unwrap(node);
    return javaElement.getInnerHtml();
  }

  public int getBodyOffsetLeft(final Document doc) {
    return 0;
  }

  public int getBodyOffsetTop(final Document doc) {
    return 0;
  }

  public int selectGetLength(final SelectElement select) {
    final JavaElement javaElement = unwrap(select);
    return javaElement.getSelectLength();
  }

  public void setEventListener(final Element elem, final EventListener listener) {
    final JavaElement element = JavaJSObject.unwrap(elem);
    element.setEventListener(listener);
  }

  public void buttonClick(final ButtonElement button) {
    final JavaElement element = JavaJSObject.unwrap(button);
    element.click();
  }

  public final String eventGetType(final NativeEvent evt) {
    final JavaEvent event = JavaJSObject.unwrap(evt);
    return event.getType();
  }

  public Element getFirstChildElement(final Element elem) {
    final JavaElement element = unwrap(elem);
    return element.getFirstChild();
  }

  public String toString(final Element element) {
    return element.toString();
  }

  public int getAbsoluteLeft(final Element element) {
    return 0;
  }

  public int getAbsoluteTop(final Element element) {
    return 0;
  }
  
  public int getScrollLeft(Element elem) {
    return 0;
  }
  
  public boolean isOrHasChild(Node parent, Node child) {
    while(child != null) {
      if(parent == child) {
        return true;
      }
      child = child.getParentNode();
      if (child != null && (child.getNodeType() != 1)) {
        child = null;
      }
    }
    return false;
  }
  
  public String getTagName(Element elem) {
    JavaElement element = unwrap(elem);
    return element.getNodeName();
  }
  
  public String getAttribute(Element elem, String name) {
    JavaElement element = unwrap(elem);
    return element.getAttribute(name);
  }
  
  public boolean hasAttribute(Element elem, String name) {
    // We can't just call hasAttribute on the element because
    // Element.hasAttribute delegates back here.
    return !elem.getAttribute(name).equals("");
  }
  
  public String imgGetSrc(Element img){
    return img.getAttribute("src");
  }
  
  public void imgSetSrc(Element img, String src){
    img.setAttribute("src", src);
  }
  
  public EventTarget eventGetTarget(NativeEvent evt) {
    JavaEvent event = unwrap(evt);
    return event.eventTarget();
  }

  public void eventPreventDefault(NativeEvent evt) {
    JavaEvent event = unwrap(evt);
    event.eventPreventDefault();
  }

  public NativeEvent createKeyEvent(Document doc, String type,
      boolean canBubble, boolean cancelable, boolean ctrlKey, boolean altKey,
      boolean shiftKey, boolean metaKey, int keyCode, int charCode) {
    // For now, ignoring doc, canBubble, cancelable, charCode.
    JavaEvent event = new JavaEvent(type);
    
    event.setCtrlKey(ctrlKey);
    event.setAltKey(altKey);
    event.setShiftKey(shiftKey);
    event.setMetaKey(metaKey);
    event.setNativeKeyCode(keyCode);
    event.setCharCode((char) charCode);
    
    return wrap(event);
  }

  public NativeEvent createMouseEvent(Document doc, String type, boolean canBubble,
      boolean cancelable, int detail, int screenX, int screenY, int clientX,
      int clientY, boolean ctrlKey, boolean altKey, boolean shiftKey,
      boolean metaKey, int button, Element relatedTarget) {
    // For now, ignoring doc, canBubble, cancelable, charCode, screen[XY],
    // relatedTarget
    JavaEvent event = new JavaEvent(type);
    
    event.setClientX(clientX);
    event.setClientY(clientY);
    event.setCtrlKey(ctrlKey);
    event.setAltKey(altKey);
    event.setShiftKey(shiftKey);
    event.setMetaKey(metaKey);
    event.setMouseButton(button);
    
    return wrap(event);
  }

  public void eventStopPropagation(NativeEvent evt) {
    JavaEvent event = unwrap(evt);
    event.stopPropagation();
  }
  
  @SuppressWarnings("unused")
  private boolean isRTL(Element elem) {
    return false;
  }


  public final int eventGetKeyCode(NativeEvent evt) {
    JavaEvent event = unwrap(evt);
    return event.getKeyCode();
  }

  public boolean eventGetAltKey(NativeEvent evt) {
    JavaEvent event = unwrap(evt);
    return event.getAltKey();
  }
  
  public boolean eventGetShiftKey(NativeEvent evt) {
    JavaEvent event = unwrap(evt);
    return event.getShiftKey();
  }
  
  public boolean eventGetCtrlKey(NativeEvent evt) {
    JavaEvent event = unwrap(evt);
    return event.getCtrlKey();
  }
  
  public boolean eventGetMetaKey(NativeEvent evt) {
    JavaEvent event = unwrap(evt);
    return event.getMetaKey();
  }
}
