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

import static com.google.gwt.jvm.asm.Type.type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.UrlUtils;
import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWTBridge;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.impl.Impl;
import com.google.gwt.core.client.impl.SchedulerImpl;
import com.google.gwt.dev.util.collect.HashMap;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.StyleInjector.StyleInjectorImpl;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.CurrencyList;
import com.google.gwt.i18n.client.Messages;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.client.constants.CurrencyCodeMapConstants;
import com.google.gwt.i18n.client.constants.DateTimeConstantsImpl;
import com.google.gwt.i18n.client.constants.NumberConstantsImpl;
import com.google.gwt.jvm.asm.ClassMap;
import com.google.gwt.jvm.asm.Type;
import com.google.gwt.jvm.mock.JavaButton;
import com.google.gwt.jvm.mock.JavaCellFormatter;
import com.google.gwt.jvm.mock.JavaClientDOMImpl;
import com.google.gwt.jvm.mock.JavaCookies;
import com.google.gwt.jvm.mock.JavaCurrencyCodeMapConstants;
import com.google.gwt.jvm.mock.JavaCurrencyListImpl;
import com.google.gwt.jvm.mock.JavaDOMImpl;
import com.google.gwt.jvm.mock.JavaDateTimeConstantsImpl;
import com.google.gwt.jvm.mock.JavaDebugIdImpl;
import com.google.gwt.jvm.mock.JavaDefaultMessages;
import com.google.gwt.jvm.mock.JavaDocument;
import com.google.gwt.jvm.mock.JavaDuration;
import com.google.gwt.jvm.mock.JavaElementMapperImpl;
import com.google.gwt.jvm.mock.JavaFlexTable;
import com.google.gwt.jvm.mock.JavaFocusImpl;
import com.google.gwt.jvm.mock.JavaGrid;
import com.google.gwt.jvm.mock.JavaHTMLTable;
import com.google.gwt.jvm.mock.JavaHistoryImpl;
import com.google.gwt.jvm.mock.JavaImpl;
import com.google.gwt.jvm.mock.JavaNumberConstantsImpl;
import com.google.gwt.jvm.mock.JavaNumberFormat;
import com.google.gwt.jvm.mock.JavaPrefixTree;
import com.google.gwt.jvm.mock.JavaRootPanel;
import com.google.gwt.jvm.mock.JavaRowFormatter;
import com.google.gwt.jvm.mock.JavaSchedulerImpl;
import com.google.gwt.jvm.mock.JavaStyle;
import com.google.gwt.jvm.mock.JavaTextBoxImpl;
import com.google.gwt.jvm.mock.JavaTimer;
import com.google.gwt.jvm.mock.JavaTree;
import com.google.gwt.jvm.mock.JavaTreeImages;
import com.google.gwt.jvm.mock.JavaUIObject;
import com.google.gwt.jvm.mock.JavaURL;
import com.google.gwt.jvm.mock.JavaWindow;
import com.google.gwt.jvm.mock.JavaWindowImpl;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.impl.DOMImpl;
import com.google.gwt.user.client.impl.ElementMapperImpl;
import com.google.gwt.user.client.impl.HistoryImpl;
import com.google.gwt.user.client.impl.WindowImpl;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeImages;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.UIObject.DebugIdImpl;
import com.google.gwt.user.client.ui.impl.ClippedImageImpl;
import com.google.gwt.user.client.ui.impl.PopupImpl;
import com.google.gwt.user.client.ui.impl.TextBoxImpl;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * This class configures GWT to pretend to be a browser. It replaces the GWT
 * bridge and configures a bunch of delegate methods to point to pure-java mocks
 * of their respective GWT class.
 */
@SuppressWarnings("deprecation")
public class GwtBrowserEmulator {
  protected WebClient webClient = new WebClient();

  /**
   * Create and activate a new GwtBrowserEmulator. Any previous browser emulator
   * objects will be invalidated.
   */
  public GwtBrowserEmulator() {
    configureResetHandlers();
  }

  private void configureResetHandlers() {
    addResetHandler(new ResetHandler() {
      @Override
      public void reset(GwtBrowserEmulator browser, java.net.URL newUrl) {
        JavaWindow.reset();
        JavaDocument.reset(browser, newUrl);
        JavaHistoryImpl.reset();
        JavaRootPanel.reset();
        JavaCookies.reset(browser);
        JavaTimer.reset();
      }
    });
  }

  public static interface ResetHandler {
    void reset(GwtBrowserEmulator browser, java.net.URL newUrl);
  }

  private final List<ResetHandler> resetHandlers = new ArrayList<ResetHandler>();

  public void addResetHandler(ResetHandler handler) {
    resetHandlers.add(handler);
  }

  public void removeResetHandler(ResetHandler handler) {
    resetHandlers.remove(handler);
  }

  public static class JavaGWTBridge extends GWTBridge {

    private final ClassMap classMap = new ClassMap();
    private final Map<Class<?>, Object> instanceMap = new HashMap<Class<?>, Object>();

    public JavaGWTBridge() {
    }

    /**
     * Returns a mock implementation of the given class.
     *
     * <p>
     * If the class is a GWT interface that we have created a mock
     * implementation for, instantiates and returns an instance of our mock
     * implementation.
     *
     * <p>
     * Otherwise, if the to return has been explicitly specified by calling one
     * of the {@code setGWTCreate*} methods in this class, returns the instance
     * or that was specified in the mapping.
     *
     * <p>
     * Throws an exception if an instance of the given class could not be
     * returned by any of the above strategies.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T create(final Class<?> classLiteral) {
      final T uiBinderMock = (T) codeGenerationStrategy.createMockClass(classLiteral);
      if (uiBinderMock != null) {
        return uiBinderMock;
      }

      final T gwtInterfaceMock = (T) createMockForGwtInterface(classLiteral);
      if (gwtInterfaceMock != null) {
        return gwtInterfaceMock;
      }

      final T mappedInstance = (T) instanceMap.get(classLiteral);
      if (mappedInstance != null) {
        return mappedInstance;
      }

      Type mappedType = classMap.map(classLiteral);
      if (automaticGwtCreate && mappedType == null) {
        mappedType = type(classLiteral);
      }

      if (mappedType != null) {
        return mappedType.newInstance();
      }

      // try to map to self
      try {
        T instance = (T) classLiteral.newInstance();
        return instance;
      } catch (Exception e) {
        // throw the exception below instead
      }

      throw new IllegalStateException(String.format("Class %s has no mapped implementation," +
          " and could not be instantiated from self", classLiteral));
    }

    /**
     * Given a class literal, returns a mock instance of that class if it's a
     * GWT interface that we have a mock implementation for. Otherwise, return
     * null.
     */
    private Object createMockForGwtInterface(final Class<?> classLiteral) {
      if (classLiteral.isInterface()) {
        if (Messages.class.isAssignableFrom(classLiteral)) {
          return JavaDefaultMessages.forMessage(classLiteral);
        }
      }
      return null;
    }

    @Override
    public String getVersion() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isClient() {
      return true;
    }

    @Override
    public void log(final String message, final Throwable e) {
      System.err.println(message + "\n");
      if (e != null) {
        e.printStackTrace();
      }
    }

    private boolean automaticGwtCreate = false;
    private GwtCodeGenerationStrategy codeGenerationStrategy = GwtCodeGenerationStrategy.mock();
  }

  private JavaGWTBridge javaGWTBridge;
  private GwtNativeDispatch dispatcher;

  public void setGWTcreateClass(final Class<?> askClass, final Class<?> returnClass) {
    javaGWTBridge.classMap.addImplementor(askClass, returnClass);
  }

  public void setGWTcreateClass(final Class<?> askClass, final String returnClass) {
    javaGWTBridge.classMap.addImplementor(askClass, returnClass);
  }

  public void setGWTcreateClass(final String askClass, final String returnClass) {
    javaGWTBridge.classMap.addImplementor(askClass, returnClass);
  }

  public void setGWTcreateClass(final String askClass, final Class<?> returnClass) {
    javaGWTBridge.classMap.addImplementor(askClass, returnClass);
  }

  public <T> void setGWTcreateInstance(final Class<T> clazz, final T objInstance) {
    javaGWTBridge.instanceMap.put(clazz, objInstance);
  }

  public void setGWTDelegate(final Class<?> nativeClass, final Class<?> mockClass) {
    dispatcher.delegate(nativeClass, mockClass);
  }

  public void setGWTDelegate(final Class<?> nativeClass, final String mockClass) {
    dispatcher.delegate(nativeClass, mockClass);
  }

  public void setGWTDelegate(final String nativeClass, final Class<?> mockClass) {
    dispatcher.delegate(nativeClass, mockClass);
  }

  public void setGWTDelegate(final String nativeClass, final String mockClass) {
    dispatcher.delegate(nativeClass, mockClass);
  }

  public void setCodeGenerationStrategy(GwtCodeGenerationStrategy codeGenerationStrategy) {
    javaGWTBridge.codeGenerationStrategy = codeGenerationStrategy;
  }

  public GwtCodeGenerationStrategy getCodeGenerationStrategy() {
    return javaGWTBridge.codeGenerationStrategy;
  }

  /**
   * Configure the bridge to attempt to automatically instantiate classes when
   * GWT.create() is called and the class has not been registered with
   * setGWTCreateClass().
   *
   * @param value
   */
  public void setAutomaticGwtCreate(boolean value) {
    javaGWTBridge.automaticGwtCreate = value;
  }

  @SuppressWarnings("deprecation")
  protected void resetBridge() {
    javaGWTBridge = new JavaGWTBridge();
//    setGWTcreateClass(BidiPolicy.BidiPolicyImpl.class, BidiPolicy.BidiPolicyImpl.class);
    setGWTcreateClass(DebugIdImpl.class, JavaDebugIdImpl.class);
    setGWTcreateClass(DOMImpl.class, "com.google.gwt.user.client.impl.DOMImplSafari");
    setGWTcreateClass("com.google.gwt.dom.client.DOMImpl",
        "com.google.gwt.dom.client.DOMImplSafari");
    setGWTcreateClass("com.google.gwt.i18n.client.impl.LocaleInfoImpl",
        "com.google.gwt.i18n.client.impl.LocaleInfoImpl");
    setGWTcreateClass("com.google.gwt.i18n.client.impl.CldrImpl",
        "com.google.gwt.i18n.client.impl.CldrImpl");
    setGWTcreateClass("com.google.gwt.user.client.ui.impl.FocusImpl",
        "com.google.gwt.user.client.ui.impl.FocusImpl");
    setGWTcreateClass("com.google.gwt.user.client.ui.impl.TextBoxImpl",
        "com.google.gwt.user.client.ui.impl.TextBoxImpl");
    setGWTcreateClass("com.google.gwt.layout.client.LayoutImpl",
        "com.google.gwt.layout.client.LayoutImpl");
    setGWTcreateClass(TreeImages.class, JavaTreeImages.class);
    setGWTcreateClass(TreeItem.TreeItemImpl.class, TreeItem.TreeItemImpl.class);
    setGWTcreateClass(WindowImpl.class, WindowImpl.class);
    setGWTcreateClass("com.google.gwt.user.client.History", HistoryImpl.class);
    setGWTcreateClass(HistoryImpl.class, HistoryImpl.class);
    setGWTcreateClass(HTMLTable.class, HTMLTable.class);
    setGWTcreateClass(SimplePanel.class, SimplePanel.class);
    setGWTcreateClass(DateTimeConstantsImpl.class, JavaDateTimeConstantsImpl.class);
//    setGWTcreateClass(DateTimeFormatInfoImpl.class, DefaultDateTimeFormatInfo.class);
    setGWTcreateClass(DateBox.DefaultFormat.class, DateBox.DefaultFormat.class);
    setGWTcreateClass(NumberConstantsImpl.class, JavaNumberConstantsImpl.class);
    setGWTcreateClass(CurrencyList.class, JavaCurrencyListImpl.class);
    setGWTcreateClass(CurrencyCodeMapConstants.class, JavaCurrencyCodeMapConstants.class);
    setGWTcreateClass(PopupImpl.class, PopupImpl.class);
    setGWTcreateClass(ClippedImageImpl.class, ClippedImageImpl.class);
    setGWTcreateClass(TextBoxImpl.class, TextBoxImpl.class);
    setGWTcreateClass(SchedulerImpl.class, JavaSchedulerImpl.class);
    setGWTcreateClass(StyleInjectorImpl.class, StyleInjectorImpl.class);
    // TODO(yizhi): Refactor into a Corp/UX/UI setup helper when we have a few
    // more.
//    setGWTcreateClass(MenuButtonDecorator.class, JavaMenuButtonDecorator.class);
    type(GWT.class).invoke("setBridge", javaGWTBridge);
  }

  protected void resetDispatcher() {
    dispatcher = new GwtNativeDispatch();
    dispatcher.delegate(JavaScriptObject.class, JavaJSObject.class);
    dispatcher.delegate(Document.class, JavaDocument.class);
    dispatcher.delegate("com.google.gwt.dom.client.DOMImplSafari", JavaDOMImpl.class);
    dispatcher.delegate("com.google.gwt.dom.client.DOMImpl", JavaDOMImpl.class);
    dispatcher.delegate("com.google.gwt.dom.client.DOMImplStandard", JavaDOMImpl.class);
    dispatcher.delegate("com.google.gwt.user.client.DOMImpl", JavaClientDOMImpl.class);
    dispatcher.delegate("com.google.gwt.user.client.impl.DOMImpl", JavaClientDOMImpl.class);
    dispatcher.delegate("com.google.gwt.user.client.impl.DOMImplStandard", JavaClientDOMImpl.class);
    dispatcher.delegate("com.google.gwt.user.client.impl.DOMImpl", JavaClientDOMImpl.class);
    dispatcher.delegate("com.google.gwt.user.client.ui.impl.FocusImpl", JavaFocusImpl.class);
    dispatcher.delegate("com.google.gwt.user.client.ui.PrefixTree", JavaPrefixTree.class);
    dispatcher.delegate(CurrencyList.class, JavaCurrencyListImpl.class);
    dispatcher.delegate(Button.class, JavaButton.class);
    dispatcher.delegate(Style.class, JavaStyle.class);
    dispatcher.delegate(WindowImpl.class, JavaWindowImpl.class);
    dispatcher.delegate(RootPanel.class, JavaRootPanel.class);
    dispatcher.delegate(HistoryImpl.class, JavaHistoryImpl.class);
    dispatcher.delegate(History.class, JavaHistoryImpl.class);
    dispatcher.delegate(UIObject.class, JavaUIObject.class);
    dispatcher.delegate(HTMLTable.class, JavaHTMLTable.class);
    dispatcher.delegate(FlexTable.class, JavaFlexTable.class);
    dispatcher.delegate(Grid.class, JavaGrid.class);
    dispatcher.delegate(HTMLTable.CellFormatter.class, JavaCellFormatter.class);
    dispatcher.delegate(HTMLTable.RowFormatter.class, JavaRowFormatter.class);
    dispatcher.delegate(ElementMapperImpl.class, JavaElementMapperImpl.class);
    dispatcher.delegate(TextBoxImpl.class, JavaTextBoxImpl.class);
    dispatcher.delegate(Impl.class, JavaImpl.class);
    dispatcher.delegate(URL.class, JavaURL.class);
    dispatcher.delegate(Duration.class, JavaDuration.class);
    dispatcher.delegate(Timer.class, JavaTimer.class);
    dispatcher.delegate(Cookies.class, JavaCookies.class);
    dispatcher.delegate(Tree.class, JavaTree.class);
    dispatcher.delegate(NumberFormat.class, JavaNumberFormat.class);
    GwtNativeDispatch.instance = dispatcher;
  }

  public void clearDispatcher() {
    GwtNativeDispatch.instance = null;
  }

  protected void callResetHandlers(java.net.URL url) {
    for (ResetHandler h : resetHandlers) {
      h.reset(this, url);
    }
  }

  public void reset(java.net.URL url) {
    resetBridge();
    resetDispatcher();

    // It would be nice to reset the GWT bridge using a registered reset handler
    // but we can't, because the GWT bridge must be reset before registering
    // any delegates.
    callResetHandlers(url);
  }

  public void reset() {
    reset(JavaDocument.ABOUT_BLANK_URL);
  }

  /**
   * Load the document at the given URL and replace the DOM with it.
   *
   * @param url The URL to load. "about:blank" is loaded if this parameter is
   * null.
   */
  public HtmlPage loadUrl(java.net.URL url) {
    if (url == null) {
      url = UrlUtils.toUrlSafe("about:blank");
    } else if (url.getProtocol().contains("jar")) {
      // Htmlunit doesn't support loading from jars.
      // TODO(gentle): Refactor this code out into a custom JarWebConnection class.
      try {
        // Suppress trying to load the compiled GWT javascript
        webClient.setJavaScriptEnabled(false);

        InputStream stream = url.openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder dataString = new StringBuilder();

        while (true) {
          String line = reader.readLine();
          if (line == null) {
            break;
          }
          dataString.append(line);
        }

        MockWebConnection mockConnection = new MockWebConnection();
        mockConnection.setDefaultResponse(dataString.toString());

        webClient.setWebConnection(mockConnection);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    try {
      return webClient.getPage(url);
    } catch (FailingHttpStatusCodeException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public CookieManager getCookieManager() {
    return webClient.getCookieManager();
  }
}

