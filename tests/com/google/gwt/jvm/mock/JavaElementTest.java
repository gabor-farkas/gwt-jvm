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
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.jvm.JavaGWTTestCase;
import com.google.gwt.user.client.DOM;

/**
 * Collection of tests for the mocked JavaElement.
 */
public class JavaElementTest extends JavaGWTTestCase {
  public void testInnerHtmlSimple() {
    Element elt = Document.get().createElement("tag");
    elt.setAttribute("x", "y");
    Element parent = Document.get().createElement("E");
    parent.appendChild(elt);
    assertEquals("<tag x=\"y\"></tag>", parent.getInnerHTML());

    elt = Document.get().createDivElement();
    parent = Document.get().createElement("E");
    parent.appendChild(elt);
    assertEquals("<div></div>", parent.getInnerHTML());
  }

  public void testInnerHtmlChildren() {
    Element elt = Document.get().createElement("var");
    elt.appendChild(Document.get().createPElement());
    elt.setAttribute("key", "value");
    Element parent = Document.get().createElement("E");
    parent.appendChild(elt);
    parent.appendChild(Document.get().createTextNode("text!"));
    assertEquals("<var key=\"value\"><p></p></var>text!", parent.getInnerHTML());
  }

  public void testInnerHtmlSpecialCases() {
    // some browsers do not like "<br></br>" for breaks, hence self-closing break used
    Element parent = Document.get().createElement("E");
    Element br = Document.get().createBRElement();
    parent.appendChild(br);
    assertEquals("<br />", parent.getInnerHTML());

    // check that styling is handled properly
    Element div = Document.get().createDivElement();
    div.getStyle().setFontSize(10, Unit.PT);
    parent.replaceChild(div, br);
    assertEquals("<div style=\"font-size:10.0pt; \"></div>", parent.getInnerHTML());
    div.getStyle().clearFontSize();
    assertEquals("<div></div>", parent.getInnerHTML());

    // check that setting className actually sets the class attribute
    div.addClassName("AB");
    assertEquals("<div class=\"AB\"></div>", parent.getInnerHTML());
    div.addClassName("CD");
    assertEquals("<div class=\"AB CD\"></div>", parent.getInnerHTML());
  }

  // Bug fixed (no b number) for mapping "className" property to "class" attribute
  public void testClassNameProperty() {
    String clazz1 = "ABC";
    String clazz2 = "DEF";
    Element elt = Document.get().createDivElement();

    assertFalse(elt.hasAttribute("class"));

    elt.setAttribute("class", clazz1);
    assertEquals(clazz1, elt.getAttribute("class")); // <div class="ABC" />
    assertEquals(clazz1, elt.getPropertyString("className")); // elt.className = "ABC"

    elt.setPropertyString("className", clazz2);
    assertEquals(clazz2, elt.getAttribute("class")); // <div class="DEF" />
    assertEquals(clazz2, elt.getPropertyString("className")); // elt.className = "DEF"

    elt.removeClassName(clazz2);
    assertEquals("", DOM.getElementProperty((com.google.gwt.user.client.Element) elt, "className"));
  }

  public void testInputElement() {
    Element elt = Document.get().createDivElement();
    InputElement inputElement = elt.cast();
    inputElement.setMaxLength(100);
    assertEquals(100, inputElement.getMaxLength());
  }

  public void testElementChecked() {
    InputElement elt = Document.get().createCheckInputElement();
    assertFalse(elt.isChecked());
    elt.setChecked(true);
    assertTrue(elt.isChecked());
    assertTrue(elt.hasAttribute("checked"));
    elt.setChecked(false);
    assertFalse(elt.isChecked());
    assertFalse(elt.hasAttribute("checked"));
  }
}
