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

import static com.google.gwt.jvm.JavaJSObject.unwrap;
import static com.google.gwt.jvm.JavaJSObject.wrap;
import static com.google.gwt.jvm.mock.JavaElement.javaElement;
import static com.google.gwt.jvm.mock.JavaNode.javaNode;

import com.google.gwt.jvm.GwtBrowserEmulator;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Text;

import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.UrlUtils;

import java.net.URL;

public class JavaDocument {
  public static final URL ABOUT_BLANK_URL = UrlUtils.toUrlSafe("about:blank");
  
  private static JavaDocument globalDocument;
  private HtmlPage document;
  private boolean valid = true;
  private int gwt_uid = 0;

  public JavaDocument(GwtBrowserEmulator browser, URL url) {
    this(browser.loadUrl(url));
  }
  
  public JavaDocument(GwtBrowserEmulator browser) {
    this(browser, ABOUT_BLANK_URL);
  }
  
  private JavaDocument(HtmlPage page) {
    document = page;
  }
    
  public static void reset(GwtBrowserEmulator browser, URL url) {
    if (globalDocument != null) {
      globalDocument.valid = false;
    }
    globalDocument = new JavaDocument(browser, url);
  }

  public static void reset(GwtBrowserEmulator browser) {
    reset(browser, ABOUT_BLANK_URL);
  }
  
  public static Document get() {
    return wrap(getJavaDocument());
  }
  
  public static JavaDocument getJavaDocument() {
    globalDocument.assertValid();
    return globalDocument;
  }
  
  private void assertValid() {
    if (!valid) {
      throw new IllegalStateException("Accesing Document from last test not allowed!"
          + " Global state present!");
    }
  }

  public BodyElement getBody() {
    assertValid();
    return wrap(javaElement(document.getBody()));
  }
  
  public Element getDocumentElement() {
    return wrap(javaElement(document.getDocumentElement()));
  }

  /**
   * Equivalent of document.activeElement
   * https://developer.mozilla.org/en/DOM/Element.activeElement
   * @return The current active element.
   */
  public JavaElement getActiveElement() {
    return javaElement(document.getFocusedElement());
  }
  
  public JavaElement createElement(String tagName) {
    assertValid();
    return javaElement(document.createElement(tagName));
  }
  
  public final Element getElementById(String elementId) {
    return wrap(javaElement(document.getElementById(elementId)));
  }

  public final NodeList<Element> getElementsByTagName(String tagName) {
    return wrap(new JavaNodeList<Element>(document.getElementsByTagName(tagName)));
  }
  
  public <T extends com.google.gwt.dom.client.Node> T appendChild(T newChild) {
    JavaElement element = unwrap(newChild);
    return wrap(document.appendChild(element.node));
  }

  public final String createUniqueId() {
    assertValid();
    // In order to force uid's to be document-unique across multiple modules,
    // we hang a counter from the document.
    return "gwt-uid-" + gwt_uid++;
  }
  
  @Override
  public String toString() {
    return javaNode(document.getDocumentElement()).toString();
  }
  
  public final String getCompatMode() {
    // Pretend to be in strict mode as opposed to quirks mode ("BackCompat") 
    return "CSS1Compat";
  }
  
  public final Text createTextNode(String data) {
    assertValid();
    return wrap(javaNode(new DomText(document, data)));
  }
  
  protected final URL getURLObject() {
    return document.getWebResponse().getRequestSettings().getUrl();
  }
  
  public final String getDomain() {
    return getURLObject().getHost();
  }
  
  public final String getReferrer() {
    throw new UnsupportedOperationException();    
  }
  
  public final String getTitle() {
    return document.getTitleText();
  }

  public final String getURL() {
    return getURLObject().toString();
  }
  
  public final void importNode(Node node, boolean deep) {
    throw new UnsupportedOperationException();
  }
  
  public final void setTitle(String title) {
    document.setTitleText(title);
  }
  
  // This is needed by WebDriver for functionality that GWT doesn't expose,
  // like XPath.
  public HtmlPage getHtmlPage() {
    return document;
  }
}
