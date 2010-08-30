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

import static com.google.gwt.jvm.asm.Descriptor.toJava;
import static com.google.gwt.jvm.asm.Type.type;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.jvm.asm.ClassMap;
import com.google.gwt.jvm.asm.InvocationDelegate;
import com.google.gwt.jvm.asm.Type;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Native methods have a body auto generated which delegate the native method to this class
 * for execution.
 */
public class GwtNativeDispatch {

  public static GwtNativeDispatch instance;

  public static GwtNativeDispatch getInstance() {
    return instance;
  }

  private final ClassMap classDelegates = new ClassMap();
  private final Map<Object, Object> delegates = new WeakHashMap<Object, Object>();

  public GwtNativeDispatch() {
    delegate(JavaScriptObject.class, JavaJSObject.class);
  }

  public void delegate(Class<?> gwtClass, Class<?> mockClass) {
    classDelegates.addImplementor(gwtClass, mockClass);
  }

  public void delegate(Class<?> gwtClass, String mockClass) {
    classDelegates.addImplementor(gwtClass, mockClass);
  }

  public void delegate(String gwtClass, Class<?> mockClass) {
    classDelegates.addImplementor(gwtClass, mockClass);
  }

  public void delegate(String gwtClass, String mockClass) {
    classDelegates.addImplementor(gwtClass, mockClass);
  }

  public InvocationDelegate getDelegate(Object instance, String className, String methodName,
      String methodDesc) throws SecurityException {
    Object delegate;
    Type delegateClass;
    if (instance == null) {
      // static method dispatch;
      delegate = null;
      delegateClass = classDelegates.map(type(toJava(className)));
    } else if (instance instanceof JavaJSObject) {
      delegate = ((JavaJSObject) instance).delegate;
      delegateClass = type(delegate.getClass());
    } else {
      delegate = delegates.get(instance);
      if (delegate == null) {
        delegateClass = classDelegates.map(instance.getClass());
        if (delegateClass == null) {
          throw new IllegalStateException("Don't have a GWT emulation delegate for '"
              + instance.getClass().getCanonicalName() + "'.");
        }
        delegate = delegateClass.newInstance(instance);
        delegates.put(instance, delegate);
      }
      delegateClass = type(delegate.getClass());
    }
    if (delegateClass == null) {
      throw new IllegalStateException("No delegate found for '" + className + "'");
    }
    return new InvocationDelegate(delegate, delegateClass.getMethod(methodName, methodDesc));
  }

}
