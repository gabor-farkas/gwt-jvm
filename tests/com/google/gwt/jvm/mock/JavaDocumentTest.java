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

import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.jvm.JavaGWTTestCase;

public class JavaDocumentTest extends JavaGWTTestCase {
  public void testEmptyDocumentHasBody() {
    Document doc = JavaDocument.get();
    BodyElement body = doc.getBody();
    assertNotNull(body);
    assertEquals(0, body.getChildCount());
  }
  
  public void testResetClearsDocument() {
    Document doc = JavaDocument.get();
    Element div = doc.createDivElement();
    doc.getBody().appendChild(div);
    assertEquals(1, doc.getBody().getChildCount());
    JavaDocument.reset(this.getBrowserEmulator());
    assertEquals(0, JavaDocument.get().getBody().getChildCount());
  }
  
  public void testGetElementByUnknownIdReturnsNull() {
    Element element = JavaDocument.get().getElementById("doesnotexist");
    assertNull(element);
  }
  
  public void testGetElementByIdUsingSetId() {
    Document doc = JavaDocument.get();
    Element div = doc.createDivElement();
    doc.getBody().appendChild(div);
    div.setId("foo");
    assertSame(div, doc.getElementById("foo"));
    
    // Also test setId before appendChild
    Element div2 = doc.createDivElement();
    div2.setId("bar");
    doc.getBody().appendChild(div2);
    assertSame(div2, doc.getElementById("bar"));
  }
  
  public void testGetElementByIdUsingSetAttribute() {
    Document doc = JavaDocument.get();
    
    // setId should function the same as setAttribute
    Element div = doc.createDivElement();
    doc.getBody().appendChild(div);
    div.setAttribute("id", "baz");
    assertSame(div, doc.getElementById("baz"));
    
    Element div2 = doc.createDivElement();
    doc.getBody().appendChild(div2);
    // id should be case insensitive
    div2.setAttribute("ID", "fooz");
    assertSame(div2, doc.getElementById("fooz"));
  }
}
