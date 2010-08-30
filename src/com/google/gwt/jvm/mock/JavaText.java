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

import static com.google.gwt.jvm.JavaJSObject.wrap;

import com.google.gwt.dom.client.Text;

import com.gargoylesoftware.htmlunit.html.DomText;

public class JavaText extends JavaNode {
  protected JavaText(final DomText node) {
    super(node);
  }

  DomText getTextNode() {
    return (DomText) node;
  }

  public final void deleteData(int offset, int length) {
    getTextNode().deleteData(offset, length);
  }

  public final String getData() {
    return getTextNode().getData();
  }

  public final int getLength() {
    return getTextNode().getLength();
  }

  public final void insertData(int offset, String data) {
    getTextNode().insertData(offset, data);
  }

  public final void replaceData(int offset, int length, String data) {
    getTextNode().replaceData(offset, length, data);
  }

  public final void setData(String data) {
    getTextNode().setData(data);
  }

  public final Text splitText(int offset) {
    return wrap(javaNode(getTextNode().splitText(offset)));
  }

  @Override
  public final String getOuterHtml() {
    return HtmlEntities.convertUnicodeToEntities(
        getData()
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;"));
  }
}
