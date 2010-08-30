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

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.jvm.mock.JavaClientBundle;
import com.google.gwt.jvm.mock.JavaCssResource;
import com.google.gwt.jvm.mock.JavaUiBinder;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.rebind.context.InlineClientBundleGenerator;
import com.google.gwt.resources.rebind.context.StaticClientBundleGenerator;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.rebind.UiBinderGenerator;

import java.io.File;
import java.util.Map;

/**
 * The current GwtBrowserEmulator UiBinderStrategy is used to determine how
 * UiBinder objects are created. Consult the documentation on each strategy
 * for detail on how they work.
 * 
 * The UiBinderStrategy is chosen by calling:
 * GwtBrowserEmulator.get().setUiBinderStrategy()
 */
public abstract class GwtCodeGenerationStrategy {
  protected abstract Object createUiBinder(final Class<UiBinder<?, ?>> classLiteral);
  protected abstract Object createClientBundle(final Class<? extends ClientBundle> classLiteral);
  protected abstract Object createCssResource(final Class<? extends CssResource> classLiteral);

  @SuppressWarnings("unchecked")
  public Object createMockClass(final Class<?> classLiteral) {
    if (UiBinder.class.isAssignableFrom(classLiteral)) {
      return createUiBinder((Class<UiBinder<?, ?>>) classLiteral);
    } else if (ClientBundle.class.isAssignableFrom(classLiteral)) {
      return createClientBundle((Class<? extends ClientBundle>) classLiteral);
    } else if (CssResource.class.isAssignableFrom(classLiteral)) {
      return createCssResource((Class<? extends CssResource>) classLiteral);
    } else {
      return null;
    }
  }
  
  /**
   * The mock strategy creates mock classes using JavaUiBinder and friends.
   * It iterates through any @UiFields and instantiates them correctly.
   * It ignores the UiBinder XML file. 
   */
  public static GwtCodeGenerationStrategy mock() {
    return new GwtCodeGenerationStrategy() {
      @SuppressWarnings("unchecked")
      @Override
      public Object createClientBundle(Class<? extends ClientBundle> classLiteral) {
        return JavaClientBundle.forClass(classLiteral);
      }
      
      @SuppressWarnings("unchecked")
      @Override
      public Object createCssResource(Class<? extends CssResource> classLiteral) {
        return JavaCssResource.forClass(classLiteral);
      }
      
      @SuppressWarnings("unchecked")
      @Override
      public Object createUiBinder(Class<UiBinder<?, ?>> classLiteral) {
        return JavaUiBinder.gwtCreateForUiBinder(classLiteral);
      }

      @Override
      public String toString() {
        return "mock";
      }
    };
  }
  
  /**
   * The manual strategy allows the user to manually assign all the
   * GWT create classes using setGWTCreateClass* and friends.   
   */
  public static GwtCodeGenerationStrategy manual() {
    return new GwtCodeGenerationStrategy() {
      // Fall through, letting JavaGwtBridge.create() do all the work
      @Override
      public Object createClientBundle(Class<? extends ClientBundle> classLiteral) { return null; }
      @Override
      public Object createCssResource(Class<? extends CssResource> classLiteral) { return null; }
      @Override
      public Object createUiBinder(Class<UiBinder<?, ?>> classLiteral) { return null; }

      @Override
      public String toString() {
        return "manual";
      }
    };
  }

  /**
   * The compiled classes strategy uses GWT's compiler to generate code &
   * compile UiBinder parts as needed, and link them into the runtime.
   * 
   * @param gwtModuleName The fully qualified name of the gwt module to load.
   * For example, "com.google.walkabout.client.editor.harness.DefaultTestHarness"
   * @param resourceDirectory The directory in which to store any static
   * resources generated. If this is null, the static resources will be compiled
   * inline into the generated classes.
   * @param logger The TreeLogger to send debugging output to. If this is set
   * to null, the default logger (TreeLogger.NULL) is used.
   * @param propertiesOverrides The overrides for permutation properties mapping
   * to be passed into the JavaGwtCompiler.
   * @return A GwtCodeGenerationStrategy which can be passed to the
   * GwtBrowserEmulator and used to generate resources
   */
  public static GwtCodeGenerationStrategy compiledClasses(
      final String gwtModuleName, final File resourceDirectory, TreeLogger logger,
      Map<String, String> propertiesOverrides) {

    final JavaGwtCompiler compiler = new JavaGwtCompiler(gwtModuleName, propertiesOverrides);
    compiler.setResourcesDirectory(resourceDirectory);
    if (logger != null) {
      compiler.setLogger(logger);
    }
    
    return new GwtCodeGenerationStrategy() {
      @Override
      public Object createClientBundle(Class<? extends ClientBundle> classLiteral) {
        Class<? extends Generator> generatorClass
          = resourceDirectory != null ? StaticClientBundleGenerator.class
              : InlineClientBundleGenerator.class;
        
        return compiler.instantiateGeneratedClass(classLiteral, generatorClass);
      }
      
      @Override
      public Object createCssResource(Class<? extends CssResource> classLiteral) {
        // This shouldn't be called generally if the CssResource occurs inside a
        // bundle.
        return null;
      }
      
      @Override
      public Object createUiBinder(Class<UiBinder<?, ?>> classLiteral) {
        return compiler.instantiateGeneratedClass(classLiteral, UiBinderGenerator.class);
      }

      @Override
      public String toString() {
        return "compiledClasses";
      }
    };
  }
}
