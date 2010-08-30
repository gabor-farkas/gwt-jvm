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
import com.google.gwt.jvm.GwtBrowserEmulator;
import com.google.gwt.jvm.mock.JavaDocument;

import static com.google.gwt.jvm.asm.Type.type;
import com.google.gwt.dev.cfg.ModuleDef;

/**
 * This is a simple helper class to statically render GWT apps.
 * If you want to render a complex app that does login or anything more complex,
 * this will not suffice.
 *
 */
public class StaticRenderer {
  Class <? extends EntryPoint> entryPoint;
  
  @SuppressWarnings("unchecked")
  public StaticRenderer(ModuleDef module) {
    String[] entryPointTypes = module.getEntryPointTypeNames();
    
    // We'll only load the first listed in the module.
    String entryPointType = entryPointTypes[0];
    
    try {
      entryPoint =
        (Class<? extends EntryPoint>) Thread.currentThread().getContextClassLoader().loadClass(entryPointType);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
  
  public StaticRenderer(Class <? extends EntryPoint> entryPointClass) {
    entryPoint = entryPointClass;
  }
  
  public String render() {
    GwtBrowserEmulator browser = new GwtBrowserEmulator();
    browser.reset();
    EntryPoint ep = type(entryPoint).newInstance();
    ep.onModuleLoad();
    return JavaDocument.get().getDocumentElement().getInnerHTML();
  }
}
