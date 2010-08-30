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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Simplified way of dealing with classes and invoking methods in reflective way.
 */
public class Type {
  // Type-wide cache of methods, for speedup.
  private static final ReflectionCache CACHE = new ReflectionCache();

  // NOTE: should be final, but creating within the constructor ends up calling static initializer
  // code running outside the classloader, which breaks for things like DOMImpl (has GWT.create)
  private Class<?> clazz;
  private final String className;

  public Type(String name) {
    this.className = name;
  }

  /** Internal constructor to use when the class is already known. */
  private Type(Class<?> clazz) {
    this.clazz = clazz;
    this.className = clazz.getName();
  }

  @SuppressWarnings("unchecked")
  public <T> T newInstance(Object... parameters) {
    try {
      Constructor<?> constructor = findConstructor(parameters);
      constructor.setAccessible(true);
      return (T) constructor.newInstance(parameters);
    } catch (SecurityException e) {
      throw new RuntimeException(className, e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(className, e);
    } catch (InstantiationException e) {
      throw new RuntimeException(className, e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(className, e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(className, e);
    }
  }

  public Class<?> getJavaClass() {
    if (this.clazz == null) {
      try {
        this.clazz = Class.forName(this.className);
      } catch (ClassNotFoundException e) {
        throw new IllegalStateException("Unknown class name in the type state: " + className);
      }
    }
    return this.clazz;
  }

  public <T> T invoke(String methodName, Object... parameters) {
    return invoke(null, methodName, parameters);
  }

  @SuppressWarnings("unchecked")
  public <T> T invoke(Object instance, String methodName, Object... parameters) {
    try {
      Method method = findMethod(methodName, parameters);
      method.setAccessible(true);
      return (T) method.invoke(instance, parameters);
    } catch (SecurityException e) {
      throw new RuntimeException(e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private Method findMethod(String methodName, Object... parameters) {
    Class<?> clazz = getJavaClass();
    while (clazz != null) {
      for (Method method : clazz.getDeclaredMethods()) {
        if (method.getName().equals(methodName)
            && parametersMatch(method.getParameterTypes(), parameters)) {
          return method;
        }
      }
      clazz = clazz.getSuperclass();
    }
    throw new IllegalArgumentException("Method '" + methodName + "' not found which can take: "
        + Arrays.toString(parameters));
  }

  @SuppressWarnings("unchecked")
  private <C> Constructor<C> findConstructor(Object... parameters) {
    for (Constructor<?> constructor : getJavaClass().getDeclaredConstructors()) {
      if (parametersMatch(constructor.getParameterTypes(), parameters)) {
        return (Constructor<C>) constructor;
      }
    }
    throw new IllegalArgumentException("Constructor for class '" + className
        + "' not found which can take: " + Arrays.toString(parameters));
  }

  private boolean parametersMatch(Class<?>[] parameterTypes, Object... parameters) {
    for (int i = 0; i < parameterTypes.length; i++) {
      if (!noPrimitive(parameterTypes[i]).isInstance(parameters[i])) {
        return false;
      }
    }
    return true;
  }

  private Class<?> noPrimitive(Class<?> clazz) {
    if (clazz.isPrimitive()) {
      if (clazz == boolean.class) {
        return Boolean.class;
      }
      if (clazz == short.class) {
        return Short.class;
      }
      if (clazz == char.class) {
        return Character.class;
      }
      if (clazz == int.class) {
        return Integer.class;
      }
      if (clazz == long.class) {
        return Long.class;
      }
      if (clazz == float.class) {
        return Float.class;
      }
      if (clazz == double.class) {
        return Double.class;
      }
      throw new IllegalStateException("Don't know type: " + clazz);
    }
    return clazz;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((className == null) ? 0 : className.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Type other = (Type) obj;
    if (className == null) {
      if (other.className != null) {
        return false;
      }
    } else if (!className.equals(other.className)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return className;
  }

  public static Type type(Class<?> clazz) {
    return clazz == null ? null : new Type(clazz);
  }

  public static Type type(String clazz) {
    return new Type(clazz);
  }

  public Method getMethod(String methodName, Class<?>...parameters) {
    Class<?> clazz = getJavaClass();
    while (clazz != null  && clazz != Object.class) {
      try {
        Method method = clazz.getDeclaredMethod(methodName, parameters);
        method.setAccessible(true);
        return method;
      } catch (SecurityException e) {
        throw new RuntimeException(e);
      } catch (NoSuchMethodException e) {
      }
      clazz = clazz.getSuperclass();
    }
    throw new RuntimeException("No method '" + methodName + Arrays.toString(parameters) + "' in '"
        + className + "'.");
  }

  public Class<?> getClassContainingMethod(String methodName, Class<?>... parameters) {
    Class<?> clazz = getJavaClass();

    while (clazz != Object.class) {
      try {
        Method[] methods = clazz.getDeclaredMethods();
        Method method = clazz.getDeclaredMethod(methodName, parameters);
        method.setAccessible(true);
        return clazz;
      } catch (SecurityException e) {
        throw new RuntimeException(e);
      } catch (NoSuchMethodException e) {
      }
      if (clazz.getSuperclass() == null) {
        return clazz;
      }
      clazz = clazz.getSuperclass();
    }
    throw new RuntimeException("No method '" + methodName + Arrays.toString(parameters) + "' in '"
        + className + "'.");
  }

  public Method getMethod(String methodName, String methodDesc) {
    return CACHE.getMethodFromType(this, className, methodName, methodDesc);
  }

  public Class<?> getClassContainingMethod(String methodName, String methodDesc) {
    return getClassContainingMethod(methodName, new Descriptor(methodDesc).getParameters());
  }

  public static <T> T invokeMethod(Object instance, String methodName, Object... args) {
    return type(instance.getClass()).invoke(instance, methodName, args);
  }

  @SuppressWarnings("unchecked")
  public <T> T getStaticField(String fieldName) {
    try {
      return (T) field(fieldName).get(null);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public Field field(String fieldName) {
    Class<?> clazz = getJavaClass();
    RuntimeException noSuchFieldException = null;
    while (clazz != null) {
      try {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
      } catch (SecurityException e) {
        throw new RuntimeException(e);
      } catch (NoSuchFieldException e) {
        if (noSuchFieldException == null) {
          noSuchFieldException = new RuntimeException(e);
        }
      }
      clazz = clazz.getSuperclass();
    }
    throw noSuchFieldException;
  }

  public void setStaticField(String fieldName, Object value) {
    try {
      field(fieldName).set(null, value);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public Type getSuperType() {
    return type(getJavaClass().getSuperclass());
  }


  /**
   * Walk up a class' inheritance tree, getting all fields
   * marked with the {@link UiField} annotation.
   */
  public static List<Field> getAllUiFields(Class<?> clazz) {
    Class<?> theClass = clazz;
    List<Field> uiFields = Lists.newArrayList();
    do {
      Field[] fields = theClass.getDeclaredFields();
      for (Field field : fields) {
        if (field.getAnnotation(UiField.class) != null) {
          uiFields.add(field);
        }
      }
      theClass = theClass.getSuperclass();
    } while (theClass != null &&
             !(theClass.getPackage().getName().startsWith("com.google.gwt.user.client.ui")));
    return uiFields;
  }

  /**
   * Walk up a class' inheritance tree, getting all fields marked with the
   * {@link UiField} annotation ignoring the ones with a UiField(provided = true)
   * annotation.
   */
  public static List<Field> getAllUnProvidedUiFields(Class<?> clazz) {
    Class<?> theClass = clazz;
    List<Field> uiFields = Lists.newArrayList();
    do {
      Field[] fields = theClass.getDeclaredFields();
      for (Field field : fields) {
        UiField annotation = field.getAnnotation(UiField.class);
        if (annotation != null && !annotation.provided()) {
          uiFields.add(field);
        }
      }
      theClass = theClass.getSuperclass();
    } while (theClass != null &&
             !(theClass.getPackage().getName().startsWith("com.google.gwt.user.client.ui")));
    return uiFields;
  }

  /**
   * Given a {@link UiField}-annotated field, find a public, no-arg
   * {@link UiFactory} method in the class that declared it.
   */
  public static Method findUiFactoryMethod(Field field) {
    Preconditions.checkArgument(field.isAnnotationPresent(UiField.class));
    Class<?> declaringClass = field.getDeclaringClass();
    Method[] methods = declaringClass.getDeclaredMethods();
    for (Method method : methods) {
      if (method.isAnnotationPresent(UiFactory.class) &&
          method.getReturnType() == field.getType()) {
        return method;
      }
    }
    return null;
  }

}
