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

package com.google.gwt.jvm;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.jvm.mock.JavaJsArray;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * For overlay types (see: http://code.google.com/p/google-web-toolkit/wiki/OverlayTypes)
 * the class loader adds all interfaces to this class making it possible to cast it 
 * to any overlay type. This class acts as a proxy to JavaScriptObject in GWT.
 */
public class JavaJSObject {
  
  private static Map<Object, JavaJSObject> identityMap = new WeakHashMap<Object, JavaJSObject>();

  public final Object delegate;

  private JavaJSObject(Object delegate) {
    this.delegate = delegate;
  }

  @SuppressWarnings("unchecked")
  public static <T> T unwrap(Object object) {
    JavaJSObject jsObject = (JavaJSObject) object;
    return (T) jsObject.delegate;
  }

  @SuppressWarnings("unchecked")
  public static <T> T wrap(Object delegate) {
    if (delegate == null) {
      return null;
    } else if (delegate instanceof JavaJSObject) {
      throw new IllegalStateException("Trying to wrap already wrapped object.");
    } else {
      JavaJSObject jsObject = identityMap.get(delegate);
      if (jsObject == null) {
        jsObject = new JavaJSObject(delegate);
        identityMap.put(delegate, jsObject);
      }
      return (T) jsObject;
    }
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
  
  public static JavaScriptObject createArray() {
    return wrap(new JavaJsArray());
  }
  
  public static String toStringSimple(JavaScriptObject obj) {
    return String.valueOf(obj);
  }

  public static String toStringVerbose(JavaScriptObject obj) {
    return String.valueOf(obj);
  }
}
