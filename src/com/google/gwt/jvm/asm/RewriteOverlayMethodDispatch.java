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
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.NEW;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class RewriteOverlayMethodDispatch extends DelegatingMethodVisitor {

  private final OverlayTypePredicate overlayTypePredicate;

  public RewriteOverlayMethodDispatch(MethodVisitor delegate,
      OverlayTypePredicate overlayTypePredicate) {
    super(delegate);
    this.overlayTypePredicate = overlayTypePredicate;
  }

  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc) {
    if (overlayTypePredicate.isOverlayDesc(owner)) {
      //System.out.println(owner + "." + name);
      if (isConstructor(name)) {
        super.visitMethodInsn(opcode, owner + "$", name, desc);
      } else {
        boolean isStatic = isOpcode(opcode, Opcodes.ACC_STATIC);

        String newDescriptor = desc;
        if (!isStatic) {
          String overlayClass = overlayTypePredicate.getImplementingClass(owner, name, desc);
          newDescriptor = new Descriptor(desc).toDescPrefix(overlayClass);
        }
        super.visitMethodInsn(Opcodes.INVOKESTATIC, owner + "$", name, newDescriptor);
      }
    } else {
      super.visitMethodInsn(opcode, owner, name, desc);
    }
  }

  @Override
  public void visitFieldInsn(int opcode, String owner, String name, String desc) {
    if (overlayTypePredicate.isOverlayDesc(owner)) {
      owner = owner + "$";
    }
    super.visitFieldInsn(opcode, owner, name, desc);
  }

  @Override
  public void visitTypeInsn(int opcode, String type) {
    if (isOpcode(opcode, NEW) || isOpcode(opcode, ANEWARRAY)) {
      delegate.visitTypeInsn(opcode, type + (overlayTypePredicate.isOverlayDesc(type) ? "$" : ""));
    } else {
      delegate.visitTypeInsn(opcode, type);
    }
  }

}
