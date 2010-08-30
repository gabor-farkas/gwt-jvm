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

import static com.google.gwt.jvm.asm.GwtClassMunger.isOpcode;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Replaces native methods with dispatch of OverlayType dispatch
 */
public class NativeMethodDispatchVisitor extends DelegatingClassVisitor {

  private final OverlayTypePredicate overlayTypePredicate;
  private String className;

  public NativeMethodDispatchVisitor(ClassVisitor delegate,
      OverlayTypePredicate overlayTypePredicate) {
    super(delegate);
    this.overlayTypePredicate = overlayTypePredicate;
  }

  @Override
  public void visit(int version, int access, String name, String signature, String superName,
      String[] interfaces) {
    this.className = name;
    delegate.visit(version, access, name, signature, superName, interfaces);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature,
      String[] exceptions) {
    int nonNativeAccess = access & ~Opcodes.ACC_NATIVE;
    MethodVisitor visitMethod =
        delegate.visitMethod(nonNativeAccess, name, desc, signature, exceptions);
    if (isOpcode(access, Opcodes.ACC_NATIVE)) {
      boolean isStatic = isOpcode(access, Opcodes.ACC_STATIC);
      return new NativeMethodDelegatingVisitor(visitMethod, className, name, new Descriptor(desc),
          isStatic);
    } else {
      //System.out.println(className + "." + name + "(" + desc + ")");
      return new RewriteOverlayMethodDispatch(visitMethod, overlayTypePredicate);
    }
  }


}
