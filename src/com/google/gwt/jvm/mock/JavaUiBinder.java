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

import com.google.common.collect.Lists;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

/**
 * Creates mock of {@link com.google.gwt.uibinder.client.UiBinder}
 * instance by manipulating widget fields at run-time.
 *
 * Intercepts the method call to create and return the root object of the UI,
 * and resets values of ui fields tagged with {@link UiField}.
 */
public class JavaUiBinder {

  private static final Logger logger =
      Logger.getLogger(JavaUiBinder.class.getCanonicalName());

  @SuppressWarnings("unchecked")
  public static <T, V extends UiBinder> T gwtCreateForUiBinder(final Class<V> clazz) {
    InvocationHandler handler = new InvocationHandler() {
      @Override
      @SuppressWarnings("unchecked")
      public T invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.fine("Invoked method: " + method.getName());
        if ("createAndBindUi".equals(method.getName())) {
          List<Object> fieldObjects = bindUiFields(args[0]);

          for (Type t : clazz.getGenericInterfaces()) {
            if (t instanceof ParameterizedType) {

              ParameterizedType p = (ParameterizedType) t;
              Class c = (Class) p.getActualTypeArguments()[0];
        
              if (c.equals(Widget.class)) {
                FlowPanel flowPanel = new FlowPanel();
                createFieldStructureInPanel(fieldObjects, flowPanel);
                return (T) flowPanel;
              } else if (c.equals(DockLayoutPanel.class)) {
                DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.EM);
                createFieldStructureInPanel(fieldObjects, dockLayoutPanel);
                return  (T) dockLayoutPanel;
              } else if (c.equals(StackLayoutPanel.class)) {
                StackLayoutPanel stackLayoutPanel = new StackLayoutPanel(Unit.EM);
                createFieldStructureInPanel(fieldObjects, stackLayoutPanel);
                return  (T) stackLayoutPanel;
              } else if (c.equals(HTMLPanel.class)) {
                HTMLPanel htmlPanel = new HTMLPanel("<div id='gwt-uid-foo'>" + "</div>");
                createFieldStructureInPanel(fieldObjects, htmlPanel);
                return  (T) htmlPanel;
              } else if (c.equals(SpanElement.class)) {
                SpanElement spanElem = Document.get().createSpanElement();
                createFieldStructureInElement(fieldObjects, spanElem);
                return (T) spanElem;
              } else if (c.equals(DivElement.class)) {
                DivElement divElem = Document.get().createDivElement();
                createFieldStructureInElement(fieldObjects, divElem);
                return (T) divElem;
              } else {
                return (T) c.newInstance();
              }
            }
          }
        }
        throw new UnsupportedOperationException();
      }
    };
    return (T) Proxy.newProxyInstance(
      clazz.getClassLoader(), new Class<?>[] {clazz}, handler);
  }

  private static void createFieldStructureInElement(List<Object> fieldObjects, Element rootElem) {
    for (Object o : fieldObjects) {
      if (o instanceof Element) {
        rootElem.appendChild((Element) o);
      } else if (o instanceof Widget) {
        rootElem.appendChild(((Widget) o).getElement());
      }
    }
  }

  private static void createFieldStructureInPanel(List<Object> fieldObjects, Widget rootPanel) {
    for (Object o : fieldObjects) {
      if (o instanceof Element) {
        rootPanel.getElement().appendChild((Element) o);
      } else if (o instanceof Widget) {
        rootPanel.getElement().appendChild(((Widget) o).getElement());
      }
    }
  }

  private static List<Object> bindUiFields(Object template) {
    List<Field> uiFields = 
        com.google.gwt.jvm.asm.Type.getAllUnProvidedUiFields(template.getClass());
    List<Object> fObjects = Lists.newArrayList();

    for (Field field : uiFields) {
      try {
        // So that IllegalAccessException is not thrown
        // if field is private
        field.setAccessible(true);
        Class<?> fieldType = field.getType();
        Object o = null;
        if (Element.class.isAssignableFrom(fieldType)) {
          o = Document.get().createDivElement();
        } else {
          Constructor<?>[] allConstructors = fieldType.getDeclaredConstructors();
          for (Constructor<?> ctor : allConstructors) {
            Class<?>[] pType = ctor.getParameterTypes();
            if (pType.length == 0) {
              o = fieldType.newInstance();
              break;
            } else if (pType.length == 1 && pType[0] == String.class) {
                o = ctor.newInstance(fieldType.getName());
                break;
            } else {
              // see if there's a @UiFactory
              Method method = com.google.gwt.jvm.asm.Type.findUiFactoryMethod(field);
              if (method != null) {
                method.setAccessible(true);
                o = method.invoke(template);
                break;
              }
              // TODO(yizhi): need to add more capabilities.
              throw new RuntimeException(
                  "Cannot parse the widgets tagged as UiField "
                  + "whose constructor parameters are more than one. ");
            }
          }
        }
        field.set(template, o);
        fObjects.add(o);
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    }    
    return fObjects;
  }
}
