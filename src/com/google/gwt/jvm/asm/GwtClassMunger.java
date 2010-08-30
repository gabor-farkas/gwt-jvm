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

import static com.google.gwt.jvm.asm.Descriptor.toDesc;

import com.google.gwt.jvm.Bucket;
import com.google.gwt.jvm.ResourceLoader;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Set;
import java.util.TreeSet;

/**
 * Main controller which decides which classes should be munged for native methods and OverlayTypes.
 */
public class GwtClassMunger implements OverlayTypePredicate {

  public static class ClassMeta {

    private final String superClass;
    private final String[] interfaces;
    private final Set<String> methods;

    public ClassMeta(String superClass, String[] interfaces, Set<String> methods) {
      this.superClass = superClass;
      this.interfaces = interfaces;
      this.methods = methods;
    }

    public String getSuperClass() {
      return superClass;
    }

    public String[] getInterfaces() {
      return interfaces;
    }

    public boolean hasMethod(String methodName, String desc) {
      return methods.contains(methodName + desc);
    }

  }

  public static final String JAVA_JS_OBJECT = "com/google/gwt/jvm/JavaJSObject";
  public static final String GWT_JAVA_SCRIPT_OBJECT = "com/google/gwt/core/client/JavaScriptObject";
  private final ResourceLoader resourceLoader;
  private Set<String> overlayTypes;

  public GwtClassMunger(ResourceLoader resourceLoader, Set<String> overlayTypes) {
    this.resourceLoader = resourceLoader;
    this.overlayTypes = overlayTypes;
  }

  public byte[] getJavaJSObject() {
    ClassWriter classWriter = new ClassWriter(0);
    ClassReader reader = new ClassReader(resourceLoader.loadBytes(JAVA_JS_OBJECT + ".class"));
    reader.accept(new AddAllInterfacesVisitor(classWriter, overlayTypes), 0);
    return classWriter.toByteArray();
  }

  public byte[] mungeBytes(String name, byte[] classBytes) {
    if (name.endsWith("$")) {
      String nameNo$ = GwtClassMunger.chop$(name);
      // TODO: we should be able to chain the delegates instead of chaining the byte arrays.
      return translateClass$InstanceToStaticMethods(translateNativeMethodsToDispatch(classBytes));
    } else if (isOverlayDesc(toDesc(name))) {
      return translateClassToInterface(classBytes);
    } else {
      return translateNativeMethodsToDispatch(classBytes);
    }
  }
  
  public byte[] munge(String name) {
    String resourceName = name;
    if (name.endsWith("$")) {
      resourceName = GwtClassMunger.chop$(name);
    }
    byte[] classBytes = resourceLoader.loadClassBytes(resourceName);

    return mungeBytes(name, classBytes);
  }

  private byte[] translateClassToInterface(byte[] classBytes) {
    ClassWriter classWriter = new ClassWriter(0);
    Class2InterfaceVistor visitor = new Class2InterfaceVistor(classWriter);
    new ClassReader(classBytes).accept(visitor, 0);
    return classWriter.toByteArray();
  }

  private byte[] translateClass$InstanceToStaticMethods(byte[] classBytes) {
    ClassWriter classWriter = new ClassWriter(0);
    ClassVisitor visitor = classWriter;
    // visitor = new NativeMethodDispatchVisitor(visitor, this);
    visitor = new ClassDollarVistor(visitor, this);
    new ClassReader(classBytes).accept(visitor, 0);
    return classWriter.toByteArray();
  }

  private byte[] translateNativeMethodsToDispatch(byte[] classBytes) {
    ClassWriter classWriter = new ClassWriter(0);
    NativeMethodDispatchVisitor visitor = new NativeMethodDispatchVisitor(classWriter, this);
    new ClassReader(classBytes).accept(visitor, 0);
    return classWriter.toByteArray();
  }

  public boolean isOverlayDesc(String name) {
    return overlayTypes.contains(name);
  }

  public ClassMeta classMeta(String name) {
    byte[] classBytes = resourceLoader.loadClassBytes(name);
    ClassReader reader = new ClassReader(classBytes);
    final Bucket<String> superClassName = new Bucket<String>();
    final Bucket<String[]> interfaceNames = new Bucket<String[]>();
    final Set<String> methods = new TreeSet<String>();
    NoopClassVisitor classMetaVisitor = new NoopClassVisitor() {
      @Override
      public void visit(int version, int access, String name, String signature, String superName,
          String[] interfaces) {
        superClassName.set(superName);
        String[] javaInterfaces = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
          javaInterfaces[i] = interfaces[i];
        }
        interfaceNames.set(javaInterfaces);
      }

      @Override
      public MethodVisitor visitMethod(int access, String name, String desc, String signature,
          String[] exceptions) {
        methods.add(name + desc);
        return null;
      }
    };
    reader.accept(classMetaVisitor, 0);
    return new ClassMeta(superClassName.get(), interfaceNames.get(), methods);
  }

  public String getImplementingClass(String clazz, String methodName, String desc) {
    do {
      ClassMeta meta = classMeta(clazz);
      if (meta.hasMethod(methodName, desc)) {
        return clazz;
      }
      clazz = meta.getSuperClass();
    } while (!clazz.equals("java/lang/Object"));
    throw new IllegalStateException();
  }

  public static String chop$(String name) {
    if (name.endsWith("$")) {
      return name.substring(0, name.length() - 1);
    } else {
      throw new IllegalStateException(name);
    }
  }

  public static boolean isStatic(int access) {
    return isOpcode(access, Opcodes.ACC_STATIC);
  }

  public static boolean isConstructor(String name) {
    return name.equals("<init>") || name.equals("<cinit>");
  }

  public static boolean isOpcode(int opcode, int value) {
    return (opcode & value) == value;
  }

  public static String packageName(String className) {
    int index = className.lastIndexOf('.');
    return className.substring(0, index);
  }

}
