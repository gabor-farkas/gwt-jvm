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

/**
 */
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;

import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class TypeTest extends TestCase {

  static class B {
    static String value;
    String method() {
      return "abc";
    }
  }

  static class C extends B {
  }

  public void testInvokeOnSuperClass() throws Exception {
    assertEquals("abc", Type.invokeMethod(new C(), "method"));
  }

  public void testMethodNotFound() throws Exception {
    try {
      Type.invokeMethod(new C(), "methodDoesNotExist");
      fail();
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("methodDoesNotExist"));
    }
  }
  
  public void testGetStaticField() throws Exception {
    B.value = "ABC";
    assertEquals("ABC", Type.type(B.class).getStaticField("value"));
  }
  
  public void testSetStaticField() throws Exception {
    Type.type(B.class).setStaticField("value", "123");
    assertEquals("123", B.value);
  }
  
  @SuppressWarnings("unused")
  private static class UiBinderUser {
    @UiField String somebody;
    @UiField int anumber;
    double noUiField;
    
    @UiFactory String provideSomebody() {
      return "somebody";
    }
  }
  
  @SuppressWarnings("unused")
  private static class UiBinderUserChild extends UiBinderUser {
    @UiField int childfield;
    @UiField (provided = true) int providedChildfield;
    
  }
  
  public void testGetAllUiFields() {
    List<Field> fields = Type.getAllUiFields(UiBinderUserChild.class);
    assertEquals(4, fields.size());
    assertEquals("childfield", fields.get(0).getName());
    assertEquals("providedChildfield", fields.get(1).getName());
    assertEquals("somebody", fields.get(2).getName());
    assertEquals("anumber", fields.get(3).getName());
  }
  
  public void testGetAllUnProvidedUiFields() {
    List<Field> fields = Type.getAllUnProvidedUiFields(UiBinderUserChild.class);
    assertEquals(3, fields.size());
    assertEquals("childfield", fields.get(0).getName());
    assertEquals("somebody", fields.get(1).getName());
    assertEquals("anumber", fields.get(2).getName());
  }
  
  public void testFindUiFactoryMethod() throws Exception {
    Field anumber = UiBinderUser.class.getDeclaredField("anumber");
    assertNull(Type.findUiFactoryMethod(anumber));
    Field somebody = UiBinderUser.class.getDeclaredField("somebody");
    Method method = Type.findUiFactoryMethod(somebody);
    assertNotNull(method);
    assertEquals("provideSomebody", method.getName());
  }
}
