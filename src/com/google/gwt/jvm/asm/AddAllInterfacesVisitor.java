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

import org.objectweb.asm.ClassWriter;

import java.util.Set;

/**
 * Add all overlay types interfaces to JavaJSObject
 */
public class AddAllInterfacesVisitor extends DelegatingClassVisitor {

  private final Set<String> overlayTypes;

  public AddAllInterfacesVisitor(ClassWriter delegate, Set<String> overlayTypes) {
    super(delegate);
    this.overlayTypes = overlayTypes;
  }

  @Override
  public void visit(int version, int access, String name, String signature, String superName,
      String[] interfaces) {
    String[] ovelayInterfaces = overlayTypes.toArray(new String[overlayTypes.size()]);
    delegate.visit(version, access, name, signature, superName, ovelayInterfaces);
  }

}
