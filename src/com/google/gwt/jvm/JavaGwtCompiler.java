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

import static com.google.gwt.jvm.asm.Type.type;

import com.google.common.collect.ImmutableMap;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.Artifact;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.dev.cfg.BindingProperty;
import com.google.gwt.dev.cfg.ConfigurationProperty;
import com.google.gwt.dev.cfg.ModuleDef;
import com.google.gwt.dev.cfg.ModuleDefLoader;
import com.google.gwt.dev.cfg.PropertyPermutations;
import com.google.gwt.dev.cfg.StaticPropertyOracle;
import com.google.gwt.dev.javac.CompilationState;
import com.google.gwt.dev.javac.CompiledClass;
import com.google.gwt.dev.javac.StandardGeneratorContext;
import com.google.gwt.dev.util.Name;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

/**
 * This is a wrapper around GWT's compilation tools
 */
public class JavaGwtCompiler {
  private ModuleDef gwtModule;
  private TreeLogger logger = TreeLogger.NULL;

  // These fields are all transient, generated as needed at runtime from the
  // gwtModule.
  private CompilationState compilationState;
  private StandardGeneratorContext generatorContext;
  private PropertyOracle propertyOracle;

  protected Map<String, CompiledClass> compiledClassMap
    = new HashMap<String, CompiledClass>();

  private final Map<String, String> propertiesOverrides;

  /**
   * Create a JavaGwtCompiler for classes in the specified module. This
   * constructor loads the module from its resource name. For example, to load
   * walkabout's editor test harness module, pass
   * "com.google.walkabout.client.editor.harness.Harness" as the module name.
   *
   * Loaded modules are cached by GWT. Hence if the module has already been
   * loaded another way, it will not be loaded twice.
   *
   * Note that loading a GWT module can be quite slow (it has to iterate through
   * every class in the GWT frontend and the user's module). Expect this to take
   * several seconds.
   *
   * @param gwtModuleName The name of the module to load. This should be fully
   * qualified and missing the .gwt.xml on the end. Eg:
   * "com.google.walkabout.client.editor.harness.Harness"
   */
  public JavaGwtCompiler(String gwtModuleName) {
    this(gwtModuleName, ImmutableMap.<String, String> of());
  }

  public JavaGwtCompiler(ModuleDef gwtModule) {
    propertiesOverrides = ImmutableMap.<String, String> of();
    setGwtModule(gwtModule);
  }

  /**
   * See {@link #JavaGwtCompiler(String gwtModuleName)}.
   *
   * @param gwtModuleName
   * @param propertiesOverrides The override mapping of properties for this
   *                            compiler, must not be null.
   */
  public JavaGwtCompiler(String gwtModuleName, Map<String, String> propertiesOverrides) {
    try {
      this.propertiesOverrides = ImmutableMap.copyOf(propertiesOverrides);
      setGwtModule(gwtModuleName);
    } catch (UnableToCompleteException e) {
      throw new RuntimeException(e);
    }
  }

  public ModuleDef getGwtModule() {
    return gwtModule;
  }

  protected void setGwtModule(ModuleDef newGwtModule) {
    if (gwtModule != null && newGwtModule != gwtModule) {
      // Clear out transient properties
      compilationState = null;
      generatorContext = null;
      propertyOracle = null;
      compiledClassMap.clear();
    }
    gwtModule = newGwtModule;
  }

  /**
   * Set the GwtModule to use from a specified module name. For example, to load
   * walkabout's editor test harness module, pass
   * "com.google.walkabout.client.editor.harness.Harness" as the module name.
   * @param gwtModuleName The name of the module to load. This should be fully
   * qualified and missing the .gwt.xml on the end. Eg:
   * "com.google.walkabout.client.editor.harness.Harness"
   */
  protected void setGwtModule(String gwtModuleName)
      throws UnableToCompleteException {
    
    setGwtModule(ModuleDefLoader.loadFromClassPath(TreeLogger.NULL, gwtModuleName));
  }

  /**
   * TODO(spoon)  delete this method and its callers
   */
  public void setResourcesDirectory(File resourcesDirectory) {
  }


  /**
   * Set the logger which the GWT compiler will use for outputting debugging
   * information. By default this is set to TreeLogger.NULL.
   *
   *
   * @param logger Log destination. For example, new PrintWriterTreeLogger()
   */
  public void setLogger(TreeLogger logger) {
    this.logger = logger;
  }

  protected CompilationState getCompilationState() {
    if (compilationState == null) {
      try {
        compilationState = gwtModule.getCompilationState(logger);
      } catch (UnableToCompleteException e) {
        throw new RuntimeException(e);
      }
    }

    return compilationState;
  }

  protected StandardGeneratorContext getGeneratorContext() {
    if (generatorContext == null) {
      ArtifactSet artifacts = new ArtifactSet();

      generatorContext = new StandardGeneratorContext(getCompilationState(),
            gwtModule, null, artifacts);

      generatorContext.setPropertyOracle(getPropertyOracle());
    }

    return generatorContext;
  }

  protected PropertyOracle getPropertyOracle() {
    if (propertyOracle == null) {
      PropertyPermutations permutations = new PropertyPermutations(gwtModule.getProperties(),
          gwtModule.getActiveLinkerNames());

      SortedSet<ConfigurationProperty> configPropSet =
          gwtModule.getProperties().getConfigurationProperties();
      ConfigurationProperty[] configProps = configPropSet.toArray(
          new ConfigurationProperty[configPropSet.size()]);

      BindingProperty[] orderedProperties = permutations.getOrderedProperties();

      String[] processedProperties = replaceOrderedPropertyValues(
          orderedProperties, permutations.getOrderedPropertyValues(0));

      propertyOracle = new StaticPropertyOracle(
          orderedProperties,
          processedProperties,
          configProps);
    }

    return propertyOracle;
  }

  /**
   * Replace (if propertiesOverrides is specified) the property values based
   * on the specified overrides. Must only be called when propertiesOverrides
   * is not null (which is not supposed to be possible).
   */
  private String[] replaceOrderedPropertyValues(BindingProperty[] orderedProperties,
      String[] orderedOriginalValues) {

    assert propertiesOverrides != null;
    
    String[] result = new String[orderedProperties.length];
    for (int i = 0; i < orderedProperties.length; i++) {
      if (propertiesOverrides.containsKey(orderedProperties[i].getName())) {
        result[i] = propertiesOverrides.get(orderedProperties[i].getName());
      } else {
        result[i] = orderedOriginalValues[i];
      }
    }
    return result;
  }
  
  protected void printClassLoaderChain(ClassLoader loader) {
    while (loader != null) {
      System.out.print("- " + loader.getClass().getCanonicalName());
      loader = loader.getParent();
    }
    System.out.println();
  }

  protected ClassLoader getClassLoader(Class<?> classLiteral) {
    // Its important that we use the right class loader (generally a
    // GwtClassLoader) as the parent class loader. This will usually be
    // consistent during the loading of the GWT module, but we'll check just
    // in case.
    
    ClassLoader gwtClassLoader = classLiteral.getClassLoader();
    // This is a giant hack to make the class loader work. We can't use
    // the instanceof operator because that always returns null from things
    // loaded from different contexts.
    type(gwtClassLoader.getClass()).invoke(
        gwtClassLoader, "setCompilationState", compilationState);
    
    return gwtClassLoader;
  }

  public CompiledClass compile(String literalName, Class<? extends Generator> generatorClass) {
    CompiledClass compiledClass = compiledClassMap.get(literalName);
    if (compiledClass != null) {
      logger.log(Type.INFO, "Using cached resource for " + literalName);
      return compiledClass;
    }

    if (gwtModule == null) {
      throw new RuntimeException("Gwt module is not set.");
    }

    logger.log(Type.INFO, "Generating " + literalName);

    String className;
    ArtifactSet newArtifacts;
    try {
      className = getGeneratorContext().runGenerator(logger,
          generatorClass, literalName);
      newArtifacts = generatorContext.finish(logger);
    } catch (UnableToCompleteException e) {
      throw new RuntimeException(e);
    }
    
    String internalName = Name.BinaryName.toInternalName(className);
    
    TreeLogger branch = logger.branch(TreeLogger.INFO, "New compilation artifacts");
    for (Artifact<?> artifact : newArtifacts) {
      branch.log(TreeLogger.INFO, artifact.toString());
    }
    
    compiledClass = getCompilationState().getClassFileMap().get(internalName);

    if (compiledClass != null) {
      compiledClassMap.put(literalName, compiledClass);
    }
    
    return compiledClass;
  }

  /**
   * This method uses GWT's compiler to generate the class literal specified by
   * classLiteral using the generator class specified by generatorClass. The
   * generator class must be capable of generating the specified classLiteral.
   *
   * Eg, to generate
   * {@link com.google.walkabout.client.debug.DomLogger.Resources} (which
   * extends {@link com.google.gwt.resources.client.ClientBundle} you can pass
   * either {@link com.google.gwt.resources.rebind.context.StaticClientBundleGenerator} or
   * {@link com.google.gwt.resources.rebind.context.InlineClientBundleGenerator} as the
   * generatorClass.
   *
   * Note: If you load anything which uses generated resources (eg,
   * {@link com.google.gwt.user.client.ui.MenuBar.Resources}, you must set
   * the resources directory using setResourcesDirectory before calling
   * generate.
   *
   * @param classLiteral Class to be generated. This class will generally be an
   * interface extending ClientBundle, UiBundle or CssResource.
   * @param generatorClass Gwt generator for generating classLiteral.
   * @return the generated class. Note that this will not be an instance of
   * classLiteral but will extend / implement it.
   */
  public Class<?> generate(Class<?> classLiteral, Class<? extends Generator> generatorClass) {
    CompiledClass compiledClass = compile(classLiteral.getCanonicalName(), generatorClass);
    if (compiledClass == null) {
      throw new RuntimeException("Could not generate class " + classLiteral);
    }
    try {
      return getClassLoader(classLiteral).loadClass(compiledClass.getInternalName());
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public <T> T instantiateGeneratedClass(
      Class<T> classLiteral, Class<? extends Generator> generatorClass) {
    Class<?> c = generate(classLiteral, generatorClass);

    try {
      return classLiteral.cast(c.newInstance());
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
