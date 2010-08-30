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

import com.google.gwt.jvm.asm.ClassMap;
import com.google.gwt.jvm.asm.Type;

import junit.framework.TestCase;

/**
 * Tests for {@link ClassMap}.
 */
public class ClassMapTest extends TestCase {

  ClassMap map = new ClassMap();

  public void testNewMappedInstance() throws Exception {
    map.addImplementor(Number.class, Integer.class);
    Object[] parameters = {123};
    Number integer = map.map(Number.class).newInstance(parameters);
    assertEquals(123, integer.intValue());

    assertNull(map.map(String.class));
    assertNull(map.map(Type.type(String.class)));
  }

}
