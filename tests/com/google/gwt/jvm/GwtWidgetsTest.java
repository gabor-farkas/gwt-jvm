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

import static com.google.gwt.jvm.asm.Type.invokeMethod;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 */
public class GwtWidgetsTest extends JavaGWTTestCase {

  public void testLabelInstantiation() throws Exception {
    final Label label = new Label();
    label.setText("Hello World!");
    assertEquals("Hello World!", label.getText());
    final boolean visible = label.isVisible();
    assertTrue(visible);
  }

  public void testInstantiationOfHorizontalPanel() throws Exception {
    final HorizontalPanel panel = new HorizontalPanel();
    panel.setWidth("100%");
    final Label label = new Label("Hello World");
    panel.add(label);
    assertEquals(1, panel.getWidgetCount());
    final Label widget = (Label) panel.getWidget(0);
    assertSame(label, widget);
    assertEquals("Hello World", widget.getText());
  }

  public void testButtonClickListenerFiresCorrectly() throws Exception {
    final HorizontalPanel panel = new HorizontalPanel();
    final Label label = new Label("Hello World");
    final TextBox inputField = new TextBox();
    final Button submitButton = new Button("Click Me!");
    submitButton.addClickHandler(new ClickHandler() {
      public void onClick(final ClickEvent event) {
        inputField.setText("Click happened");
      }
    });
    panel.setTitle("Main UI");
    panel.add(label);
    panel.add(inputField);
    panel.add(submitButton);
    invokeMethod(panel, "doAttachChildren");

    submitButton.click();
    final Label actualLabel = (Label) panel.getWidget(0);
    assertSame(actualLabel, actualLabel);
    assertEquals("Hello World", actualLabel.getText());
    final TextBox actualTextBox = (TextBox) panel.getWidget(1);
    assertSame(inputField, actualTextBox);
    assertEquals("Click happened", inputField.getText());
    final Button actualButton = (Button) panel.getWidget(2);
    assertSame(submitButton, actualButton);
    assertEquals("Click Me!", actualButton.getText());
  }

  // Disabled by jat so the new GWT can land
  public void disabled_testTree() throws Exception {
    final Tree tree = new Tree();
    tree.addItem("Hello");
    final TreeItem item = tree.getItem(0);
    assertEquals("Hello", item.getText());
    tree.setSelectedItem(item);
    assertEquals(tree.getSelectedItem(), item);
  }

  // Disabled by jat so the new GWT can land
  public void disabled_testTreeGlobalStateBug() throws Exception {
    Tree tree = new Tree();
    tree.addItem("Hello");
    TreeItem item = tree.getItem(0);
    assertEquals("Hello", item.getText());
    tree.setSelectedItem(item);
    assertEquals(tree.getSelectedItem(), item);

    beforeEachTest();

    tree = new Tree();
    tree.addItem("Hello");
    item = tree.getItem(0);
    assertEquals("Hello", item.getText());
    tree.setSelectedItem(item);
    assertEquals(tree.getSelectedItem(), item);
  }

  public void testTextBoxValueAndVisibility() throws Exception {
    final TextBox textBox = new TextBox();
    textBox.setText("ABC");
    assertEquals("ABC", textBox.getText());

    textBox.setVisible(false);
    assertFalse(textBox.isVisible());
  }

  public void testTextAreaValueAndVisibility() throws Exception {
    final TextArea textArea = new TextArea();
    textArea.setText("ABC");
    assertEquals("ABC", textArea.getText());

    textArea.setVisible(false);
    assertFalse(textArea.isVisible());

    textArea.setVisibleLines(10);
    assertEquals(10, textArea.getVisibleLines());
  }

  public void testHistory() throws Exception {
    final StringBuilder builder = new StringBuilder();
    History.addValueChangeHandler(new ValueChangeHandler<String>(){
      @Override
      public void onValueChange(final ValueChangeEvent<String> event) {
        builder.append(event.getValue()).append(";");
      }
    });

    History.newItem("new");
    assertEquals("new", History.getToken());
    assertEquals("new;", builder.toString());
    History.newItem("new2");
    assertEquals("new2", History.getToken());
    assertEquals("new;new2;", builder.toString());
    History.back();
    assertEquals("new", History.getToken());
    assertEquals("new;new2;", builder.toString());
  }

  public void testDomToStringShouldPrintXMLOfContent() throws Exception {
    final Element div = DOM.createDiv();
    div.appendChild(DOM.createSpan());
    assertEquals("<div>\n  <span>\n  </span>\n</div>\n", div.toString());
  }

  public void testGridCreation() throws Exception {
    final Grid grid = new Grid(1, 1);
    grid.setWidget(0, 0, new Label("Hello"));

    assertEquals("Hello", ((Label) grid.getWidget(0, 0)).getText());
  }

  public void testFlexTableCreation() throws Exception {
    final FlexTable table = new FlexTable();
    assertNotNull(table);

    assertEquals(0, table.getRowCount());

    table.setWidget(0, 0, new Label("Hello"));
    assertEquals(1, table.getCellCount(0));
    table.setWidget(2, 2, new Label("Hello"));
    assertEquals(0, table.getCellCount(1));
    assertEquals(3, table.getCellCount(2));
  }

  public void testTextBoxWrapWorks() throws Exception {
    final BodyElement bodyElement = Document.get().getBody();
    final InputElement input = Document.get().createTextInputElement();
    bodyElement.appendChild(input);
    assertNotNull(TextBox.wrap(input));
  }

  public void testEnablingAndDisablingInputs() throws Exception {
    final TextBox inputField = new TextBox();
    assertTrue(inputField.isEnabled());
    inputField.setEnabled(false);
    assertFalse(inputField.isEnabled());
    inputField.setEnabled(true);
    assertTrue(inputField.isEnabled());
  }

  public void testListBoxCreationAndUse() throws Exception {
    final ListBox listBox = new ListBox();
    assertNotNull(listBox);

    listBox.addItem("test1");
    listBox.addItem("test2");
    listBox.addItem("test3");
    listBox.addItem("test4");
    assertEquals(4, listBox.getItemCount());
    assertEquals("test1", listBox.getItemText(0));

    listBox.setSelectedIndex(2);
    assertEquals(2, listBox.getSelectedIndex());
  }

  public void testItShouldSupportImages() throws Exception {
    final Image image = new Image();
    image.setUrl("http://server");
    assertEquals("http://server", image.getUrl());
  }

  public void testCheckBoxCreationAndUse() throws Exception {
    final CheckBox box = new CheckBox();
    final boolean curValue = box.getValue();
    box.setValue(!curValue);
    assertEquals(!curValue, box.getValue().booleanValue());
    box.setValue(curValue);
    assertEquals(curValue, box.getValue().booleanValue());
  }

  public static class UiBinderWidget extends Composite {
    interface Binder extends UiBinder<Widget, UiBinderWidget> {}
    private static final Binder BINDER = GWT.create(Binder.class);
    @UiField SpanElement span;
    @UiField DivElement div;
    public UiBinderWidget() {
      initWidget(BINDER.createAndBindUi(this));
    }
  }

  public void testUiBinder() throws Exception {
    final UiBinderWidget widget = new UiBinderWidget();
    assertNotNull(widget.span);
    assertNotNull(widget.div);
  }

  public void testHasChildNodes() throws Exception {
    final Element div = DOM.createDiv();
    assertFalse(div.hasChildNodes());
    div.appendChild(DOM.createDiv());
    assertTrue(div.hasChildNodes());
  }

  public void testSetAndGetName() throws Exception {
    final CheckBox checkbox = new CheckBox();
    final String expectedName = "foo";
    checkbox.setName(expectedName);
    assertEquals(expectedName, checkbox.getName());
  }

  public void testDateBox(){
    SimplePanel panel = new SimplePanel();
    DateBox dateBox = new DateBox();
    panel.add(dateBox);
  }

  public void testSetAndGetHref() throws Exception {
    final Anchor anchor = new Anchor();
    final String url = "test";
    anchor.setHref(url);
    assertEquals(url, anchor.getHref());
  }

  public void testWidgetEventListenerFiresCorrectly() throws Exception {

    final NativeEvent clickEvent = Document.get().createClickEvent(0, 0, 0, 0, 0,
        false, false, false, false);
    final NativeEvent mouseOverEvent = Document.get().createMouseOverEvent(0, 0, 0, 0, 0,
        false, false, false, false, 0, null);

    final Label label = new Label("Hello World");
    final TextBox inputField = new TextBox();
    final CheckBox checkbox = new CheckBox("Checkbox");
    label.addClickHandler(new ClickHandler() {
      public void onClick(final ClickEvent event) {
        inputField.setText("Label clicked");
      }
    });
    checkbox.addMouseOverHandler(new MouseOverHandler() {
      @Override
      public void onMouseOver(final MouseOverEvent event) {
        inputField.setText("Mouse over checkbox");
      }
    });
    DomEvent.fireNativeEvent(clickEvent, label);
    assertEquals("Label clicked", inputField.getText());
    DomEvent.fireNativeEvent(mouseOverEvent, checkbox);
    assertEquals("Mouse over checkbox", inputField.getText());
  }

  public void testPopPanelWorks() throws Exception {
    final PopupPanel panel = new PopupPanel(true);
    panel.setWidget(new Label("Whoo hooo"));
    panel.show();

    assertTrue(panel.isShowing());

    panel.hide();

    assertFalse(panel.isShowing());
  }
  
  public void testGwtCreateWidget() throws Exception {
    // UIBinder generated code sometimes calls GWT.create() to instantiate
    // widget objects instead of calling new.
    
    Image image = GWT.create(Image.class);
    assertNotNull(image);
    assertEquals(image.getClass().getCanonicalName(),
        Image.class.getCanonicalName());
  }
}
