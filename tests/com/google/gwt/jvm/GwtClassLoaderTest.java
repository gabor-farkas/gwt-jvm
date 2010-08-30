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

import com.google.common.collect.Sets;
import com.google.gwt.core.client.JavaScriptObject;

import junit.framework.TestCase;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 */
public class GwtClassLoaderTest extends TestCase {

  static class NativeMethod {
    public String value;

    native boolean _boolean();

    native byte _byte();

    native char _char();

    native char _short();

    native int _int();

    native long _long();

    native float _float();

    native static double _double();

    native String _string();

    native void _void();

    native String _args(boolean z, byte b, char c, short s, int i, long j, float f, double d);

    @Override
    public String toString() {
      return "native";
    }
  }

  static class JavaMethod {
    private final NativeMethod nativeMethod;

    public JavaMethod(NativeMethod nativeMethod) {
      this.nativeMethod = nativeMethod;
    }

    public boolean _boolean() {
      return true;
    }

    public byte _byte() {
      return 123;
    }

    public char _char() {
      return 'Z';
    }

    public char _short() {
      return 12;
    }

    public int _int() {
      return 1234;
    }

    public long _long() {
      return 12345;
    }

    public float _float() {
      return 1.2f;
    }

    public static double _double() {
      return 1.23;
    }

    public String _string() {
      return "string()" + nativeMethod;
    }

    public void _void() {
      nativeMethod.value = "void()";
    }

    public String _args(boolean z, byte b, char c, short s, int i, long j, float f, double d) {
      return String.format("args(%s, %s, %s, %s, %s, %s, %s, %s)%s", z, b, c, s, i, j, f, d,
          nativeMethod);
    }
  }

  public void testCallNoArgNativeMethodReturnTypes() throws Exception {
    GwtNativeDispatch.instance = new GwtNativeDispatch();
    GwtNativeDispatch.instance.delegate(NativeMethod.class, JavaMethod.class);
    assertEquals(true, new NativeMethod()._boolean());
    assertEquals(123, new NativeMethod()._byte());
    assertEquals('Z', new NativeMethod()._char());
    assertEquals(12, new NativeMethod()._short());
    assertEquals(1234, new NativeMethod()._int());
    assertEquals(12345l, new NativeMethod()._long());
    assertEquals(1.2f, new NativeMethod()._float());
    assertEquals(1.23d, NativeMethod._double());
    assertEquals("string()native", new NativeMethod()._string());
    NativeMethod nativeMethod = new NativeMethod();
    nativeMethod._void();
    assertEquals("void()", nativeMethod.value);
  }

  public void testCallArgTypeNativeMethodReturnObject() throws Exception {
    assertEquals("args(true, 1, Z, 9, 2, 3, 1.4, 1.5)native", new NativeMethod()._args(true,
        (byte) 1, 'Z', (short) 9, 2, 3l, 1.4f, 1.5d));
  }

  public void testItShouldDeclareAPackege() throws Exception {
    assertNotNull(TestCase.class.getPackage());
    assertEquals("junit.framework", TestCase.class.getPackage().getName());
  }

  public void testDefaultOverlayTypes() {
    // default loader
    GwtClassLoader target = new GwtClassLoader(this.getClass().getClassLoader());

    // test three - not an overlay, then forced overlay, then additional overlay.
    assertFalse(target.munger.isOverlayDesc("com/google/gwt/jvm/GwtClassLoader"));
    assertTrue(target.munger.isOverlayDesc("com/google/gwt/dom/client/Node"));
    assertTrue(target.munger.isOverlayDesc(
        "com/google/gwt/jvm/GwtClassMungerTest$EnglishGreeter"));
  }

  public void testCustomOverlayTypes() {
    // create with single additional overlay type
    String additionalType = "com/google/gwt/jvm/mock/JavaStyleTest$JsoWrap";
    GwtClassLoader target = new GwtClassLoader(this.getClass().getClassLoader(),
        Sets.newHashSet(additionalType), null);

    // test with non overlay, then forced
    assertFalse(target.munger.isOverlayDesc("com/google/gwt/jvm/GwtClassLoader"));
    assertTrue(target.munger.isOverlayDesc("com/google/gwt/dom/client/Node"));

    // make sure overlay types aren't read from file, but sourced from set instead
    assertFalse(target.munger.isOverlayDesc(
        "com/google/gwt/jvm/GwtClassMungerTest$EnglishGreeter"));
    assertTrue(target.munger.isOverlayDesc(additionalType));
  }

  public void testDefaultPackageMocking() {
    // default loader
    final Queue<String> loadedQueue = new LinkedList<String>();
    GwtClassLoader target = new GwtClassLoader(new ClassLoader(){
      @Override
      public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // append to queue before passing through to class's loader
        loadedQueue.add(name);
        return this.getClass().getClassLoader().loadClass(name);
      }
    });

    // check org.xml is passed through
    try {
      Class.forName(org.xml.sax.SAXException.class.getName(), true, target);
    } catch (ClassNotFoundException e) {
      fail("forName raised exception: " + e.getMessage());
    }
    assertEquals(2, loadedQueue.size());
    assertEquals(java.lang.Object.class.getName(), loadedQueue.remove());
    assertEquals(org.xml.sax.SAXException.class.getName(), loadedQueue.remove());

    // check class that should be mocked isn't passed through
    try {
      Class.forName(JavaScriptObject.class.getName(), true, target);
      for (String s : loadedQueue) {
        System.out.println(s);
      }
    } catch (ClassNotFoundException e) {
      fail("forName raised exception: " + e.getMessage());
    }
    assertEquals(0, loadedQueue.size());
  }

  public void testCustomPackageMocking() {
    // custom prefix set for loader:
    Set<String> noMock =  Sets.newHashSet(
        "java.", "javax.", "sun.", "org.xml.", "com.sun.net", "junit.framework.");

    // loader using custom prefix set.
    final Queue<String> loadedQueue = new LinkedList<String>();
    GwtClassLoader target = new GwtClassLoader(new ClassLoader(){
      @Override
      public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // append to queue before passing through to class's loader
        loadedQueue.add(name);
        return this.getClass().getClassLoader().loadClass(name);
      }
    }, null, noMock);

    // check org.xml is passed through
    try {
      Class.forName(org.xml.sax.SAXException.class.getName(), true, target);
    } catch (ClassNotFoundException e) {
      fail("forName raised exception: " + e.getMessage());
    }
    assertEquals(2, loadedQueue.size());
    assertEquals(java.lang.Object.class.getName(), loadedQueue.remove());
    assertEquals(org.xml.sax.SAXException.class.getName(), loadedQueue.remove());

    // check class that matches prefix isn't passed through
    try {
      Class.forName(TestCase.class.getName(), true, target);
      for (String s : loadedQueue) {
        System.out.println(s);
      }
    } catch (ClassNotFoundException e) {
      fail("forName raised exception: " + e.getMessage());
    }
    assertEquals(1, loadedQueue.size());
    assertEquals(TestCase.class.getName(), loadedQueue.remove());
  }
}
