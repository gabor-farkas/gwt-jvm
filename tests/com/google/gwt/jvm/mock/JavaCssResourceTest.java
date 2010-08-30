/* Copyright 2009 Google Inc.
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

package com.google.gwt.jvm.mock;

import com.google.gwt.resources.client.CssResource;

import junit.framework.TestCase;

/**
 * Tests for {@link JavaCssResource}.
 */
public class JavaCssResourceTest extends TestCase {

  public void testForClass() {
    MyCssResource myCssResource = JavaCssResource.forClass(MyCssResource.class);
    assertEquals("foo", myCssResource.foo());
    assertEquals("bar", myCssResource.bar());
    assertFalse(myCssResource.ensureInjected());
  }

  private static interface MyCssResource extends CssResource {
    @ClassName("foo")
    String foo();

    String bar();
  }
}
