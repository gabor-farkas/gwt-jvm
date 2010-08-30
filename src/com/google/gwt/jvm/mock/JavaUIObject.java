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
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.ui.UIObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaUIObject {

  private static final Pattern DECIMAL_PATTERN =
      Pattern.compile("(^\\d*[0-9](\\.\\d*[0-9])?)\\D*");
  protected final UIObject delegate;
  public JavaUIObject(UIObject delegate) {
    this.delegate = delegate;
  }

  public static boolean isVisible(Element elem) {
    JavaElement element = JavaJSObject.unwrap(elem);
    String visibility = element.getStyle().getVisibility();
    return Visibility.VISIBLE.getCssName().equals(visibility);
  }

  public static void setVisible(Element elem, boolean visible) {
    JavaElement element = JavaJSObject.unwrap(elem);
    if (visible) {
      element.getStyle().setVisibility(Visibility.VISIBLE);
    } else {
      element.getStyle().setVisibility(Visibility.HIDDEN);
    }
  }

  public static void updatePrimaryAndDependentStyleNames(Element elem,
      String newPrimaryStyle) {
    JavaElement element = JavaJSObject.unwrap(elem);
    element.addClassName(newPrimaryStyle);
  }

  public double extractLengthValue(String s) {
    if ("auto".equals(s) || "inherit".equals(s) || "".equals(s)) {
      return 0;
    } else {
      return parseFloat(s);
    }
  }

  private double parseFloat(String s) {
    double val = 0;
    s = s.trim();
    Matcher matcher = DECIMAL_PATTERN.matcher(s);
    if (matcher.matches()) {
      val = Double.parseDouble(matcher.group(1));
    }
    return val;
  }
}
