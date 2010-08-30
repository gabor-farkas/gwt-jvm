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

import com.google.gwt.jvm.GwtNativeDispatch;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Converts a native method to regular method which delegates its invocation to
 * GwtNativeDispatch
 */
public class NativeMethodDelegatingVisitor implements MethodVisitor {
  private static final String NULL_POINTER_EXCEPTION = "java/lang/NullPointerException";

  private static String InvocationDelegate = InvocationDelegate.class.getName().replace('.', '/');

  private static String GwtNativeDispatch = GwtNativeDispatch.class.getName().replace('.', '/');
  private static final String GwtNativeDispatch_getDelegate =
      "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)L"
          + InvocationDelegate + ";";

  private final MethodVisitor delegate;
  private final String className;
  private final String methodName;
  private final Descriptor descriptor;
  private final boolean isStatic;

  public NativeMethodDelegatingVisitor(MethodVisitor delegate, String className, String methodName,
      Descriptor descriptor, boolean isStatic) {
    this.delegate = delegate;
    this.className = className;
    this.methodName = methodName;
    this.descriptor = descriptor;
    this.isStatic = isStatic;
    //System.out.println("Native Method Delegate: " + className + "." + methodName + descriptor);
  }

  public void visitEnd() {
    delegate.visitCode();

    if (!isStatic) {
      assertThisNotNull();
    }

    GwtNativeDispatch_getInstance();
    dispatch_getDelegate();
    int parameterCount = isStatic ? 0 : 1;
    for (String parameter : descriptor.getParameterDescs()) {
      parameterCount += nativeDelegate_addArgument(parameter, parameterCount);
    }
    nativeDelegate_invokeReturnObject();

    delegate.visitMaxs(5, parameterCount + 1);
    delegate.visitEnd();
  }

  private void assertThisNotNull() {
    delegate.visitVarInsn(Opcodes.ALOAD, 0);
    Label notNull = new Label();
    delegate.visitJumpInsn(Opcodes.IFNONNULL, notNull);
    delegate.visitTypeInsn(Opcodes.NEW, NULL_POINTER_EXCEPTION);
    delegate.visitInsn(Opcodes.DUP);
    delegate.visitMethodInsn(Opcodes.INVOKESPECIAL, NULL_POINTER_EXCEPTION, "<init>", "()V");
    delegate.visitInsn(Opcodes.ATHROW);
    delegate.visitLabel(notNull);
  }

  private void GwtNativeDispatch_getInstance() {
    delegate.visitMethodInsn(Opcodes.INVOKESTATIC, GwtNativeDispatch, "getInstance", "()L"
        + GwtNativeDispatch + ";");
  }

  private void dispatch_getDelegate() {
    if (isStatic) {
      delegate.visitInsn(Opcodes.ACONST_NULL);
    } else {
      delegate.visitVarInsn(Opcodes.ALOAD, 0); // this
    }
    delegate.visitLdcInsn(className);
    delegate.visitLdcInsn(methodName);
    delegate.visitLdcInsn(descriptor.getMethodDesc());
    delegate.visitMethodInsn(Opcodes.INVOKEVIRTUAL, GwtNativeDispatch, "getDelegate",
        GwtNativeDispatch_getDelegate);
  }

  private void nativeDelegate_invokeReturnObject() {
    String methodReturn = descriptor.getReturnDesc();
    if (methodReturn == null) {
      throw new NullPointerException(descriptor.getMethodDesc());
    }
    switch (methodReturn.charAt(0)) {
      case 'L':
        delegate.visitMethodInsn(Opcodes.INVOKEVIRTUAL, InvocationDelegate, "invokeReturnObject",
            "()Ljava/lang/Object;");
        delegate.visitTypeInsn(Opcodes.CHECKCAST, methodReturn.substring(1,
            methodReturn.length() - 1));
        delegate.visitInsn(Opcodes.ARETURN);
        break;
      case 'Z':
      case 'B':
      case 'S':
      case 'C':
      case 'I':
        delegate.visitMethodInsn(Opcodes.INVOKEVIRTUAL, InvocationDelegate, "invokeReturn"
            + methodReturn, "()" + methodReturn);
        delegate.visitInsn(Opcodes.IRETURN);
        break;
      case 'J':
        delegate.visitMethodInsn(Opcodes.INVOKEVIRTUAL, InvocationDelegate, "invokeReturn"
            + methodReturn, "()" + methodReturn);
        delegate.visitInsn(Opcodes.LRETURN);
        break;
      case 'F':
        delegate.visitMethodInsn(Opcodes.INVOKEVIRTUAL, InvocationDelegate, "invokeReturn"
            + methodReturn, "()" + methodReturn);
        delegate.visitInsn(Opcodes.FRETURN);
        break;
      case 'D':
        delegate.visitMethodInsn(Opcodes.INVOKEVIRTUAL, InvocationDelegate, "invokeReturn"
            + methodReturn, "()" + methodReturn);
        delegate.visitInsn(Opcodes.DRETURN);
        break;
      case 'V':
        delegate.visitMethodInsn(Opcodes.INVOKEVIRTUAL, InvocationDelegate, "invokeReturn"
            + methodReturn, "()" + methodReturn);
        delegate.visitInsn(Opcodes.RETURN);
        break;
      default:
        throw new IllegalStateException(methodReturn);
    }
  }

  private int nativeDelegate_addArgument(String parameter, int index) {
    int size = 1;
    delegate.visitInsn(Opcodes.DUP);
    switch (parameter.charAt(0)) {
      case 'L':
      case '[':
        parameter = "Ljava/lang/Object;";
        delegate.visitVarInsn(Opcodes.ALOAD, index);
        break;
      case 'Z':
      case 'B':
      case 'C':
      case 'S':
      case 'I':
        delegate.visitVarInsn(Opcodes.ILOAD, index);
        break;
      case 'J':
        delegate.visitVarInsn(Opcodes.LLOAD, index);
        size = 2;
        break;
      case 'F':
        delegate.visitVarInsn(Opcodes.FLOAD, index);
        break;
      case 'D':
        delegate.visitVarInsn(Opcodes.DLOAD, index);
        size = 2;
        break;
      default:
        throw new IllegalStateException(parameter);
    }
    delegate.visitMethodInsn(Opcodes.INVOKEVIRTUAL, InvocationDelegate, "addArg", "(" + parameter
        + ")V");
    return size;
  }

  // //////////////////////////////////////

  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    return delegate.visitAnnotation(desc, visible);
  }

  public AnnotationVisitor visitAnnotationDefault() {
    return delegate.visitAnnotationDefault();
  }

  public void visitAttribute(Attribute attr) {
    delegate.visitAttribute(attr);
  }

  // //////////////////////////////////////

  public void visitCode() {
    throw new UnsupportedOperationException();
  }

  public void visitFieldInsn(int opcode, String owner, String name, String desc) {
    throw new UnsupportedOperationException();
  }

  public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
    throw new UnsupportedOperationException();
  }

  public void visitIincInsn(int var, int increment) {
    throw new UnsupportedOperationException();
  }

  public void visitInsn(int opcode) {
    throw new UnsupportedOperationException();
  }

  public void visitIntInsn(int opcode, int operand) {
    throw new UnsupportedOperationException();
  }

  public void visitJumpInsn(int opcode, Label label) {
    throw new UnsupportedOperationException();
  }

  public void visitLabel(Label label) {
    throw new UnsupportedOperationException();
  }

  public void visitLdcInsn(Object cst) {
    throw new UnsupportedOperationException();
  }

  public void visitLineNumber(int line, Label start) {
    throw new UnsupportedOperationException();
  }

  public void visitLocalVariable(String name, String desc, String signature, Label start,
      Label end, int index) {
    throw new UnsupportedOperationException();
  }

  public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
    throw new UnsupportedOperationException();
  }

  public void visitMaxs(int maxStack, int maxLocals) {
    throw new UnsupportedOperationException();
  }

  public void visitMethodInsn(int opcode, String owner, String name, String desc) {
    throw new UnsupportedOperationException();
  }

  public void visitMultiANewArrayInsn(String desc, int dims) {
    throw new UnsupportedOperationException();
  }

  public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
    throw new UnsupportedOperationException();
  }

  public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
    throw new UnsupportedOperationException();
  }

  public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
    throw new UnsupportedOperationException();
  }

  public void visitTypeInsn(int opcode, String type) {
    throw new UnsupportedOperationException();
  }

  public void visitVarInsn(int opcode, int var) {
    throw new UnsupportedOperationException();
  }

}
