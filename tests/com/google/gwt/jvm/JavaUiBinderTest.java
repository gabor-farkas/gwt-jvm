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
package com.google.gwt.jvm;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * Test of Uibinder creation.
 */
public class JavaUiBinderTest extends JavaGWTTestCase {

  private TestingWidget testingWidget;
  private SecondTestingWidget secondTestingWidget;
  private SpanElementBinder spanElemBinder;
  private DivElementBinder divElemBinder;

  @SuppressWarnings("cast")
  public void testGwtCreateUiBinderShouldReturnMockWhichInstantiatesUiFileds() {
    testingWidget = new TestingWidget();
    assertTrue(testingWidget.innerLabel instanceof Label);
    assertTrue(testingWidget.documentLayoutPanel instanceof DockLayoutPanel);
    assertTrue(testingWidget.stackLayoutPanel instanceof StackLayoutPanel);

    secondTestingWidget = new SecondTestingWidget();
    assertTrue(secondTestingWidget.radioButton instanceof RadioButton);
    assertTrue(secondTestingWidget.htmlPanel instanceof HTMLPanel);

    spanElemBinder = new SpanElementBinder();
    assertTrue(spanElemBinder.divElement instanceof DivElement);
    assertTrue(spanElemBinder.anchorElement instanceof AnchorElement);

    divElemBinder = new DivElementBinder();
    assertTrue(divElemBinder.divElement instanceof DivElement);
    assertTrue(divElemBinder.anchorElement instanceof AnchorElement);
  }
  
  static class TestingWidget extends SimplePanel {

    interface Binder extends UiBinder<Widget, TestingWidget> {}

    @UiField Label innerLabel;
    @UiField HTML bodyHtml;
    @UiField TextBox textBox;
    @UiField StackLayoutPanel stackLayoutPanel;
    @UiField DockLayoutPanel documentLayoutPanel;

    private static final Binder BINDER = GWT.create(Binder.class);

    public TestingWidget() {
      add(BINDER.createAndBindUi(this));
    }
    
    @UiFactory StackLayoutPanel getStackLatoutPanel() {
      return new StackLayoutPanel(Unit.EM);
    }
    
    @UiFactory DockLayoutPanel getDockLayoutPanel() {
      return new DockLayoutPanel(Unit.EM);
    }
  }

  static class SecondTestingWidget extends Composite {

    @UiTemplate("SecondTestingWidget.ui.xml")
    interface Binder extends UiBinder<Widget, SecondTestingWidget> {}

    @UiField RadioButton radioButton;
    @UiField DivElement divElement;
    @UiField HTMLPanel htmlPanel;

    private static final Binder BINDER = GWT.create(Binder.class);

    public SecondTestingWidget() {
      initWidget(BINDER.createAndBindUi(this));
    }
  }

  static class SpanElementBinder extends UIObject {

    @UiTemplate("ElementBinder.ui.xml")
    interface Binder extends UiBinder<SpanElement, SpanElementBinder> {}

    @UiField DivElement divElement;
    @UiField AnchorElement anchorElement;

    private static final Binder BINDER = GWT.create(Binder.class);

    public SpanElementBinder() {
      setElement(BINDER.createAndBindUi(this));
      divElement.appendChild(Document.get().createDivElement());
    }
  }

  static class DivElementBinder extends UIObject {

    @UiTemplate("ElementBinder.ui.xml")
    interface Binder extends UiBinder<DivElement, DivElementBinder> {}

    @UiField DivElement divElement;
    @UiField AnchorElement anchorElement;

    private static final Binder BINDER = GWT.create(Binder.class);

    public DivElementBinder() {
      setElement(BINDER.createAndBindUi(this));
    }
  }
}
