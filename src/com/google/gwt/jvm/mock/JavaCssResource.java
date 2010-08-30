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

import com.google.gwt.resources.client.CssResource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Logger;

/**
 * Mock for {@link CssResource}.
 *
 * @see JavaClientBundle
 * @see com.google.gwt.jvm.JavaGWTTestCase
 */
public class JavaCssResource {

  private static final Logger logger =
      Logger.getLogger(JavaCssResource.class.getCanonicalName());

  private JavaCssResource() { }

  /**
   * Returns a mock CssResource that attempts to emulate the behavior of
   * the object normally returned by calling GWT.create() on the given class.
   */
  @SuppressWarnings({"unchecked"})
  public static <T extends CssResource> T forClass(Class<T> classLiteral) {
    return (T) Proxy.newProxyInstance(
        classLiteral.getClassLoader(), new Class<?>[]{classLiteral},
        new MethodInvocationHandler());
  }

  private static class MethodInvocationHandler implements InvocationHandler {

    @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      logger.fine("Invoked method: " + method.getName());
      // TODO(tmao): annotation inspection doesn't actually work because
      // CssResource.ClassName does not have a retention runtime policy
      // so just return the name of the method
      if (method.getReturnType() == String.class) {
        return method.getName();
      } else if (method.getReturnType() == boolean.class) {
        return false;
      } else {
        throw new UnsupportedOperationException(method.getName());
      }
    }
  }
}
