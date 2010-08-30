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
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.jvm.JavaGWTTestCase;

/**
 * Tests for JavaJsArray
 */
public class JavaJsArrayTest extends JavaGWTTestCase {
  public void testCreateArray() {
    JsArrayInteger intArray = (JsArrayInteger) JavaScriptObject.createArray();
    JsArrayString stringArray = (JsArrayString) JavaScriptObject.createArray();
  }
  
  public void testPushShift() {
    JsArrayInteger arr = (JsArrayInteger) JavaScriptObject.createArray();

    assertEquals(0, arr.length());
    arr.push(123);
    assertEquals(1, arr.length());
    arr.push(321);
    assertEquals(2, arr.length());
    
    assertEquals(123, arr.get(0));
    assertEquals(321, arr.get(1));
    
    assertEquals(123, arr.shift());
    assertEquals(1, arr.length());
    
    arr.unshift(999);
    assertEquals(999, arr.get(0));
    assertEquals(999, arr.shift());

    assertEquals(1, arr.length());

    assertEquals(321, arr.shift());
    assertEquals(0, arr.length());
  }
  
  public void testSetLength() {
    JsArrayString arr = (JsArrayString) JavaScriptObject.createArray();
    
    assertEquals(0, arr.length());
    arr.set(5, "foo");
    assertEquals(6, arr.length());
    assertEquals("foo", arr.get(5));
    assertNull(arr.get(4));
    assertNull(arr.get(3));
    assertNull(arr.get(0));
    
    arr.setLength(5);
    assertEquals(5, arr.length());
    arr.setLength(6);
    assertNull(arr.get(5));
  }
  
  public void testJoin() {
    // Join can have some browser-dependent behavior. For now, we'll just test
    // that very standard cases work correctly.
    JsArrayString stringArray = (JsArrayString) JavaScriptObject.createArray();
    stringArray.push("Oh");
    stringArray.push("hi");
    stringArray.push("there");
    assertEquals("Oh hi there", stringArray.join(" "));
    assertEquals("Oh, hi, there", stringArray.join(", "));
    assertEquals("Oh,hi,there", stringArray.join());
    
    JsArrayInteger intArray = (JsArrayInteger) JavaScriptObject.createArray();
    for (int i = 0; i < 5; i++) {
      intArray.push(i);
    }
    assertEquals("0,1,2,3,4", intArray.join());
  }
}
