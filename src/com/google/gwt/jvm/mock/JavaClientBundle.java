/* Copyright 2009 Google Inc.
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

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Logger;

/**
 * Mock for {@link ClientBundle}.
 *
 * @see com.google.gwt.jvm.JavaGWTTestCase
 */
public class JavaClientBundle {

  private static final Logger logger =
      Logger.getLogger(JavaClientBundle.class.getCanonicalName());

  private JavaClientBundle() { }

  /**
   * Returns a mock ClientBundle that attempts to emulate the behavior of
   * the object normally returned by calling GWT.create() on the given class.
   */
  @SuppressWarnings({"unchecked"})
  public static <T extends ClientBundle> T forClass(Class<T> classLiteral) {
    return (T) Proxy.newProxyInstance(
        classLiteral.getClassLoader(), new Class<?>[]{classLiteral},
        new MethodInvocationhandler());
  }

  private static class MethodInvocationhandler implements InvocationHandler {

    @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      logger.fine("Invoked method: " + method.getName());
      Class<?> returnType = method.getReturnType();
      if (returnType.isInterface() && CssResource.class.isAssignableFrom(returnType)) {
        @SuppressWarnings({"unchecked"})
        Class<? extends CssResource> cssResourceClass =
            (Class<? extends CssResource>) returnType;
        return JavaCssResource.forClass(cssResourceClass);
      } else if (returnType.isInterface() && DataResource.class.isAssignableFrom(returnType)) {
        return new JavaDataResource(method + " foo_img_path", method.getName());
      } else if (ImageResource.class.isAssignableFrom(returnType)) {
        return new ImageResourcePrototype("name", "url", 0, 0, 16, 15, false, false);
      } else {
        return returnType.newInstance();
      }
    }
  }
}
