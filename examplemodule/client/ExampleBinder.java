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

package client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Simple UIBinder widget
 *
 */
public class ExampleBinder extends Composite {
  private static FavoriteColorWidgetUiBinder uiBinder = GWT.create(FavoriteColorWidgetUiBinder.class);
  interface FavoriteColorWidgetUiBinder extends UiBinder<Widget, ExampleBinder> { }

  @UiField Label greeting;
  @UiField CheckBox red;
  @UiField CheckBox white;
  @UiField CheckBox blue;

  public ExampleBinder() {
    initWidget(uiBinder.createAndBindUi(this));

    greeting.setText("Hi from a uibinder widget");
  }
}
