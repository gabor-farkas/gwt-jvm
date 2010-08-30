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

import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Parser/Convertor for JVM descriptors in form Ljava/lang/Object; to java.lang.Object
 */
public class Descriptor {
  // Descriptor-wide cache of methods, for speedup.
  private static final ReflectionCache CACHE = new ReflectionCache();

  private static class Visitor extends NoopSignatureVisitor {

    private class ParameterSignatureVisitor extends NoopSignatureVisitor {
      private final String prefix;

      public ParameterSignatureVisitor(String prefix) {
        this.prefix = prefix;
      }

      @Override
      public void visitType(String name) {
        parameters.add(this.prefix + name);
      }

      @Override
      public SignatureVisitor visitArrayType() {
        return new ParameterSignatureVisitor(this.prefix + "[");
      }
    }

    private final ArrayList<String> parameters = new ArrayList<String>();
    private String returnType;

    @Override
    public SignatureVisitor visitParameterType() {
      return new ParameterSignatureVisitor("");
    }

    @Override
    public SignatureVisitor visitReturnType() {
      return new NoopSignatureVisitor() {
        @Override
        public void visitType(String name) {
          returnType = name;
        }

        @Override
        public SignatureVisitor visitArrayType() {
          returnType = "Ljava/lang/Object;";
          return new NoopSignatureVisitor();
        }
      };
    }

    public String getReturnType() {
      return returnType;
    }

    public String[] getParameters() {
      return parameters.toArray(new String[parameters.size()]);
    }
  }

  private final String signature;
  private final String[] parameterDescs;
  private final String returnDesc;

  public Descriptor(String signature) {
    this.signature = signature;
    Visitor visitor = new Visitor();
    new SignatureReader(signature).accept(visitor);
    parameterDescs = visitor.getParameters();
    returnDesc = visitor.getReturnType();
  }

  public Class<?>[] getParameters() {
    Class<?>[] classes = new Class<?>[parameterDescs.length];
    for (int i = 0; i < parameterDescs.length; i++) {
      classes[i] = toClass(parameterDescs[i]);
    }
    return classes;
  }

  public String getMethodDesc() {
    return signature;
  }

  public String getReturnDesc() {
    return returnDesc;
  }

  public String[] getParameterDescs() {
    return parameterDescs;
  }

  public String toDescPrefix(String owner) {
    StringBuilder builder = new StringBuilder();
    builder.append("(");
    if (owner != null) {
      builder.append(inlineDesc(owner));
    }
    for (String descriptor : parameterDescs) {
      builder.append(descriptor);
    }
    builder.append(")");
    builder.append(returnDesc);
    return builder.toString();
  }

  @Override
  public String toString() {
    return toDescPrefix(null);
  }

  // Static utilities
  public static Class<?> toClass(String descriptor) {
    // cached version of the descriptor->cache loading below
    return CACHE.descriptorToClass(descriptor);
  }

  static Class<?> toClassInner(String descriptor) {
    switch (descriptor.charAt(0)) {
      case 'Z':
        return boolean.class;
      case 'B':
        return byte.class;
      case 'C':
        return char.class;
      case 'S':
        return short.class;
      case 'I':
        return int.class;
      case 'J':
        return long.class;
      case 'D':
        return double.class;
      case 'F':
        return float.class;
      case 'V':
        return void.class;
      case 'L':
        try {
          return Class.forName(toJava(descriptor));
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      case '[':
        return Array.newInstance(toClass(descriptor.substring(1)), 0).getClass();
      default:
        throw new IllegalStateException();
    }
  }

  public static String toJava(String descriptor) {
    String javaClass = descriptor.replace('/', '.');
    if (descriptor.endsWith(";")) {
      return javaClass.substring(1, javaClass.length() - 1);
    } else {
      return javaClass;
    }
  }

  private static String inlineDesc(String descriptor) {
    return descriptor.length() == 1 ? descriptor : "L" + descriptor + ";";
  }

  public static String toDesc(String name) {
    return name.replace('.', '/');
  }
}
