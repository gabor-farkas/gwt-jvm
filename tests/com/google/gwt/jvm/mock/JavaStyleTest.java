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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.jvm.JavaGWTTestCase;

/**
 * Collection of tests for the mocked JavaStyle
 */
public class JavaStyleTest extends JavaGWTTestCase {
  // Check simple styles are treated correctly
  public void testSimpleStyle() {
    Element elt = Document.get().createDivElement();
    Style style = elt.getStyle();
    style.setDisplay(Display.NONE);
    Element wrap = Document.get().createDivElement();
    wrap.appendChild(elt);
    assertEquals("<div style=\"display:none; \"></div>", wrap.getInnerHTML());

    style.clearDisplay();
    assertEquals("<div></div>", wrap.getInnerHTML());
  }

  // Check JS properties are properly escaped when serialised
  public void testStyleJsManipulation() {
    Element elt = Document.get().createDivElement();
    Style style = elt.getStyle();
    style.setFontStyle(FontStyle.ITALIC);

    // fontStyle as a JS property:
    assertEquals("italic", style.getProperty("fontStyle"));

    // font-style as css serialisation:
    Element wrap = Document.get().createDivElement();
    wrap.appendChild(elt);
    assertEquals("<div style=\"font-style:italic; \"></div>", wrap.getInnerHTML());
  }

  // Ensure gwt apps with style workarounds can still use this in java.
  public void testCustomSetString() {
    Element elt = Document.get().createDivElement();
    Style style = elt.getStyle();
    addCustom(style, "white-space", "prewrap");
    Element wrap = Document.get().createDivElement();
    wrap.appendChild(elt);
    assertEquals("<div style=\"white-space:prewrap; \"></div>", wrap.getInnerHTML());
  }

  // Helper methods

  // utility to addd a custom string to a style element
  private static void addCustom(Style style, String key, String value) {
    JsoWrap jso = style.cast();
    jso.setString(key, value);
  }
  private static class JsoWrap extends JavaScriptObject {
    @SuppressWarnings("unused")
    protected JsoWrap() {}
    // workaround for GWT styles preventing setting non-camel-case keys with '-'.
    public native void setString(String key, String value) /*-{
      this[key] = value;
    }-*/;
  }
}
