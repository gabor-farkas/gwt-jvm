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

import static com.google.gwt.jvm.asm.Type.type;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TreeItem.TreeItemImpl;

import java.util.Map;

public class JavaRootPanel {

  public JavaRootPanel(RootPanel rootPanel) {
  }
  
  public static Element getBodyElement() {
    return (Element) (Object)JavaDocument.get().getBody();
  }
  
  @SuppressWarnings("unchecked")
  public static void reset() {
    type(RootPanel.class).<Map>getStaticField("rootPanels").clear();
    new TreeItemImpl(); // calling this constructor sets some magical global state.
  }
}
