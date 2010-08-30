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

import static com.google.gwt.jvm.asm.GwtClassMunger.JAVA_JS_OBJECT;

import com.google.common.collect.Sets;
import com.google.gwt.dev.javac.CompilationState;
import com.google.gwt.dev.javac.CompiledClass;
import com.google.gwt.dev.util.Name;
import com.google.gwt.jvm.asm.GwtClassMunger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

/**
 * The GWT class loader which needs to be included in the JVM start up like this
 *
 * java -Djava.system.class.loader=com.google.gwt.jvm.GwtClassLoader
 *
 * to take advantage of running of GWT in java only mode.
 */
public class GwtClassLoader extends ClassLoader {
  public static final String OVERLAY_TYPES =
      "com/google/gwt/jvm/OverlayTypes.properties";
  public static final String ADDITIONAL_OVERLAY_TYPES_JVM_PROPERTY =
      "com.google.gwt.jvm.additionalOverlayTypes";

  private final ResourceLoader resourceLoader;
  private final Set<String> unmockedPrefixes; // prefixes of classes to be loaded by parent loader.
  public GwtClassMunger munger;

  /**
   * Construct a class loader using defaults for the unmocked prefix set and additional overlays.
   * The former into the other constructor, the latter is read from a file defined
   * in the jvm for ADDITIONAL_OVERLAY_TYPES_JVM_PROPERTY.
   * @param delegate Parent classloader.
   */
  public GwtClassLoader(ClassLoader delegate) {
    this(delegate, null, null);
  }

  /**
   * Construct a class loader along with additional mocking information.
   * @param delegate Parent classloader.
   * @param additionalOverlayTypes Set of class names for overlay types (i.e. JSO implementations).
   *   Use null if they are to be loaded from a file configured in the jvm.
   * @param unmockedPrefixes Non-null prefixes, any class with a prefix in this will not be mocked.
   */
  public GwtClassLoader(ClassLoader delegate, Set<String> additionalOverlayTypes,
      Set<String> unmockedPrefixes) {
    super(delegate);
    this.resourceLoader = new ClasspathResourceLoader(delegate);
    Set<String> defaultOverlayTypes = resourceLoader.loadSet(OVERLAY_TYPES);

    // load overlays:
    if (additionalOverlayTypes == null) {
      String additionalOverlayTypesFile = System.getProperty(ADDITIONAL_OVERLAY_TYPES_JVM_PROPERTY);
      if (additionalOverlayTypesFile != null) {
        additionalOverlayTypes = resourceLoader.loadSet(additionalOverlayTypesFile);
      } else {
        additionalOverlayTypes = Collections.emptySet();
      }
    }
    defaultOverlayTypes.addAll(additionalOverlayTypes);

    // initialise prefixes, using default if none set.
    if (unmockedPrefixes == null) {
      unmockedPrefixes = Sets.newHashSet("java.", "javax.", "sun.", "org.xml.", "com.sun.net",
          "org.mockito.");
    }
    this.unmockedPrefixes = unmockedPrefixes;

    // initialise class munging:
    this.munger = new GwtClassMunger(resourceLoader, defaultOverlayTypes);
    try {
      defineMungedClass(JAVA_JS_OBJECT.replace('/', '.'), munger.getJavaJSObject());
    } catch (ClassFormatError e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    Class<?> clazz = findLoadedClass(name);
    if (clazz != null) {
      return clazz;
    }

    for (String prefix : unmockedPrefixes) {
      if (name.startsWith(prefix)) {
        return super.loadClass(name);
      }
    }

    // special case for exception:
    if (name.equals(ClassResourceNotFoundException.class.getName())) {
      return super.loadClass(name);
    }

    // Try and load it 'normally', munging the bytes.
    try {
      byte[] mungedBytes = munger.munge(name);
      if (mungedBytes != null) {
        return defineMungedClass(name, mungedBytes);
      }
    } catch (ClassResourceNotFoundException e) {
      // Try and load the class from the compilation state. The generator
      // will need to have already been run on the class for this to work.
      if (compilationState != null) {
        String internalName = Name.BinaryName.toInternalName(name);
        CompiledClass compiledClass
          = compilationState.getClassFileMap().get(internalName);
        
        if (compiledClass != null) {
          byte[] bytes = munger.mungeBytes(name, compiledClass.getBytes());
          return defineMungedClass(name, bytes);
        }
      }

      throw new ClassNotFoundException(e.getMessage(), e);
    }

    byte[] originalBytes = resourceLoader.loadClassBytes(name);
    return defineClass(name, originalBytes, 0, originalBytes.length, null);
  }
  
  protected Class<?> defineMungedClass(String name, byte[] mungedBytes)
          throws ClassFormatError {
    // Left, but disabled, for debugging purposes.
    // writeClassToFile(name, mungedBytes);
    String packageName = GwtClassMunger.packageName(name);
    if (getPackage(packageName) == null) {
      definePackage(packageName, null, null, null, null, null, null, null);
    }
    return defineClass(name, mungedBytes, 0, mungedBytes.length);
  }
  
  public byte[] mungeBytes(String name, byte[] classBytes) {
    return munger.mungeBytes(name, classBytes);
  }
  
  /**
   * Define a class using the specified bytes. Munge the bytes.
   * @param name
   * @param classBytes
   * @return The requested class, munged.
   */
  protected Class<?> defineAndMungeClass(String name, byte[] classBytes) {
    byte[] mungedBytes = munger.mungeBytes(name, classBytes);
    if (mungedBytes != null) {
      return defineMungedClass(name, mungedBytes);
    } else {
      return defineClass(name, classBytes, 0, classBytes.length, null);
    }
  }

  void writeClassToFile(String name, byte[] mungedBytes) {
    String path = name.replace('.', '/') + ".class";
    File file = new File("bin-munged", path);
    file.getParentFile().mkdirs();
    try {
      FileOutputStream out = new FileOutputStream(file);
      out.write(mungedBytes);
      out.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  private CompilationState compilationState;

  public CompilationState getCompilationState() {
    return compilationState;
  }

  public void setCompilationState(CompilationState compilationState) {
    this.compilationState = compilationState;
  }
}
