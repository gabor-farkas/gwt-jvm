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
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.impl.DOMImpl;

public class JavaClientDOMImpl {

  public JavaClientDOMImpl(final DOMImpl domImpl) {
  }

  public int eventGetTypeInt(final String eventType) {
    if (eventType.equals("blur")) {
      return 0x01000;
    }
    if (eventType.equals("change")) {
      return 0x00400;
    }
    if (eventType.equals("click")) {
      return 0x00001;
    }
    if (eventType.equals("dblclick")) {
      return 0x00002;
    }
    if (eventType.equals("focus")) {
      return 0x00800;
    }
    if (eventType.equals("keydown")) {
      return 0x00080;
    }
    if (eventType.equals("keypress")) {
      return 0x00100;
    }
    if (eventType.equals("keyup")) {
      return 0x00200;
    }
    if (eventType.equals("load")) {
      return 0x08000;
    }
    if (eventType.equals("losecapture")) {
      return 0x02000;
    }
    if (eventType.equals("mousedown")) {
      return 0x00004;
    }
    if (eventType.equals("mousemove")) {
      return 0x00040;
    }
    if (eventType.equals("mouseout")) {
      return 0x00020;
    }
    if (eventType.equals("mouseover")) {
      return 0x00010;
    }
    if (eventType.equals("mouseup")) {
      return 0x00008;
    }
    if (eventType.equals("scroll")) {
      return 0x04000;
    }
    if (eventType.equals("error")) {
      return 0x10000;
    }
    if (eventType.equals("mousewheel")) {
      return 0x20000;
    }
    if (eventType.equals("DOMMouseScroll")) {
      return 0x20000;
    }
    if (eventType.equals("contextmenu")) {
      return 0x40000;
    }
    throw new IllegalArgumentException();
  }

  public void sinkEventsImpl(final Element elem, final int bits) {
    // var chMask = (elem.__eventBits || 0) ^ bits;
    // elem.__eventBits = bits;
    // if (!chMask) return;
    //
    // if (chMask & 0x00001) elem.onclick = (bits & 0x00001) ?
    // @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    // if (chMask & 0x00002) elem.ondblclick = (bits & 0x00002) ?
    // @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    // if (chMask & 0x00004) elem.onmousedown = (bits & 0x00004) ?
    // @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    // if (chMask & 0x00008) elem.onmouseup = (bits & 0x00008) ?
    // @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    // if (chMask & 0x00010) elem.onmouseover = (bits & 0x00010) ?
    // @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    // if (chMask & 0x00020) elem.onmouseout = (bits & 0x00020) ?
    // @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    // if (chMask & 0x00040) elem.onmousemove = (bits & 0x00040) ?
    // @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    // if (chMask & 0x00080) elem.onkeydown = (bits & 0x00080) ?
    // @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    // if (chMask & 0x00100) elem.onkeypress = (bits & 0x00100) ?
    // @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    // if (chMask & 0x00200) elem.onkeyup = (bits & 0x00200) ?
    // @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    // if (chMask & 0x00400) elem.onchange = (bits & 0x00400) ?
    // @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    // if (chMask & 0x00800) elem.onfocus = (bits & 0x00800) ?
    // @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    // if (chMask & 0x01000) elem.onblur = (bits & 0x01000) ?
    // @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    // if (chMask & 0x02000) elem.onlosecapture = (bits & 0x02000) ?
    // @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    // if (chMask & 0x04000) elem.onscroll = (bits & 0x04000) ?
    // @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    // if (chMask & 0x08000) elem.onload = (bits & 0x08000) ?
    // @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    // if (chMask & 0x10000) elem.onerror = (bits & 0x10000) ?
    // @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    // if (chMask & 0x20000) elem.onmousewheel = (bits & 0x20000) ?
    // @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
    // if (chMask & 0x40000) elem.oncontextmenu = (bits & 0x40000) ?
    // @com.google.gwt.user.client.impl.DOMImplStandard::dispatchEvent : null;
  }

  public void setEventListener(final Element elem, final EventListener listener) {
    final JavaElement element = JavaJSObject.unwrap(elem);
    element.setEventListener(listener);
  }

  public EventListener getEventListener(final Element elem) {
    final JavaElement element = JavaJSObject.unwrap(elem);
    return element.getEventListener();
  }

  public int getEventsSunk(final Element elem) {
    return 0;
  }

  protected void initEventSystem() {
  }

  /**
   * Inserts the given element before the index mentioned.
   * If index is one past last element, it appends.
   * If the index is far past that, it throw child out of bounds.
   * @param parent the parent element
   * @param child the child element
   * @param index the index.
   */
  public void insertChild(final Element parent, final Element child, final int index){
    //
    assert index <= parent.getChildCount() : "Child index out of bounds";

    if (index < parent.getChildCount()) {
      parent.insertBefore(child, parent.getChild(index));
    } else {
      parent.appendChild(child);
    }
  }

  public Element getChild(final Element elem, int index) {
    final JavaElement javaElement = unwrap(elem);
    return javaElement.<Element>getChildNodes().getItem(index);
  }
  
  public int getChildCount(Element elem) {
    return elem.getChildCount();
  }
}
