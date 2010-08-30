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

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.MapMaker;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;

/**
 * Caching utility that reduces the reflection performed within the asm classes by storing
 * reflected results that aren't going to change (like class, field and method lookups by name).
 */
public class ReflectionCache {
  /** Key for identifying methods, parameterized by class & method names plus parameters. */
  private static class MethodKey {
    private final Type type; // additionally store the Type for the class.
    private final String className;
    private final String methodName;
    private final String methodDesc;
    public MethodKey(Type type, String className, String methodName, String methodDesc) {
      this.type = type;
      this.className = className;
      this.methodName = methodName;
      this.methodDesc = methodDesc;
    }
    @Override
    public int hashCode() {
      return Objects.hashCode(className, methodName, methodDesc);
    }
    @Override
    public boolean equals(Object o) {
      if (o == null || !(o instanceof MethodKey)) {
        return false;
      }
      MethodKey that = (MethodKey) o;
      // exclude the type check in the equality.
      return className.equals(that.className) && methodName.equals(that.methodName)
          && methodDesc.equals(that.methodDesc);
    }
  }

  /** Cache for Type's method lookups, stores everything for now, and calculates lazily. */
  public ConcurrentMap<MethodKey, Method> typeMethodCache = new MapMaker().makeComputingMap(
      new Function<MethodKey, Method>() {
        @Override
        public Method apply(MethodKey from) {
          return from.type.getMethod(
              from.methodName, new Descriptor(from.methodDesc).getParameters());
        }
      });

  /** Cache for Descriptor's toClass lookups, stores everything for now, and calculates lazily. */
  public ConcurrentMap<String, Class<?>> descriptorClassCache = new MapMaker().makeComputingMap(
      new Function<String, Class<?>>() {
        @Override
        public Class<?> apply(String descriptor) {
          return Descriptor.toClassInner(descriptor);
        }
      });

  /** Cached retrieval of a named method from a named class, including parameter list. */
  public Method getMethodFromType(final Type type, final String className,
      final String methodName, final String methodDesc) {
    return typeMethodCache.get(new MethodKey(type, className, methodName, methodDesc));
  }

  /** Cached class calculation from classname descriptors. */
  public Class<?> descriptorToClass(String descriptor) {
    return descriptorClassCache.get(descriptor);
  }
}
