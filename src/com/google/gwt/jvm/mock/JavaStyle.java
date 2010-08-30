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

import com.google.gwt.dom.client.Style.Visibility;

import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class JavaStyle {

  private final Map<String, String> properties = new HashMap<String, String>();
  private static final String STYLE_VISIBILITY = "visibility";

  public JavaStyle(Element element) {
    properties.put(STYLE_VISIBILITY, Visibility.VISIBLE.getCssName());
  }

  // NOTE(patcoleman): apps might work around some style settings (e.g. "white-space: prewrap")
  // by setting them manually on the style JSO - this can be worked around by casting the style to
  // a JSO with an identical method on it. That workaround will be supported by this mock.
  public void setString(String name, String value) {
    setPropertyImpl(name, value);
  }

  public void setPropertyImpl(String name, String value) {
    // NOTE(patcoleman): gwt clears a property by setting it to ""
    if ("".equals(value)) {
      properties.remove(name);
    } else {
      properties.put(name, value);
    }
  }

  public String getPropertyImpl(String name) {
    return properties.get(name);
  }

  public void put(String name, Object value) {
    setPropertyImpl(name, value.toString());
  }

  /**
   * Internal helper to aid serialization - not a method on Style.java
   * @return The style bundle serialized into something that can be used in html. Never null.
   */
  String toHtmlString() {
    StringBuilder builder = new StringBuilder();
    for (Entry<String, String> style : properties.entrySet()) {
      // special-case to not serialize default values
      if (!isDefault(style.getKey(), style.getValue())) {
        // convert and append the new style:
        appendJsAsCss(style.getKey(), builder);
        builder.append(":").append(style.getValue()).append("; ");
      }
    }
    return builder.toString();
  }

  /**
   * Utility for mapping javascript members (e.g. fontSize and MozBorderRadius) to
   * their css equivalent (e.g. font-size and -moz-border-radius), appending to a builder.
   */
  private static void appendJsAsCss(String js, StringBuilder builder) {
    // achieved by swapping every upper case letter ("C") to lower case plus dash ("-c")
    for (int i = 0; i < js.length(); i++) {
      char at = js.charAt(i);
      if ('A' <= at && at <= 'Z') {
        builder.append('-').append((char)(at - 'A' + 'a'));
      } else {
        builder.append(at);
      }
    }
  }

  /** Utility to white-list css values as default (i.e. don't need to be serialized). */
  private static boolean isDefault(String key, String value) {
    if (value == null) {
      return true; // default by definition.
    }
    // from constructor:
    if (STYLE_VISIBILITY.equals(key)) {
      return Visibility.VISIBLE.getCssName().equals(value);
    }
    return false;
  }
}
