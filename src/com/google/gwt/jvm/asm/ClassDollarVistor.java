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

import static com.google.gwt.jvm.asm.GwtClassMunger.isConstructor;
import static com.google.gwt.jvm.asm.GwtClassMunger.isStatic;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Convert OverlayType to OverlayType$ and change any method dispatch to static method dispatch.
 */
public class ClassDollarVistor extends DelegatingClassVisitor {

  private String originalClassName;
  private OverlayTypePredicate overlayTypePredicate;

  public ClassDollarVistor(ClassVisitor delegate, OverlayTypePredicate overlayTypePredicate) {
    super(delegate);
    this.overlayTypePredicate = overlayTypePredicate;
  }

  @Override
  public void visit(int version, int access, String name, String signature, String superName,
      String[] interfaces) {
    originalClassName = name;
    String superNameMunged = superName + (overlayTypePredicate.isOverlayDesc(superName) ? "$" : "");
    super.visit(version, access, name + "$", signature, superNameMunged, interfaces);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature,
      String[] exceptions) {
    MethodVisitor visitor;
    if (!isStatic(access) && (!isConstructor(name))) {
      int staticMethodAccess = access | Opcodes.ACC_STATIC;
      String staticDesc = new Descriptor(desc).toDescPrefix(originalClassName);
      // System.out.println("Creating: " + originalClassName + "$." + name +
      // staticDesc);
      visitor = delegate.visitMethod(staticMethodAccess, name, staticDesc, signature, exceptions);
      visitor = new RewriteInvokeSpecial2InvokeVirtualForSuper(visitor, overlayTypePredicate);
    } else if (isConstructor(name)) {
      visitor =
          new ConstructorDollarMethodVistor(delegate.visitMethod(access, name, desc, signature,
              exceptions), overlayTypePredicate);
    } else {
      visitor = delegate.visitMethod(access, name, desc, signature, exceptions);
    }
    return new RewriteOverlayMethodDispatch(visitor, overlayTypePredicate);
  }

}
