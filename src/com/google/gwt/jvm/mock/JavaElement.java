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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.WeakHashMap;

public class JavaElement extends JavaNode {
  private EventListener listener;

  protected JavaElement(final DomElement element) {
    super(element);
  }

  public DomElement getHtmlUnitElement() {
    return (DomElement) node;
  }

  public Element getGwtElement() {
    return wrap(this);
  }

  public boolean hasAttribute(final String name) {
    return getHtmlUnitElement().hasAttribute(name);
  }

  public String getAttribute(final String name) {
    // special-case: lazily calculate style attribute
    if ("style".equals(name)) {
      JavaStyle style = styleForElement.get(node);
      return style == null ? "" : style.toHtmlString();
    }
    return getHtmlUnitElement().getAttribute(name);
  }

  public void setAttribute(String name, String value) {
    if (value == null) {
      value = DomElement.ATTRIBUTE_NOT_DEFINED;
    }

    // HtmlUnit won't identify "ID" as an id for an element.
    if (name.equalsIgnoreCase("id")) {
      name = "id";
    }
    getHtmlUnitElement().setAttribute(name, value);
  }

  public void removeAttribute(final String name) {
    final DomElement element = getHtmlUnitElement();
    if (element.hasAttribute(name)) {
      element.removeAttribute(name);
    }
  }

  public boolean isMultiple() {
    return hasProperty("multiple");
  }

  // NOTE(patcoleman): whitelist for all property->attribute renames in browsers.
  private String propertyAsAttribute(String property) {
    if ("className".equals(property)) {
      return "class";
    }
    return property;
  }

  public boolean hasProperty(final String property) {
    String attribute = propertyAsAttribute(property);
    return getHtmlUnitElement().hasAttribute(attribute);
  }

  public String getPropertyString(final String property) {
    String attribute = propertyAsAttribute(property);
    return getAttribute(attribute);
  }

  public void setPropertyString(final String property, final String value) {
    String attribute = propertyAsAttribute(property);
    setAttribute(attribute, value);
  }

  public boolean getPropertyBoolean(final String property) {
    String attribute = propertyAsAttribute(property);
    return hasProperty(property) && Boolean.valueOf(getAttribute(attribute));
  }

  public void setPropertyBoolean(final String property, final boolean value) {
    String attribute = propertyAsAttribute(property);
    setAttribute(attribute, Boolean.toString(value));
  }

  public int getPropertyInt(final String property) {
    if (hasProperty(property)) {
      final String value = getPropertyString(property);
      try {
        return Integer.parseInt(value);
      } catch (final NumberFormatException e) {
        // fall thru to default value;
      }
    }

    return 0;
  }

  public void setPropertyInt(final String property, final int value) {
    setPropertyString(property, "" + value);
  }

  public int getSelectedIndex() {
    return hasProperty("selected") ? getPropertyInt("selected") : -1;
  }

  public void setSelectedIndex(final int index) {
    setPropertyInt("selected", index);
  }

  private HashMap<String, Object> propertyObjects;

  public final Object getPropertyObject(String name) {
    return propertyObjects == null ? null : propertyObjects.get(name);
  }

  public final void setPropertyObject(String name, Object value) {
    if (propertyObjects == null) {
      propertyObjects = new HashMap<String, Object>();
    }

    propertyObjects.put(name, value);
  }

  public final JavaScriptObject getPropertyJSO(String name) {
    return wrap(getPropertyObject(name));
  }

  public final void setPropertyJSO(String name, JavaScriptObject value) {
    setPropertyObject(name, unwrap(value));
  }

  private final WeakHashMap<DomNode, JavaStyle> styleForElement
      = new WeakHashMap<DomNode, JavaStyle>();

  public Style getStyle() {
    JavaStyle style = styleForElement.get(node);
    if (style == null) {
      style = new JavaStyle(getHtmlUnitElement());
      styleForElement.put(node, style);
    }
    return wrap(style);
  }

  public String getValue() {
    return getAttribute("value");
  }

  public void setValue(final String text) {
    setAttribute("value", text);
  }

  public String getText() {
    return getAttribute("text");
  }

  public void setText(final String text) {
    setAttribute("text", text);
  }

  public String getHref() {
    return getAttribute("href");
  }

  public void setHref(final String href) {
    setAttribute("href", href);
  }

  public void setEventListener(final EventListener listener) {
    this.listener = listener;
  }

  protected void invokeEventHandlers(final JavaEvent event) {
    if (listener != null) {
      listener.onBrowserEvent((Event) wrap(event));
    }
  }

  HtmlElement getHtmlElement() {
    if (node instanceof HtmlElement) {
      return (HtmlElement) node;
    }
    return null;
  }

  public void click() {
    if (listener != null) {
      listener.onBrowserEvent((Event) wrap(new JavaEvent("click")));
    }

    HtmlElement htmlElement = getHtmlElement();
    if (htmlElement != null) {
      try {
        htmlElement.click();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public EventListener getEventListener() {
    return listener;
  }

  public int getWidth() {
    return 0;
  }

  public int getRows() {
    return getPropertyInt("rows");
  }

  public void setRows(final int rows) {
    setPropertyInt("rows", rows);
  }

  public String getClassName() {
    return getPropertyString("className");
  }

  public void setClassName(final String className) {
    setPropertyString("className", className);
  }

  public static JavaElement javaElement(final DomElement element) {
    return (JavaElement) javaNode(element);
  }

  public String getType() {
    return getAttribute("type");
  }

  public NodeList<com.google.gwt.dom.client.Element> getElementsByTagName(String name) {
    name = name.toLowerCase();
    return wrap(new JavaNodeList<com.google.gwt.dom.client.Element>(
        getHtmlUnitElement().getElementsByTagName(name)));
  }

  public int getSelectLength() {
    return getHtmlUnitElement().getElementsByTagName("option").getLength();
  }

  public void setChecked(boolean checked) {
    // Browsers may set it to checked visually whenever the attribute is present, including
    // when checked="false". safer to remove completely.
    if (!checked) {
      removeAttribute("checked");
    } else {
      setAttribute("checked", "true");
    }
  }

  public boolean isChecked() {
    // Keeping with browser implementations, an element is unchecked iff it doesn't have the attr.
    return hasAttribute("checked");
  }

  public void setHtmlFor(String html) {
    setPropertyString("htmlFor", html);
  }

  public int getTabIndex() {
    return getPropertyInt("tabIndex");
  }

  public void setTabIndex(int index) {
    setPropertyInt("tabIndex", index);
  }

  public boolean isDefaultChecked() {
    return Boolean.parseBoolean(getAttribute("checked"));
  }

  public void setDefaultChecked(boolean checked) {
    setAttribute("checked", checked + "");
  }

  public boolean hasChildNodes() {
    return getHtmlUnitElement().hasChildNodes();
  }

  public void setName(String name) {
    setAttribute("name", name);
  }

  public String getName() {
    return getAttribute("name");
  }

  public void addClassName(String styleName) {
    String currentStyle = getClassName();
    setClassName(currentStyle + " " + styleName);
  }

  public void setId(String id) {
    setAttribute("id", id);
  }

  public final String getId() {
    return getAttribute("id");
  }

  public void setTitle(String title) {
    setAttribute("title", title);
  }

  public String getTitle() {
    return getAttribute("title");
  }

  public void setSize(int size) {
    setAttribute("size", String.valueOf(size));
  }

  public int getSize() {
    String s = getAttribute("size");
    return s == null || s.isEmpty() ? 0 : Integer.valueOf(s);
  }

  public void setInnerHTML(String html) {
    node.removeAllChildren();
    try {
      HTMLParser.parseFragment(node, html);
    } catch (SAXException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String getInnerHtml() {
    Node child = this.getFirstChild();
    StringBuilder builder = new StringBuilder();
    while (child != null) {
      JavaNode javaChild = unwrap(child);
      builder.append(javaChild.getOuterHtml());
      child = child.getNextSibling();
    }
    return builder.toString();
  }

  @Override
  public String getOuterHtml() {
    String tagName = getHtmlElement().getTagName();
    // ouch - browser special-case:
    if ("br".equals(tagName)) {
      return "<br />";
    }

    // build up opening and closing tags
    StringBuilder openTag = new StringBuilder().append("<").append(tagName);
    for (Entry<String, DomAttr> attribute : getHtmlElement().getAttributesMap().entrySet()) {
      openTag.append(" ").append(attribute.getKey()).append("=\"")
             .append(attribute.getValue().getValue()).append("\"");
    }

    // special-case: lazily evaluate the style attribute from a JavaStyle object
    JavaStyle style = styleForElement.get(node);
    if (style != null) {
      String styleString = style.toHtmlString();
      if (styleString != null && !"".equals(styleString.trim())) {
        openTag.append(" style=\"").append(styleString).append("\"");
      }
    }

    openTag.append(">");
    StringBuilder closeTag = new StringBuilder().append("</").append(tagName).append(">");

    // build and we're done
    return new StringBuilder()
        .append(HtmlEntities.convertUnicodeToEntities(openTag.toString()))
        .append(getInnerHtml())
        .append(HtmlEntities.convertUnicodeToEntities(closeTag.toString())).toString();
  }

  public final int getClientWidth() {
    return 1024;
  }

  public final int getClientHeight() {
    return 768;
  }

  public final String getDir() {
    throw new UnsupportedOperationException();
  }

  public final String getLang() {
    throw new UnsupportedOperationException();
  }

  public final int getOffsetHeight() {
    throw new UnsupportedOperationException();
  }

  public final int getOffsetLeft() {
    throw new UnsupportedOperationException();
  }

  public final Element getOffsetParent() {
    throw new UnsupportedOperationException();
  }

  public final int getOffsetTop() {
    throw new UnsupportedOperationException();
  }

  public final int getOffsetWidth() {
    throw new UnsupportedOperationException();
  }

  public final int getScrollTop() {
    return 0;
  }

  public final void setScrollTop(int scrollTop) {
  }

  public final int getScrollWidth() {
    return 0;
  }

  public final void setScrollWidth(int scrollWidth) {
  }

  // Needed by InputElement, which is sometimes casted directly from Element
  private int maxLength = 20;

  public int getMaxLength() {
    return maxLength;
  }

  public void setMaxLength(int maxLength) {
    this.maxLength = maxLength;
  }
  
  public void focus() {
    getHtmlElement().focus();
    
    // TODO(gentle): Also generate an onfocus event.
  }
}
