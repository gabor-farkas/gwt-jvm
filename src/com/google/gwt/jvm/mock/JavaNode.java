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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.KeyCodes;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.WeakHashMap;

/**
 * JavaNode is a mock for Node. It wraps htmlunit's DomNode.
 * 
 * JavaNode is also an overlay type over Node.
 * 
 * You can use the static javaNode(node) to get the JavaNode from a Node, and
 * javaNode.getGwtNode() to go the other way. 
 */
public class JavaNode {

  protected final DomNode node;

  /**
   * Enumeration of Html Entities which are used by Gwt widgets.
   */
  protected enum HtmlEntities {
    NBSP("&nbsp;", "\u00A0"),
    RAQUO("&raquo;", "\u00BB"),
    LAQUO("&laquo;", "\u00AB");

    // NOTE(patcoleman): Stored once to avoid repeated array cloning.
    static HtmlEntities[] ALL_ENTITIES = HtmlEntities.values();

    private String htmlEntity;
    private String unicodeValue;

    private HtmlEntities(String htmlEntity, String unicodeValue) {
      this.htmlEntity = htmlEntity;
      this.unicodeValue = unicodeValue;
    }

    static String convertEntitiesToUnicode(String html) {
      String result = html;
      for (HtmlEntities entity : ALL_ENTITIES) {
        result = result.replace(entity.htmlEntity, entity.unicodeValue);
      }
      return result;
    }

    static String convertUnicodeToEntities(String xml) {
      String result = xml;
      for (HtmlEntities entity : ALL_ENTITIES) {
        result = result.replace(entity.unicodeValue, entity.htmlEntity);
      }
      return result;
    }
  }

  public JavaNode(final DomNode node) {
    this.node = node;
  }

  public org.w3c.dom.Document getOwnerHtmlUnitPage() {
    return node.getPage();
  }

  /**
   * Get a list of all the ancestors of this node, from the node's parent
   * to the document.
   * 
   * The element must be in the DOM.
   * 
   * @return A list of Nodes up the DOM hierarchy from this element to the
   * containing document.
   */
  public ArrayList<Node> getAncestors() {
    ArrayList<Node> ancestors = new ArrayList<Node>();
    Node parent = wrap(this);
    do {
      parent = parent.getParentNode();
      if (parent == null) {
        throw new RuntimeException("Event recipient is not in a DOM");
      }

      ancestors.add(parent);
    } while (parent.getNodeType() != Node.DOCUMENT_NODE);

    return ancestors;
  }

  public Element getFirstChild() {
    return wrap(javaNode(node.getFirstChild()));
  }

  public final Node getLastChild() {
    return wrap(javaNode(node.getLastChild()));
  }

  public Element getNextSibling() {
    return wrap(javaNode(node.getNextSibling()));
  }

  public Element getPreviousSibling() {
    return wrap(javaNode(node.getPreviousSibling()));
  }

  public void setInnerText(final String text) {
    node.setTextContent(text);
  }

  public String getInnerText() {
    return node.getTextContent();
  }

  public Node appendChild(final Node newChild) {
    return wrap(node.appendChild(w3cNode(newChild)));
  }

  public Node insertBefore(final Node newChild, final Node before) {
    if (before == null) {
      return appendChild(newChild);
    } else {
      return wrap(node.insertBefore(w3cNode(newChild), w3cNode(before)));
    }
  }

  @Override
  public String toString() {
    return node.asXml();
  }

  public String getOuterHtml() {
    // NOTE(patcoleman): this is assumed to be escaped properly.
    return null; // Ideally, should override in child classes.
  }

  public Node removeChild(final Node child) {
    final JavaNode delegate = unwrap(child);
    JavaWindow.getSelection().willRemoveNode(child);
    node.removeChild(delegate.node);
    return child;
  }

  public final Node cloneNode(final boolean deep) {
    return wrap(javaNode(node.cloneNode(deep)));
  }

  public String getNodeValue() {
    return node.getNodeValue();
  }

  public <T extends Node> NodeList<T> getChildNodes() {
    return wrap(new JavaNodeList<T>(node.getChildNodes()));
  }

  public DomNode getNode() {
    return node;
  }

  private static WeakHashMap<DomNode, JavaNode> javaNodeForDomNode
                      = new WeakHashMap<DomNode, JavaNode>();

  public static JavaNode javaNode(final DomNode element) {
    if (element == null) {
      return null;
    }
    JavaNode javaNode = javaNodeForDomNode.get(element);
    if (javaNode == null) {
      if (element instanceof DomElement) {
        javaNode = new JavaElement((DomElement) element);
      } else if (element instanceof DomText) {
        javaNode = new JavaText((DomText) element);
      } else {
        javaNode = new JavaNode(element);
      }
      javaNodeForDomNode.put(element, javaNode);
    }
    return javaNode;
  }

  public static JavaNode javaNode(final Node node) {
    return unwrap(node);
  }

  public static DomNode w3cNode(final Node newChild) {
    final JavaNode delegate = unwrap(newChild);
    return delegate.node;
  }

  public Node getGwtNode() {
    return wrap(this);
  }

  public JavaNode getParentElement() {
    final DomNode parentNode = node.getParentNode();
    if (parentNode instanceof HtmlPage) {
      return null;
    }
    return javaNode(parentNode);
  }

  public String getNodeName() {
    return node.getNodeName();
  }

  public short getNodeType() {
    return node.getNodeType();
  }

  public Node getParentNode() {
    return wrap(javaNode(node.getParentNode()));
  }

  public Node replaceChild(Node newChild, Node oldChild) {
    JavaElement newChildDelegate = unwrap(newChild);
    JavaElement oldChildDelegate = unwrap(oldChild);
    node.replaceChild(newChildDelegate.node, oldChildDelegate.node);
    return newChild;
  }

  /**
   * Fetches the position of the node in the children of its parent.
   * @return The position of the node in its parent. 0-based.
   */
  public int positionInParent() {
    int count = 0;

    // Walk backwards through the node's siblings, counting as we go
    for (Node n = wrap(this); n != null; n = n.getPreviousSibling()) {
      count++;
    }

    // The position index is one less than the number of nodes traversed
    return count - 1;
  }

  /**
   * This is called after all event listeners have been notified of the event.
   * This does the default native browser action for the event. For typing, it
   * inserts characters. For clicking on links, it should navigate away, etc.
   */
   void performNativeEventAction(JavaEvent event) {
    // TODO(gentle): Check that the element which keypresses are sent to
    // is editable (contentEditable or an input element)
    if (event.getType().equals("keydown")) {
      switch (event.getKeyCode()) {
        case KeyCodes.KEY_DELETE:
          JavaWindow.getSelection().deleteCharacterAtCursor();
          break;
        case KeyCodes.KEY_LEFT:
          JavaWindow.getSelection().moveLeft();
          break;
        case KeyCodes.KEY_RIGHT:
          JavaWindow.getSelection().moveRight();
          break;
          // TODO(gentle): add up and down.
      }
    }

    if (event.getType().equals("keypress")) {
      try {
        // TODO(gentle): Check if the node is within a contenteditable region.
        // if so, type text. (Htmlunit hasn't implemented contenteditable.)

        if (node instanceof HtmlElement) {
          if (node instanceof HtmlTextInput) {
            JavaSelection selection = JavaWindow.getSelection();
            if (selection.focusNode() == getGwtNode()) {
              ((HtmlTextInput) node).setSelectionStart(selection.focusOffset());
              ((HtmlTextInput) node).setSelectionEnd(selection.anchorOffset());
            }
          }
          ((HtmlElement) node).type(event.getCharacter(), event.getShiftKey(),
              event.getCtrlKey(), event.getAltKey());
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
