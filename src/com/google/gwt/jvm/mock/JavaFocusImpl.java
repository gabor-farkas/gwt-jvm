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
import com.google.gwt.jvm.JavaJSObject;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.impl.FocusImpl;

public class JavaFocusImpl {

  public JavaFocusImpl(final FocusImpl delegate) {
  }

  public JavaScriptObject createBlurHandler() {
    return JavaJSObject.wrap(null);
  }

  public JavaScriptObject createFocusHandler() {
    return JavaJSObject.wrap(null);
  }

  public JavaScriptObject createMouseHandler() {
    return JavaJSObject.wrap(null);
  }

  public void setTabIndex(final Element elem, final int index) {
    final JavaElement element = JavaJSObject.unwrap(elem);
    element.setAttribute("tabIndex", Integer.toString(index));
  }

  public Element createFocusable() {
    final com.google.gwt.dom.client.Element e = JavaDocument.get().createElement("DIV");
    e.setAttribute("tabIndex", "0");
    return (Element) e;
  }

  public void focus(final Element element) {
    JavaElement javaElement = JavaJSObject.unwrap(element);
    javaElement.getHtmlElement().focus();
  }


}
