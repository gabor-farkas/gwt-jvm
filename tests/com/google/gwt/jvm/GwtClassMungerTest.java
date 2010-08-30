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

import static com.google.gwt.jvm.GwtClassLoader.OVERLAY_TYPES;
import static com.google.gwt.jvm.JavaJSObject.wrap;
import static com.google.gwt.jvm.asm.GwtClassMunger.GWT_JAVA_SCRIPT_OBJECT;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Node;
import com.google.gwt.jvm.ClasspathResourceLoader;
import com.google.gwt.jvm.GwtNativeDispatch;
import com.google.gwt.jvm.JavaJSObject;
import com.google.gwt.jvm.asm.GwtClassMunger;
import com.google.gwt.jvm.asm.GwtClassMunger.ClassMeta;
import com.google.gwt.user.client.Element;

import junit.framework.TestCase;

/**
 */
public class GwtClassMungerTest extends TestCase {

  ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader();
  GwtClassMunger munger = new GwtClassMunger(resourceLoader, resourceLoader.loadSet(OVERLAY_TYPES));

  private String desc(Class<?> clazz) {
    return clazz.getName().replace('.', '/');
  }

  public void testIsOverlayTypeDirect() throws Exception {
    assertTrue(munger.isOverlayDesc(desc(JavaScriptObject.class)));
    assertFalse(munger.isOverlayDesc(desc(Object.class)));
  }

  public void testIsOverlayTypeThroughInheritance() throws Exception {
    assertTrue(munger.isOverlayDesc(desc(Node.class)));
  }

  public void testIsOverlayTypeThroughSeveralInheritance() throws Exception {
    assertTrue(munger.isOverlayDesc(desc(Element.class)));
  }

  static class Building {
  }

  static interface Dweling {
  }

  static class House extends Building implements Dweling {
  }

  public void testClassMeta() throws Exception {
    ClassMeta classMeta = munger.classMeta(House.class.getName());
    assertEquals(desc(Building.class), classMeta.getSuperClass());
    assertEquals(desc(Dweling.class), classMeta.getInterfaces()[0]);
  }

  public static class MockJavaScriptObject {
    @Override
    public String toString() {
      return "MockJavaScriptObject";
    }
  }

  static class User extends JavaScriptObject {
    String greet() {
      return "Hello " + this.toString();
    }
  }

  static class UserJava extends MockJavaScriptObject {
    @Override
    public String toString() {
      return "Java User";
    }
  }

  public void testItShouldTranslateNativeMethodCallTo$Dispatch() throws Exception {
    GwtNativeDispatch.instance = new GwtNativeDispatch();
    User user = wrap(new UserJava());
    assertEquals("Hello Java User", user.greet());
  }

  static class StaticNative {
    static native String greet(String name);
  }

  static class JavaStaticNative {
    static String greet(String name) {
      return "hello " + name;
    }
  }

  public void testStaticNative() throws Exception {
    GwtNativeDispatch.instance = new GwtNativeDispatch();
    GwtNativeDispatch.instance.delegate(StaticNative.class, JavaStaticNative.class);
    assertEquals("hello shyam", StaticNative.greet("shyam"));
  }

  static class StaticJS extends JavaScriptObject {
    static String greet(String name) {
      return "hello " + name;
    }
  }

  public void testInvokeStaticMethodOnJSObject() throws Exception {
    assertEquals("hello shyam", StaticJS.greet("shyam"));
  }

  static class EnglishGreeter extends JavaScriptObject {
    private static final String HELLO;
    static {
      HELLO = "hello";
    }

    String greet(String name) {
      return HELLO + name;
    }
  }

  static class SpanishGreeter extends EnglishGreeter {
    @Override
    String greet(String name) {
      return "hola " + name;
    }
  }

  public void testInvokeStaticMethodOnChildJSObject() throws Exception {
    assertEquals("hola shyam", new SpanishGreeter().greet("shyam"));
  }

  public void testGetAnInterface() throws Exception {
    Class<?> clazz =
        Class.forName("com.google.gwt.jvm.GwtClassMungerTest$EnglishGreeter");
    assertTrue(clazz.isInterface());
  }

  public void testImplementingClassReturnsSelf() throws Exception {
    assertEquals(GWT_JAVA_SCRIPT_OBJECT, munger.getImplementingClass(GWT_JAVA_SCRIPT_OBJECT,
        "toString", "()Ljava/lang/String;"));
  }

  public void testImplementingClassOfUserReturnsJSObject() throws Exception {
    assertEquals(GWT_JAVA_SCRIPT_OBJECT, munger.getImplementingClass(desc(User.class), "toString",
        "()Ljava/lang/String;"));
  }

  public void testJavaJSObjectImplementsAll() throws Exception {
    Object obj = wrap(new Object());
    assertTrue(obj instanceof JavaScriptObject);
    assertTrue(obj instanceof User);
  }

  static class NativeClass extends JavaScriptObject {
    native String hello();
    native String identity(String name);
    native String concat(String...names);
  }

  public void testShouldSimulateNPEWhenDispatchingOnNull() throws Exception {
    GwtNativeDispatch.instance = new GwtNativeDispatch();
    GwtNativeDispatch.instance.delegate(JavaScriptObject.class, JavaJSObject.class);
    try {
      ((NativeClass) null).hello();
      fail("NPE expected");
    } catch (NullPointerException e) {
    }
  }

  public static class JavaNativeClass {
    public String identity(String names) {
      return names;
    }

    public String concat(String...names) {
      String text = "";
      for (String name : names) {
        text += name;
      }
      return text;
    }
  }

  public void testItShouldDelegateArraySignatures() throws Exception {
    GwtNativeDispatch.instance = new GwtNativeDispatch();
    GwtNativeDispatch.instance.delegate(NativeClass.class, JavaNativeClass.class);
    NativeClass obj = JavaJSObject.wrap(new JavaNativeClass());
    assertEquals("a", obj.identity("a"));
    assertEquals("ab", obj.concat("a","b"));
  }

  public static class Stateful extends JavaScriptObject {
    public native int getValue();
    public native void setValue(int newValue);
  }
  
  public static class JavaStateful {
    int value;
    
    public int getValue() {
      return value;
    }
    
    public void setValue(int newValue) {
      value = newValue;
    }

    // If these methods are included, the test below fails.
    // TODO(gentle): Wrap JavaJSObject's map such that it is always an identity
    // map.
/*    @Override
    public int hashCode() {
      return value;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      JavaStateful other = (JavaStateful) obj;
      if (value != other.value) return false;
      return true;
    }*/
  }
  
  public void testDifferentJSObjectsShouldNotBeMappedTogether() {
    GwtNativeDispatch.instance = new GwtNativeDispatch();
    GwtNativeDispatch.instance.delegate(Stateful.class, JavaStateful.class);
    Stateful obj1 = JavaJSObject.wrap(new JavaStateful());
    Stateful obj2 = JavaJSObject.wrap(new JavaStateful());
    assertNotSame(obj1, obj2);
    obj1.setValue(5);
    obj2.setValue(999);
    assertEquals(5, obj1.getValue());
    assertEquals(999, obj2.getValue());
  }
}
