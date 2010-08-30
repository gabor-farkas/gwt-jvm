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
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;

public class JavaCellFormatter {

  public JavaCellFormatter(CellFormatter delegate) {    
  }
  
  public Element getCellElement(Element table, int row, int col) {
    Node rowElement = table.getChild(row);
    if (!rowElement.getNodeName().equals("tr")) {
      throw new IllegalStateException(rowElement.getNodeName());
    }
    return (Element) rowElement.getChild(col);
  }
}
