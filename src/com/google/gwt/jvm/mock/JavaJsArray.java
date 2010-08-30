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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Mock class for JsArrayNumber, JsArrayString, JsArrayBoolean, JsArrayInteger
 * 
 */
public class JavaJsArray {
  ArrayList<Object> delegate = new ArrayList<Object>();
  
  public final String join() {
    return join(",");
  }

  public final String join(String separator) {
    if (delegate.isEmpty()) {
      return "";
    }
    
    Iterator<Object> iter = delegate.iterator();
    StringBuilder builder = new StringBuilder(iter.next().toString());
    while (iter.hasNext()) {
      builder.append(separator);
      builder.append(iter.next().toString());
    }
    
    return builder.toString();
  }

  public final int length() {
    return delegate.size();
  }
  
  private void extendTo(int length) {
    while (length > delegate.size()) {
      delegate.add(null);
    }    
  }

  public final void setLength(int newLength) {
    if (newLength > delegate.size()) {
      extendTo(newLength);      
    }
    
    while (newLength < delegate.size()) {
      delegate.remove(delegate.size() - 1);
    }
  }
  
  // String methods
  
  public final void push(String value) {
    delegate.add(value);
  }
  
  public final void set(int index, String value) {
    extendTo(index + 1);
    delegate.set(index, value);
  }

  public final void unshift(String value) {
    delegate.add(0, value);
  }
  
  // int methods
  
  public final void push(int value) {
    delegate.add(value);
  }
  
  public final void set(int index, int value) {
    extendTo(index + 1);
    delegate.set(index, value);
  }
  
  public final void unshift(int value) {
    delegate.add(0, value);
  }
  
  // double methods

  public final void push(double value) {
    delegate.add(value);
  }
  
  public final void set(int index, double value) {
    extendTo(index + 1);
    delegate.set(index, value);
  }
  
  public final void unshift(double value) {
    delegate.add(0, value);
  }
  
  // We can rely on autoboxing to make these methods work for
  // JsArrayInteger, String, etc.
  public final Object get(int index) {
    return delegate.get(index);
  }
  
  public final Object shift() {
    Object value = get(0);
    delegate.remove(0);
    return value;
  }
}
