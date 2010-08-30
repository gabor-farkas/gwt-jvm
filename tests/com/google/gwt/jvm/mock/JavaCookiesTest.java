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

package com.google.gwt.jvm.mock;

import com.google.gwt.jvm.JavaGWTTestCase;
import com.google.gwt.user.client.Cookies;

import java.util.GregorianCalendar;

/**
 * Test for JavaCookies.
 */
public class JavaCookiesTest extends JavaGWTTestCase {
  public void testAddCookie() {
    Cookies.setCookie("Foo", "bar");
    assertEquals("bar", Cookies.getCookie("Foo"));
    Cookies.setCookie("Foo2", "bar2",
        new GregorianCalendar(3000, 0, 0).getTime());
    assertEquals("bar2", Cookies.getCookie("Foo2"));
      
    Cookies.setCookie("Foo3", "bar3", 
        new GregorianCalendar(3000, 0, 0).getTime(),
        null, "/ig/nored/", false);
    assertEquals("bar3", Cookies.getCookie("Foo3"));
  }
  
  public void testRemoveCookie() {
    Cookies.setCookie("Foo", "bar");
    assertEquals("bar", Cookies.getCookie("Foo"));

    Cookies.removeCookie("Foo");
    assertNull(Cookies.getCookie("Foo"));
  }
}
