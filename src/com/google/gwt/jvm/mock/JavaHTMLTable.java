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

import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTMLTable;


public class JavaHTMLTable extends JavaUIObject {

  public JavaHTMLTable(final HTMLTable table) {
    super(table);
  }

  public int getDOMRowCount(final Element element) {
    return element.getChildCount();
  }

  public int getDOMCellCount(final Element element, final int row) {
    final Node child = element.getChild(row);
    return child.getChildCount();
  }

}
