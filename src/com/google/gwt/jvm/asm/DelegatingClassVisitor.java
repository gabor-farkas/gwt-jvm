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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * Default (delegating) implementation of ClassVisitor (hence noop)
 */
public class DelegatingClassVisitor implements ClassVisitor {

  protected final ClassVisitor delegate;

  public DelegatingClassVisitor(ClassVisitor delegate) {
    this.delegate = delegate;
  }

  public void visit(int version, int access, String name, String signature, String superName,
      String[] interfaces) {
    delegate.visit(version, access, name, signature, superName, interfaces);
  }

  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    return delegate.visitAnnotation(desc, visible);
  }

  public void visitInnerClass(String name, String outerName, String innerName, int access) {
    delegate.visitInnerClass(name, outerName, innerName, access);
  }

  public void visitAttribute(Attribute attr) {
    delegate.visitAttribute(attr);
  }

  public void visitEnd() {
    delegate.visitEnd();
  }

  public FieldVisitor visitField(int access, String name, String desc, String signature,
      Object value) {
    return delegate.visitField(access, name, desc, signature, value);
  }

  public void visitOuterClass(String owner, String name, String desc) {
    delegate.visitOuterClass(owner, name, desc);
  }

  public void visitSource(String file, String debug) {
    delegate.visitSource(file, debug);
  }

  public MethodVisitor visitMethod(int access, String name, String desc, String signature,
      String[] exceptions) {
    return delegate.visitMethod(access, name, desc, signature, exceptions);
  }

}
