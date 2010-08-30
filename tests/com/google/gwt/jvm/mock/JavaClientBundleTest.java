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

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

import junit.framework.TestCase;

/**
 * Tests for {@link JavaClientBundle}.
 */
public class JavaClientBundleTest extends TestCase {

  public void testForClass() {
    MyClientBundle myClientBundle = JavaClientBundle.forClass(MyClientBundle.class);
    assertEquals("baz", myClientBundle.cssResource().baz());
    assertEquals("", myClientBundle.bar());
  }

  private static interface MyClientBundle extends ClientBundle {
    @ClientBundle.Source("com/google/foo/foo.css")
    @CssResource.Strict
    MyCssResource cssResource();

    String bar();
  }

  /**
   * tmao, 2009-11-10: could mock out dependency on {@link JavaCssResource},
   * but that would change the static way all of the Java* emulation classes
   * are used.
   */
  private static interface MyCssResource extends CssResource {
    @ClassName("baz")
    String baz();
  }

}
