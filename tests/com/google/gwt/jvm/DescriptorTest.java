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
package com.google.gwt.jvm;

import com.google.gwt.jvm.asm.Descriptor;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 */
public class DescriptorTest extends TestCase {

  public void testVoid() throws Exception {
    assertSignature("()V");
  }

  public void testPrimitiveTypes() throws Exception {
    assertSignature("(Z)V", boolean.class);
    assertSignature("(B)V", byte.class);
    assertSignature("(C)V", char.class);
    assertSignature("(S)V", short.class);
    assertSignature("(I)V", int.class);
    assertSignature("(J)V", long.class);
    assertSignature("(D)V", double.class);
    assertSignature("(F)V", float.class);
    assertSignature("(V)V", void.class);
  }

  public void testObjectType() throws Exception {
    assertSignature("(Ljava/lang/String;)V", String.class);
  }

  public void testMultipleTypes() throws Exception {
    assertSignature("(Ljava/lang/String;Z)V", String.class, boolean.class);
  }

  private void assertSignature(String signature, Class<?>... expected) {
    Class<?>[] actual = new Descriptor(signature).getParameters();
    assertEquals(Arrays.toString(expected), Arrays.toString(actual));
  }

  public void testReproduceBug() throws Exception {
    Descriptor descriptor = new Descriptor("(Ljava/lang/String;)[Ljava/lang/String;");
    assertEquals("Ljava/lang/Object;", descriptor.getReturnDesc());
  }

  public void testBugNotHandlingArrayArgumentsProperly() throws Exception {
    Descriptor descriptor = new Descriptor("([Ljava/lang/String;)Ljava/lang/String;");
    assertEquals("(LC$;[Ljava/lang/String;)Ljava/lang/String;", descriptor.toDescPrefix("C$"));
  }

}
