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
package com.google.gwt.jvm.asm;

import static com.google.gwt.jvm.asm.Type.type;

import com.google.gwt.jvm.ClassResourceNotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Map of GWT class to Mock Class
 */
public class ClassMap {
  private final Map<Type, Type> classes = new HashMap<Type, Type>();

  public void addImplementor(Type key, Type use) {
    classes.put(key, use);
  }

  public void addImplementor(String keyName, String useName) {
    /* Its possible to add an implementor for a class which is not actually
     * loaded in the JVM. This will happen in large projects (like wave) which
     * have multiple build targets which use the same GWT browser configuration
     * code. Some build targets will not include all classes. If you attempt to
     * set a mock or delegate of a class not loaded in the JVM, assume the class
     * isn't used and return.
     *
     * If the regular GWT class is found, the mock class must also exist.
     * Exceptions raised while loading the mock class are not ignored.
     *
     * Ultimately, it'd be nice to use annotations to specify the mock classes
     * to use rather than a single configuration method.
     */
    final Type keyClass;
    try {
      keyClass = type(keyName);
    } catch (ClassResourceNotFoundException e) {
      // Swallow this and return (see comment above).
      return;
    }
    addImplementor(keyClass, type(useName));
  }

  public void addImplementor(Class<?> key, String useName) {
    final Type keyClass;
    try {
      keyClass = type(key);
    } catch (ClassResourceNotFoundException e) {
      // Swallow this and return (see comment above).
      return;
    }
    addImplementor(keyClass, type(useName));
  }

  public void addImplementor(String keyName, Class<?> use) {
    final Type keyClass;
    try {
      keyClass = type(keyName);
    } catch (ClassResourceNotFoundException e) {
      // Swallow this and return (see comment above).
      return;
    }
    addImplementor(keyClass, type(use));
  }

  public void addImplementor(Class<?> key, Class<?> use) {
    final Type keyClass;
    try {
      keyClass = type(key);
    } catch (ClassResourceNotFoundException e) {
      // Swallow this and return (see comment above).
      return;
    }
    addImplementor(keyClass, type(use));
  }

  /**
   * Returns the mapped type associated with the given class, or null if
   * a mapping does not exist.
   */
  public Type map(Class<?> classLiteral) {
    return map(type(classLiteral));
  }

  /**
   * Returns the mapped type associated with the given type, or null if
   * a mapping does not exist.
   */
  public Type map(Type type) {
    Type t = type;
    while (t != null) {
      Type delegate = classes.get(t);
      if (delegate != null) {
        return delegate;
      } else {
        if (t.getJavaClass() == Object.class) {
          break;
        } else {
          t = t.getSuperType();
        }
      }
    }
    return null;
  }
}
