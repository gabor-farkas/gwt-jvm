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
import static com.google.gwt.jvm.asm.GwtClassMunger.isOpcode;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.MethodVisitor;

public class RewriteInvokeSpecial2InvokeVirtualForSuper extends DelegatingMethodVisitor {

  private final OverlayTypePredicate predicate;

  public RewriteInvokeSpecial2InvokeVirtualForSuper(MethodVisitor delegate,
      OverlayTypePredicate predicate) {
    super(delegate);
    this.predicate = predicate;
  }

  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc) {
    boolean isSuperCall = isOpcode(opcode, INVOKESPECIAL);
    boolean overlayDesc = predicate.isOverlayDesc(owner);
    if (isSuperCall && !overlayDesc && !isConstructor(name)) {
      super.visitMethodInsn(INVOKEVIRTUAL, owner, name, desc);
    } else {
      super.visitMethodInsn(opcode, owner, name, desc);
    }
  }

}
