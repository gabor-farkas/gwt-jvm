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

import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * Convert overlay type to interface
 */
public class Class2InterfaceVistor extends DelegatingClassVisitor {

  private static final String JAVA_LANG_OBJECT = "java/lang/Object";

  public Class2InterfaceVistor(ClassWriter delegate) {
    super(delegate);
  }

  @Override
  public void visit(int version, int access, String name, String signature, String superName,
      String[] interfaces) {
    String[] interfacesWithSuper = interfaces;
    if (!superName.equals(JAVA_LANG_OBJECT)) {
      interfacesWithSuper = new String[interfaces.length + 1];
      interfacesWithSuper[0] = superName;
      System.arraycopy(interfaces, 0, interfacesWithSuper, 1, interfaces.length);
    }
    super.visit(version, ACC_INTERFACE | ACC_PUBLIC | ACC_ABSTRACT, name, signature,
        JAVA_LANG_OBJECT, interfacesWithSuper);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature,
      String[] exceptions) {
    return null;
  }

  @Override
  public FieldVisitor visitField(int access, String name, String desc, String signature,
      Object value) {
    return null;
  }

}
