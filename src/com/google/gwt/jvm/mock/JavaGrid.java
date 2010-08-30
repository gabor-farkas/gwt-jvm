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
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;

public class JavaGrid {

  public static void addRows(com.google.gwt.user.client.Element userClientTable, int rows,
      int columns) {
    Element table = userClientTable;
    addRows(table, rows, columns);
  }

  public static void addRows(Element table, int rows, int columns) {
    Document $doc = Document.get();
    Element td = $doc.createElement("td");
    td.setInnerHTML("&nbsp;");
    Element row = $doc.createElement("tr");
    for (int cellNum = 0; cellNum < columns; cellNum++) {
      Node cell = td.cloneNode(true);
      row.appendChild(cell);
    }
    table.appendChild(row);
    for (int rowNum = 1; rowNum < rows; rowNum++) {
      table.appendChild(row.cloneNode(true));
    }
  }
}
