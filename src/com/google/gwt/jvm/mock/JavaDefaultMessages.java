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

import com.google.gwt.i18n.client.Messages;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Logger;

/**
 * Mocks the implementation of GWT DefaultMessage which is
 * used for GWT widget message internationalization.
 *
 * <p>When a method of this mock implementation is invoked, returns the
 * {@link Messages.DefaultMessage} that the method is annotated with.
 */
public class JavaDefaultMessages {

  private static final Logger logger =
      Logger.getLogger(JavaDefaultMessages.class.getCanonicalName());

    @SuppressWarnings("unchecked")
    public static Object forMessage(Class<?> clazz) {
      InvocationHandler handler = new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
          logger.fine("Invoked method: " + method.getName());
          return method.getAnnotation(Messages.DefaultMessage.class).value();
        }
      };
      return Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, handler);
    }
}
