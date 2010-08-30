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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dev.util.log.PrintWriterTreeLogger;

import junit.framework.TestCase;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Test for the java compiler. NOT YET WORKING
 */
public class RenderExampleModule extends TestCase {
  @SuppressWarnings("unchecked")
  public void testRenderExampleModule() throws Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    Class<? extends EntryPoint> entryPointClass = (Class<? extends EntryPoint>) classLoader.loadClass("com.google.gwt.corp.testing.junit.examplemodule.client.ExampleModule");
    
    String rendered_output = new StaticRenderer(entryPointClass).render();
    System.out.println(rendered_output);
  }
}
