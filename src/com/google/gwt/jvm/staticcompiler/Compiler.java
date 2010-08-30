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

package com.google.gwt.jvm.staticcompiler;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.jvm.ClasspathResourceLoader;
import com.google.gwt.jvm.GwtClassLoader;
import com.google.gwt.jvm.JavaGwtCompiler;
import com.google.gwt.jvm.asm.GwtClassMunger;
import com.google.gwt.dev.cfg.ModuleDef;
import com.google.gwt.dev.cfg.ModuleDefLoader;
import com.google.gwt.dev.javac.CompiledClass;
import com.google.gwt.dev.util.Name;
import com.google.gwt.dev.util.collect.HashMap;
import com.google.gwt.dev.util.log.PrintWriterTreeLogger;
import com.google.gwt.uibinder.rebind.UiBinderGenerator;

import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * This is a static compiler for offline processing of JARs. Once a JAR has been
 * compiled with this code, it can be loaded like normal.
 */
public class Compiler {
  TreeLogger logger = TreeLogger.NULL;
  ModuleDef gwtModule;
  JavaGwtCompiler gwtCompiler;
  
  Map<String, String> gwtCreateClasses = new HashMap<String, String>();
  
  public void setLogger(TreeLogger logger) {
    this.logger = logger;
  }

  interface CompiledClassHandler {
    void accept(String className, byte[] compiledClass);
  }
  
  protected void inspectClass(String internalName, byte[] bytes, CompiledClassHandler handler) {
    ClassReader reader = new ClassReader(bytes);
    System.out.println("maybeCompile " + reader.getClassName() + " extends " + reader.getSuperName());
    
    boolean isUiBinder = false;
    for (String i : reader.getInterfaces()) {
      System.out.println("i " + i);
      if (i.equals("com/google/gwt/uibinder/client/UiBinder")) {
        isUiBinder = true;
      }
    }
    
    if (isUiBinder) {
      if (gwtCompiler == null) {
        gwtCompiler = new JavaGwtCompiler(gwtModule);
        gwtCompiler.setLogger(logger);
      }
      
      String className = Name.InternalName.toSourceName(internalName);
      CompiledClass compiledClass = gwtCompiler.compile(className, UiBinderGenerator.class);
      
      if (handler != null) {
        handler.accept(compiledClass.getInternalName(), compiledClass.getBytes());
      }
      
      gwtCreateClasses.put(Name.InternalName.toBinaryName(internalName), compiledClass.getSourceName());
    }
    
    // This is insufficient. We also need to track anything that has JSO as the
    // root of its type heirachy.
    if (reader.getSuperName().equals(Name.BinaryName.toInternalName(JavaScriptObject.class.getCanonicalName()))) {
      System.out.println("Javascriptobject");
    }
  }
  
  public String compileJar(String jarFileName, String moduleName)
          throws IOException, UnableToCompleteException {
    final JarFile jar = new JarFile(jarFileName);
    gwtModule = ModuleDefLoader.loadFromClassPath(TreeLogger.NULL, moduleName);
    
    String outFilename = "/home/gentle/out.jar"; 
    File file = new File(outFilename);
    System.out.println("Writing to " + file.getCanonicalPath());
    FileOutputStream fos = new FileOutputStream(file);
    
    final JarOutputStream outJar = new JarOutputStream(fos, jar.getManifest());
    
    ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader(new ClassLoader() {
      @Override
      public InputStream getResourceAsStream(String name) {
        System.out.println("Reading " + name);
        try {
          ZipEntry entry = jar.getEntry(name);
          if (entry != null) {
            return jar.getInputStream(entry);
          }
        } catch (IOException e) {
          // Swallow and fall through below.
        }
        
        return getClass().getClassLoader().getResourceAsStream(name);
      }
    });
    
    ClasspathResourceLoader contextResourceLoader = new ClasspathResourceLoader();
    Set<String> defaultOverlayTypes = contextResourceLoader.loadSet(GwtClassLoader.OVERLAY_TYPES);
    
    final GwtClassMunger munger = new GwtClassMunger(resourceLoader, defaultOverlayTypes);
    
    CompiledClassHandler handler = new CompiledClassHandler() {
      @Override
      public void accept(String internalName, byte[] compiledClass) {
        // Internal name is a name like com/google/walkabout/Foo$Bar
        String binaryName = Name.InternalName.toBinaryName(internalName);
        byte[] mungedBytes = munger.mungeBytes(binaryName, compiledClass);
        String fileName = internalName + ".class";
        System.out.println("Adding " + fileName + " to the jar");
        JarEntry je = new JarEntry(fileName);
        try {
          outJar.putNextEntry(je);
          outJar.write(mungedBytes);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    };
    
    for (Enumeration<JarEntry> e = jar.entries(); e.hasMoreElements();) {
      JarEntry entry = e.nextElement();

      String entryName = entry.getName();
      
      if (entryName.endsWith(".class")) {
        byte[] bytes = resourceLoader.loadBytes(entryName);
        String internalName = entryName.replaceAll("\\.class$", "");
        // Copy the class itself
        handler.accept(internalName, bytes);
        // ... And if its a UIBinder or something, compile it and add the result.
        inspectClass(internalName, bytes, handler);
        
      } else {
        // Just write the chunk directly.
        System.out.println("Copying " + entryName);
        if (!entry.isDirectory() && !entryName.equals("META-INF/MANIFEST.MF")) {
          outJar.putNextEntry(entry);
          outJar.write(resourceLoader.loadBytes(entryName));
        }
      }
    }
    
    outJar.close();
    
    return outFilename;
  }
  
  public static void main(String[] args) throws Exception {
    // For now, the first argument must be a GWT module.
    
    if (args.length != 2) {
      System.err.println("Usage: java Compiler jar_file.jar my_gwt_module");
    } else {
      Compiler compiler = new Compiler();
      compiler.setLogger(new PrintWriterTreeLogger());
      compiler.compileJar(args[0], args[1]);
    }
  }
}
