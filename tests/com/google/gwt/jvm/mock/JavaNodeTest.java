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
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.jvm.JavaGWTTestCase;

/**
 */
public class JavaNodeTest extends JavaGWTTestCase {

  public void testItShouldReturnNullForParentOfHTML() throws Exception {
    JavaDocument doc = JavaDocument.getJavaDocument();
    BodyElement body = doc.getBody();
    Element html = body.getParentElement();
    assertNull(html.getParentElement());
  }

  public void testItShouldHaveObjectIdentity() throws Exception {
    JavaDocument doc = JavaDocument.getJavaDocument();
    assertSame(doc.getBody(), doc.getBody());
    assertSame(doc.getBody().getParentElement(), doc.getBody().getParentElement());
  }

  public void testGetChildNodes() throws Exception {
    Document document = Document.get();
    Element root = document.createDivElement();
    assertEquals(root.toString(), 0, root.getChildCount());
    DivElement d1 = document.createDivElement();
    DivElement d2 = document.createDivElement();
    DivElement d3 = document.createDivElement();
    root.appendChild(d1);
    root.appendChild(d2);
    root.appendChild(d3);

    assertEquals(3, root.getChildNodes().getLength());
    assertEquals(3, root.getChildCount());
    assertSame(d1, root.getChild(0));
    assertSame(d2, root.getChildNodes().getItem(1));
    assertSame(d3, root.getChild(2));
  }

  public void testItShouldAppendChildrenWhenSetInnerHtmlIsCalled() throws Exception {
    String html = "<div>Hello&gt;&nbsp;&lt;world</div>";
    Document document = Document.get();
    Element root = document.createSpanElement();
    assertEquals(0, root.getChildCount());
    root.setInnerHTML(html);
    assertEquals(1, root.getChildCount());
    Element child = (Element) root.getChild(0);
    System.out.println("ACTUALLY IS " + root.getInnerHTML());
    assertEquals(html, root.getInnerHTML());
    assertEquals("Hello>\u00A0<world", child.getInnerText());
  }

  public void testItShouldAppendMultipleChildrenWhenSetInnerHtmlIsCalled() throws Exception {
    String html = "<div>Hello</div><p>World</p>";
    Document document = Document.get();
    Element root = document.createSpanElement();
    assertEquals(0, root.getChildCount());
    root.setInnerHTML("<span>should get removed</span>");
    root.setInnerHTML(html);
    assertEquals(2, root.getChildCount());
    assertEquals("HelloWorld", root.getInnerText());
    Element child = (Element) root.getChild(0);
    assertEquals(html, root.getInnerHTML());
    assertEquals("Hello", child.getInnerText());
    child = (Element) root.getChild(1);
    assertEquals("World", child.getInnerText());
  }

  public void testElementsByTagName() throws Exception {
    SpanElement span = Document.get().createSpanElement();
    span.appendChild(Document.get().createTextInputElement());
    span.appendChild(Document.get().createSpanElement());
    assertEquals("[HtmlTextInput[<input type=\"text\">]]",
        span.getElementsByTagName("INPUT").toString());
    assertEquals("[HtmlSpan[<span>]]", span.getElementsByTagName("span").toString());
  }

  public void testElementsByTagNameIsCaseInsensitive() throws Exception {
    SpanElement span = Document.get().createSpanElement();
    span.appendChild(Document.get().createTextInputElement());
    assertEquals(span.getElementsByTagName("INPUT").toString(),
        span.getElementsByTagName("input").toString());
  }

  public void testReplaceChild() throws Exception {
    DivElement parent = Document.get().createDivElement();
    DivElement child = Document.get().createDivElement();
    parent.appendChild(child);

    DivElement newChild = Document.get().createDivElement();
    parent.replaceChild(newChild, child);
    NodeList<?> list = parent.getChildNodes();
    assertEquals(1, list.getLength());
    assertSame(newChild, list.getItem(0));
  }

  public void testCastToClientElement() throws Exception {
    DivElement div = Document.get().createDivElement();
    com.google.gwt.user.client.Element oldElement
          = div.<com.google.gwt.user.client.Element>cast();
  }

  public void testSetPropertyObject() throws Exception {
    DivElement div = Document.get().createDivElement();
    Object obj = new Object();
    div.setPropertyObject("foo", obj);
    assertSame(obj, div.getPropertyObject("foo"));
    Document.get().getBody().appendChild(div);

    Element e = Document.get().getBody().getFirstChildElement();
    assertSame(obj, e.getPropertyObject("foo"));
  }
}
