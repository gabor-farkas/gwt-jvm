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
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * Default delegating implementation of {@link MethodVisitor}
 */
public class DelegatingMethodVisitor implements MethodVisitor {

  protected final MethodVisitor delegate;

  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    return delegate.visitAnnotation(desc, visible);
  }

  public AnnotationVisitor visitAnnotationDefault() {
    return delegate.visitAnnotationDefault();
  }

  public void visitAttribute(Attribute attr) {
    delegate.visitAttribute(attr);
  }

  public void visitCode() {
    delegate.visitCode();
  }

  public void visitEnd() {
    delegate.visitEnd();
  }

  public void visitFieldInsn(int opcode, String owner, String name, String desc) {
    delegate.visitFieldInsn(opcode, owner, name, desc);
  }

  public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
    delegate.visitFrame(type, nLocal, local, nStack, stack);
  }

  public void visitIincInsn(int var, int increment) {
    delegate.visitIincInsn(var, increment);
  }

  public void visitInsn(int opcode) {
    delegate.visitInsn(opcode);
  }

  public void visitIntInsn(int opcode, int operand) {
    delegate.visitIntInsn(opcode, operand);
  }

  public void visitJumpInsn(int opcode, Label label) {
    delegate.visitJumpInsn(opcode, label);
  }

  public void visitLabel(Label label) {
    delegate.visitLabel(label);
  }

  public void visitLdcInsn(Object cst) {
    delegate.visitLdcInsn(cst);
  }

  public void visitLineNumber(int line, Label start) {
    delegate.visitLineNumber(line, start);
  }

  public void visitLocalVariable(String name, String desc, String signature, Label start,
      Label end, int index) {
    delegate.visitLocalVariable(name, desc, signature, start, end, index);
  }

  public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
    delegate.visitLookupSwitchInsn(dflt, keys, labels);
  }

  public void visitMaxs(int maxStack, int maxLocals) {
    delegate.visitMaxs(maxStack, maxLocals);
  }

  public void visitMethodInsn(int opcode, String owner, String name, String desc) {
    delegate.visitMethodInsn(opcode, owner, name, desc);
  }

  public void visitMultiANewArrayInsn(String desc, int dims) {
    delegate.visitMultiANewArrayInsn(desc, dims);
  }

  public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
    return delegate.visitParameterAnnotation(parameter, desc, visible);
  }

  public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
    delegate.visitTableSwitchInsn(min, max, dflt, labels);
  }

  public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
    delegate.visitTryCatchBlock(start, end, handler, type);
  }

  public void visitTypeInsn(int opcode, String type) {
    delegate.visitTypeInsn(opcode, type);
  }

  public void visitVarInsn(int opcode, int var) {
    delegate.visitVarInsn(opcode, var);
  }

  public DelegatingMethodVisitor(MethodVisitor delegate) {
    this.delegate = delegate;
  }
}
