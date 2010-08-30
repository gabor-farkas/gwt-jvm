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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;

public class JavaFlexTable extends JavaHTMLTable {

  public JavaFlexTable(final FlexTable table) {
    super(table);
  }

  public int insertRow(final int beforeRow) {
    return beforeRow;
  }

  public static void addCells(final Element table, final int row, final int columns) {
    final Node child = table.getChild(row);
    for (int column = 0; column < columns; column++) {
      final TableCellElement td = Document.get().createTDElement();
      child.appendChild(td);
    }
  }

}
